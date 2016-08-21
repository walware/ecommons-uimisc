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
package de.walware.ecommons.waltable.export.excel;

import de.walware.ecommons.waltable.config.CellConfigAttributes;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.data.convert.IDisplayConverter;
import de.walware.ecommons.waltable.export.IExportFormatter;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;

public class DefaultExportFormatter implements IExportFormatter {

	@Override
	public Object formatForExport(final ILayerCell cell, final IConfigRegistry configRegistry) {
		final Object dataValue= cell.getDataValue(0, null);
		final IDisplayConverter displayConverter= configRegistry.getConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, cell.getDisplayMode(), cell.getConfigLabels().getLabels());
		return displayConverter.canonicalToDisplayValue(cell, configRegistry, dataValue);
	}

}
