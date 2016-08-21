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
package de.walware.ecommons.waltable.ui.util;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;
import static de.walware.ecommons.waltable.ui.util.CellEdgeEnum.BOTTOM;
import static de.walware.ecommons.waltable.ui.util.CellEdgeEnum.LEFT;
import static de.walware.ecommons.waltable.ui.util.CellEdgeEnum.NONE;
import static de.walware.ecommons.waltable.ui.util.CellEdgeEnum.RIGHT;
import static de.walware.ecommons.waltable.ui.util.CellEdgeEnum.TOP;
import static de.walware.ecommons.waltable.util.GUIHelper.DEFAULT_RESIZE_HANDLE_SIZE;

import de.walware.ecommons.waltable.coordinate.LPoint;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;


public class CellEdgeDetectUtil {
	
	
	/**
	 * Calculate the column position to resize depending on the cursor's position
	 * on the left/right edges of the cell.
	 * Does <i>not</i> take into account columns which are not allowed to be resized.
	 */
	public static long getPositionToResize(final ILayer layer, final LPoint clickPoint,
			final Orientation orientation) {
		final ILayerCell cell= layer.getCellByPosition(
				layer.getDim(HORIZONTAL).getPositionByPixel(clickPoint.x), 
				layer.getDim(VERTICAL).getPositionByPixel(clickPoint.y) );
		if (cell != null) {
			final long position= cell.getDim(orientation).getPosition();
			final CellEdgeEnum edge= (orientation == HORIZONTAL) ?
					getHorizontalCellEdge(cell.getBounds(), clickPoint, DEFAULT_RESIZE_HANDLE_SIZE) :
					getVerticalCellEdge(cell.getBounds(), clickPoint, DEFAULT_RESIZE_HANDLE_SIZE);
			switch (edge) {
			case LEFT:
			case TOP:
				if (position == 1) { // can't resize left edge of first column/row
					break;
				}
				return position - 1;
			case RIGHT:
			case BOTTOM:
				return position;
			default:
				break;
			}
		}
		return Long.MIN_VALUE;
	}
	
	
	public static CellEdgeEnum getHorizontalCellEdge(final ILayer layer, final LPoint clickPt) {
		return getHorizontalCellEdge(layer, clickPt, -1);
	}
	
	public static CellEdgeEnum getHorizontalCellEdge(final ILayer layer, final LPoint clickPt,
			final long handleWidth) {
		final ILayerCell cell= layer.getCellByPosition(
				layer.getColumnPositionByX(clickPt.x),
				layer.getRowPositionByY(clickPt.y) );
		
		if (cell != null) {
			return getHorizontalCellEdge(cell.getBounds(), clickPt, handleWidth);
		} else {
			return NONE;
		}
	}
	
	/**
	 * Figure out if the click point is closer to the left/right edge of the cell.
	 * @param cellBounds of the table cell containing the click
	 * @param clickPt
	 * @param distanceFromEdge distance from the edge to qualify as <i>close</i> to the cell edge
	 */
	private static CellEdgeEnum getHorizontalCellEdge(final LRectangle cellBounds, final LPoint clickPt,
			long distanceFromEdge) {
		if (distanceFromEdge < 0) {
			distanceFromEdge= cellBounds.width / 2;
		}
		
		final LRectangle left= new LRectangle(cellBounds.x, cellBounds.y, distanceFromEdge, cellBounds.height);
		final LRectangle right= new LRectangle(cellBounds.x + cellBounds.width - distanceFromEdge, cellBounds.y, 
				distanceFromEdge, cellBounds.height);
		
		if (left.contains(clickPt)) {
			return LEFT;
		} else if (right.contains(clickPt)) {
			return RIGHT;
		} else {
			return NONE;
		}
	}
	
	
	public static CellEdgeEnum getVerticalCellEdge(final ILayer layer, final LPoint clickPt) {
		return getVerticalCellEdge(layer, clickPt, -1);
	}
	
	public static CellEdgeEnum getVerticalCellEdge(final ILayer layer, final LPoint clickPt,
			final long handleHeight) {
		final ILayerCell cell= layer.getCellByPosition(
				layer.getColumnPositionByX(clickPt.x),
				layer.getRowPositionByY(clickPt.y) );
		
		if (cell != null) {
			return getVerticalCellEdge(cell.getBounds(), clickPt, handleHeight);
		}
		else {
			return NONE;
		}
	}
	
	private static CellEdgeEnum getVerticalCellEdge(final LRectangle cellBounds, final LPoint clickPt,
			long distanceFromEdge) {
		if (distanceFromEdge < 0) {
			distanceFromEdge= cellBounds.height / 2;
		}
		
		final LRectangle top= new LRectangle(cellBounds.x, cellBounds.y, cellBounds.width, distanceFromEdge);
		final LRectangle bottom= new LRectangle(cellBounds.x, cellBounds.y + cellBounds.height - distanceFromEdge, cellBounds.width, distanceFromEdge);
		
		if (top.contains(clickPt)) {
			return TOP;
		} else if (bottom.contains(clickPt)) {
			return BOTTOM;
		} else {
			return NONE;
		}
	}
	
}
