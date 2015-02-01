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

import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;

import de.walware.ecommons.collections.ConstArrayList;


public class ObservableSetBinding implements ISetChangeListener {
	
	
	private final TableFilterController fController;
	
	private final SetElementFilter fFilter;
	
	
	public ObservableSetBinding(final TableFilterController controller, final IObservableSet set, final SetElementFilter filter) {
		fController = controller;
		fFilter = filter;
		set.addSetChangeListener(this);
	}
	
	
	protected Collection<?> getAll() {
		return null;
	}
	
	protected Collection<?> createFilterSet(final Collection<?> set) {
		return new ConstArrayList<Object>(set);
	}
	
	@Override
	public void handleSetChange(final SetChangeEvent event) {
		final IObservableSet set = event.getObservableSet();
		Collection<?> copy;
		if (set.isEmpty() || ((copy = getAll()) != null && set.containsAll(copy))) {
			copy = null;
		}
		else {
			copy = createFilterSet(set);
		}
		if (fFilter.setSet(copy)) {
			fController.refresh(true);
		}
	}
	
}
