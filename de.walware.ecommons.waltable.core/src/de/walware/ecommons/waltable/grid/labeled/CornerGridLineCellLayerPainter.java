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

package de.walware.ecommons.waltable.grid.labeled;

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
import de.walware.ecommons.waltable.painter.layer.CellLayerPainter;
import de.walware.ecommons.waltable.util.GUIHelper;


public class CornerGridLineCellLayerPainter extends CellLayerPainter {
	
	
	private final Color gridColor;
	
	public CornerGridLineCellLayerPainter(final Color gridColor) {
		this.gridColor= gridColor;
	}
	
	public CornerGridLineCellLayerPainter() {
		this.gridColor= GUIHelper.COLOR_GRAY;
	}
	
	
	public Color getGridColor() {
		return this.gridColor;
	}
	
	private ILayer getCornerLayer(final ILayer layer) {
		return layer.getUnderlyingLayerByPosition(0, 0).getUnderlyingLayerByPosition(0, 0);
	}
	
	
	@Override
	public void paintLayer(final ILayer natLayer, final GC gc, final int xOffset, final int yOffset, final Rectangle rectangle, final IConfigRegistry configRegistry) {
		//Draw GridLines
		drawGridLines(natLayer, gc, rectangle);
		
		super.paintLayer(natLayer, gc, xOffset, yOffset, rectangle, configRegistry);
	}
	
	@Override
	public LRectangle adjustCellBounds(final long columnPosition, final long rowPosition, final LRectangle bounds) {
		return new LRectangle(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
	}
	
	protected void drawGridLines(final ILayer natLayer, final GC gc, final Rectangle rectangle) {
		gc.setForeground(this.gridColor);
		
		drawHorizontalLines(natLayer, gc, rectangle);
		drawVerticalLines(natLayer, gc, rectangle);
	}
	
	private void drawHorizontalLines(final ILayer natLayer, final GC gc, final Rectangle rectangle) {
		final int startX, endX;
		{	final ILayerDim colDim= natLayer.getDim(HORIZONTAL);
			startX= safe(rectangle.x + colDim.getPositionStart(
					getCornerLayer(natLayer).getColumnCount() - 1 ) - 1 );
			endX= safe(rectangle.x + Math.min(colDim.getSize() - 1, rectangle.width));
		}
		final ILayerDim dim= natLayer.getDim(VERTICAL);
		final long lastPosition= getEndPosition(dim, rectangle.y + rectangle.height) - 1;
		if (startX < endX) {
			for (long position= dim.getPositionByPixel(rectangle.y); position < lastPosition; position++) {
				final int size= dim.getPositionSize(position);
				if (size > 0) {
					final int y= safe(dim.getPositionStart(position) + dim.getPositionSize(position) - 1);
					gc.drawLine(startX, y, endX, y);
				}
			}
		}
		{	final int y= safe(dim.getPositionStart(lastPosition) + dim.getPositionSize(lastPosition) - 1);
			gc.drawLine(safe(rectangle.x), y, endX, y);
		}
	}
	
	private void drawVerticalLines(final ILayer natLayer, final GC gc, final Rectangle rectangle) {
		final int startY, endY;
		{	final ILayerDim rowDim= natLayer.getDim(VERTICAL);
			startY= safe(rectangle.y + rowDim.getPositionStart(
					getCornerLayer(natLayer).getRowCount() - 1 ) - 1 );
			endY= safe(rectangle.y + Math.min(rowDim.getSize() - 1, rectangle.height));
		}
		
		final ILayerDim dim= natLayer.getDim(HORIZONTAL);
		final long lastPosition= getEndPosition(dim, rectangle.x + rectangle.width) - 1;
		if (startY < endY) {
			for (long position= dim.getPositionByPixel(rectangle.x); position < lastPosition; position++) {
				final int size= dim.getPositionSize(position);
				if (size > 0) {
					final int x= safe(dim.getPositionStart(position) + size - 1);
					gc.drawLine(x, startY, x, endY);
				}
			}
		}
		{	final int x= safe(dim.getPositionStart(lastPosition) + dim.getPositionSize(lastPosition) - 1);
			gc.drawLine(x, safe(rectangle.y), x, endY);
		}
	}
	
}
