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


public class IntValue2Double2TextBinding extends AbstractObservableValue
		implements IIntValueListener, Listener {
	
	private static IConverter gDouble2TextConverter;
	private static IConverter gText2DoubleConverter;
	
	static {
		class Access extends UpdateValueStrategy {
			@Override
			public IConverter createConverter(final Object fromType, final Object toType) {
				return super.createConverter(fromType, toType);
			}
		};
		final Access access = new Access();
		gDouble2TextConverter = access.createConverter(Double.TYPE, String.class);
		gText2DoubleConverter = access.createConverter(String.class, Double.TYPE);
	}
	
	
	public static class LowerUpperGroup {
		
		
		private final int FACTOR_TARGET_UP = (int) (Integer.MAX_VALUE * 0.9);
		private final int FACTOR_TARGET_LOW = (int) (FACTOR_TARGET_UP * 0.9);
		
		private final WaScale fScale;
		
		private final IntValue2Double2TextBinding fLower;
		private final IntValue2Double2TextBinding fUpper;
		
		private double fMin;
		private double fMax;
		
		
		public LowerUpperGroup(final WaScale scale, final Text lowerText, final Text upperText,
				final Realm realm, final IConverter double2textConverter, final IConverter text2doubleConverter) {
			fScale = scale;
			fLower = new IntValue2Double2TextBinding(scale, 0, lowerText,
					realm, double2textConverter, text2doubleConverter) {
				@Override
				public double checkValue(double value) {
					if (value > fUpper.fLastValue) {
						value = fUpper.fLastValue;
					}
					return checkMinMax(value);
 				}
			};
			fUpper = new IntValue2Double2TextBinding(scale, 1, upperText,
					realm, double2textConverter, text2doubleConverter) {
				@Override
				public double checkValue(double value) {
					if (value < fLower.fLastValue) {
						value = fLower.fLastValue;
					}
					return checkMinMax(value);
				}
			};
		}
		
		
		public IntValue2Double2TextBinding getLower() {
			return fLower;
		}
		
		public IntValue2Double2TextBinding getUpper() {
			return fUpper;
		}
		
		private double checkMinMax(final double value){
			if (value < fMin) {
				return fMin;
			}
			if (value > fMax) {
				return fMax;
			}
			return value;
		}
		
		public void setMinMax(final double min, final double max) {
			fMin = min;
			fMax = max;
			final double bias = min;
			double factor = 1.0;
			final double range = (max - min);
			if (range > FACTOR_TARGET_UP || range < FACTOR_TARGET_LOW) {
				factor = FACTOR_TARGET_UP / range;
			}
			fLower.setFactor(bias, factor);
			fUpper.setFactor(bias, factor);
			fScale.setMinimum(fLower.double2int(min));
			fScale.setMaximum(fUpper.double2int(max));
			fScale.setIncrement(Math.max(1, (fScale.getMaximum() - fScale.getMinimum()) / 100));
			fScale.setPageIncrement(Math.max(10, (fScale.getMaximum() - fScale.getMinimum()) / 10));
			
			fLower.setValue(fLower.getValue());
			fUpper.setValue(fUpper.getValue());
		}
		
	}
	
	
	private final WaScale fIntWidget;
	private final int fIdx;
	private final Text fTextWidget;
	
	private boolean fIgnoreChanges;
	
	private double fLastValue;
	private String fLastText;
	private boolean fCorrect;
	
	private final IConverter fDouble2TextConverter;
	private final IConverter fText2IntConverter;
	
	private double fBias = 0.0;
	private double fFactor = 1.0;
	
	
	public IntValue2Double2TextBinding(final WaScale scale, final int knobIdx,final Text text,
			final Realm realm, IConverter double2textConverter, IConverter text2doubleConverter) {
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
		
		if (double2textConverter != null) {
			if (!double2textConverter.getFromType().equals(Double.TYPE)
					|| !double2textConverter.getToType().equals(String.class)) {
				throw new IllegalArgumentException("int2TextConverter");
			}
		}
		else {
			double2textConverter = gDouble2TextConverter;
		}
		fDouble2TextConverter = double2textConverter;
		if (text2doubleConverter != null) {
			if (!text2doubleConverter.getFromType().equals(String.class)
					|| !text2doubleConverter.getToType().equals(Double.TYPE)) {
				throw new IllegalArgumentException("text2IntConverter");
			}
		}
		else {
			text2doubleConverter = gText2DoubleConverter;
		}
		fText2IntConverter = text2doubleConverter;
	}
	
	
	@Override
	public void valueAboutToChange(final IntValueEvent event) {
		if (event.valueIdx != fIdx) {
			return;
		}
		event.newValue = double2int(checkValue(int2double(event.newValue)));
	}
	
	@Override
	public void valueChanged(final IntValueEvent event) {
		if (event.valueIdx != fIdx) {
			return;
		}
		setValue(checkValue(int2double(event.newValue)), false);
	}
	
	protected void setValue(final double value, final boolean updateKnob) {
		if (fIgnoreChanges) {
			return;
		}
		fIgnoreChanges = true;
		try {
			final String text = double2text(value);
			fTextWidget.setText(text);
			if (updateKnob) {
				fIntWidget.setValue(fIdx, double2int(value));
			}
			
			fCorrect = false;
			setLast(value, text);
		}
		finally {
			fIgnoreChanges = false;
		}
	}
	
	protected void setLast(final double value, final String text) {
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
				final double value = text2double(text);
				final double validValue = checkValue(value);
				fIntWidget.setValue(fIdx, double2int(validValue));
				if (value == validValue) {
					fCorrect = false;
					setLast(value, text);
				}
				else {
					fCorrect = true;
					setLast(validValue, double2text(validValue));
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
	
	
	protected double text2double(final String text) {
		return ((Double) fText2IntConverter.convert(text)).doubleValue();
	}
	
	protected int double2int(final double value) {
		return (int) Math.round((value - fBias) * fFactor);
	}
	
	protected double int2double(final int value) {
		return (value / fFactor) + fBias;
	}
	
	protected String double2text(final double value) {
		return (String) fDouble2TextConverter.convert(Double.valueOf(value));
	}
	
	public void setFactor(final double bias, final double factor) {
		fBias = bias;
		fFactor = factor;
	}
	
	
	public double checkValue(final double value) {
		return value;
	}
	
	
	@Override
	public Object getValueType() {
		return fDouble2TextConverter.getFromType();
	}
	
	@Override
	protected void doSetValue(final Object value) {
		setValue(((Double) value).doubleValue(), true);
	}
	
	@Override
	protected Object doGetValue() {
		return fLastValue;
	}
	
}
