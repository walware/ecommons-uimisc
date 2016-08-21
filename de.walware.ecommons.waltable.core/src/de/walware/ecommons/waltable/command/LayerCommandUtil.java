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
package de.walware.ecommons.waltable.command;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import de.walware.ecommons.waltable.coordinate.ColumnPositionCoordinate;
import de.walware.ecommons.waltable.coordinate.PositionCoordinate;
import de.walware.ecommons.waltable.coordinate.RowPositionCoordinate;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.layer.LayerUtil;


public class LayerCommandUtil {
	
	public static PositionCoordinate convertPositionToTargetContext(final PositionCoordinate positionCoordinate, final ILayer targetLayer) {
		final ILayer layer= positionCoordinate.getLayer();
		
		if (layer == targetLayer) {
			return positionCoordinate;
		}
		
		final long columnPosition= positionCoordinate.getColumnPosition();
		final long underlyingColumnPosition= layer.getDim(HORIZONTAL).localToUnderlyingPosition(columnPosition, columnPosition);
		if (underlyingColumnPosition == Long.MIN_VALUE) {
			return null;
		}
		
		final long rowPosition= positionCoordinate.getRowPosition();
		final long underlyingRowPosition= layer.getDim(VERTICAL).localToUnderlyingPosition(rowPosition, rowPosition);
		if (underlyingRowPosition == Long.MIN_VALUE) {
			return null;
		}
		
		final ILayer underlyingLayer= layer.getUnderlyingLayerByPosition(columnPosition, rowPosition);
		if (underlyingLayer == null) {
			return null;
		}
		
		return convertPositionToTargetContext(new PositionCoordinate(underlyingLayer, underlyingColumnPosition, underlyingRowPosition), targetLayer);
	}
	
	public static ColumnPositionCoordinate convertColumnPositionToTargetContext(final ColumnPositionCoordinate columnPositionCoordinate, final ILayer targetLayer) {
		if (columnPositionCoordinate != null) {
			final ILayer layer= columnPositionCoordinate.getLayer();
			
			final long targetPosition= LayerUtil.convertPosition(layer.getDim(HORIZONTAL),
					columnPositionCoordinate.columnPosition, columnPositionCoordinate.columnPosition,
					targetLayer.getDim(HORIZONTAL) );
			return (targetPosition != ILayerDim.POSITION_NA) ?
					new ColumnPositionCoordinate(targetLayer, targetPosition) :
					null;
		}
		return null;
	}
	
	public static RowPositionCoordinate convertRowPositionToTargetContext(final RowPositionCoordinate rowPositionCoordinate, final ILayer targetLayer) {
		if (rowPositionCoordinate != null) {
			final ILayer layer= rowPositionCoordinate.getLayer();
			
			final long targetPosition= LayerUtil.convertPosition(layer.getDim(VERTICAL),
					rowPositionCoordinate.rowPosition, rowPositionCoordinate.rowPosition,
					targetLayer.getDim(VERTICAL) );
			return (targetPosition != ILayerDim.POSITION_NA) ?
					new RowPositionCoordinate(targetLayer, targetPosition) :
					null;
		}
		return null;
	}
	
}
