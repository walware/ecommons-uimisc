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

import java.util.Collection;

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
		
		int columnPosition = positionCoordinate.getColumnPosition();
		int underlyingColumnPosition = LayerUtil.localToUnderlyingPosition(layer.getDim(HORIZONTAL), columnPosition);
		if (underlyingColumnPosition == Integer.MIN_VALUE) {
			return null;
		}
		
		int rowPosition = positionCoordinate.getRowPosition();
		int underlyingRowPosition = LayerUtil.localToUnderlyingPosition(layer.getDim(VERTICAL), rowPosition);
		if (underlyingRowPosition == Integer.MIN_VALUE) {
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
				int index = layer.getColumnIndexByPosition(columnPositionCoordinate.columnPosition);
				if (index >= 0) {
					int targetPosition = ((IUniqueIndexLayer) targetLayer).getColumnPositionByIndex(index);
					return (targetPosition != Integer.MIN_VALUE) ?
							new ColumnPositionCoordinate(targetLayer, targetPosition) :
							null;
				}
			}
			
			int columnPosition = columnPositionCoordinate.getColumnPosition();
			int underlyingColumnPosition = LayerUtil.localToUnderlyingPosition(dim, columnPosition);
			if (underlyingColumnPosition == Integer.MIN_VALUE) {
				return null;
			}
			
			Collection<ILayer> underlyingLayers = dim.getUnderlyingLayersByPosition(columnPosition);
			if (underlyingLayers != null) {
				for (ILayer underlyingLayer : underlyingLayers) {
					if (underlyingLayer != null) {
						ColumnPositionCoordinate convertedColumnPositionCoordinate = convertColumnPositionToTargetContext(new ColumnPositionCoordinate(underlyingLayer, underlyingColumnPosition), targetLayer);
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
				int index = layer.getRowIndexByPosition(rowPositionCoordinate.rowPosition);
				if (index >= 0) {
					int targetPosition = ((IUniqueIndexLayer) targetLayer).getRowPositionByIndex(index);
					return (targetPosition != Integer.MIN_VALUE) ?
							new RowPositionCoordinate(targetLayer, targetPosition) :
							null;
				}
			}
			
			int rowPosition = rowPositionCoordinate.getRowPosition();
			int underlyingRowPosition = LayerUtil.localToUnderlyingPosition(dim, rowPosition);
			if (underlyingRowPosition == Integer.MIN_VALUE) {
				return null;
			}
			
			Collection<ILayer> underlyingLayers = dim.getUnderlyingLayersByPosition(rowPosition);
			if (underlyingLayers != null) {
				for (ILayer underlyingLayer : underlyingLayers) {
					if (underlyingLayer != null) {
						RowPositionCoordinate convertedRowPositionCoordinate = convertRowPositionToTargetContext(new RowPositionCoordinate(underlyingLayer, underlyingRowPosition), targetLayer);
						if (convertedRowPositionCoordinate != null) {
							return convertedRowPositionCoordinate;
						}
					}
				}
			}
		}
		return null;
	}
	
	public static int convertPositionToTargetContext(ILayerDim dim, int refPosition,
			int position, ILayerDim targetDim) {
		if (dim == targetDim) {
			return position;
		}
		
		int underlyingRefPosition = dim.localToUnderlyingPosition(refPosition, refPosition);
		int underlyingPosition = dim.localToUnderlyingPosition(refPosition, position);
		if (underlyingPosition == Integer.MIN_VALUE) {
			return Integer.MIN_VALUE;
		}
		
		final Collection<ILayer> underlyingLayers = dim.getUnderlyingLayersByPosition(refPosition);
		if (underlyingLayers != null) {
			for (ILayer underlyingLayer : underlyingLayers) {
				if (underlyingLayer != null) {
					int targetPosition = convertPositionToTargetContext(underlyingLayer.getDim(dim.getOrientation()),
							underlyingRefPosition, underlyingPosition, targetDim );
					if (targetPosition != Integer.MIN_VALUE) {
						return targetPosition;
					}
				}
			}
		}
		return Integer.MIN_VALUE;
	}
	
}
