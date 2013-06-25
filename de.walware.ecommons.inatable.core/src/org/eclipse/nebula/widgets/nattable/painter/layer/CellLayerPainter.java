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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.swt.graphics.GC;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;


public class CellLayerPainter implements ILayerPainter {
	
	private ILayer natLayer;
	private Map<Long, Long> horizontalPositionToPixelMap;
	private Map<Long, Long> verticalPositionToPixelMap;
	
	
	public void paintLayer(ILayer natLayer, GC gc, int xOffset, int yOffset, org.eclipse.swt.graphics.Rectangle pixelRectangle, IConfigRegistry configRegistry) {
		if (pixelRectangle.width <= 0 || pixelRectangle.height <= 0) {
			return;
		}
		
		this.natLayer = natLayer;
		Rectangle positionRectangle = getPositionRectangleFromPixelRectangle(natLayer, pixelRectangle);
		
		calculateDimensionInfo(positionRectangle);
		
		Collection<ILayerCell> spannedCells = new HashSet<ILayerCell>();
		
		for (long columnPosition = positionRectangle.x; columnPosition < positionRectangle.x + positionRectangle.width; columnPosition++) {
			for (long rowPosition = positionRectangle.y; rowPosition < positionRectangle.y + positionRectangle.height; rowPosition++) {
				ILayerCell cell = natLayer.getCellByPosition(columnPosition, rowPosition);
				if (cell != null) {
					if (cell.isSpannedCell()) {
						spannedCells.add(cell);
					} else {
						paintCell(cell, gc, configRegistry);
					}
				}
			}
		}
		
		for (ILayerCell cell : spannedCells) {
			paintCell(cell, gc, configRegistry);
		}
	}
	
	private void calculateDimensionInfo(Rectangle positionRectangle) {
		{	horizontalPositionToPixelMap = new HashMap<Long, Long>();
			final long startPosition = positionRectangle.x;
			final long endPosition = startPosition + positionRectangle.width;
			long start2 = (startPosition > 0) ?
					natLayer.getStartXOfColumnPosition(startPosition - 1)
							+ natLayer.getColumnWidthByPosition(startPosition - 1) :
					Long.MIN_VALUE;
			for (long position = startPosition; position < endPosition; position++) {
				long start1 = natLayer.getStartXOfColumnPosition(position);
				horizontalPositionToPixelMap.put(position, Math.max(start1, start2));
				start2 = start1 + natLayer.getColumnWidthByPosition(position);
			}
			if (endPosition < natLayer.getColumnCount()) {
				long start1 = natLayer.getStartXOfColumnPosition(endPosition);
				horizontalPositionToPixelMap.put(endPosition, Math.max(start1, start2));
			}
		}
		{	verticalPositionToPixelMap = new HashMap<Long, Long>();
			final long startPosition = positionRectangle.y;
			final long endPosition = startPosition + positionRectangle.height;
			long start2 = (startPosition > 0) ?
					natLayer.getStartYOfRowPosition(startPosition - 1)
							+ natLayer.getRowHeightByPosition(startPosition - 1) :
					Long.MIN_VALUE;
			for (long position = startPosition; position < endPosition; position++) {
				long start1 = natLayer.getStartYOfRowPosition(position);
				verticalPositionToPixelMap.put(position, Math.max(start1, start2));
				start2 = start1 + natLayer.getRowHeightByPosition(position);
			}
			if (endPosition < natLayer.getRowCount()) {
				long start1 = natLayer.getStartYOfRowPosition(endPosition);
				verticalPositionToPixelMap.put(endPosition, Math.max(start1, start2));
			}
		}
	}

	public Rectangle adjustCellBounds(long columnPosition, long rowPosition, Rectangle cellBounds) {
		return cellBounds;
	}
	
