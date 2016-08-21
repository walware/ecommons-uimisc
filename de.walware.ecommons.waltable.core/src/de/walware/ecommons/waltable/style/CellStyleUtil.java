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
package de.walware.ecommons.waltable.style;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;

import de.walware.ecommons.waltable.config.CellConfigAttributes;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.swt.SWTUtil;

public class CellStyleUtil {

	public static IStyle getCellStyle(final ILayerCell cell, final IConfigRegistry configRegistry) {
		return new CellStyleProxy(configRegistry, cell.getDisplayMode(), cell.getConfigLabels().getLabels());
	}

	public static int getHorizontalAlignmentSWT(final IStyle cellStyle, final int swtDefault) {
		final HorizontalAlignment horizontalAlignment= cellStyle.getAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT);
		return (horizontalAlignment != null) ? SWTUtil.toSWT(horizontalAlignment) : swtDefault;
	}

	public static long getHorizontalAlignmentPadding(final IStyle cellStyle, final LRectangle lRectangle, final long contentWidth) {
		final HorizontalAlignment horizontalAlignment= cellStyle.getAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT);
		return getHorizontalAlignmentPadding(horizontalAlignment, lRectangle.width, contentWidth);
	}
	
	/**
	 * Calculate padding needed at the left to align horizontally. Defaults to CENTER horizontal alignment.
	 */
	public static long getHorizontalAlignmentPadding(HorizontalAlignment horizontalAlignment, final long width, final long contentWidth) {
		if (horizontalAlignment == null) {
			horizontalAlignment= HorizontalAlignment.CENTER;
		}
		
		long padding;
		
		switch (horizontalAlignment) {
		case CENTER:
			padding= (width - contentWidth) / 2;
			break;
		case RIGHT:
			padding= width - contentWidth;
			break;
		default:
			padding= 0;
			break;
		}
		
		if (padding < 0) {
			padding= 0;
		}
		
		return padding;
	}

	public static long getVerticalAlignmentPadding(final IStyle cellStyle, final LRectangle lRectangle, final long contentHeight) {
		final VerticalAlignmentEnum verticalAlignment= cellStyle.getAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT);
		return getVerticalAlignmentPadding(verticalAlignment, lRectangle.height, contentHeight);
	}
	
	/**
	 * Calculate padding needed at the top to align vertically. Defaults to MIDDLE vertical alignment.
	 */
	public static long getVerticalAlignmentPadding(VerticalAlignmentEnum verticalAlignment, final long height, final long contentHeight) {
		if (verticalAlignment == null) {
			verticalAlignment= VerticalAlignmentEnum.MIDDLE;
		}
		
		long padding= 0;

		switch (verticalAlignment) {
		case MIDDLE:
			padding= (height - contentHeight) / 2;
			break;
		case BOTTOM:
			padding= height - contentHeight;
			break;
		}
		
		if (padding < 0) {
			padding= 0;
		}

		return padding;
	}
	
	public static List<Color> getAllBackgroundColors(final ILayerCell cell, final IConfigRegistry configRegistry,
			final DisplayMode displayMode) {
		
		final List<Color> colors= new ArrayList<>();
		
		for (final String configLabel : cell.getConfigLabels().getLabels()) {
			final IStyle cellStyle= configRegistry.getSpecificConfigAttribute(CellConfigAttributes.CELL_STYLE, displayMode, configLabel);
			if (cellStyle != null) {
				final Color color= cellStyle.getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR);
				
				if (color != null) {
					colors.add(color);
				}
			}			
		}
		
		return colors;
	}
	
}
