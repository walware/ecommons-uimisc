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

package de.walware.ecommons.ui.mpbv;

import java.util.List;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.walware.ecommons.ui.components.ButtonGroup;
import de.walware.ecommons.ui.util.LayoutUtil;


public class ManageBookmarksDialog extends TrayDialog {
	
	
	private TableViewer fTableViewer;
	
	private ButtonGroup<BrowserBookmark> fButtons;
	
	private final PageBookBrowserView fView;
	private final List<BrowserBookmark> fBookmarks;
	
	
	protected ManageBookmarksDialog(final PageBookBrowserView view) {
		super(view.getViewSite().getShell());
		fView = view;
		fBookmarks = view.getBookmarks();
		
		create();
	}
	
	
	@Override
	protected void configureShell(final Shell shell) {
		shell.setText("Bookmarks");
		
		super.configureShell(shell);
	}
	
	@Override
	public boolean isHelpAvailable() {
		return false;
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	@Override
	protected Control createContents(final Composite parent) {
		final Control control = super.createContents(parent);
		
		fButtons.updateState();
		
		return control;
	}
	
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(LayoutUtil.applyDialogDefaults(new GridLayout(), 2));
		
		{	fTableViewer = new TableViewer(composite);
			fTableViewer.setUseHashlookup(true);
			final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.widthHint = LayoutUtil.hintWidth(fTableViewer.getTable(), 60);
			gd.heightHint = LayoutUtil.hintHeight(fTableViewer.getTable(), 20);
			fTableViewer.getControl().setLayoutData(gd);
			
			fTableViewer.setLabelProvider(new LabelProvider());
		}
		
		fButtons = new ButtonGroup<BrowserBookmark>(composite) {
			@Override
			protected BrowserBookmark edit1(final BrowserBookmark item, final boolean newItem, final Object parent) {
				final EditBookmarkDialog dialog = new EditBookmarkDialog(getShell(), item);
				if (dialog.open() == Dialog.OK) {
					return dialog.getBookmark();
				}
				return null;
			}
			@Override
			public void updateState() {
				super.updateState();
				getButton(IDialogConstants.OPEN_ID).setEnabled(
						((IStructuredSelection) fTableViewer.getSelection()).size() == 1);
			}
		};
		fButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		fButtons.addAddButton(null);
		fButtons.addEditButton(null);
		fButtons.addDeleteButton(null);
		fButtons.addSeparator();
		fButtons.addUpButton(null);
		fButtons.addDownButton(null);
		
		final WritableList writableList = new WritableList(fBookmarks, BrowserBookmark.class);
		fTableViewer.setContentProvider(new ObservableListContentProvider());
		fTableViewer.setInput(writableList);
		fButtons.connectTo(fTableViewer, writableList, null);
		
		applyDialogFont(composite);
		return composite;
	}
	
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createButton(parent, IDialogConstants.OPEN_ID, IDialogConstants.OPEN_LABEL, false);
		createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
	}
	
	@Override
	protected void buttonPressed(final int buttonId) {
		if (buttonId == IDialogConstants.OPEN_ID) {
			final BrowserBookmark bookmark = (BrowserBookmark)
					((IStructuredSelection) fTableViewer.getSelection()).getFirstElement();
			fView.openBookmark(bookmark, fView.getCurrentSession());
			close();
		}
		if (buttonId == IDialogConstants.CLOSE_ID) {
			close();
		}
	}
	
}
