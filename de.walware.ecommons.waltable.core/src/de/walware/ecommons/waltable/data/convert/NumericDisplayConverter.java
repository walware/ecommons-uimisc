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
// ~
package de.walware.ecommons.waltable.data.convert;

import de.walware.ecommons.waltable.Messages;


/**
 * Converts the display value to a double and vice versa.
 */
public abstract class NumericDisplayConverter extends DisplayConverter {

	@Override
	public Object canonicalToDisplayValue(final Object canonicalValue) {
		try {
			if (canonicalValue != null) {
				return canonicalValue.toString();
			}
			return null;
		} catch (final Exception e) {
			return canonicalValue;
		}
	}

	@Override
	public Object displayToCanonicalValue(final Object displayValue) {
		try {
			String s;
			if (displayValue != null
					&& (s= displayValue.toString()) != null && !s.isEmpty() ) {
				return convertToNumericValue(s);
			}
			return null;
		} catch (final Exception e) {
			throw new ConversionFailedException(
					Messages.getString("NumericDisplayConverter.failure", //$NON-NLS-1$
							new Object[] {displayValue}), e);
		}
	}
	
	protected abstract Object convertToNumericValue(String value);
}
