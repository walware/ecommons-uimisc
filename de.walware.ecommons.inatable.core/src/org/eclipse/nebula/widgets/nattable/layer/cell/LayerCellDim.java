/*******************************************************************************
 * Copyright (c) 2013 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.layer.cell;

import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;


public final class LayerCellDim {
	
	
	private final Orientation orientation;
	
	private final int index;
	
	private final int position;
	
	private final int originPosition;
	private final int positionSpan;
	
	
	public LayerCellDim(final Orientation orientation, final int index,
			final int position) {
		this(orientation, index, position, position, 1);
	}
	
	public LayerCellDim(final Orientation orientation, final int index,
			final int position, final int originPosition, final int positionSpan) {
		if (orientation == null) {
			throw new NullPointerException("orientation"); //$NON-NLS-1$
		}
		if (positionSpan < 0 || position < originPosition || position >= originPosition + positionSpan) {
			throw new IllegalArgumentException("position: " + position + " [" + originPosition + ", " + (originPosition + positionSpan) + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		this.orientation = orientation;
		this.index = index;
		this.position = position;
		this.originPosition = originPosition;
		this.positionSpan = positionSpan;
	}
	
	
	public Orientation getOrientation() {
		return this.orientation;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public int getPosition() {
		return this.position;
	}
	
	public int getOriginPosition() {
		return this.originPosition;
	}
	
	public int getPositionSpan() {
		return this.positionSpan;
	}
	
	
	@Override
	public int hashCode() {
		int h = this.originPosition;
		if (this.orientation == Orientation.VERTICAL) {
			h = 17 + Integer.rotateRight(h, 15);
		}
		return h ^ (h >>> 7);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof LayerCellDim)) {
			return false;
		}
		final LayerCellDim other = (LayerCellDim) obj;
		return (this.orientation == other.orientation
				&& this.originPosition == other.originPosition
				&& this.positionSpan == other.positionSpan);
	}
	
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("index: ").append(this.index); //$NON-NLS-1$
		sb.append(", ").append("position: ").append(this.position); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append(" [").append(this.originPosition).append(", ").append(this.originPosition + this.positionSpan).append("]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return sb.toString();
	}
	
}
