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
package de.walware.ecommons.waltable.style.editor;


import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

import de.walware.ecommons.waltable.config.CellConfigAttributes;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.grid.cell.AlternatingRowConfigLabelAccumulator;
import de.walware.ecommons.waltable.style.CellStyleAttributes;
import de.walware.ecommons.waltable.style.DisplayMode;
import de.walware.ecommons.waltable.style.IStyle;

public class GridStyleParameterObject {

	public Font tableFont;
	public Color evenRowColor;
	public Color oddRowColor;
	public Color selectionColor;

	public IStyle evenRowStyle;
	public IStyle oddRowStyle;
	public IStyle selectionStyle;
	public IStyle tableStyle;

	private final IConfigRegistry configRegistry;

	public GridStyleParameterObject(final IConfigRegistry configRegistry) {
		this.configRegistry= configRegistry;
		init(configRegistry);
	}

	private void init(final IConfigRegistry configRegistry) {
		this.evenRowStyle= configRegistry.getConfigAttribute(
				CellConfigAttributes.CELL_STYLE, 
				DisplayMode.NORMAL, 
				AlternatingRowConfigLabelAccumulator.EVEN_ROW_CONFIG_TYPE);
		this.evenRowColor= this.evenRowStyle.getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR);

		this.oddRowStyle= configRegistry.getConfigAttribute(
				CellConfigAttributes.CELL_STYLE, 
				DisplayMode.NORMAL, 
				AlternatingRowConfigLabelAccumulator.ODD_ROW_CONFIG_TYPE);
		this.oddRowColor= this.oddRowStyle.getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR);

		this.selectionStyle= configRegistry.getConfigAttribute(CellConfigAttributes.CELL_STYLE, DisplayMode.SELECT);
		this.selectionColor= this.selectionStyle.getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR);
		
		this.tableStyle= configRegistry.getConfigAttribute(CellConfigAttributes.CELL_STYLE, DisplayMode.NORMAL);
		this.tableFont= this.tableStyle.getAttributeValue(CellStyleAttributes.FONT);
	}
	
	public IConfigRegistry getConfigRegistry() {
		return this.configRegistry;
	}

}
