/*******************************************************************************
 * Copyright (c) 2009-2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.walware.ecommons.collections.ConstList;
import de.walware.ecommons.ui.SharedMessages;
import de.walware.ecommons.ui.util.LayoutUtil;


/**
 * Composite with buttons to manipulate list or tree items.
 */
public class ButtonGroup<ItemType> extends Composite {
	
	
	public static final int ADD_NEW = 1;
	public static final int ADD_COPY = 2;
	public static final int ADD_ANY = ADD_NEW | ADD_COPY;
	public static final int EDIT = 4;
	
	
	public static interface IActions<ItemType> {
		
		
		ItemType edit(final int command, final ItemType item, final Object parent);
		
		void updateState(IStructuredSelection selection);
		
	}
	
	public static interface IImportExportActions<ItemType> extends IActions<ItemType> {
		
		void importItems();
		
		void exportItems(final List<? extends Object> items);
		
	}
	
	
	private DataAdapter<ItemType> fDataAdapter;
	
	private IActions<ItemType> fActions;
	
	private StructuredViewer fViewer;
	private boolean fTreeMode;
	private boolean fCellMode;
	
	private Button fAddButton;
	private Button fCopyButton;
	private Button fEditButton;
	private Button fDeleteButton;
	
	private Button fDefaultButton;
	
	private Button fUpButton;
	private Button fDownButton;
	
	private Button fImportButton;
	private Button fExportButton;
	
	private int fCachedWidthHint;
	
	
	public ButtonGroup(final Composite parent) {
		super(parent, SWT.NONE);
		setLayout(LayoutUtil.applyCompositeDefaults(new GridLayout(), 1));
	}
	
	public ButtonGroup(final Composite parent, final IActions actions, final boolean cellMode) {
		super(parent, SWT.NONE);
		setLayout(LayoutUtil.applyCompositeDefaults(new GridLayout(), 1));
		fActions = actions;
		fCellMode = cellMode;
	}
	
	
	public DataAdapter<ItemType> getDataAdapter() {
		return fDataAdapter;
	}
	
	protected void addLayoutData(final Control control) {
		if (fCachedWidthHint == 0 && control instanceof Button) {
			fCachedWidthHint = LayoutUtil.hintWidth((Button) control);
		}
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.widthHint = fCachedWidthHint;
		control.setLayoutData(gd);
	}
	
