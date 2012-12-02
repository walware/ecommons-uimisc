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
import org.eclipse.core.databinding.observable.value.ValueDiff;

import de.walware.ecommons.ui.components.IObjValueListener;
import de.walware.ecommons.ui.components.IObjValueWidget;
import de.walware.ecommons.ui.components.ObjValueEvent;


public class ObjValueObservable<T> extends AbstractSWTObservableValue implements IObjValueListener<T> {
	
	
	private final IObjValueWidget<T> fWidget;
	
	private final int fValueIdx;
	
	
	public ObjValueObservable(final Realm realm, final IObjValueWidget<T> widget) {
		this(realm, widget, 0);
	}
	
	public ObjValueObservable(final Realm realm, final IObjValueWidget<T> widget, final int idx) {
		super(realm, widget.getControl());
		
		fWidget = widget;
		
		fValueIdx = idx;
		
		fWidget.addValueListener(this);
	}
	
	
	public Object getValueType() {
		return fWidget.getValueType();
	}
	
	@Override
	protected void doSetValue(final Object value) {
		fWidget.setValue(fValueIdx,(T) value);
	}
	
	@Override
	protected Object doGetValue() {
		return fWidget.getValue(fValueIdx);
	}
	
	@Override
	public void valueAboutToChange(final ObjValueEvent<T> event) {
	}
	
	@Override
	public void valueChanged(final ObjValueEvent<T> event) {
		if (event.valueIdx != fValueIdx) {
			return;
		}
		final ValueDiff diff = Diffs.createValueDiff(event.oldValue, event.newValue);
		fireValueChange(diff);
	}
	
}
