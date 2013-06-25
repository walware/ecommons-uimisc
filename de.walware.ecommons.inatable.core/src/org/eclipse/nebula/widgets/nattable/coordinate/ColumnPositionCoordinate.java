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


public final class ColumnPositionCoordinate {


	private final ILayer layer;

	public final int columnPosition;


	public ColumnPositionCoordinate(final ILayer layer, final int columnPosition) {
		this.layer = layer;
		this.columnPosition = columnPosition;
	}


	public ILayer getLayer() {
		return layer;
	}

	public int getColumnPosition() {
		return columnPosition;
	}


	@Override
	public int hashCode() {
		int h = 253 + columnPosition * 17 + columnPosition & 0xff000000;
		return layer.hashCode() + (h ^ (h >>> 7));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ColumnPositionCoordinate)) {
			return false;
		}
		final ColumnPositionCoordinate other = (ColumnPositionCoordinate) obj;
		return (layer == other.layer
				&& columnPosition == other.columnPosition );
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + layer + ":" + columnPosition + "]";
	}

}
