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
// ~
package de.walware.ecommons.waltable.coordinate;

import de.walware.ecommons.waltable.layer.ILayer;


public final class RowPositionCoordinate {
	
	
	private final ILayer layer;
	
	public final long rowPosition;
	
	
	public RowPositionCoordinate(final ILayer layer, final long rowPosition) {
		this.layer= layer;
		this.rowPosition= rowPosition;
	}
	
	
	public ILayer getLayer() {
		return this.layer;
	}
	
	public long getRowPosition() {
		return this.rowPosition;
	}
	
	
	@Override
	public int hashCode() {
		final int h= 125315 + (int) (this.rowPosition ^ (this.rowPosition >>> 32));
		return this.layer.hashCode() + (h ^ (h >>> 7));
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof RowPositionCoordinate)) {
			return false;
		}
		final RowPositionCoordinate other= (RowPositionCoordinate) obj;
		return (this.layer == other.layer
				&& this.rowPosition == other.rowPosition);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + this.layer + ":" + this.rowPosition + "]";
	}
	
}
