/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.style;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.coordinate.SWTUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

public class CellStyleUtil {

	public static IStyle getCellStyle(ILayerCell cell, IConfigRegistry configRegistry) {
		return new CellStyleProxy(configRegistry, cell.getDisplayMode(), cell.getConfigLabels().getLabels());
	}

	public static int getHorizontalAlignmentSWT(IStyle cellStyle, int swtDefault) {
		HorizontalAlignment horizontalAlignment = cellStyle.getAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT);
		return (horizontalAlignment != null) ? SWTUtil.toSWT(horizontalAlignment) : swtDefault;
	}

	public static int getHorizontalAlignmentPadding(IStyle cellStyle, Rectangle rectangle, int contentWidth) {
		HorizontalAlignment horizontalAlignment = cellStyle.getAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT);
		return getHorizontalAlignmentPadding(horizontalAlignment, rectangle, contentWidth);
	}
	
	/**
	 * Calculate padding needed at the left to align horizontally. Defaults to CENTER horizontal alignment.
	 */
	public static int getHorizontalAlignmentPadding(HorizontalAlignment horizontalAlignment, Rectangle rectangle, int contentWidth) {
		return getHorizontalAlignmentPadding(horizontalAlignment, rectangle.width, contentWidth);
	}
	
	/**
	 * Calculate padding needed at the left to align horizontally. Defaults to CENTER horizontal alignment.
	 */
	public static int getHorizontalAlignmentPadding(HorizontalAlignment horizontalAlignment, int width, int contentWidth) {
		if (horizontalAlignment == null) {
			horizontalAlignment = HorizontalAlignment.CENTER;
		}
		
		int padding;
		
		switch (horizontalAlignment) {
		case CENTER:
			padding = (width - contentWidth) / 2;
			break;
		case RIGHT:
			padding = width - contentWidth;
			break;
		default:
			padding = 0;
			break;
		}
		
		if (padding < 0) {
			padding = 0;
		}
		
		return padding;
	}

	public static int getVerticalAlignmentPadding(IStyle cellStyle, Rectangle rectangle, int contentHeight) {
		VerticalAlignmentEnum verticalAlignment = cellStyle.getAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT);
		return getVerticalAlignmentPadding(verticalAlignment, rectangle, contentHeight);
	}
	
	/**
	 * Calculate padding needed at the top to align vertically. Defaults to MIDDLE vertical alignment.
	 */
	public static int getVerticalAlignmentPadding(VerticalAlignmentEnum verticalAlignment, Rectangle rectangle, int contentHeight) {
		if (verticalAlignment == null) {
			verticalAlignment = VerticalAlignmentEnum.MIDDLE;
		}
		
		int padding = 0;

		switch (verticalAlignment) {
		case MIDDLE:
			padding = (rectangle.height - contentHeight) / 2;
			break;
		case BOTTOM:
			padding = rectangle.height - contentHeight;
			break;
		}
		
		if (padding < 0) {
			padding = 0;
		}

		return padding;
	}
	
	public static List<Color> getAllBackgroundColors(ILayerCell cell, IConfigRegistry configRegistry, String displayMode) {
		
		final List<Color> colors = new ArrayList<Color>();
		
		for (String configLabel : cell.getConfigLabels().getLabels()) {
			IStyle cellStyle = configRegistry.getSpecificConfigAttribute(CellConfigAttributes.CELL_STYLE, displayMode, configLabel);
			if (cellStyle != null) {
				Color color = cellStyle.getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR);
				
				if (color != null) {
					colors.add(color);
				}
			}			
		}
		
		return colors;
	}
	
}
