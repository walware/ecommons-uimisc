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
package org.eclipse.nebula.widgets.nattable.layer;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;


public class LayerUtil {
	
	public static final long getColumnPositionByX(ILayer layer, long x) {
		long width = layer.getWidth();
		
		if (x < 0 || x >= width) {
			return Long.MIN_VALUE;
		}

		return findColumnPosition(0, 0, layer, x, width, layer.getColumnCount());
	}
	
	protected static final long findColumnPosition(long xOffset, long columnOffset, ILayer layer, long x, long totalWidth, long columnCount) {
		double size = (double) (totalWidth - xOffset) / (columnCount - columnOffset);
		long columnPosition = columnOffset + (int) ((x - xOffset) / size);
		
		long startX = layer.getStartXOfColumnPosition(columnPosition);
		long endX = startX + layer.getColumnWidthByPosition(columnPosition);
		if (x < startX) {
			return findColumnPosition(xOffset, columnOffset, layer, x, startX, columnPosition);
		} else if (x >= endX) {
			return findColumnPosition(endX, columnPosition + 1, layer, x, totalWidth, columnCount);
		} else {
			return columnPosition;
		}
	}
	
	public static final long getRowPositionByY(ILayer layer, long y) {
		long height = layer.getHeight();
		
		if (y < 0 || y >= height) {
			return Long.MIN_VALUE;
		}
		
		return findRowPosition(0, 0, layer, y, height, layer.getRowCount());
	}
	
	protected static final long findRowPosition(long yOffset, long rowOffset, ILayer layer, long y, long totalHeight, long rowCount) {
		double size = (double) (totalHeight - yOffset) / (rowCount - rowOffset);
		long rowPosition = rowOffset + (int) ((y - yOffset) / size);
		
		long startY = layer.getStartYOfRowPosition(rowPosition);
		long endY = startY + layer.getRowHeightByPosition(rowPosition);
		if (y < startY) {
			if(startY == totalHeight){
				return rowCount;
			}
			return findRowPosition(yOffset, rowOffset, layer, y, startY, rowPosition);
		} else if (y >= endY) {
			return findRowPosition(endY, rowPosition + 1, layer, y, totalHeight, rowCount);
		} else {
			return rowPosition;
		}
	}
	
	/**
	 * Convert column/row position from the source layer to the target layer
	 * @param sourceLayer source layer
	 * @param sourceColumnPosition column position in the source layer
	 * @param targetLayer layer to convert the from position to 
	 * @return converted column position, or -1 if conversion not possible
	 */
	public static final long convertPosition(final ILayerDim source, final long sourceRefPosition,
			final long sourcePosition, final IUniqueIndexLayer targetLayer) {
		if (targetLayer == source.getLayer()) {
			return sourcePosition;
		}
		final long index = source.getPositionIndex(sourceRefPosition, sourcePosition);
		if (index < 0) {
			return Long.MIN_VALUE;
		}
		return (source.getOrientation() == HORIZONTAL) ?
				targetLayer.getColumnPositionByIndex(index) :
				targetLayer.getRowPositionByIndex(index);
	}
	
	/**
	 * Convert column position from the source layer to the target layer
	 * @param sourceLayer source layer
	 * @param sourceColumnPosition column position in the source layer
	 * @param targetLayer layer to convert the from position to 
	 * @return converted column position, or -1 if conversion not possible
	 */
	public static final long convertColumnPosition(ILayer sourceLayer, long sourceColumnPosition, IUniqueIndexLayer targetLayer) {
		if (targetLayer == sourceLayer) {
			return sourceColumnPosition;
		}
		long columnIndex = sourceLayer.getColumnIndexByPosition(sourceColumnPosition);
		if (columnIndex < 0) {
			return Long.MIN_VALUE;
		}
		return targetLayer.getColumnPositionByIndex(columnIndex);
	}
	
	/**
	 * Convert row position from the source layer to the target layer
	 * @param sourceLayer source layer
	 * @param sourceRowPosition position in the source layer
	 * @param targetLayer layer to convert the from position to 
	 * @return converted row position, or -1 if conversion not possible
	 */
	public static final long convertRowPosition(ILayer sourceLayer, long sourceRowPosition, IUniqueIndexLayer targetLayer) {
		if (targetLayer == sourceLayer) {
			return sourceRowPosition;
		}
		long rowIndex = sourceLayer.getRowIndexByPosition(sourceRowPosition);
		if (rowIndex < 0) {
			return Long.MIN_VALUE;
		}
		return targetLayer.getRowPositionByIndex(rowIndex);
	}
	
	public static final long localToUnderlyingPosition(final ILayerDim dim, final long position) {
		if (position < 0 || position >= dim.getPositionCount()) {
			return Long.MIN_VALUE;
		}
		return dim.localToUnderlyingPosition(position, position);
	}
	
}
