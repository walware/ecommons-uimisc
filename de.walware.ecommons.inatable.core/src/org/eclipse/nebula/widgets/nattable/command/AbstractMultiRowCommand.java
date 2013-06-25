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

import org.eclipse.nebula.widgets.nattable.coordinate.RowPositionCoordinate;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;


public abstract class AbstractMultiRowCommand implements ILayerCommand {
	
	
	private Collection<RowPositionCoordinate> rowPositionCoordinates;
	
	
	protected AbstractMultiRowCommand(ILayer layer, int rowPositions) {
		if (rowPositions < 0) {
			throw new IllegalArgumentException("rowPosition (" + rowPositions + ')'); //$NON-NLS-1$
		}
		setRowPositions(layer, rowPositions);
	}
	
	protected AbstractMultiRowCommand(ILayer layer, int... rowPositions) {
		setRowPositions(layer, rowPositions);
	}
	
	protected AbstractMultiRowCommand(ILayer layer, Collection<Integer> rowPositions) {
		setRowPositions(layer, rowPositions);
	}
	
	protected AbstractMultiRowCommand(AbstractMultiRowCommand command) {
		this.rowPositionCoordinates = new HashSet<RowPositionCoordinate>(command.rowPositionCoordinates);
	}
	
	public Collection<Integer> getRowPositions() {
		Collection<Integer> rowPositions = new HashSet<Integer>();
		for (RowPositionCoordinate rowPositionCoordinate : rowPositionCoordinates) {
			rowPositions.add(Integer.valueOf(rowPositionCoordinate.rowPosition));
		}
		return rowPositions;
	}
	
	protected final void setRowPositions(ILayer layer, int...rowPositions) {
		rowPositionCoordinates = new HashSet<RowPositionCoordinate>();
		for (int rowPosition : rowPositions) {
			rowPositionCoordinates.add(new RowPositionCoordinate(layer, rowPosition));
		}
	}
	
	protected final void setRowPositions(ILayer layer, Collection<Integer> columnPositions) {
		rowPositionCoordinates = new HashSet<RowPositionCoordinate>();
		for (int columnPosition : columnPositions) {
			rowPositionCoordinates.add(new RowPositionCoordinate(layer, columnPosition));
		}
	}
	
	public boolean convertToTargetLayer(ILayer targetLayer) {
		Collection<RowPositionCoordinate> targetRowPositionCoordinates = new HashSet<RowPositionCoordinate>();
		for (RowPositionCoordinate rowPositionCoordinate : rowPositionCoordinates) {
			RowPositionCoordinate targetRowPositionCoordinate = LayerCommandUtil.convertRowPositionToTargetContext(rowPositionCoordinate, targetLayer);
			if (targetRowPositionCoordinate != null) {
				targetRowPositionCoordinates.add(targetRowPositionCoordinate);
			}
		}
		
		if (targetRowPositionCoordinates.size() > 0) {
			rowPositionCoordinates = targetRowPositionCoordinates;
			return true;
		} else {
			return false;
		}
	}
	
}
