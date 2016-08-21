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
 * Converts the display value to a {@link Character} and vice versa.
 */
public class DefaultCharacterDisplayConverter extends DisplayConverter {

	@Override
	public Object canonicalToDisplayValue(final Object sourceValue) {
		return sourceValue != null ? sourceValue.toString() : ""; //$NON-NLS-1$
	}

	@Override
	public Object displayToCanonicalValue(final Object displayValue) {
		String s;
		if (displayValue != null
				&& (s= displayValue.toString()) != null && !s.isEmpty() ) {
			if (s.length() > 1) {
				throw new ConversionFailedException(Messages.getString("DefaultCharacterDisplayConverter.failure", //$NON-NLS-1$
						new Object[] {s}));
			} else {
				return s.charAt(0);
			}
		} 
		return null;
	}
}
