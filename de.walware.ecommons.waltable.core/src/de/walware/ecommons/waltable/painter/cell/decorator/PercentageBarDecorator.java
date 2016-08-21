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
package de.walware.ecommons.waltable.painter.cell.decorator;


import static de.walware.ecommons.waltable.painter.cell.GraphicsUtils.safe;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.painter.cell.CellPainterWrapper;
import de.walware.ecommons.waltable.painter.cell.ICellPainter;
import de.walware.ecommons.waltable.style.CellStyleUtil;
import de.walware.ecommons.waltable.style.ConfigAttribute;
import de.walware.ecommons.waltable.util.GUIHelper;

/**
 * Draws a rectangular bar in cell proportional to the value of the cell.
 */
public class PercentageBarDecorator extends CellPainterWrapper {

	public static final ConfigAttribute<Color> PERCENTAGE_BAR_COMPLETE_REGION_START_COLOR= new ConfigAttribute<>();
	public static final ConfigAttribute<Color> PERCENTAGE_BAR_COMPLETE_REGION_END_COLOR= new ConfigAttribute<>();
	public static final ConfigAttribute<Color> PERCENTAGE_BAR_INCOMPLETE_REGION_COLOR= new ConfigAttribute<>();
	
	private static final Color DEFAULT_COMPLETE_REGION_START_COLOR= GUIHelper.getColor(new RGB(187, 216, 254));
	private static final Color DEFAULT_COMPLETE_REGION_END_COLOR= GUIHelper.getColor(new RGB(255, 255, 255));
	
	public PercentageBarDecorator(final ICellPainter interiorPainter) {
		super(interiorPainter);
	}

	@Override
	public void paintCell(final ILayerCell cell, final GC gc, final LRectangle lRectangle, final IConfigRegistry configRegistry) {
		final Pattern originalBackgroundPattern= gc.getBackgroundPattern();

		double factor= Math.min(1.0, ((Double) cell.getDataValue(0)).doubleValue());
		factor= Math.max(0.0, factor);

		final LRectangle bar= new LRectangle(lRectangle.x, lRectangle.y, (long) (lRectangle.width * factor), lRectangle.height);
		final org.eclipse.swt.graphics.Rectangle rect= safe(bar);
		final LRectangle bounds= cell.getBounds();
		
		Color color1= CellStyleUtil.getCellStyle(cell, configRegistry).getAttributeValue(PERCENTAGE_BAR_COMPLETE_REGION_START_COLOR);
		Color color2= CellStyleUtil.getCellStyle(cell, configRegistry).getAttributeValue(PERCENTAGE_BAR_COMPLETE_REGION_END_COLOR);
		if (color1 == null) {
			color1= DEFAULT_COMPLETE_REGION_START_COLOR;
		}
		if (color2 == null) {
			color2= DEFAULT_COMPLETE_REGION_END_COLOR;
		}
		
		final Pattern pattern= new Pattern(Display.getCurrent(),
				bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height,
				color1,
				color2);
		gc.setBackgroundPattern(pattern);
		gc.fillRectangle(rect);

		gc.setBackgroundPattern(originalBackgroundPattern);
		pattern.dispose();
		
		final Color incompleteRegionColor= CellStyleUtil.getCellStyle(cell, configRegistry).getAttributeValue(PERCENTAGE_BAR_INCOMPLETE_REGION_COLOR);
		if (incompleteRegionColor != null) {
			final Region incompleteRegion= new Region();
			
			incompleteRegion.add(safe(lRectangle));
			incompleteRegion.subtract(rect);
			final Color originalBackgroundColor= gc.getBackground();
			gc.setBackground(incompleteRegionColor);
			gc.fillRectangle(incompleteRegion.getBounds());
			gc.setBackground(originalBackgroundColor);
			
			incompleteRegion.dispose();
		}
		
		super.paintCell(cell, gc, lRectangle, configRegistry);
	}

}
