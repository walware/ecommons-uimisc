/*******************************************************************************
 * Copyright (c) 2012-2013 Original authors and others.
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


public final class RowPositionCoordinate {
	
	
	private final ILayer layer;
	
	public final int rowPosition;
	
	
	public RowPositionCoordinate(final ILayer layer, final int rowPosition) {
		this.layer = layer;
		this.rowPosition = rowPosition;
	}
	
	
	public ILayer getLayer() {
		return layer;
	}
	
	public int getRowPosition() {
		return rowPosition;
	}
	
	
	@Override
	public int hashCode() {
		int h = 125315 + rowPosition * 17 + rowPosition & 0xff000000;
		return layer.hashCode() + (h ^ (h >>> 7));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof RowPositionCoordinate)) {
			return false;
		}
		final RowPositionCoordinate other = (RowPositionCoordinate) obj;
		return (layer == other.layer
				&& rowPosition == other.rowPosition);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + layer + ":" + rowPosition + "]";
	}
	
}
