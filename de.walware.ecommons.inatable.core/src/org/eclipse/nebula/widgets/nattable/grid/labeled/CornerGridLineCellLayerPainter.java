/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.grid.labeled;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.painter.layer.CellLayerPainter;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;


public class CornerGridLineCellLayerPainter extends CellLayerPainter {


	private final Color gridColor;
	
	public CornerGridLineCellLayerPainter(final Color gridColor) {
		this.gridColor = gridColor;
	}

	public CornerGridLineCellLayerPainter() {
		this.gridColor = GUIHelper.COLOR_GRAY;
	}


	public Color getGridColor() {
		return gridColor;
	}

	public void paintLayer(ILayer natLayer, GC gc, int xOffset, int yOffset, Rectangle rectangle, IConfigRegistry configRegistry) {
		//Draw GridLines
		drawGridLines(natLayer, gc, rectangle);

		super.paintLayer(natLayer, gc, xOffset, yOffset, rectangle, configRegistry);
	}

	@Override
	public Rectangle adjustCellBounds(int columnPosition, int rowPosition, Rectangle bounds) {
		return new Rectangle(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
	}

	protected void drawGridLines(ILayer natLayer, GC gc, Rectangle rectangle) {
		gc.setForeground(gridColor);
		
		drawHorizontalLines(natLayer, gc, rectangle);
		drawVerticalLines(natLayer, gc, rectangle);
	}

	private ILayer getCornerLayer(ILayer layer) {
		return layer.getUnderlyingLayerByPosition(0, 0).getUnderlyingLayerByPosition(0, 0);
	}

	private void drawHorizontalLines(ILayer natLayer, GC gc, Rectangle rectangle) {
		int rowPositionByY = natLayer.getRowPositionByY(rectangle.y + rectangle.height);
		int lastRowPosition = ((rowPositionByY > 0) ? Math.min(natLayer.getRowCount(), rowPositionByY) : natLayer.getRowCount())
				- 1;
		
		ILayer cornerLayer = getCornerLayer(natLayer);
		int startX = rectangle.x + natLayer.getStartXOfColumnPosition(cornerLayer.getColumnCount() - 1) - 1;
		int endX = rectangle.x + Math.min(natLayer.getWidth() - 1, rectangle.width);
		if (startX < endX) {
			for (int rowPosition = natLayer.getRowPositionByY(rectangle.y); rowPosition < lastRowPosition; rowPosition++) {
				int y = natLayer.getStartYOfRowPosition(rowPosition) + natLayer.getRowHeightByPosition(rowPosition) - 1;
				gc.drawLine(startX, y, endX, y);
			}
		}
		{	int y = natLayer.getStartYOfRowPosition(lastRowPosition) + natLayer.getRowHeightByPosition(lastRowPosition) - 1;
			gc.drawLine(rectangle.x, y, endX, y);
		}
	}

	private void drawVerticalLines(ILayer natLayer, GC gc, Rectangle rectangle) {
		int columnPositionByX = natLayer.getColumnPositionByX(rectangle.x + rectangle.width);
		int lastColumnPosition = ((columnPositionByX > 0) ? Math.min(natLayer.getColumnCount(), columnPositionByX) : natLayer.getColumnCount())
				- 1;
		
		ILayer cornerLayer = getCornerLayer(natLayer);
		int startY = rectangle.y + natLayer.getStartYOfRowPosition(cornerLayer.getRowCount() - 1) - 1;
		int endY = rectangle.y + Math.min(natLayer.getHeight() - 1, rectangle.height);
		if (startY < endY) {
			for (int columnPosition = natLayer.getColumnPositionByX(rectangle.x); columnPosition < lastColumnPosition; columnPosition++) {
				int x = natLayer.getStartXOfColumnPosition(columnPosition) + natLayer.getColumnWidthByPosition(columnPosition) - 1;
				gc.drawLine(x, startY, x, endY);
			}
		}
		{	int x = natLayer.getStartXOfColumnPosition(lastColumnPosition) + natLayer.getColumnWidthByPosition(lastColumnPosition) - 1;
			gc.drawLine(x, rectangle.y, x, endY);
		}
	}

}
