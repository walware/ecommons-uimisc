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
package de.walware.ecommons.waltable.data.validate;

public class DefaultNumericDataValidator extends DataValidator {

	@Override
	public boolean validate(final long columnIndex, final long rowIndex, final Object newValue) {
		try {
			if (newValue != null) {
				new Double(newValue.toString());
			}
		} catch (final Exception e) {
			return false;
		}
		return true;
	}

}
