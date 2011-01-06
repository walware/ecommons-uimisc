/*******************************************************************************
 * Copyright (c) 2010-2011 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.mpbv;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.walware.ecommons.ui.util.LayoutUtil;


class EditBookmarkDialog extends Dialog {
	
	
	private Text fNameControl;
	private Text fUrlControl;
	
	private BrowserBookmark fBookmark;
	
	
	public EditBookmarkDialog(final Shell parentShell, final BrowserBookmark bookmark) {
		super(parentShell);
		
		fBookmark = bookmark;
		create();
	}
	
	
	@Override
	protected void configureShell(final Shell shell) {
		shell.setText("Edit Bookmark");
		
		super.configureShell(shell);
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(LayoutUtil.applyDialogDefaults(new GridLayout(), 2));
		
		{	final Label label = new Label(composite, SWT.NONE);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			label.setText("&Name:");
		}
		{	final Text text = new Text(composite, SWT.BORDER);
			final GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gd.widthHint = LayoutUtil.hintWidth(text, 80);
			text.setLayoutData(gd);
			fNameControl = text;
		}
		{	final Label label = new Label(composite, SWT.NONE);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			label.setText("&URL:");
		}
		{	final Text text = new Text(composite, SWT.BORDER | SWT.LEFT);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			fUrlControl = text;
		}
		
		if (fBookmark != null) {
			fNameControl.setText(fBookmark.getLabel());
			fUrlControl.setText(fBookmark.getUrl());
		}
		
		applyDialogFont(composite);
		return composite;
	}
	
	
	@Override
	protected void okPressed() {
		fBookmark = new BrowserBookmark(fNameControl.getText(), fUrlControl.getText());
		
		super.okPressed();
	}
	
	public BrowserBookmark getBookmark() {
		return fBookmark;
	}
	
}
