/*******************************************************************************
 * Copyright (c) 2012 Edwin Park and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Edwin Park - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.layer.cell;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;
import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;

import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;


public class LayerCell implements ILayerCell {
	
	
	private final ILayer layer;
	
	private final LayerCellDim h;
	private final LayerCellDim v;
	
	private final String displayMode;
	
	private boolean isConfigLabelsCached = false;
	private LabelStack configLabels = null;

	private boolean isDataValueCached = false;
	private Object dataValue = null;

	private boolean isBoundsCached = false;
	private Rectangle bounds = null;
	
	
	public LayerCell(final ILayer layer,
			final LayerCellDim horizontalDim, final LayerCellDim verticalDim) {
		this(layer, horizontalDim, verticalDim, DisplayMode.NORMAL);
	}
	
	public LayerCell(final ILayer layer,
			final LayerCellDim horizontalDim, final LayerCellDim verticalDim,
			final String displayMode) {
		this.layer = layer;
		this.h = horizontalDim;
		this.v = verticalDim;
		this.displayMode = displayMode;
	}
	
	
	@Override
	public ILayer getLayer() {
		return this.layer;
	}
	
	@Override
	public LayerCellDim getDim(final Orientation orientation) {
		if (orientation == null) {
			throw new NullPointerException("orientation"); //$NON-NLS-1$
		}
		
		return (orientation == HORIZONTAL) ? this.h : this.v;
	}
	
	
	@Override
	public long getColumnIndex() {
		return this.h.getIndex();
	}
	
	@Override
	public long getRowIndex() {
		return this.v.getIndex();
	}
	
	
	public long getColumnPosition() {
		return this.h.getPosition();
	}
	
	public long getRowPosition() {
		return this.v.getPosition();
	}
	
	public long getOriginColumnPosition() {
		return this.h.getOriginPosition();
	}
	
	public long getOriginRowPosition() {
		return this.v.getOriginPosition();
	}
	
	@Override
	public long getColumnSpan() {
		return this.h.getPositionSpan();
	}
	
	@Override
	public long getRowSpan() {
		return this.v.getPositionSpan();
	}
	
	public boolean isSpannedCell() {
		return getColumnSpan() > 1 || getRowSpan() > 1;
	}
	
	public String getDisplayMode() {
		return this.displayMode;
	}
	
	
	public LabelStack getConfigLabels() {
		if (!this.isConfigLabelsCached) {
			this.isConfigLabelsCached = true;
			
			this.configLabels = getLayer().getConfigLabelsByPosition(getColumnPosition(), getRowPosition());
		}
		
		return this.configLabels;
	}

	public Object getDataValue() {
		if (!this.isDataValueCached) {
			this.isDataValueCached = true;
			
			this.dataValue = getLayer().getDataValueByPosition(getColumnPosition(), getRowPosition());
		}
		
		return this.dataValue;
	}
	
	public Rectangle getBounds() {
		if (!this.isBoundsCached) {
			this.isBoundsCached = true;

			this.bounds = getLayer().getBoundsByPosition(getColumnPosition(), getRowPosition());
		}
		
		return this.bounds;
	}
	
	
	// Spanned cells are equal if they have the same origin positions (different positions possible)
	// required in LayerCellPainter
	
	@Override
	public int hashCode() {
		final int h = this.h.hashCode() + this.v.hashCode();
		return h ^ h * getLayer().hashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ILayerCell)) {
			return false;
		}
		final ILayerCell other = (ILayerCell) obj;
		return (this.layer.equals(other.getLayer())
				&& this.h.equals(other.getDim(HORIZONTAL))
				&& this.v.equals(other.getDim(VERTICAL)) );
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " (" //$NON-NLS-1$
			+ "\n\tdata= " + getDataValue() //$NON-NLS-1$
			+ "\n\tlayer= " + getLayer().getClass().getSimpleName() //$NON-NLS-1$
			+ "\n\thorizontal= " + this.h //$NON-NLS-1$
			+ "\n\tvertical= " + this.v //$NON-NLS-1$
			+ "\n)"; //$NON-NLS-1$
	}
	
}
