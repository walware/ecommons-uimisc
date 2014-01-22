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
package org.eclipse.nebula.widgets.nattable.command;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;
import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.coordinate.ColumnPositionCoordinate;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.coordinate.RowPositionCoordinate;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerDim;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;


public class LayerCommandUtil {
	
	public static PositionCoordinate convertPositionToTargetContext(PositionCoordinate positionCoordinate, ILayer targetLayer) {
		ILayer layer = positionCoordinate.getLayer();
		
		if (layer == targetLayer) {
			return positionCoordinate;
		}
		
		long columnPosition = positionCoordinate.getColumnPosition();
		long underlyingColumnPosition = LayerUtil.localToUnderlyingPosition(layer.getDim(HORIZONTAL), columnPosition);
		if (underlyingColumnPosition == Long.MIN_VALUE) {
			return null;
		}
		
		long rowPosition = positionCoordinate.getRowPosition();
		long underlyingRowPosition = LayerUtil.localToUnderlyingPosition(layer.getDim(VERTICAL), rowPosition);
		if (underlyingRowPosition == Long.MIN_VALUE) {
			return null;
		}
		
		ILayer underlyingLayer = layer.getUnderlyingLayerByPosition(columnPosition, rowPosition);
		if (underlyingLayer == null) {
			return null;
		}
		
		return convertPositionToTargetContext(new PositionCoordinate(underlyingLayer, underlyingColumnPosition, underlyingRowPosition), targetLayer);
	}
	
	public static ColumnPositionCoordinate convertColumnPositionToTargetContext(ColumnPositionCoordinate columnPositionCoordinate, ILayer targetLayer) {
		if (columnPositionCoordinate != null) {
			ILayer layer = columnPositionCoordinate.getLayer();
			ILayerDim dim = layer.getDim(HORIZONTAL);
			
			if (layer == targetLayer) {
				return columnPositionCoordinate;
			}
			if (targetLayer instanceof IUniqueIndexLayer) {
				long index = layer.getColumnIndexByPosition(columnPositionCoordinate.columnPosition);
				if (index >= 0) {
					long targetPosition = ((IUniqueIndexLayer) targetLayer).getColumnPositionByIndex(index);
					return (targetPosition != Long.MIN_VALUE) ?
							new ColumnPositionCoordinate(targetLayer, targetPosition) :
							null;
				}
			}
			
			long columnPosition = columnPositionCoordinate.getColumnPosition();
			long underlyingColumnPosition = LayerUtil.localToUnderlyingPosition(dim, columnPosition);
			if (underlyingColumnPosition == Long.MIN_VALUE) {
				return null;
			}
			
			List<ILayerDim> underlyingDims = dim.getUnderlyingDimsByPosition(columnPosition);
			if (underlyingDims != null) {
				for (ILayerDim underlyingDim : underlyingDims) {
					if (underlyingDim != null) {
						ColumnPositionCoordinate convertedColumnPositionCoordinate = convertColumnPositionToTargetContext(
								new ColumnPositionCoordinate(underlyingDim.getLayer(), underlyingColumnPosition),
								targetLayer );
						if (convertedColumnPositionCoordinate != null) {
							return convertedColumnPositionCoordinate;
						}
					}
				}
			}
		}
		return null;
	}
	
	public static RowPositionCoordinate convertRowPositionToTargetContext(RowPositionCoordinate rowPositionCoordinate, ILayer targetLayer) {
		if (rowPositionCoordinate != null) {
			ILayer layer = rowPositionCoordinate.getLayer();
			ILayerDim dim = layer.getDim(VERTICAL);
			
			if (layer == targetLayer) {
				return rowPositionCoordinate;
			}
			if (targetLayer instanceof IUniqueIndexLayer) {
				long index = layer.getRowIndexByPosition(rowPositionCoordinate.rowPosition);
				if (index >= 0) {
					long targetPosition = ((IUniqueIndexLayer) targetLayer).getRowPositionByIndex(index);
					return (targetPosition != Long.MIN_VALUE) ?
							new RowPositionCoordinate(targetLayer, targetPosition) :
							null;
				}
			}
			
			long rowPosition = rowPositionCoordinate.getRowPosition();
			long underlyingRowPosition = LayerUtil.localToUnderlyingPosition(dim, rowPosition);
			if (underlyingRowPosition == Long.MIN_VALUE) {
				return null;
			}
			
			List<ILayerDim> underlyingDims = dim.getUnderlyingDimsByPosition(rowPosition);
			if (underlyingDims != null) {
				for (ILayerDim underlyingDim : underlyingDims) {
					if (underlyingDim != null) {
						RowPositionCoordinate convertedRowPositionCoordinate = convertRowPositionToTargetContext(
								new RowPositionCoordinate(underlyingDim.getLayer(), underlyingRowPosition),
								targetLayer );
						if (convertedRowPositionCoordinate != null) {
							return convertedRowPositionCoordinate;
						}
					}
				}
			}
		}
		return null;
	}
	
	public static long convertPositionToTargetContext(/*@NonNull*/ final ILayerDim dim,
			final long refPosition, final long position, /*@NonNull*/ final ILayerDim targetDim) {
		if (dim == targetDim) {
			return position;
		}
		
		long underlyingRefPosition = dim.localToUnderlyingPosition(refPosition, refPosition);
		long underlyingPosition = dim.localToUnderlyingPosition(refPosition, position);
		if (underlyingPosition == Long.MIN_VALUE) {
			return Long.MIN_VALUE;
		}
		
		final List<ILayerDim> underlyingDims = dim.getUnderlyingDimsByPosition(refPosition);
		if (underlyingDims != null) {
			for (ILayerDim underlyingDim : underlyingDims) {
				if (underlyingDim != null) {
					final long targetPosition = convertPositionToTargetContext(underlyingDim,
							underlyingRefPosition, underlyingPosition, targetDim );
					if (targetPosition != Long.MIN_VALUE) {
						return targetPosition;
					}
				}
			}
		}
		return Long.MIN_VALUE;
	}
	
}
