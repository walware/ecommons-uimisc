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

import java.util.Collection;
import java.util.Collections;


public class SetFilter implements IElementFilter {
	
	
	private class Filter implements IFinalFilter {
		
		
		private final Collection<?> fSet;
		
		
		Filter(final Collection<?> set) {
			fSet = set;
		}
		
		
		@Override
		public boolean select(final Object element) {
			return SetFilter.this.select(fSet, element);
		}
		
		@Override
		public boolean isSubOf(final IFinalFilter other) {
			return (other == this || other == null || ((Filter) other).fSet.containsAll(fSet));
		}
		
		@Override
		public boolean isEqualTo(final IFinalFilter other) {
			return (other == this || ((other instanceof Filter)
					&& fSet.equals((((Filter) other).fSet)) ));
		}
		
	}
	
	
	private Collection<?> fSet;
	private boolean fChanged;
	
	private Filter fCurrentFilter;
	
	
	public SetFilter() {
		fSet = Collections.EMPTY_LIST;
	}
	
	
	public synchronized boolean setSet(Collection<?> set) {
		if (set == null || set.isEmpty()) {
			set = Collections.EMPTY_LIST;
		}
		fChanged |= !fSet.equals(set);
		fSet = set;
		return fChanged;
	}
	
	@Override
	public synchronized IFinalFilter getFinal(final boolean newData) {
		if (fSet.isEmpty()) {
			fCurrentFilter = null;
		}
		else if (fCurrentFilter == null
				|| (newData && fCurrentFilter.fSet != fSet)
				|| (fChanged && fCurrentFilter.fSet != fSet) ) {
			fCurrentFilter = new Filter(fSet);
		}
		fChanged = false;
		return fCurrentFilter;
	}
	
	
	protected boolean select(final Collection<?> set, final Object element) {
		return set.contains(element.toString());
	}
	
	
}
