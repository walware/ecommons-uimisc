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
// -depend
package de.walware.ecommons.waltable.data.validate;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;


public abstract class ContextualDataValidator implements IDataValidator {

	@Override
	public boolean validate(final long columnIndex, final long rowIndex, final Object newValue) {
		throw new UnsupportedOperationException(this.getClass().getName() 
				+ " is a ContextualDataValidator and has therefore to be called with context informations."); //$NON-NLS-1$
	}

	@Override
	public abstract boolean validate(ILayerCell cell, IConfigRegistry configRegistry, Object newValue);

}
