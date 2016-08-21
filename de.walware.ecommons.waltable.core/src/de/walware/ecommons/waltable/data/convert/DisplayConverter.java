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

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;

public abstract class DisplayConverter implements IDisplayConverter {

	public abstract Object canonicalToDisplayValue(Object canonicalValue);

	public abstract Object displayToCanonicalValue(Object displayValue);
	
	@Override
	public Object canonicalToDisplayValue(final ILayerCell cell, final IConfigRegistry configRegistry, final Object canonicalValue) {
		return canonicalToDisplayValue(canonicalValue);
	}

	@Override
	public Object displayToCanonicalValue(final ILayerCell cell, final IConfigRegistry configRegistry, final Object displayValue) {
		return displayToCanonicalValue(displayValue);
	}

}
