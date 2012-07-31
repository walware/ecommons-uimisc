/*******************************************************************************
 * Copyright (c) 2012 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.databinding.jface;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;

import de.walware.ecommons.ui.components.IIntValueListener;
import de.walware.ecommons.ui.components.IntValueEvent;
import de.walware.ecommons.ui.components.WaScale;


public class IntValueObservable extends AbstractSWTObservableValue implements IIntValueListener {
	
	
	private final WaScale fIntWidget;
	private final int fIntIdx;
	
	
	public IntValueObservable(final WaScale widget, final int idx) {
		this(Realm.getDefault(), widget, idx);
	}
	
	public IntValueObservable(final Realm realm, final WaScale widget, final int idx) {
		super(realm, widget);
		
		fIntWidget = widget;
		fIntIdx = idx;
		
		fIntWidget.addValueListener(this);
	}
	
	
	@Override
	public Object getValueType() {
		return Integer.TYPE;
	}
	
	@Override
	protected void doSetValue(final Object value) {
		final int newValue = ((Integer) value).intValue();
		final int oldValue = fIntWidget.getValue(fIntIdx);
		if (newValue != oldValue) {
			fIntWidget.setValue(fIntIdx, newValue);
			fireValueChange(Diffs.createValueDiff(oldValue, newValue));
		}
	}
	
	@Override
	protected Object doGetValue() {
		return fIntWidget.getValue(fIntIdx);
	}
	
	@Override
	public void valueAboutToChange(final IntValueEvent event) {
	}
	
	@Override
	public void valueChanged(final IntValueEvent event) {
		setValue(event.newValue);
	}
	
}
