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

import java.util.Collection;

import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;

import de.walware.ecommons.collections.ConstList;


public class ObservableSetBinding implements ISetChangeListener {
	
	
	private final TableFilterController fController;
	
	private final SetFilter fFilter;
	
	
	public ObservableSetBinding(final TableFilterController controller, final IObservableSet set, final SetFilter filter) {
		fController = controller;
		fFilter = filter;
		set.addSetChangeListener(this);
	}
	
	
	protected Collection<?> getAll() {
		return null;
	}
	
	protected Collection<?> createFilterSet(final Collection<?> set) {
		return new ConstList<Object>(set);
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
