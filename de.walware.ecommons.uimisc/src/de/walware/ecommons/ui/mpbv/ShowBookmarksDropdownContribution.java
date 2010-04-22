/*******************************************************************************
 * Copyright (c) 2009-2010 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.mpbv;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.actions.CompoundContributionItem;

import de.walware.ecommons.ui.actions.SimpleContributionItem;


public class ShowBookmarksDropdownContribution extends CompoundContributionItem {
	
	
	public static class OpenBookmarkContributionItem extends SimpleContributionItem {
		
		
		private final PageBookBrowserView fView;
		private final BrowserBookmark fBookmark;
		
		
		public OpenBookmarkContributionItem(final PageBookBrowserView view, final BrowserBookmark bookmark) {
			super(bookmark.getLabel(), null);
			fView = view;
			fBookmark = bookmark;
		}
		
		public OpenBookmarkContributionItem(final PageBookBrowserView view, final BrowserBookmark bookmark,
				final String label, final String mnemonic) {
			super((label != null) ? label : bookmark.getLabel(), mnemonic);
			fView = view;
			fBookmark = bookmark;
		}
		
		
		@Override
		protected void execute() throws ExecutionException {
			fView.openBookmark(fBookmark, fView.getCurrentSession());
		}
		
	}
	
	
	private final PageBookBrowserView fView;
	
	
	public ShowBookmarksDropdownContribution(final PageBookBrowserView view) {
		fView = view;
	}
	
	
	@Override
	protected IContributionItem[] getContributionItems() {
		final List<IContributionItem> list = new ArrayList<IContributionItem>();
		
		final List<BrowserBookmark> bookmarks = fView.getBookmarks();
		for (int i = 0; i < bookmarks.size(); i++) {
			list.add(createPageContribution(bookmarks.get(i), i+1));
		}
		
		return list.toArray(new IContributionItem[list.size()]);
	}
	
	private ContributionItem createPageContribution(final BrowserBookmark bookmark, final int num) {
		String label = bookmark.getLabel();
		String mnemonic = null;
		if (num < 10) {
			mnemonic = Integer.toString(num);
			label = mnemonic + " " + label;
		}
		return new OpenBookmarkContributionItem(fView, bookmark, label, mnemonic);
	}
	
}