	protected Rectangle getPositionRectangleFromPixelRectangle(ILayer natLayer, org.eclipse.swt.graphics.Rectangle pixelRectangle) {
		long columnPositionOffset = natLayer.getColumnPositionByX(pixelRectangle.x);
		long rowPositionOffset = natLayer.getRowPositionByY(pixelRectangle.y);
		long numColumns = natLayer.getColumnPositionByX(Math.min(natLayer.getWidth(), pixelRectangle.x + pixelRectangle.width) - 1) - columnPositionOffset + 1;
		long numRows = natLayer.getRowPositionByY(Math.min(natLayer.getHeight(), pixelRectangle.y + pixelRectangle.height) - 1) - rowPositionOffset + 1;
		
		if (columnPositionOffset < 0 || rowPositionOffset < 0 || numColumns < 0 || numRows < 0) {
//			getPositionRectangleFromPixelRectangle(natLayer, pixelRectangle);
			throw new RuntimeException();
		}
		
		return new Rectangle(columnPositionOffset, rowPositionOffset, numColumns, numRows);
	}

	protected void paintCell(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
		ILayer layer = cell.getLayer();
		long columnPosition = cell.getColumnPosition();
		long rowPosition = cell.getRowPosition();
		ICellPainter cellPainter = layer.getCellPainter(columnPosition, rowPosition, cell, configRegistry);
		Rectangle adjustedCellBounds = layer.getLayerPainter().adjustCellBounds(columnPosition, rowPosition, cell.getBounds());
		if (cellPainter != null) {
			org.eclipse.swt.graphics.Rectangle originalClipping = gc.getClipping();
			
			long startX = getStartXOfColumnPosition(columnPosition);
			long startY = getStartYOfRowPosition(rowPosition);
			
			long endX = getStartXOfColumnPosition(cell.getOriginColumnPosition() + cell.getColumnSpan());
			long endY = getStartYOfRowPosition(cell.getOriginRowPosition() + cell.getRowSpan());
			
			Rectangle clipBounds = new Rectangle(startX, startY, endX - startX, endY - startY);
			Rectangle adjustedClipBounds = clipBounds.intersection(adjustedCellBounds);
//			Rectangle adjustedClipBounds = layer.getLayerPainter().adjustCellBounds(columnPosition, rowPosition, clipBounds);
			gc.setClipping(safe(adjustedClipBounds));
			
			cellPainter.paintCell(cell, gc, adjustedCellBounds, configRegistry);
			
			gc.setClipping(originalClipping);
		}
	}
	
	private long getStartXOfColumnPosition(final long columnPosition) {
		if (columnPosition < natLayer.getColumnCount()) {
			Long start = horizontalPositionToPixelMap.get(columnPosition);
			if (start == null) {
				start = Long.valueOf(natLayer.getStartXOfColumnPosition(columnPosition));
				if (columnPosition > 0) {
					long start2 = natLayer.getStartXOfColumnPosition(columnPosition - 1)
							+ natLayer.getColumnWidthByPosition(columnPosition - 1);
					if (start2 > start.longValue()) {
						start = Long.valueOf(start2);
					}
				}
				horizontalPositionToPixelMap.put(columnPosition, start);
			}
			return start.longValue();
		} else {
			return natLayer.getWidth();
		}
	}
	
	private long getStartYOfRowPosition(final long rowPosition) {
		if (rowPosition < natLayer.getRowCount()) {
			Long start = verticalPositionToPixelMap.get(rowPosition);
			if (start == null) {
				start = Long.valueOf(natLayer.getStartYOfRowPosition(rowPosition));
				if (rowPosition > 0) {
					long start2 = natLayer.getStartYOfRowPosition(rowPosition - 1)
							+ natLayer.getRowHeightByPosition(rowPosition - 1);
					if (start2 > start.longValue()) {
						start = Long.valueOf(start2);
					}
				}
				verticalPositionToPixelMap.put(rowPosition, start);
			}
			return start.longValue();
		} else {
			return natLayer.getHeight();
		}
	}
	
}
