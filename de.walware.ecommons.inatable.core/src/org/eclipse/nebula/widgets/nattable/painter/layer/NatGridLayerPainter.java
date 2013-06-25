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
package org.eclipse.nebula.widgets.nattable.painter.layer;

import static org.eclipse.nebula.widgets.nattable.painter.cell.GraphicsUtils.safe;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;


public class NatGridLayerPainter extends NatLayerPainter {

	private final Color gridColor;

	public NatGridLayerPainter(NatTable natTable) {
		this(natTable, GUIHelper.COLOR_GRAY);
	}
	
	public NatGridLayerPainter(NatTable natTable, Color gridColor) {
		super(natTable);
		this.gridColor = gridColor;
	}
	
	@Override
	protected void paintBackground(ILayer natLayer, GC gc, long xOffset, long yOffset, org.eclipse.swt.graphics.Rectangle rectangle, IConfigRegistry configRegistry) {
		super.paintBackground(natLayer, gc, xOffset, yOffset, rectangle, configRegistry);
		
		gc.setForeground(gridColor);
		drawHorizontalLines(natLayer, gc, rectangle);
		drawVerticalLines(natLayer, gc, rectangle);
	}
	
	private void drawHorizontalLines(ILayer natLayer, GC gc, org.eclipse.swt.graphics.Rectangle rectangle) {
		int startX = rectangle.x;
		int endX = rectangle.x + rectangle.width;
		
		long rowPositionByY = natLayer.getRowPositionByY(rectangle.y + rectangle.height);
		long maxRowPosition = rowPositionByY > 0 ? Math.min(natLayer.getRowCount(), rowPositionByY) : natLayer.getRowCount();
		for (long rowPosition = natLayer.getRowPositionByY(rectangle.y); rowPosition < maxRowPosition; rowPosition++) {
			int y = safe(natLayer.getStartYOfRowPosition(rowPosition) + natLayer.getRowHeightByPosition(rowPosition) - 1);
			gc.drawLine(startX, y, endX, y);
		}
	}

	private void drawVerticalLines(ILayer natLayer, GC gc, org.eclipse.swt.graphics.Rectangle rectangle) {
		int startY = rectangle.y;
		int endY = rectangle.y + rectangle.height;
		
		long columnPositionByX = natLayer.getColumnPositionByX(rectangle.x + rectangle.width);
		long maxColumnPosition = columnPositionByX > 0 ? Math.min(natLayer.getColumnCount(), columnPositionByX) : natLayer.getColumnCount();
		for (long columnPosition = natLayer.getColumnPositionByX(rectangle.x); columnPosition < maxColumnPosition; columnPosition++) {
			int x = safe(natLayer.getStartXOfColumnPosition(columnPosition) + natLayer.getColumnWidthByPosition(columnPosition) - 1);
			gc.drawLine(x, startY, x, endY);
		}
	}

}
