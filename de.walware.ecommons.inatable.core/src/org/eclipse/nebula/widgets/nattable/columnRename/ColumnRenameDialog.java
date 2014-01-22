/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// -depend
package org.eclipse.nebula.widgets.nattable.columnRename;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.nebula.widgets.nattable.Messages;
import org.eclipse.nebula.widgets.nattable.internal.NatTablePlugin;
import org.eclipse.nebula.widgets.nattable.style.editor.AbstractStyleEditorDialog;


public class ColumnRenameDialog extends AbstractStyleEditorDialog {
	
	
	private ColumnLabelPanel columnLabelPanel;
	private final String columnLabel;
	private String renamedColumnLabel;

	public ColumnRenameDialog(Shell parent, String columnLabel, String renamedColumnLabel) {
		super(parent);
		this.columnLabel = columnLabel;
		this.renamedColumnLabel = renamedColumnLabel;
	}

	@Override
	protected void initComponents(final Shell shell) {
		GridLayout shellLayout = new GridLayout();
		shell.setLayout(shellLayout);
		shell.setText(Messages.getString("ColumnRenameDialog.shellTitle")); //$NON-NLS-1$

		// Closing the window is the same as canceling the form
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				doFormCancel(shell);
			}
		});

		// Tabs panel
		Composite panel = new Composite(shell, SWT.NONE);
		panel.setLayout(new GridLayout());

		GridData fillGridData = new GridData();
		fillGridData.grabExcessHorizontalSpace = true;
		fillGridData.horizontalAlignment = GridData.FILL;
		panel.setLayoutData(fillGridData);

		columnLabelPanel = new ColumnLabelPanel(panel, columnLabel, renamedColumnLabel);
		try {
			columnLabelPanel.edit(renamedColumnLabel);
		} catch (Exception e) {
			NatTablePlugin.log(new Status(IStatus.ERROR, NatTablePlugin.PLUGIN_ID, "An error occurred when initializing the edit component.", e));
		}
	}

	@Override
	protected void doFormOK(Shell shell) {
		renamedColumnLabel = columnLabelPanel.getNewValue();
		shell.dispose();
	}

	@Override
	protected void doFormClear(Shell shell) {
		renamedColumnLabel = null;
		shell.dispose();
	}

	public String getNewColumnLabel() {
		return renamedColumnLabel;
	}
}
