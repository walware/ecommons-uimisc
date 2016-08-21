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
package de.walware.ecommons.waltable.layer.cell;

import de.walware.ecommons.waltable.config.CellConfigAttributes;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.data.convert.IDisplayConverter;


public final class CellDisplayConversionUtils {
	
	
	public static String convertDataType(final ILayerCell cell, final IConfigRegistry configRegistry) {
		return convertDataType(cell, cell.getDataValue(0, null), configRegistry);
	}
	
	public static String convertDataType(final ILayerCell cell, final Object canonicalValue, final IConfigRegistry configRegistry) {
		Object displayValue;
		
		final IDisplayConverter displayConverter= configRegistry.getConfigAttribute(
				CellConfigAttributes.DISPLAY_CONVERTER,
				cell.getDisplayMode(),
				cell.getConfigLabels().getLabels());
		
		if (displayConverter != null) {
			displayValue= displayConverter.canonicalToDisplayValue(cell, configRegistry, canonicalValue);
		}
		else {
			displayValue= canonicalValue;
		}
		
		return (displayValue == null) ? "" : String.valueOf(displayValue); //$NON-NLS-1$
	}
	
}
