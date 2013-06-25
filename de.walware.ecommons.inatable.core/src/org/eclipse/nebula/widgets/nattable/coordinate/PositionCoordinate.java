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
// ~
package org.eclipse.nebula.widgets.nattable.coordinate;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;


public final class PositionCoordinate {
	
	
	private final ILayer layer;
	
	public long columnPosition;
	public long rowPosition;
	
	
	public PositionCoordinate(final ILayer layer, final long columnPosition, final long rowPosition) {
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
	
	public long getColumnPosition() {
		return columnPosition;
	}
	
	public long getRowPosition() {
		return rowPosition;
	}
	
	public void set(final long rowPosition, final long columnPosition) {
		this.rowPosition = rowPosition;
		this.columnPosition = columnPosition;
	}
	
	@Override
	public int hashCode() {
		int h = (int) (columnPosition ^ (columnPosition >>> 32));
		h = Integer.rotateRight(h, 15);
		h ^= (int) (rowPosition ^ (rowPosition));
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
