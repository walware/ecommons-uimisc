/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
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


public class SetElementFilter implements IElementFilter {
	
	
	private class Filter implements IFinalFilter {
		
		
		private final Collection<?> set;
		
		
		Filter(final Collection<?> set) {
			this.set= set;
		}
		
		
		@Override
		public boolean select(final Object element) {
			return SetElementFilter.this.select(this.set, element);
		}
		
		@Override
		public boolean isSubOf(final IFinalFilter other) {
			return (other == this || other == null || ((Filter) other).set.containsAll(this.set));
		}
		
		@Override
		public boolean isEqualTo(final IFinalFilter other) {
			return (other == this || ((other instanceof Filter)
					&& this.set.equals((((Filter) other).set)) ));
		}
		
	}
	
	
	private Collection<?> set;
	private boolean changed;
	
	private Filter currentFilter;
	
	
	public SetElementFilter() {
		this.set= Collections.EMPTY_LIST;
	}
	
	
	public synchronized boolean setSet(Collection<?> set) {
		if (set == null || set.isEmpty()) {
			set= Collections.EMPTY_LIST;
		}
		this.changed |= !this.set.equals(set);
		this.set= set;
		return this.changed;
	}
	
	@Override
	public synchronized IFinalFilter getFinal(final boolean newData) {
		if (this.set.isEmpty()) {
			this.currentFilter= null;
		}
		else if (this.currentFilter == null
				|| (newData && this.currentFilter.set != this.set)
				|| (this.changed && this.currentFilter.set != this.set) ) {
			this.currentFilter= new Filter(this.set);
		}
		this.changed= false;
		return this.currentFilter;
	}
	
	
	protected boolean select(final Collection<?> set, final Object element) {
		return set.contains(element.toString());
	}
	
}
