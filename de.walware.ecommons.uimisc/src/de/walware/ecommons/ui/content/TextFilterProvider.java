/*******************************************************************************
 * Copyright (c) 2012-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.content;

import org.eclipse.ui.dialogs.SearchPattern;


public class TextFilterProvider implements IElementFilter {
	
	
	private class Filter implements IFinalFilter {
		
		private final SearchPattern fPattern;
		
		Filter(final SearchPattern pattern) {
			fPattern = pattern;
		}
		
		@Override
		public boolean select(final Object element) {
			return TextFilterProvider.this.select(fPattern, element);
		}
		
		@Override
		public boolean isSubOf(final IFinalFilter other) {
			return (other == null || ((Filter) other).fPattern.isSubPattern(fPattern));
		}
		
		@Override
		public boolean isEqualTo(final IFinalFilter other) {
			return (this == other || ((other instanceof TextFilterProvider.Filter)
					&& fPattern.equalsPattern(((Filter) other).fPattern) ));
		}
		
	}
	
	
	private volatile String fText;
	
	private String fCurrentText;
	private Filter fCurrentFilter;
	
	
	public TextFilterProvider() {
		fText = ""; //$NON-NLS-1$
	}
	
	
	public boolean setText(String text) {
		if (text == null) {
			text = ""; //$NON-NLS-1$
		}
		final boolean changed = !fText.equals(text);
		fText = text;
		return changed;
	}
	
	@Override
	public IFinalFilter getFinal(final boolean newData) {
		final String text = fText;
		if (text.length() == 0) {
			fCurrentFilter = null;
		}
		else if (fCurrentFilter == null
				|| (newData && fText != fCurrentText)
				|| !text.equals(fCurrentText)) {
			fCurrentText = text;
			final SearchPattern pattern = createSearchPattern();
			pattern.setPattern(text);
			fCurrentFilter = new Filter(pattern);
		}
		return fCurrentFilter;
	}
	
	
	protected SearchPattern createSearchPattern() {
		return new SearchPattern(SearchPattern.RULE_EXACT_MATCH
				| SearchPattern.RULE_PREFIX_MATCH | SearchPattern.RULE_CAMELCASE_MATCH
				| SearchPattern.RULE_PATTERN_MATCH | SearchPattern.RULE_BLANK_MATCH);
	}
	
	protected boolean select(final SearchPattern pattern, final Object element) {
		return pattern.matches(element.toString());
	}
	
}
