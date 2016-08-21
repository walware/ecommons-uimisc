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

import java.math.BigInteger;

/**
 * Converts the display value to a {@link BigInteger} and vice versa.
 */
public class DefaultBigIntegerDisplayConverter extends DefaultLongDisplayConverter {

	@Override
	protected Object convertToNumericValue(final String value) {
		return new BigInteger(value);
	}
}
