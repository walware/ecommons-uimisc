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
// +(Collection args)
package org.eclipse.nebula.widgets.nattable.command;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.nebula.widgets.nattable.coordinate.ColumnPositionCoordinate;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;


public abstract class AbstractMultiColumnCommand implements ILayerCommand {
	
	
	protected Collection<ColumnPositionCoordinate> columnPositionCoordinates;
	
	
	protected AbstractMultiColumnCommand(ILayer layer, long columnPositions) {
		if (columnPositions < 0) {
			throw new IllegalArgumentException("columnPositions (" + columnPositions + ')'); //$NON-NLS-1$
		}
		setColumnPositions(layer, columnPositions);
	}
	
	protected AbstractMultiColumnCommand(ILayer layer, long... columnPositions) {
		setColumnPositions(layer, columnPositions);
	}
	
	protected AbstractMultiColumnCommand(ILayer layer, Collection<Long> columnPositions) {
		setColumnPositions(layer, columnPositions);
	}
	
	protected AbstractMultiColumnCommand(AbstractMultiColumnCommand command) {
		this.columnPositionCoordinates = new HashSet<ColumnPositionCoordinate>(command.columnPositionCoordinates);
	}
	
	
	public Collection<Long> getColumnPositions() {
		Collection<Long> columnPositions = new HashSet<Long>();
		for (ColumnPositionCoordinate columnPositionCoordinate : columnPositionCoordinates) {
			columnPositions.add(Long.valueOf(columnPositionCoordinate.columnPosition));
		}
		return columnPositions;
	}
	
	protected final void setColumnPositions(ILayer layer, long... columnPositions) {
		columnPositionCoordinates = new HashSet<ColumnPositionCoordinate>();
		for (long columnPosition : columnPositions) {
			columnPositionCoordinates.add(new ColumnPositionCoordinate(layer, columnPosition));
		}
	}
	
	protected final void setColumnPositions(ILayer layer, Collection<Long> columnPositions) {
		columnPositionCoordinates = new HashSet<ColumnPositionCoordinate>();
		for (long columnPosition : columnPositions) {
			columnPositionCoordinates.add(new ColumnPositionCoordinate(layer, columnPosition));
		}
	}
	
	public boolean convertToTargetLayer(ILayer targetLayer) {
		Collection<ColumnPositionCoordinate> targetColumnPositionCoordinates = new HashSet<ColumnPositionCoordinate>();
		
		for (ColumnPositionCoordinate columnPositionCoordinate : columnPositionCoordinates) {
			ColumnPositionCoordinate targetColumnPositionCoordinate = LayerCommandUtil.convertColumnPositionToTargetContext(columnPositionCoordinate, targetLayer);
			if (targetColumnPositionCoordinate != null) {
				targetColumnPositionCoordinates.add(targetColumnPositionCoordinate);
			}
		}
		
		if (targetColumnPositionCoordinates.size() > 0) {
			columnPositionCoordinates = targetColumnPositionCoordinates;
			return true;
		} else {
			return false;
		}
	}
	
}
