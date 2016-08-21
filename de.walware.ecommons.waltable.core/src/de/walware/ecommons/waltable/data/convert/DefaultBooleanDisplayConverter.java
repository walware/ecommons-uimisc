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


/**
 * Data type converter for a Check Box. 
 * Assumes that the data value is stored as a boolean.
 */
public class DefaultBooleanDisplayConverter extends DisplayConverter {

	@Override
	public Object displayToCanonicalValue(final Object displayValue) {
		return Boolean.valueOf(displayValue.toString());
	}

	@Override
	public Object canonicalToDisplayValue(final Object canonicalValue) {
		if (canonicalValue == null) {
			return null;
		} else {
			return canonicalValue.toString();
		}
	}

}
