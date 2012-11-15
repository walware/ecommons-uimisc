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
// ~
package org.eclipse.nebula.widgets.nattable.coordinate;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;


public final class PositionCoordinate {
	
	
	private final ILayer layer;
	
	public int columnPosition;
	public int rowPosition;
	
	
	public PositionCoordinate(final ILayer layer, final int columnPosition, final int rowPosition) {
		this.layer = layer;
		this.columnPosition = columnPosition;
		this.rowPosition = rowPosition;
	}
	
	public PositionCoordinate(final PositionCoordinate coordinate) {
		this.layer = coordinate.layer;
		this.columnPosition = coordinate.columnPosition;
		this.rowPosition = coordinate.rowPosition;
	}
	
	
	public ILayer getLayer() {
		return layer;
	}
	
	public int getColumnPosition() {
		return columnPosition;
	}
	
	public int getRowPosition() {
		return rowPosition;
	}
	
	public void set(final int rowPosition, final int columnPosition) {
		this.rowPosition = rowPosition;
		this.columnPosition = columnPosition;
	}
	
	@Override
	public int hashCode() {
		int h = Integer.rotateRight(columnPosition, 15) + rowPosition * 17 ^ rowPosition & 0xff000000;
		return layer.hashCode() + (h ^ (h >>> 7));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof PositionCoordinate)) {
			return false;
		}
		final PositionCoordinate other = (PositionCoordinate) obj;
		return (this.layer == other.layer
				&& this.columnPosition == other.columnPosition
				&& this.rowPosition == other.rowPosition );
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + layer + ":" + columnPosition + "," + rowPosition + "]";
	}
	
}
