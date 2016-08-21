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

package de.walware.ecommons.waltable.painter.layer;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;
import static de.walware.ecommons.waltable.painter.cell.GraphicsUtils.safe;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.util.GUIHelper;


public class GridLineCellLayerPainter extends CellLayerPainter {
	
	
	private final Color gridColor;
	
	
	public GridLineCellLayerPainter(final Color gridColor) {
		this.gridColor= gridColor;
	}
	
	public GridLineCellLayerPainter() {
		this.gridColor= GUIHelper.COLOR_GRAY;
	}
	
	
	public Color getGridColor() {
		return this.gridColor;
	}
	
	
	@Override
	public void paintLayer(final ILayer natLayer, final GC gc,
			final int xOffset, final int yOffset, final Rectangle pixelRectangle,
			final IConfigRegistry configRegistry) {
		//Draw GridLines
		drawGridLines(natLayer, gc, pixelRectangle);
		
		super.paintLayer(natLayer, gc, xOffset, yOffset, pixelRectangle, configRegistry);
	}
	
	@Override
	public LRectangle adjustCellBounds(final long columnPosition, final long rowPosition, final LRectangle bounds) {
		return new LRectangle(bounds.x, bounds.y, Math.max(bounds.width - 1, 0), Math.max(bounds.height - 1, 0));
	}
	
	protected void drawGridLines(final ILayer natLayer, final GC gc, final Rectangle rectangle) {
		gc.setForeground(this.gridColor);
		
		drawHorizontalLines(natLayer, gc, rectangle);
		drawVerticalLines(natLayer, gc, rectangle);
	}
	
	private void drawHorizontalLines(final ILayer natLayer, final GC gc, final Rectangle rectangle) {
		final int startX= safe(rectangle.x);
		final int endX= safe(rectangle.x + Math.min(natLayer.getWidth() - 1, rectangle.width));
		
		final ILayerDim dim= natLayer.getDim(VERTICAL);
		final long endPosition= Math.min(dim.getPositionCount(), dim.getPositionByPixel(rectangle.y + rectangle.height - 1) + 1);
		for (long position= dim.getPositionByPixel(rectangle.y); position < endPosition; position++) {
			final int size= dim.getPositionSize(position);
			if (size > 0) {
				final int y= safe(dim.getPositionStart(position) + size - 1);
				gc.drawLine(startX, y, endX, y);
			}
		}
	}
	
	private void drawVerticalLines(final ILayer natLayer, final GC gc, final Rectangle rectangle) {
		final int startY= safe(rectangle.y);
		final int endY= safe(rectangle.y + Math.min(natLayer.getHeight() - 1, rectangle.height));
		
		final ILayerDim dim= natLayer.getDim(HORIZONTAL);
		final long endPosition= Math.min(dim.getPositionCount(), dim.getPositionByPixel(rectangle.x + rectangle.width - 1) + 1);
		for (long position= dim.getPositionByPixel(rectangle.x); position < endPosition; position++) {
			final long size= dim.getPositionSize(position);
			if (size > 0) {
				final int x= safe(dim.getPositionStart(position) + size - 1);
				gc.drawLine(x, startY, x, endY);
			}
		}
	}
	
}
