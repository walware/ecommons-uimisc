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
package org.eclipse.nebula.widgets.nattable.painter.layer;

import static org.eclipse.nebula.widgets.nattable.painter.cell.GraphicsUtils.safe;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;


public class GridLineCellLayerPainter extends CellLayerPainter {
	
	private final Color gridColor;
	
	public GridLineCellLayerPainter(final Color gridColor) {
		this.gridColor = gridColor;
	}
	
	public GridLineCellLayerPainter() {
		this.gridColor = GUIHelper.COLOR_GRAY;
	}
	
	public Color getGridColor() {
		return gridColor;
	}
	
	public void paintLayer(ILayer natLayer, GC gc, int xOffset, int yOffset, org.eclipse.swt.graphics.Rectangle rectangle, IConfigRegistry configRegistry) {
		//Draw GridLines
		drawGridLines(natLayer, gc, rectangle);
		
		super.paintLayer(natLayer, gc, xOffset, yOffset, rectangle, configRegistry);
	}
	
	@Override
	public Rectangle adjustCellBounds(long columnPosition, long rowPosition, Rectangle bounds) {
		return new Rectangle(bounds.x, bounds.y, Math.max(bounds.width - 1, 0), Math.max(bounds.height - 1, 0));
	}
	
	protected void drawGridLines(ILayer natLayer, GC gc, org.eclipse.swt.graphics.Rectangle rectangle) {
		gc.setForeground(gridColor);
		
		drawHorizontalLines(natLayer, gc, rectangle);
		drawVerticalLines(natLayer, gc, rectangle);
	}
	
	private void drawHorizontalLines(ILayer natLayer, GC gc, org.eclipse.swt.graphics.Rectangle rectangle) {
		int endX = safe(rectangle.x + Math.min(natLayer.getWidth() - 1, rectangle.width));
		
		long maxRowPosition = Math.min(natLayer.getRowCount(), natLayer.getRowPositionByY(rectangle.y + rectangle.height - 1) + 1);
		for (long rowPosition = natLayer.getRowPositionByY(rectangle.y); rowPosition < maxRowPosition; rowPosition++) {
			final int size = natLayer.getRowHeightByPosition(rowPosition);
			if (size > 0) {
				int y = safe(natLayer.getStartYOfRowPosition(rowPosition) + size - 1);
				gc.drawLine(safe(rectangle.x), y, endX, y);
			}
		}
	}
	
	private void drawVerticalLines(ILayer natLayer, GC gc, org.eclipse.swt.graphics.Rectangle rectangle) {
		int endY = safe(rectangle.y + Math.min(natLayer.getHeight() - 1, rectangle.height));
		
		long maxColumnPosition = Math.min(natLayer.getColumnCount(), natLayer.getColumnPositionByX(rectangle.x + rectangle.width - 1) + 1);
		for (long columnPosition = natLayer.getColumnPositionByX(rectangle.x); columnPosition < maxColumnPosition; columnPosition++) {
			final long size = natLayer.getColumnWidthByPosition(columnPosition);
			if (size > 0) {
				int x = safe(natLayer.getStartXOfColumnPosition(columnPosition) + size - 1);
				gc.drawLine(x, safe(rectangle.y), x, endY);
			}
		}
	}
	
}
