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

package de.walware.ecommons.databinding.jface;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;

import de.walware.ecommons.ui.components.IIntValueListener;
import de.walware.ecommons.ui.components.IIntValueWidget;
import de.walware.ecommons.ui.components.IntValueEvent;


public class IntValueObservable extends AbstractSWTObservableValue implements IIntValueListener {
	
	
	private final IIntValueWidget fWidget;
	
	private final int fValueIdx;
	
	
	public IntValueObservable(final IIntValueWidget widget, final int idx) {
		this(Realm.getDefault(), widget, idx);
	}
	
	public IntValueObservable(final Realm realm, final IIntValueWidget widget, final int idx) {
		super(realm, widget.getControl());
		
		fWidget = widget;
		fValueIdx = idx;
		
		fWidget.addValueListener(this);
	}
	
	
	@Override
	public Object getValueType() {
		return Integer.TYPE;
	}
	
	@Override
	protected void doSetValue(final Object value) {
		final int newValue = ((Integer) value).intValue();
		final int oldValue = fWidget.getValue(fValueIdx);
		if (newValue != oldValue) {
			fWidget.setValue(fValueIdx, newValue);
			fireValueChange(Diffs.createValueDiff(oldValue, newValue));
		}
	}
	
	@Override
	protected Object doGetValue() {
		return fWidget.getValue(fValueIdx);
	}
	
	@Override
	public void valueAboutToChange(final IntValueEvent event) {
	}
	
	@Override
	public void valueChanged(final IntValueEvent event) {
		setValue(event.newValue);
	}
	
}
