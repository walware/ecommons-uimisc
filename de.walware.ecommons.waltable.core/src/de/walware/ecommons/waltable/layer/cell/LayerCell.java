/*******************************************************************************
 * Copyright (c) 2012-2016 Edwin Park and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Edwin Park - initial API and implementation
 ******************************************************************************/
package de.walware.ecommons.waltable.layer.cell;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.data.ControlData;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.style.DisplayMode;


public class LayerCell implements ILayerCell {
	
	
	private static final ControlData NO_DATA= new ControlData(0, ""); //$NON-NLS-1$
	
	
	private final ILayer layer;
	
	private final ILayerCellDim h;
	private final ILayerCellDim v;
	
	
	public LayerCell(final ILayer layer,
			final ILayerCellDim horizontalDim, final ILayerCellDim verticalDim) {
		this.layer= layer;
		this.h= horizontalDim;
		this.v= verticalDim;
	}
	
	
	@Override
	public final ILayer getLayer() {
		return this.layer;
	}
	
	@Override
	public final ILayerCellDim getDim(final Orientation orientation) {
		if (orientation == null) {
			throw new NullPointerException("orientation"); //$NON-NLS-1$
		}
		
		return (orientation == HORIZONTAL) ? this.h : this.v;
	}
	
	
	@Override
	public final long getColumnPosition() {
		return this.h.getPosition();
	}
	
	@Override
	public final long getRowPosition() {
		return this.v.getPosition();
	}
	
	@Override
	public final long getOriginColumnPosition() {
		return this.h.getOriginPosition();
	}
	
	@Override
	public final long getOriginRowPosition() {
		return this.v.getOriginPosition();
	}
	
	@Override
	public final long getColumnSpan() {
		return this.h.getPositionSpan();
	}
	
	@Override
	public final long getRowSpan() {
		return this.v.getPositionSpan();
	}
	
	@Override
	public final boolean isSpannedCell() {
		return (getColumnSpan() > 1 || getRowSpan() > 1);
	}
	
	
	@Override
	public DisplayMode getDisplayMode() {
		return DisplayMode.NORMAL;
	}
	
	@Override
	public LabelStack getConfigLabels() {
		return new LabelStack();
	}
	
	@Override
	public Object getDataValue(final int flags) {
		return NO_DATA;
	}
	
	
	@Override
	public LRectangle getBounds() {
		final long xOffset;
		final long yOffset;
		final long width;
		final long height;
		{	final ILayerCellDim dim= getDim(HORIZONTAL);
			
			final long cellPosition= dim.getPosition();
			final long firstPosition= dim.getOriginPosition();
			final long lastPosition= firstPosition + dim.getPositionSpan() - 1;
			
			final ILayerDim layerDim= getLayer().getDim(HORIZONTAL);
			xOffset= layerDim.getPositionStart(cellPosition, firstPosition);
			width= (firstPosition == lastPosition) ?
					layerDim.getPositionSize(cellPosition, lastPosition) :
					layerDim.getPositionStart(cellPosition, lastPosition) - xOffset + layerDim.getPositionSize(cellPosition, lastPosition);
		}
		{	final ILayerCellDim dim= getDim(VERTICAL);
			
			final long cellPosition= dim.getPosition();
			final long firstPosition= dim.getOriginPosition();
			final long lastPosition= firstPosition + dim.getPositionSpan() - 1;
			
			final ILayerDim layerDim= getLayer().getDim(VERTICAL);
			yOffset= layerDim.getPositionStart(cellPosition, firstPosition);
			height= (firstPosition == lastPosition) ?
					layerDim.getPositionSize(cellPosition, lastPosition) :
					layerDim.getPositionStart(cellPosition, lastPosition) - yOffset + layerDim.getPositionSize(cellPosition, lastPosition);
		}
		
		return new LRectangle(xOffset, yOffset, width, height);
	}
	
	
	// Spanned cells are equal if they have the same origin positions (different positions possible)
	// required in LayerCellPainter
	
	@Override
	public int hashCode() {
		final int h= this.h.hashCode() + this.v.hashCode();
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
		final ILayerCell other= (ILayerCell) obj;
		return (this.layer.equals(other.getLayer())
				&& this.h.equals(other.getDim(HORIZONTAL))
				&& this.v.equals(other.getDim(VERTICAL)) );
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " (" //$NON-NLS-1$
			+ "\n\tdata= " + getDataValue(0) //$NON-NLS-1$
			+ "\n\tlayer= " + getLayer().getClass().getSimpleName() //$NON-NLS-1$
			+ "\n\thorizontal= " + this.h //$NON-NLS-1$
			+ "\n\tvertical= " + this.v //$NON-NLS-1$
			+ "\n)"; //$NON-NLS-1$
	}
	
}
