/*=============================================================================#
 # Copyright (c) 2013-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.components;

import java.math.BigDecimal;
import java.util.Locale;

import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.DecimalFormatSymbols;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import de.walware.ecommons.FastList;


public class DoubleText implements IObjValueWidget<Double> {
	
	
	public static DecimalFormat createFormat(final int maxDigits) {
		final DecimalFormat format = new DecimalFormat("0.0"); //$NON-NLS-1$
		format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		format.setMaximumFractionDigits(maxDigits);
		format.setRoundingMode(BigDecimal.ROUND_HALF_DOWN);
		return format;
	}
	
	private class SWTListener implements Listener {
		
		@Override
		public void handleEvent(final Event event) {
			switch (event.type) {
			case SWT.Modify:
				updateValue(event.time);
				return;
			case SWT.KeyDown:
				switch (event.keyCode) {
				case SWT.ARROW_UP:
					incrementValue(fIncrement);
					event.doit = false;
					return;
				case SWT.ARROW_DOWN:
					incrementValue(-fIncrement);
					event.doit = false;
					return;
				case SWT.PAGE_UP:
					incrementValue(fIncrement * 10);
					event.doit = false;
					return;
				case SWT.PAGE_DOWN:
					incrementValue(-fIncrement * 10);
					event.doit = false;
					return;
				}
			}
		}
		
	}
	
	
	private final Text fText;
	
	private Double fValue;
	
	private final FastList<IObjValueListener<Double>> fValueListeners = (FastList) new FastList<IObjValueListener>(IObjValueListener.class);
	
	private double fIncrement;
	private double fMin;
	private double fMax = Double.NaN;
	
	private DecimalFormat fFormat;
	
	
	public DoubleText(final Composite parent, final int flags) {
		fText = new Text(parent, SWT.LEFT | SWT.SINGLE | flags);
		final SWTListener swtListener = new SWTListener();
		fText.addListener(SWT.Modify, swtListener);
		fText.addListener(SWT.KeyDown, swtListener);
	}
	
	
	@Override
	public Text getControl() {
		return fText;
	}
	
	@Override
	public Class<Double> getValueType() {
		return Double.class;
	}
	
	public void setIncrement(final double v) {
		fIncrement = v;
	}
	
	public void setMinMax(final double min, final double max) {
		if (Double.isNaN(min) || Double.isNaN(max)) {
			fMin = 0;
			fMax = Double.NaN;
		}
		else {
			fMin = min;
			fMax = max;
		}
	}
	
	public void setFormat(final DecimalFormat format) {
		fFormat = format;
	}
	
	@Override
	public void addValueListener(final IObjValueListener<Double> listener) {
		fValueListeners.add(listener);
	}
	
	@Override
	public void removeValueListener(final IObjValueListener<Double> listener) {
		fValueListeners.remove(listener);
	}
	
	@Override
	public Double getValue(final int idx) {
		return fValue;
	}
	
	@Override
	public void setValue(final int idx, final Double value) {
		fText.setText(formatValue(value));
	}
	
	
	private void incrementValue(final double increment) {
		double newValue = (fValue != null) ?
				(fValue.doubleValue() + increment) : fMin;
		if (!Double.isNaN(fMax)) {
			if (newValue < fMin) {
				newValue = fMin;
			}
			else if (newValue > fMax) {
				newValue = fMax;
			}
		}
		fText.setText(formatValue(newValue));
	}
	
	protected Double parseValue(final String text) {
		try {
			return Double.valueOf(text);
		}
		catch (final NumberFormatException e) {
			return null;
		}
	}
	
	protected String formatValue(final Double value) {
		if (value == null) {
			return ""; //$NON-NLS-1$
		}
		if (fFormat != null) {
			return fFormat.format(value.doubleValue());
		}
		return value.toString();
	}
	
	private void updateValue(final int time) {
		final Double newValue = parseValue(fText.getText());
		if ((newValue != null) ? newValue.equals(fValue) : null == fValue) {
			return;
		}
		final IObjValueListener<Double>[] listeners = fValueListeners.toArray();
		final ObjValueEvent<Double> event = new ObjValueEvent<Double>(this, time, 0,
				fValue, newValue, 0 );
		fValue = newValue;
		
		for (int i = 0; i < listeners.length; i++) {
			event.newValue = newValue;
			listeners[i].valueChanged(event);
		}
	}
	
}
