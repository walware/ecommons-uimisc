/*******************************************************************************
 * Copyright (c) 2011-2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.dialogs;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import de.walware.ecommons.databinding.jface.DatabindingSupport;
import de.walware.ecommons.ui.util.LayoutUtil;


public abstract class AbstractCheckboxSelectionDialog extends ExtStatusDialog {
	
	
	private CheckboxTableViewer fViewer;
	
	private WritableSet fCheckedValue;
	
	
	/**
	 * Creates a new dialog
	 * 
	 * @param shell
	 * @param checkedElements optional collection of initially checked elements
	 */
	public AbstractCheckboxSelectionDialog(final Shell shell, final Collection<?> checkedElements) {
		super(shell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		
		fCheckedValue = (checkedElements != null) ? new WritableSet() : new WritableSet(checkedElements, null);
	}
	
	
	/**
	 * Returns the viewer cast to the correct instance.  Possibly <code>null</code> if
	 * the viewer has not been created yet.
	 * 
	 * @return the viewer cast to CheckboxTableViewer
	 */
	protected CheckboxTableViewer getCheckBoxTableViewer() {
		return fViewer;
	}
	
	protected Composite createCheckboxComposite(Composite parent, String message) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(LayoutUtil.applyCompositeDefaults(new GridLayout(), 3));
		
		if (message != null)
		{	Label label = new Label(composite, SWT.NONE);
			label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
			label.setText(message);
		}
		{	final Table table = new Table(composite, SWT.BORDER | SWT.SINGLE | SWT.CHECK);
			final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
			gd.heightHint = 250;
			gd.widthHint = 300;
			table.setLayoutData(gd);
			
			fViewer = new CheckboxTableViewer(table);
			fViewer.addCheckStateListener(new ICheckStateListener() {
				@Override
				public void checkStateChanged(CheckStateChangedEvent event) {
					updateCheckedStatus();
				}
			});
			
			configureViewer(fViewer);
		}
		
		{	final Button button = new Button(composite, SWT.PUSH);
			GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
			gd.widthHint = LayoutUtil.hintWidth(button);
			button.setLayoutData(gd);
			button.setText("Select All");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					getCheckBoxTableViewer().setAllChecked(true);
					updateCheckedStatus();
				}
			});
		}
		{	final Button button = new Button(composite, SWT.PUSH);
			GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
			gd.widthHint = LayoutUtil.hintWidth(button);
			button.setLayoutData(gd);
			button.setText("Deselect All");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					getCheckBoxTableViewer().setAllChecked(false);
					updateCheckedStatus();
				}
			});
		}
		return composite;
	}
	
	protected abstract void configureViewer(CheckboxTableViewer viewer);
	
	@Override
	protected void addBindings(final DatabindingSupport databinding) {
		databinding.getContext().bindSet(ViewersObservables.observeCheckedElements(fViewer, null),
				fCheckedValue);
	}
	
	public Set getCheckedElements() {
		return fCheckedValue;
	}
	
	protected void updateCheckedStatus() {
		getButton(IDialogConstants.OK_ID).setEnabled(isValid());
	}
	
	protected boolean isValid() {
		return getCheckBoxTableViewer().getCheckedElements().length > 0;
	}
	
}
