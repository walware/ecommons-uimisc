/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package de.walware.ecommons.waltable.data.convert;

public class PercentageDisplayConverter extends DisplayConverter {

	@Override
	public Object canonicalToDisplayValue(final Object canonicalValue) {
		if (canonicalValue != null) {
			final double percentageValue= ((Double) canonicalValue).doubleValue();
			final int displayInt= (int) (percentageValue * 100);
			return String.valueOf(displayInt) + "%"; //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}

	@Override
	public Object displayToCanonicalValue(final Object displayValue) {
		String displayString= (String) displayValue;
		displayString= displayString.trim();
		if (displayString.endsWith("%")) { //$NON-NLS-1$
			displayString= displayString.substring(0, displayString.length() - 1);
		}
		displayString= displayString.trim();
		final int displayInt= Integer.valueOf(displayString).intValue();
		final double percentageValue= (double) displayInt / 100;
		return Double.valueOf(percentageValue);
	}

}
