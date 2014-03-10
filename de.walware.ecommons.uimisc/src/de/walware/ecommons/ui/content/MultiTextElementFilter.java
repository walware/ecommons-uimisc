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


public abstract class MultiTextElementFilter implements ITextElementFilter {
	
	
	private class Filter implements IFinalFilter {
		
		private final IFinalFilter[] finalFilters;
		
		Filter(final IFinalFilter[] filters) {
			this.finalFilters= filters;
		}
		
		private MultiTextElementFilter getSource() {
			return MultiTextElementFilter.this;
		}
		
		@Override
		public boolean select(final Object element) {
			final IFinalFilter filter= this.finalFilters[getIdx(element)];
			return filter.select(element);
		}
		
		@Override
		public boolean isSubOf(final IFinalFilter other) {
			return (other == null || (other instanceof MultiTextElementFilter.Filter
					&& isSubOf(((MultiTextElementFilter.Filter) other)) ));
		}
		
		private boolean isSubOf(final MultiTextElementFilter.Filter other) {
			if (getSource() != other.getSource()) {
				return false;
			}
			for (int i= 0; i < this.finalFilters.length; i++) {
				if (!this.finalFilters[i].isSubOf(other.finalFilters[i])) {
					return false;
				}
			}
			return true;
		}
		
		@Override
		public boolean isEqualTo(final IFinalFilter other) {
			return (this == other || (other instanceof MultiTextElementFilter.Filter
					&& isEqualTo(((MultiTextElementFilter.Filter) other)) ));
		}
		
		private boolean isEqualTo(final MultiTextElementFilter.Filter other) {
			if (getSource() != other.getSource()) {
				return false;
			}
			for (int i= 0; i < this.finalFilters.length; i++) {
				if (!this.finalFilters[i].isEqualTo(other.finalFilters[i])) {
					return false;
				}
			}
			return true;
		}
		
	}
	
	
	private final ITextElementFilter[] filters;
	
	private volatile String text;
	
	private String currentText;
	private Filter currentFilter;
	
	
	public MultiTextElementFilter(final ITextElementFilter[] filters) {
		this.filters= filters;
		this.text= ""; //$NON-NLS-1$
	}
	
	
	protected abstract int getIdx(Object element);
	
	
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
			final IFinalFilter[] finalFilters= new IFinalFilter[this.filters.length];
			for (int i= 0; i < this.filters.length; i++) {
				this.filters[i].setText(text);
				finalFilters[i]= this.filters[i].getFinal(newData);
			}
			this.currentFilter= new Filter(finalFilters);
		}
		return this.currentFilter;
	}
	
}
