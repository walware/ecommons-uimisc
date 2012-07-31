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

package de.walware.ecommons.ui.content;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import de.walware.ecommons.ui.components.IIntValueListener;
import de.walware.ecommons.ui.components.IntValueEvent;
import de.walware.ecommons.ui.components.WaScale;


public class IntValue2TextBinding extends AbstractObservableValue
		implements IIntValueListener, Listener {
	
	
	private static IConverter gInt2TextConverter;
	private static IConverter gText2IntConverter;
	
	static {
		class Access extends UpdateValueStrategy {
			@Override
			public IConverter createConverter(final Object fromType, final Object toType) {
				return super.createConverter(fromType, toType);
			}
		};
		final Access access = new Access();
		gInt2TextConverter = access.createConverter(Integer.TYPE, String.class);
		gText2IntConverter = access.createConverter(String.class, Integer.TYPE);
	}
	
	
	public static class LowerUpperGroup {
		
		
		private final WaScale fScale;
		
		private final IntValue2TextBinding fLower;
		private final IntValue2TextBinding fUpper;
		
		private int fMin;
		private int fMax;
		
		
		public LowerUpperGroup(final WaScale scale, final Text lowerText, final Text upperText,
				final Realm realm, final IConverter int2textConverter, final IConverter text2intConverter) {
			fScale = scale;
			fLower = new IntValue2TextBinding(scale, 0, lowerText,
					realm, int2textConverter, text2intConverter) {
				@Override
				public int checkValue(int value) {
					if (value > fUpper.fLastValue) {
						value = fUpper.fLastValue;
					}
					return checkMinMax(value);
 				}
			};
			fUpper = new IntValue2TextBinding(scale, 1, upperText,
					realm, int2textConverter, text2intConverter) {
				@Override
				public int checkValue(int value) {
					if (value < fLower.fLastValue) {
						value = fLower.fLastValue;
					}
					return checkMinMax(value);
				}
			};
		}
		
		
		public IntValue2TextBinding getLower() {
			return fLower;
		}
		
		public IntValue2TextBinding getUpper() {
			return fUpper;
		}
		
		private int checkMinMax(final int value){
			if (value < fMin) {
				return fMin;
			}
			if (value > fMax) {
				return fMax;
			}
			return value;
		}
		
		public void setMinMax(final int min, final int max) {
			fMin = min;
			fMax = max;
			fScale.setMinimum(min);
			fScale.setMaximum(max);
			fScale.setPageIncrement(Math.max(10, (fScale.getMaximum() - fScale.getMinimum()) / 10));
			
			fLower.setValue(fLower.getValue());
			fUpper.setValue(fUpper.getValue());
		}
		
	}
	
	
	private final WaScale fIntWidget;
	private final int fIdx;
	private final Text fTextWidget;
	
	private boolean fIgnoreChanges;
	
	private int fLastValue;
	private String fLastText;
	private boolean fCorrect;
	
	private final IConverter fInt2TextConverter;
	private final IConverter fText2IntConverter;
	
	
	public IntValue2TextBinding(final WaScale scale, final int knobIdx, final Text text) {
		this(scale, knobIdx, text, Realm.getDefault(), null, null);
	}
	
	public IntValue2TextBinding(final WaScale scale, final int knobIdx, final Text text,
			final Realm realm, IConverter int2textConverter, IConverter text2intConverter) {
		super(realm);
		if (scale == null) {
			throw new NullPointerException("scale");
		}
		if (text == null) {
			throw new NullPointerException("text");
		}
		fIntWidget = scale;
		fIdx = knobIdx;
		fTextWidget = text;
		
		scale.addValueListener(this);
		text.addListener(SWT.Modify, this);
		text.addListener(SWT.FocusOut, this);
		
		fLastText = fTextWidget.getText();
		
		if (int2textConverter != null) {
			if (!int2textConverter.getFromType().equals(Integer.TYPE)
					|| !int2textConverter.getToType().equals(String.class)) {
				throw new IllegalArgumentException("int2TextConverter");
			}
		}
		else {
			int2textConverter = gInt2TextConverter;
		}
		fInt2TextConverter = int2textConverter;
		if (text2intConverter != null) {
			if (!text2intConverter.getFromType().equals(String.class)
					|| !text2intConverter.getToType().equals(Integer.TYPE)) {
				throw new IllegalArgumentException("text2IntConverter");
			}
		}
		else {
			text2intConverter = gText2IntConverter;
		}
		fText2IntConverter = text2intConverter;
	}
	
	
	@Override
	public void valueAboutToChange(final IntValueEvent event) {
		if (event.valueIdx != fIdx) {
			return;
		}
		event.newValue = checkValue(event.newValue);
	}
	
	@Override
	public void valueChanged(final IntValueEvent event) {
		if (event.valueIdx != fIdx) {
			return;
		}
		setValue(checkValue(event.newValue), false);
	}
	
	protected void setValue(final int value, final boolean updateKnob) {
		if (fIgnoreChanges) {
			return;
		}
		fIgnoreChanges = true;
		try {
			final String text = int2Text(value);
			fTextWidget.setText(text);
			if (updateKnob) {
				fIntWidget.setValue(fIdx, value);
			}
			
			fCorrect = false;
			setLast(value, text);
		}
		finally {
			fIgnoreChanges = false;
		}
	}
	
	protected void setLast(final int value, final String text) {
		final ValueDiff diff = (fLastValue != value) ? Diffs.createValueDiff(fLastValue, value) : null;
		
		fLastValue = value;
		fLastText = text;
		
		if (diff != null) {
			fireValueChange(diff);
		}
	}
	
	@Override
	public void handleEvent(final Event event) {
		switch (event.type) {
		
		case SWT.Modify:
			if (fIgnoreChanges) {
				return;
			}
			fIgnoreChanges = true;
			try {
				final String text = fTextWidget.getText();
				final int value = text2Int(text);
				final int validValue = checkValue(value);
				fIntWidget.setValue(fIdx, validValue);
				if (value == validValue) {
					fCorrect = false;
					setLast(value, text);
				}
				else {
					fCorrect = true;
					setLast(validValue, int2Text(validValue));
				}
			}
			catch (final IllegalArgumentException e) {
				fCorrect = true;
			}
			finally {
				fIgnoreChanges = false;
			}
			return;
		
		case SWT.FocusOut:
			try {
				if (fCorrect) {
					fTextWidget.setText(fLastText);
				}
			}
			catch (final IllegalArgumentException e) {}
			return;
		
		default:
			return;
		}
	}
	
	
	protected int text2Int(final String text) {
		return ((Integer) fText2IntConverter.convert(text)).intValue();
	}
	
	protected String int2Text(final int value) {
		return (String) fInt2TextConverter.convert(Integer.valueOf(value));
	}
	
	public void setValue(final int value) {
		setValue(value, true);
	}
	
	
	protected int checkValue(final int value) {
		return value;
	}
	
	
	@Override
	public Object getValueType() {
		return fInt2TextConverter.getFromType();
	}
	
	@Override
	protected void doSetValue(final Object value) {
		setValue((value != null) ? ((Integer) value).intValue() : 0, true);
	}
	
	@Override
	protected Object doGetValue() {
		return fLastValue;
	}
	
}