	public void addAddButton() {
		fAddButton = new Button(this, SWT.PUSH);
		addLayoutData(fAddButton);
		String label = SharedMessages.CollectionEditing_AddItem_label;
		if (!fCellMode) {
			label += "...";
		}
		fAddButton.setText(label);
		fAddButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Object item = ((IStructuredSelection) fViewer.getSelection()).getFirstElement();
				editElement(ADD_NEW, item);
			}
		});
	}
	
	public void addCopyButton() {
		fCopyButton = new Button(this, SWT.PUSH);
		addLayoutData(fCopyButton);
		String label = SharedMessages.CollectionEditing_CopyItem_label;
		if (!fCellMode) {
			label += "...";
		}
		fCopyButton.setText(label);
		fCopyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Object item = getElementToEdit((IStructuredSelection) fViewer.getSelection());
				if (item != null) {
					editElement(ADD_COPY, item);
				}
			}
		});
	}
	
	public void addEditButton() {
		fEditButton = new Button(this, SWT.PUSH);
		addLayoutData(fEditButton);
		String label = SharedMessages.CollectionEditing_EditItem_label;
		if (!fCellMode) {
			label += "...";
		}
		fEditButton.setText(label);
		fEditButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Object item = getElementToEdit((IStructuredSelection) fViewer.getSelection());
				if (item != null) {
					editElement(EDIT, item);
				}
			}
		});
	}
	
	public void addDeleteButton() {
		fDeleteButton = new Button(this, SWT.PUSH);
		addLayoutData(fDeleteButton);
		fDeleteButton.setText(SharedMessages.CollectionEditing_RemoveItem_label);
		fDeleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final List<? extends Object> items = getElementsToDelete((IStructuredSelection) fViewer.getSelection());
				delete0(items);
			}
		});
	}
	
	public void addDefaultButton() {
		fDefaultButton = new Button(this, SWT.PUSH);
		addLayoutData(fDefaultButton);
		fDefaultButton.setText(SharedMessages.CollectionEditing_DefaultItem_label);
		fDefaultButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Object item = getElementForDefault((IStructuredSelection) fViewer.getSelection());
				if (item != null) {
					setDefault0(item);
				}
			}
		});
	}
	
	public void addUpButton() {
		fUpButton = new Button(this, SWT.PUSH);
		addLayoutData(fUpButton);
		fUpButton.setText(SharedMessages.CollectionEditing_MoveItemUp_label);
		fUpButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final IStructuredSelection selection = (IStructuredSelection) fViewer.getSelection();
				final Object item = getElementToEdit(selection);
				if (item != null) {
					move0(item, -1);
				}
			}
		});
	}
	
	public void addDownButton() {
		fDownButton = new Button(this, SWT.PUSH);
		addLayoutData(fDownButton);
		fDownButton.setText(SharedMessages.CollectionEditing_MoveItemDown_label);
		fDownButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fDownButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Object item = getElementToEdit((IStructuredSelection) fViewer.getSelection());
				if (item != null) {
					move0(item, 1);
				}
			}
		});
	}
	
	public void addImportButton() {
		fImportButton = new Button(this, SWT.PUSH);
		addLayoutData(fImportButton);
		fImportButton.setText(SharedMessages.CollectionEditing_Import_label);
		fImportButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fImportButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				import0();
			}
		});
	}
	
	public void addExportButton() {
		fExportButton = new Button(this, SWT.PUSH);
		addLayoutData(fExportButton);
		fExportButton.setText(SharedMessages.CollectionEditing_Export_label);
		fExportButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fExportButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final List<? extends Object> items = getElementsToExport((IStructuredSelection) fViewer.getSelection());
				export0(items);
			}
		});
	}
	
	
	public void addSeparator() {
		LayoutUtil.addSmallFiller(this, false);
	}
	
	public void connectTo(final StructuredViewer viewer, final IObservableCollection list,
			final IObservableValue defaultValue) {
		connectTo(viewer, new DataAdapter.ListAdapter<ItemType>(
				(viewer.getContentProvider() instanceof ITreeContentProvider) ?
						(ITreeContentProvider) viewer.getContentProvider() : null,
				list, defaultValue ));
	}
	
	public void connectTo(final StructuredViewer viewer, final DataAdapter<ItemType> adapter) {
		fViewer = viewer;
		fTreeMode = (viewer instanceof TreeViewer);
		if (fDeleteButton != null) {
			fViewer.getControl().addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(final KeyEvent event) {
					if (event.character == SWT.DEL && event.stateMask == 0 && fDeleteButton != null) {
						final List<? extends Object> items = getElementsToDelete((IStructuredSelection) fViewer.getSelection());
						if (items != null) {
							delete0(items);
						}
					} 
				}	
			});
		}
		if (fEditButton != null && !fCellMode) {
			fViewer.addDoubleClickListener(new IDoubleClickListener() {
				@Override
				public void doubleClick(final DoubleClickEvent event) {
					final Object item = getElementToEdit((IStructuredSelection) event.getSelection());
					if (item != null) {
						editElement(EDIT, item);
					}
				}
			});
		}
		fViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				updateState();
			}
		});
		
		fDataAdapter = adapter;
	}
	
	public void setCheckedModel(final Set<ItemType> set) {
		fDataAdapter.setCheckedModel(set);
	}
	
	public void updateState() {
		final IStructuredSelection selection = (IStructuredSelection) fViewer.getSelection();
		if (fAddButton != null) {
			if (fTreeMode) {
				fAddButton.setEnabled(selection.size() == 1
						&& fDataAdapter.isAddAllowed(selection.getFirstElement()));
			}
			else {
				fAddButton.setEnabled(true);
			}
		}
		final Object item = getElementToEdit(selection);
		if (fCopyButton != null) {
			fCopyButton.setEnabled(item != null);
		}
		if (fEditButton != null) {
			fEditButton.setEnabled(item != null);
		}
		if (fDeleteButton != null) {
			fDeleteButton.setEnabled(getElementsToDelete(selection) != null);
		}
		
		if (fDefaultButton != null) {
			fDefaultButton.setEnabled(getElementForDefault(selection) != null);
		}
		
		if (fUpButton != null) {
			fUpButton.setEnabled(item != null);
		}
		if (fDownButton != null) {
			fDownButton.setEnabled(item != null);
		}
		
		if (fExportButton != null) {
			fExportButton.setEnabled(!selection.isEmpty());
		}
		
		if (fActions != null) {
			fActions.updateState(selection);
		}
	}
	
	protected Object getElementToEdit(final IStructuredSelection selection) {
		if (selection.size() == 1) {
			final Object element = selection.getFirstElement();
			if (!fDataAdapter.isModifyAllowed(element)) {
				return null;
			}
			return element;
		}
		return null;
	}
	
	protected Object getElementForDefault(final IStructuredSelection selection) {
		if (selection.size() == 1) {
			final Object element = selection.getFirstElement();
			if (fDataAdapter.isContentItem(element)) {
				return element;
			}
		}
		return null;
	}
	
	protected List<? extends Object> getElementsToDelete(final IStructuredSelection selection) {
		if (!selection.isEmpty()) {
			final Object[] elements = selection.toArray();
			for (final Object element : elements) {
				if (!fDataAdapter.isModifyAllowed(element)) {
					return null;
				}
			}
			return new ConstList<Object>(elements);
		}
		return null;
	}
	
	protected List<? extends Object> getElementsToExport(final IStructuredSelection selection) {
		if (!selection.isEmpty()) {
			final List<Object> toExport = new ArrayList<Object>();
			final Object[] elements = selection.toArray();
			for (final Object element : elements) {
				if (fDataAdapter.isContentItem(element)) {
					toExport.add(element);
				}
				else {
					final Object[] children = fDataAdapter.getChildren(element);
					if (children != null) {
						for (int i = 0; i < children.length; i++) {
							if (fDataAdapter.isContentItem(children[i])) {
								toExport.add(children[i]);
							}
						}
					}
				}
			}
			return toExport;
		}
		return null;
	}
	
	
	public void editElement(final int command, Object element) {
		final ItemType orgItem = ((command & ADD_NEW) == 0 && element != null) ? fDataAdapter.getModelItem(element) : null;
		final Object parent = ((command & ADD_NEW) != 0) ? fDataAdapter.getAddParent(element) : fDataAdapter.getParent(element);
		
		final ItemType editItem = (fActions != null) ?
				fActions.edit(command, ((command & ADD_NEW) == 0) ? orgItem : null, parent) :
				edit1(((command & ADD_NEW) == 0) ? orgItem : null, (command & (ADD_NEW | ADD_COPY)) != 0, parent);
		if (editItem == null) {
			return;
		}
		element = fDataAdapter.change(((command & ADD_ANY) == 0) ? orgItem : null, editItem, 
				parent, fDataAdapter.getContainerFor(element) );
		refresh0(element);
		if (/*fCellMode &&*/ fViewer instanceof ColumnViewer) {
			((ColumnViewer) fViewer).editElement(element, 0);
		}
	}
	
	public void apply(final ItemType oldItem, final ItemType newItem) {
		if (newItem == null) {
			delete0(Collections.singletonList(oldItem));
		}
		else {
			final Object element = fDataAdapter.change(oldItem, newItem, null,
					fDataAdapter.getContainerFor(fDataAdapter.getViewerElement(
							(oldItem != null) ? oldItem : newItem, null )));
			refresh0(element);
		}
	}
	
	public void deleteElements(final int command, final List<? extends Object> elements) {
		delete0(elements);
	}
	
	protected ItemType edit1(final ItemType item, final boolean newItem, final Object parent) {
		return null;
	}
	
	private void delete0(final List<? extends Object> elements) {
		fDataAdapter.delete(elements);
		refresh0(null);
	}
	
	private void setDefault0(final Object element) {
		final ItemType item = fDataAdapter.getModelItem(element);
		fDataAdapter.setDefault(item);
		refresh0(null);
	}
	
	private void move0(final Object element, final int direction) {
		fDataAdapter.move(element, direction);
		refresh0(element);
	}
	
	private void import0() {
		((IImportExportActions<?>) fDataAdapter).importItems();
		refresh0(null);
	}
	
	private void export0(final List<? extends Object> items) {
		if (items == null || items.isEmpty()) {
			return;
		}
		((IImportExportActions<?>) fActions).exportItems(items);
	}
	
	public void refresh() {
		refresh0(null);
	}
	
	private void refresh0(final Object elementToSelect) {
		refresh1();
		if (elementToSelect != null) {
//			Display.getCurrent().asyncExec(new Runnable() {
//				public void run() {
					if (fTreeMode) {
						((TreeViewer) fViewer).expandToLevel(elementToSelect, 0);
					}
					fViewer.setSelection(new StructuredSelection(elementToSelect), true);
//				}
//			});
		}
		updateState();
	}
	
	protected void refresh1() {
		fViewer.refresh();
	}
	
}
