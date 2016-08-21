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

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;


public abstract class DataValidator implements IDataValidator {
	
	
	@Override
	public boolean validate(final ILayerCell cell, final IConfigRegistry configRegistry, final Object newValue) {
		return validate(cell.getDim(HORIZONTAL).getId(), cell.getDim(VERTICAL).getId(),
				newValue );
	}
	
	@Override
	public abstract boolean validate(long columnIndex, long rowIndex, Object newValue);
	
}
