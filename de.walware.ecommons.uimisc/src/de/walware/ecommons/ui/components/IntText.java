/*=============================================================================#
 # Copyright (c) 2013-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.components;

import com.ibm.icu.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import de.walware.ecommons.FastList;


public class IntText implements IObjValueWidget<Integer> {
	
	
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
	
	private Integer fValue;
	
	private final FastList<IObjValueListener<Integer>> fValueListeners = (FastList) new FastList<IObjValueListener>(IObjValueListener.class);
	
	private int fIncrement;
	private int fMin = Integer.MIN_VALUE;
	private int fMax = Integer.MAX_VALUE;
	
	private DecimalFormat fFormat;
	
	
	public IntText(final Composite parent, final int flags) {
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
	public Class<Integer> getValueType() {
		return Integer.class;
	}
	
	public void setIncrement(final int v) {
		fIncrement = v;
	}
	
	public void setMinMax(final int min, final int max) {
		fMin = min;
		fMax = max;
	}
	
	public void setFormat(final DecimalFormat format) {
		fFormat = format;
	}
	
	@Override
	public void addValueListener(final IObjValueListener<Integer> listener) {
		fValueListeners.add(listener);
	}
	
	@Override
	public void removeValueListener(final IObjValueListener<Integer> listener) {
		fValueListeners.remove(listener);
	}
	
	@Override
	public Integer getValue(final int idx) {
		return fValue;
	}
	
	@Override
	public void setValue(final int idx, final Integer value) {
		fText.setText(formatValue(value));
	}
	
	
	private void incrementValue(final int increment) {
		int newValue = (fValue != null) ?
				(fValue.intValue() + increment) : fMin;
		if (newValue < fMin) {
			newValue = fMin;
		}
		else if (newValue > fMax) {
			newValue = fMax;
		}
		fText.setText(formatValue(newValue));
	}
	
	protected Integer parseValue(final String text) {
		try {
			return Integer.valueOf(text);
		}
		catch (final NumberFormatException e) {
			return null;
		}
	}
	
	protected String formatValue(final Integer value) {
		if (value == null) {
			return ""; //$NON-NLS-1$
		}
		if (fFormat != null) {
			return fFormat.format(value.intValue());
		}
		return value.toString();
	}
	
	private void updateValue(final int time) {
		final Integer newValue = parseValue(fText.getText());
		if ((newValue != null) ? newValue.equals(fValue) : null == fValue) {
			return;
		}
		final IObjValueListener<Integer>[] listeners = fValueListeners.toArray();
		final ObjValueEvent<Integer> event = new ObjValueEvent<Integer>(this, time, 0,
				fValue, newValue, 0 );
		fValue = newValue;
		
		for (int i = 0; i < listeners.length; i++) {
			event.newValue = newValue;
			listeners[i].valueChanged(event);
		}
	}
	
}
