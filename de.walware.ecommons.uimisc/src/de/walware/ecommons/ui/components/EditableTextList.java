/*=============================================================================#
 # Copyright (c) 2012-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.components;

import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.ui.util.ViewerUtil;


public class EditableTextList implements ButtonGroup.IActions<String> {
	
	
	private TableViewer fViewer;
	private ViewerUtil.TableComposite fComposite;
	private TableViewerColumn fColumn;
	
	private ButtonGroup<String> fButtonGroup;
	
	
	public Control create(final Composite parent, final ViewerComparator comparator) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(LayoutUtil.applyCompositeDefaults(new GridLayout(),2));
		
		fComposite = new ViewerUtil.TableComposite(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		fComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fViewer = fComposite.viewer;
		fViewer.setContentProvider(new ArrayContentProvider());
		fViewer.setComparator(comparator);
		fComposite.table.setFont(JFaceResources.getTextFont());
		fComposite.table.setLinesVisible(true);
		
		fColumn = new TableViewerColumn(fViewer, SWT.NONE);
		fColumn.setLabelProvider(new ColumnLabelProvider());
		fComposite.layout.setColumnData(fColumn.getColumn(), new ColumnWeightData(100));
		
		fButtonGroup = new ButtonGroup<String>(composite, this, true);
		fButtonGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, true));
		
		fButtonGroup.addAddButton(null);
		fButtonGroup.addEditButton(null);
		fButtonGroup.addDeleteButton(null);
		
		return composite;
	}
	
	public TableViewer getViewer() {
		return fViewer;
	}
	
	public TableViewerColumn getColumn() {
		return fColumn;
	}
	
	
	public void setInput(final IObservableCollection input) {
		fButtonGroup.connectTo(fViewer, input, null);
		fViewer.setInput(input);
		fComposite.layout(false);
	}
	
	public void applyChange(final Object oldWord, final Object newWord) {
		if (oldWord.equals(newWord)) {
			return;
		}
		fButtonGroup.apply((String) oldWord, (String) newWord);
	}
	
	public void refresh() {
		fViewer.refresh(false);
		fComposite.layout(false);
	}
	
	
	@Override
	public String edit(final int command, final String item, final Object parent) {
		if (command == ButtonGroup.ADD_NEW) {
			return ""; //$NON-NLS-1$
		}
		if (command == ButtonGroup.EDIT) {
			return item;
		}
		return null;
	}
	
	@Override
	public void updateState(final IStructuredSelection selection) {
		fComposite.layout(false);
	}
	
}
