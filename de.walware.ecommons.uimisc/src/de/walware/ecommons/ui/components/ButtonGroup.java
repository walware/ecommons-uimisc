/*=============================================================================#
 # Copyright (c) 2009-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

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
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.walware.ecommons.ui.SharedMessages;
import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.ui.util.ViewerUtil;


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
	
	
	public static class SelectionHandler extends SelectionAdapter implements DisposeListener {
		
		
		private ButtonGroup<?> fGroup;
		
		private Control fControl;
		
		
		public void update(final IStructuredSelection selection) {
			setEnabled(getElement(selection) != null);
		}
		
		protected ButtonGroup<?> getGroup() {
			return fGroup;
		}
		
		protected Control getControl() {
			return fControl;
		}
		
		protected void setEnabled(final boolean enabled) {
			fControl.setEnabled(enabled);
		}
		
		protected Object getElement(final IStructuredSelection selection) {
			if (selection.size() == 1) {
				return selection.getFirstElement();
			}
			return null;
		}
		
		@Override
		public void widgetSelected(final SelectionEvent e) {
			run((IStructuredSelection) fGroup.fViewer.getSelection());
		}
		
		public boolean run(final IStructuredSelection selection) {
			return false;
		}
		
		@Override
		public void widgetDisposed(final DisposeEvent e) {
		}
		
	}
	
	public static class ElementListHandler extends SelectionHandler {
		
		
		@Override
		public void update(final IStructuredSelection selection) {
			final List<? extends Object> list = getElement(selection);
			setEnabled(list != null && !list.isEmpty());
		}
		
		@Override
		protected List<? extends Object> getElement(final IStructuredSelection selection) {
			return selection.toList();
		}
		
		@Override
		public boolean run(final IStructuredSelection selection) {
			return false;
		}
		
	}
	
	public static class ItemListHandler<ItemType> extends ElementListHandler {
		
		
		@Override
		public void update(final IStructuredSelection selection) {
			setEnabled(hasItem(selection.toList()));
		}
		
		@Override
		protected List<ItemType> getElement(final IStructuredSelection selection) {
			return getItems(selection.toList());
		}
		
		
		protected boolean hasItem(final List<? extends Object> list) {
			final DataAdapter<?> dataAdapter = getGroup().getDataAdapter();
			for (final Object element : list) {
				if (dataAdapter.isContentItem(element)) {
					return true;
				}
				if (getGroup().fTreeMode) {
					if (hasItem(dataAdapter.getChildren(element))) {
						return true;
					}
				}
			}
			return false;
		}
		
		protected boolean hasItem(final Object[] array) {
			if (array != null) {
				final DataAdapter<?> dataAdapter = getGroup().getDataAdapter();
				for (final Object element : array) {
					if (dataAdapter.isContentItem(element)) {
						return true;
					}
					if (hasItem(dataAdapter.getChildren(element))) {
						return true;
					}
				}
			}
			return false;
		}
		
		protected List<ItemType> getItems(final List<? extends Object> list) {
			if (!list.isEmpty()) {
				final DataAdapter<ItemType> dataAdapter = (DataAdapter<ItemType>) getGroup().getDataAdapter();
				final List<ItemType> items = new ArrayList<ItemType>();
				for (final Object element : list) {
					if (dataAdapter.isContentItem(element)) {
						items.add(dataAdapter.getModelItem(element));
					}
					else if (getGroup().fTreeMode) {
						collectItems(dataAdapter.getChildren(element), items);
					}
				}
				return items;
			}
			return null;
		}
		
		protected void collectItems(final Object[] elements, final List<ItemType> items) {
			if (elements != null) {
				final DataAdapter<ItemType> dataAdapter = (DataAdapter<ItemType>) getGroup().getDataAdapter();
				for (final Object element : elements) {
					if (dataAdapter.isContentItem(element)) {
						items.add(dataAdapter.getModelItem(element));
					}
					else {
						collectItems(dataAdapter.getChildren(element), items);
					}
				}
			}
		}
		
	}
	
	public static class AddHandler extends SelectionHandler {
		
		
		@Override
		public void update(final IStructuredSelection selection) {
			if (getGroup().fTreeMode) {
				final Object element = getElement(selection);
				setEnabled(element != null
						&& getGroup().getDataAdapter().isAddAllowed(selection.getFirstElement()) );
			}
			else {
				setEnabled(true);
			}
		}
		
		@Override
		public boolean run(final IStructuredSelection selection) {
			getGroup().editElement(ADD_NEW, getElement(selection));
			return true;
		}
		
	}
	
	public static class CopyHandler extends SelectionHandler {
		
		
		@Override
		protected Object getElement(final IStructuredSelection selection) {
			final Object element = super.getElement(selection);
			return (element != null && getGroup().getDataAdapter().isModifyAllowed(element)) ?
					element : null;
		}
		
		@Override
		public boolean run(final IStructuredSelection selection) {
			final Object element = getElement(selection);
			if (element != null) {
				getGroup().editElement(ADD_COPY, element);
				return true;
			}
			return false;
		}
		
	}
	
	public static class EditHandler extends SelectionHandler {
		
		
		@Override
		protected Object getElement(final IStructuredSelection selection) {
			final Object element = super.getElement(selection);
			return (element != null && getGroup().getDataAdapter().isModifyAllowed(element)) ?
					element : null;
		}
		
		@Override
		public boolean run(final IStructuredSelection selection) {
			final Object element = getElement(selection);
			if (element != null) {
				getGroup().editElement(EDIT, element);
				return true;
			}
			return false;
		}
		
	}
	
	public static class DeleteHandler extends ElementListHandler {
		
		@Override
		protected List<? extends Object> getElement(final IStructuredSelection selection) {
			final List<? extends Object> list = super.getElement(selection);
			final DataAdapter<?> adapter = getGroup().getDataAdapter();
			for (final Object object : list) {
				if (!adapter.isDeleteAllowed(object)) {
					return null;
				}
			}
			return list;
		}
		
		@Override
		public boolean run(final IStructuredSelection selection) {
			final List<? extends Object> list = getElement(selection);
			if (list != null) {
				getGroup().delete0(list);
				return true;
			}
			return false;
		}
		
	}
	
	public static class DefaultHandler extends SelectionHandler {
		
		
		@Override
		protected Object getElement(final IStructuredSelection selection) {
			final Object element = super.getElement(selection);
			return (element != null && getGroup().getDataAdapter().isContentItem(element)) ?
					element : null;
		}
		
		@Override
		public boolean run(final IStructuredSelection selection) {
			final Object element = getElement(selection);
			if (element != null) {
				getGroup().setDefault(element);
				return true;
			}
			return false;
		}
		
	}
	
	public static class MoveHandler extends SelectionHandler {
		
		
		private final int fDirection;
		
		
		public MoveHandler(final int direction) {
			fDirection = direction;
		}
		
		
		protected int getDirection() {
			return fDirection;
		}
		
		@Override
		protected Object getElement(final IStructuredSelection selection) {
			final Object element = super.getElement(selection);
			return (element != null && getGroup().getDataAdapter().isMoveAllowed(element, fDirection)) ?
					element : null;
		}
		
		@Override
		public boolean run(final IStructuredSelection selection) {
			final Object element = getElement(selection);
			if (element != null) {
				getGroup().move0(element, fDirection);
				return true;
			}
			return false;
		}
		
	}
	
	public static class ImportHandler extends SelectionHandler {
		
		
		@Override
		public void update(final IStructuredSelection selection) {
		}
		
		@Override
		public boolean run(final IStructuredSelection selection) {
			getGroup().import0();
			return true;
		}
		
	}
	
	public static class ExportHandler<ItemType> extends ItemListHandler<ItemType> {
		
		
		@Override
		public boolean run(final IStructuredSelection selection) {
			final List<ItemType> list = getElement(selection);
			if (list != null) {
				getGroup().export0(list);
				return true;
			}
			return false;
		}
		
	}
	
	
	private DataAdapter<ItemType> fDataAdapter;
	
	private IActions<ItemType> fActions;
	
	private StructuredViewer fViewer;
	private boolean fTreeMode;
	private boolean fCellMode;
	
	private SelectionHandler fEditHandler;
	private SelectionHandler fDeleteHandler;
	
	private final List<SelectionHandler> fHandlers = new ArrayList<SelectionHandler>();
	
	private int fCachedWidthHint;
	
	
	public ButtonGroup(final Composite parent) {
		super(parent, SWT.NONE);
		setLayout(LayoutUtil.createCompositeGrid(1));
	}
	
	public ButtonGroup(final Composite parent, final IActions<ItemType> actions, final boolean cellMode) {
		super(parent, SWT.NONE);
		setLayout(LayoutUtil.createCompositeGrid(1));
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
	
	public void add(final Control control, final SelectionHandler handler) {
		handler.fGroup = this;
		handler.fControl = control;
		addLayoutData(control);
		
		control.addDisposeListener(handler);
		if (control instanceof Button) {
			((Button) control).addSelectionListener(handler);
		}
		
		fHandlers.add(handler);
	}
	
	public void addAddButton(SelectionHandler handler) {
		final Button button = new Button(this, SWT.PUSH);
		String label = SharedMessages.CollectionEditing_AddItem_label;
		if (!fCellMode) {
			label += "..."; //$NON-NLS-1$
		}
		button.setText(label);
		if (handler == null) {
			handler = new AddHandler();
		}
		add(button, handler);
	}
	
	public void addCopyButton(SelectionHandler handler) {
		final Button button = new Button(this, SWT.PUSH);
		String label = SharedMessages.CollectionEditing_CopyItem_label;
		if (!fCellMode) {
			label += "..."; //$NON-NLS-1$
		}
		button.setText(label);
		if (handler == null) {
			handler = new CopyHandler();
		}
		add(button, handler);
	}
	
	public void addEditButton(SelectionHandler handler) {
		final Button button = new Button(this, SWT.PUSH);
		String label = SharedMessages.CollectionEditing_EditItem_label;
		if (!fCellMode) {
			label += "..."; //$NON-NLS-1$
		}
		button.setText(label);
		if (handler == null) {
			handler = new EditHandler();
		}
		fEditHandler = handler;
		add(button, handler);
	}
	
	public void addDeleteButton(SelectionHandler handler) {
		final Button button = new Button(this, SWT.PUSH);
		button.setText(SharedMessages.CollectionEditing_RemoveItem_label);
		if (handler == null) {
			handler = new DeleteHandler();
		}
		fDeleteHandler = handler;
		add(button, handler);
	}
	
	public void addDefaultButton(SelectionHandler handler) {
		final Button button = new Button(this, SWT.PUSH);
		button.setText(SharedMessages.CollectionEditing_DefaultItem_label);
		if (handler == null) {
			handler = new DefaultHandler();
		}
		add(button, handler);
	}
	
	public void addUpButton(SelectionHandler handler) {
		final Button button = new Button(this, SWT.PUSH);
		button.setText(SharedMessages.CollectionEditing_MoveItemUp_label);
		if (handler == null) {
			handler = new MoveHandler(-1);
		}
		add(button, handler);
	}
	
	public void addDownButton(SelectionHandler handler) {
		final Button button = new Button(this, SWT.PUSH);
		button.setText(SharedMessages.CollectionEditing_MoveItemDown_label);
		if (handler == null) {
			handler = new MoveHandler(1);
		}
		add(button, handler);
	}
	
	public void addImportButton(SelectionHandler handler) {
		final Button button = new Button(this, SWT.PUSH);
		button.setText(SharedMessages.CollectionEditing_Import_label);
		if (handler == null) {
			handler = new ImportHandler();
		}
		add(button, handler);
	}
	
	public void addExportButton(SelectionHandler handler) {
		final Button button = new Button(this, SWT.PUSH);
		button.setText(SharedMessages.CollectionEditing_Export_label);
		if (handler == null) {
			handler = new ExportHandler<ItemType>();
		}
		add(button, handler);
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
		if (fDeleteHandler != null) {
			fViewer.getControl().addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(final KeyEvent event) {
					if (event.character == SWT.DEL && event.stateMask == 0 && fDeleteHandler != null) {
						fDeleteHandler.run((IStructuredSelection) fViewer.getSelection());
					}
				}	
			});
		}
		if (fEditHandler != null && !fCellMode) {
			fViewer.addDoubleClickListener(new IDoubleClickListener() {
				@Override
				public void doubleClick(final DoubleClickEvent event) {
					final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					if (fEditHandler != null && !fEditHandler.run(selection)
							&& fTreeMode && selection.size() == 1) {
						((TreeViewer) fViewer).setExpandedState(selection.getFirstElement(), 
								!((TreeViewer) fViewer).getExpandedState(selection.getFirstElement()));
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
		
		for (final SelectionHandler handler : fHandlers) {
			handler.update(selection);
		}
		
		if (fActions != null) {
			fActions.updateState(selection);
		}
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
	
	public void setDefault(final Object element) {
		setDefault0(element);
	}
	
	/**
	 * @deprecated implement {@link IActions#edit(int, Object, Object)}
	 */
	@Deprecated
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
		((IImportExportActions<?>) fActions).importItems();
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
						ViewerUtil.expandToLevel((TreeViewer) fViewer, elementToSelect, 0);
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
