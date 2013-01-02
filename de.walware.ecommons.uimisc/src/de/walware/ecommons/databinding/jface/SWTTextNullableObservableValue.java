/*******************************************************************************
 * Copyright (c) 2009-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.databinding.jface;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;


public class SWTTextNullableObservableValue extends AbstractSWTObservableValue {
	
	
	private final Text fTextWidget;
	
	private Object fValue;
	
	private boolean fUpdating = false;
	
	private final ModifyListener fListener = new ModifyListener() {
		@Override
		public void modifyText(final ModifyEvent e) {
			if (!fUpdating) {
				final Object oldValue = fValue;
				final Object newValue = doGetValue();
				if ((newValue != null) ? (!newValue.equals(fValue)) : (fValue != null)) {
					fireValueChange(Diffs.createValueDiff(oldValue, newValue));
				}
			}
		}
	};
	
	
	public SWTTextNullableObservableValue(final Text text) {
		super(text);
		fTextWidget = text;
		
		fTextWidget.addModifyListener(fListener);
	}
	
	@Override
	public synchronized void dispose() {
		fTextWidget.removeModifyListener(fListener);
		
		super.dispose();
	}
	
	@Override
	public Object getValueType() {
		return String.class;
	}
	
	@Override
	protected Object doGetValue() {
		final String text = fTextWidget.getText();
		if (text.trim().isEmpty()) {
			return (fValue = null);
		}
		else {
			return (fValue = text);
		}
	}
	
	@Override
	protected void doSetValue(final Object value) {
		try {
			fUpdating = true;
			fValue = value;
			if (value == null) {
				fTextWidget.setText("");
			}
			else {
				fTextWidget.setText((String) value);
			}
		}
		finally {
			fUpdating = false;
		}
	}
	
}
