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
// ~
package de.walware.ecommons.waltable.layer.config;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import de.walware.ecommons.waltable.config.AbstractRegistryConfiguration;
import de.walware.ecommons.waltable.config.CellConfigAttributes;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.grid.GridRegion;
import de.walware.ecommons.waltable.painter.cell.ICellPainter;
import de.walware.ecommons.waltable.painter.cell.TextPainter;
import de.walware.ecommons.waltable.style.BorderStyle;
import de.walware.ecommons.waltable.style.CellStyleAttributes;
import de.walware.ecommons.waltable.style.DisplayMode;
import de.walware.ecommons.waltable.style.HorizontalAlignment;
import de.walware.ecommons.waltable.style.Style;
import de.walware.ecommons.waltable.style.VerticalAlignmentEnum;
import de.walware.ecommons.waltable.util.GUIHelper;


public class DefaultRowHeaderStyleConfiguration extends AbstractRegistryConfiguration {

	public Font font= GUIHelper.getFont(new FontData("Verdana", 10, SWT.NORMAL)); //$NON-NLS-1$
	public Color bgColor= GUIHelper.COLOR_WIDGET_BACKGROUND;
	public Color fgColor= GUIHelper.COLOR_WIDGET_FOREGROUND;
	public Color gradientBgColor= GUIHelper.COLOR_WHITE;
	public Color gradientFgColor= GUIHelper.getColor(136, 212, 215);
	public HorizontalAlignment hAlign= HorizontalAlignment.CENTER;
	public VerticalAlignmentEnum vAlign= VerticalAlignmentEnum.MIDDLE;
	public BorderStyle borderStyle= null;

	public ICellPainter cellPainter= new TextPainter();

	@Override
	public void configureRegistry(final IConfigRegistry configRegistry) {
		configureRowHeaderCellPainter(configRegistry);
		configureRowHeaderStyle(configRegistry);
	}

	protected void configureRowHeaderStyle(final IConfigRegistry configRegistry) {
		final Style cellStyle= new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, this.bgColor);
		cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, this.fgColor);
		cellStyle.setAttributeValue(CellStyleAttributes.GRADIENT_BACKGROUND_COLOR, this.gradientBgColor);
		cellStyle.setAttributeValue(CellStyleAttributes.GRADIENT_FOREGROUND_COLOR, this.gradientFgColor);
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, this.hAlign);
		cellStyle.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT, this.vAlign);
		cellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, this.borderStyle);
		cellStyle.setAttributeValue(CellStyleAttributes.FONT, this.font);

		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, GridRegion.ROW_HEADER);
	}

	protected void configureRowHeaderCellPainter(final IConfigRegistry configRegistry) {
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, this.cellPainter, DisplayMode.NORMAL, GridRegion.ROW_HEADER);
	}

}
