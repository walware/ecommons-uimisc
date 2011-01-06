/*******************************************************************************
 * Copyright (c) 2009-2011 WalWare/StatET-Project (www.walware.de/goto/statet)
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.walware.ecommons.preferences.Preference;
import de.walware.ecommons.preferences.Preference.StringArrayPref;
import de.walware.ecommons.preferences.PreferencesUtil;


public class BookmarkCollection {
	
	
	private static final Map<String, BookmarkCollection> gCollections = new HashMap<String, BookmarkCollection>();
	
	public static BookmarkCollection getCollection(final String qualifier) {
		synchronized (gCollections) {
			BookmarkCollection collection = gCollections.get(qualifier);
			if (collection == null) {
				collection = new BookmarkCollection(qualifier);
				gCollections.put(qualifier, collection);
			}
			return collection;
		}
	}
	
	
	private static final char SEPARATOR = '\u001f';
	private static final Pattern SEPARATOR_PATTERN = Pattern.compile(Pattern.quote("\u001f"));
	
	
	private final String fQualifier;
	
	private final StringArrayPref fPref;
	
	private final List<BrowserBookmark> fBookmarks = new ArrayList<BrowserBookmark>();
	
	
	private BookmarkCollection(final String qualifier) {
		fQualifier = qualifier;
		fPref = new Preference.StringArrayPref(qualifier, "bookmarks", Preference.IS2_SEPARATOR_CHAR);
		
		load();
	}
	
	
	public String getQualifier() {
		return fQualifier;
	}
	
	public List<BrowserBookmark> getBookmarks() {
		return fBookmarks;
	}
	
	
	private void load() {
		synchronized (fBookmarks) {
			final String[] strings = PreferencesUtil.getInstancePrefs().getPreferenceValue(fPref);
			for (final String s : strings) {
				BrowserBookmark bookmark = null;
				final String[] split = SEPARATOR_PATTERN.split(s);
				if (split.length == 2) {
					bookmark = new BrowserBookmark(split[0], split[1]);
				}
				if (bookmark != null) {
					fBookmarks.add(bookmark);
				}
			}
		}
	}
	
	public void save() {
		synchronized (fBookmarks) {
			final String[] strings = new String[fBookmarks.size()];
			for (int i = 0; i < fBookmarks.size(); i++) {
				final BrowserBookmark bookmark = fBookmarks.get(i);
				strings[i] = bookmark.getLabel() + SEPARATOR + bookmark.getUrl();
			}
			PreferencesUtil.setPrefValue(PreferencesUtil.getInstancePrefs().getPreferenceContexts()[0],
					fPref, strings);
		}
	}
	
}
