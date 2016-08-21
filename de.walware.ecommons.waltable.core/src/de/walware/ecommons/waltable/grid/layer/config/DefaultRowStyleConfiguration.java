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
package de.walware.ecommons.waltable.grid.layer.config;


import org.eclipse.swt.graphics.Color;

import de.walware.ecommons.waltable.config.AbstractRegistryConfiguration;
import de.walware.ecommons.waltable.config.CellConfigAttributes;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.grid.cell.AlternatingRowConfigLabelAccumulator;
import de.walware.ecommons.waltable.style.CellStyleAttributes;
import de.walware.ecommons.waltable.style.DisplayMode;
import de.walware.ecommons.waltable.style.Style;
import de.walware.ecommons.waltable.util.GUIHelper;

/**
 * Sets up alternate row coloring. Applied by {@link DefaultGridLayerConfiguration}
 */
public class DefaultRowStyleConfiguration extends AbstractRegistryConfiguration {

	public Color evenRowBgColor= GUIHelper.COLOR_WIDGET_BACKGROUND;
	public Color oddRowBgColor= GUIHelper.COLOR_WHITE;

	@Override
	public void configureRegistry(final IConfigRegistry configRegistry) {
		configureOddRowStyle(configRegistry);
		configureEvenRowStyle(configRegistry);
	}

	protected void configureOddRowStyle(final IConfigRegistry configRegistry) {
		final Style cellStyle= new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,  this.oddRowBgColor);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, AlternatingRowConfigLabelAccumulator.EVEN_ROW_CONFIG_TYPE);
	}

	protected void configureEvenRowStyle(final IConfigRegistry configRegistry) {
		final Style cellStyle= new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,  this.evenRowBgColor);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, AlternatingRowConfigLabelAccumulator.ODD_ROW_CONFIG_TYPE);
	}
}
