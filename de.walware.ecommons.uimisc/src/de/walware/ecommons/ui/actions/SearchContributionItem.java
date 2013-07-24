/*******************************************************************************
 * Copyright (c) 2009-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.actions;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.walware.ecommons.ui.components.SearchText;
import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.ui.util.UIAccess;
import de.walware.ecommons.workbench.ui.WorkbenchUIUtil;


public class SearchContributionItem extends ContributionItem {
	
	
	public static final int VIEW_TOOLBAR = 0x10000000;
	
	
	private class SWTListener implements Listener {
		
		@Override
		public void handleEvent(final Event event) {
			switch (event.type) {
			case SWT.Resize:
				scheduleSizeCheck();
				return;
			}
		}
		
	}
	
	
	private final int fOptions;
	
	private SearchText fControl;
	private ToolItem fTextItem;
	
	private String fToolTipText;
	
	private Composite fSizeControl;
	private Control fResultControl;
	
	private final boolean fUpdateWhenTyping;
	
	private final Runnable fSizeCheckRunnable = new Runnable() {
		@Override
		public void run() {
			fSizeCheckScheduled = false;
			resize();
		}
	};
	private boolean fSizeCheckScheduled;
	
	
	public SearchContributionItem(final String id, final int options) {
		this(id, options, false);
	}
	
	public SearchContributionItem(final String id, final int options, final boolean updateWhenTyping) {
		super(id);
		fOptions = options;
		fUpdateWhenTyping = updateWhenTyping;
	}
	
	
	public SearchText getSearchText() {
		return fControl;
	}
	
	/**
	 * Table or tree to select
	 */
	public void setResultControl(final Control control) {
		fResultControl = control;
	}
	
	public void setToolTip(final String text) {
		fToolTipText = text;
	}
	
	/**
	 * For views the control of the view
	 */
	public void setSizeControl(final Composite control) {
		fSizeControl = control;
		fSizeControl.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				resize();
			}
		});
	}
	
	public void resize() {
		if (fTextItem != null && !fTextItem.isDisposed() 
				&& fSizeControl != null) {
			final int viewWidth = fSizeControl.getClientArea().width;
			if (viewWidth <= 0) {
				return;
			}
			final ToolBar toolBar = fTextItem.getParent();
			final Composite toolBarParent = toolBar.getParent();
			final int toolBarWidth = toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
			final int currentWidth = fTextItem.getWidth();
			final int minWidth = LayoutUtil.hintWidth(fControl.getTextControl(), 8);
			
			int corr = toolBarWidth - currentWidth;
			if ((fOptions & VIEW_TOOLBAR) != 0) {
				if (WorkbenchUIUtil.IS_E4) {
					// E-4.2 View Toolbar (=> space for view menu)
					final Layout layout = toolBarParent.getLayout();
					if (layout instanceof RowLayout && ((RowLayout) layout).type == SWT.HORIZONTAL) {
						final Control[] children = toolBarParent.getChildren();
						for (int i = 0; i < children.length; i++) {
							if (children[i] != toolBar) {
								corr += children[i].getSize().x;
							}
						}
						corr += (children.length - 1) * ((RowLayout) layout).spacing;
					}
				}
				else {
					corr += 18;
				}
			}
			corr += 16; // 2 required
			
			final int width = Math.min(310, Math.max(minWidth, viewWidth - corr));
			if (width == currentWidth) {
				return;
			}
			
//			scheduleSizeCheck();
			
			fTextItem.setWidth(width);
			toolBar.layout(new Control[] { fControl });
			toolBarParent.layout(true, true);
			if (WorkbenchUIUtil.IS_E4) {
				toolBarParent.pack(true);
			}
		}
	}
	
	private void scheduleSizeCheck() {
		if (!fSizeCheckScheduled && fTextItem != null && !fTextItem.isDisposed()) {
			fSizeCheckScheduled = true;
			fTextItem.getDisplay().asyncExec(fSizeCheckRunnable);
		}
	}
	
	@Override
	public void fill(final ToolBar parent, final int index) {
		fControl = new SearchText(parent);
		fControl.addListener(createSearchTextListener());
		final Listener swtListener = new SWTListener();
		fControl.addListener(SWT.Resize, swtListener);
		fControl.setToolTipText(fToolTipText);
		
		fTextItem = new ToolItem(parent, SWT.SEPARATOR, index);
		fTextItem.setControl(fControl);
		fTextItem.setToolTipText(fToolTipText);
		fTextItem.setWidth(310); // high value prevents that the toolbar is moved to tabs
	}
	
	public Control create(final Composite parent) {
		fControl = new SearchText(parent);
		fControl.addListener(createSearchTextListener());
		final Listener swtListener = new SWTListener();
		fControl.addListener(SWT.Resize, swtListener);
		fControl.setToolTipText(fToolTipText);
		return fControl;
	}
	
	protected SearchText.Listener createSearchTextListener() {
		return new SearchText.Listener() {
			@Override
			public void textChanged(final boolean user) {
				if (fUpdateWhenTyping || !user) {
					SearchContributionItem.this.search();
				}
			}
			@Override
			public void okPressed() {
				SearchContributionItem.this.search();
			}
			@Override
			public void downPressed() {
				SearchContributionItem.this.selectFirst();
			}
		};
	}
	
	protected void search() {
	}
	
	protected void selectFirst() {
		if (fResultControl instanceof Table) {
			final Table table = (Table) fResultControl;
			table.setFocus();
			if (table.getSelectionCount() == 0) {
				final int idx = table.getTopIndex();
				if (idx >= 0) {
					table.setSelection(idx);
				}
			}
		}
		else if (fResultControl instanceof Tree) {
			final Tree table = (Tree) fResultControl;
			table.setFocus();
			if (table.getSelectionCount() == 0) {
				final TreeItem item = table.getTopItem();
				if (item != null) {
					table.setSelection(item);
				}
			}
		}
	}
	
	public String getText() {
		return fControl.getText();
	}
	
	public void show() {
		if (!UIAccess.isOkToUse(fControl)) {
			return;
		}
		fControl.setFocus();
	}
	
}
