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


public final class ColumnPositionCoordinate {


	private final ILayer layer;

	public final long columnPosition;


	public ColumnPositionCoordinate(final ILayer layer, final long columnPosition) {
		this.layer= layer;
		this.columnPosition= columnPosition;
	}


	public ILayer getLayer() {
		return this.layer;
	}

	public long getColumnPosition() {
		return this.columnPosition;
	}


	@Override
	public int hashCode() {
		final int h= 253 + (int) (this.columnPosition ^ (this.columnPosition >>> 32));
		return this.layer.hashCode() + (h ^ (h >>> 7));
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ColumnPositionCoordinate)) {
			return false;
		}
		final ColumnPositionCoordinate other= (ColumnPositionCoordinate) obj;
		return (this.layer == other.layer
				&& this.columnPosition == other.columnPosition );
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + this.layer + ":" + this.columnPosition + "]";
	}

}
