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

package de.walware.ecommons.ui.content;

import org.eclipse.ui.dialogs.SearchPattern;


public class TextElementFilter implements ITextElementFilter {
	
	
	private class Filter implements IFinalFilter {
		
		private final SearchPattern pattern;
		
		Filter(final SearchPattern pattern) {
			this.pattern= pattern;
		}
		
		@Override
		public boolean select(final Object element) {
			return TextElementFilter.this.select(this.pattern, element);
		}
		
		@Override
		public boolean isSubOf(final IFinalFilter other) {
			return (other == null || (other instanceof TextElementFilter.Filter
					&& ((Filter) other).pattern.isSubPattern(this.pattern) ));
		}
		
		@Override
		public boolean isEqualTo(final IFinalFilter other) {
			return (this == other || (other instanceof TextElementFilter.Filter
					&& this.pattern.equalsPattern(((Filter) other).pattern) ));
		}
		
	}
	
	
	private volatile String text;
	
	private String currentText;
	private Filter currentFilter;
	
	
	public TextElementFilter() {
		this.text= ""; //$NON-NLS-1$
	}
	
	
	@Override
	public boolean setText(String text) {
		if (text == null) {
			text= ""; //$NON-NLS-1$
		}
		final boolean changed= !this.text.equals(text);
		this.text= text;
		return changed;
	}
	
	@Override
	public IFinalFilter getFinal(final boolean newData) {
		final String text= this.text;
		if (text.length() == 0) {
			this.currentFilter= null;
		}
		else if (this.currentFilter == null
				|| (newData && this.text != this.currentText)
				|| !text.equals(this.currentText)) {
			this.currentText= text;
			final SearchPattern pattern= createSearchPattern();
			pattern.setPattern(text);
			this.currentFilter= new Filter(pattern);
		}
		return this.currentFilter;
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
