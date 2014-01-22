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

package org.eclipse.nebula.widgets.nattable.layer;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;


public class CompositeDim implements ILayerDim {
	
	
	public static class IgnoreRef extends CompositeDim {
		
		
		public IgnoreRef(final CompositeLayer layer, final Orientation orientation) {
			super(layer, orientation);
		}
		
		
		@Override
		public long getPositionIndex(final long refPosition, final long position) {
			return super.getPositionIndex(position, position);
		}
		
		
		@Override
		public long localToUnderlyingPosition(final long refPosition, final long position) {
			return super.localToUnderlyingPosition(position, position);
		}
		
		@Override
		public long underlyingToLocalPosition(final long refPosition, final long underlyingPosition) {
			return super.underlyingToLocalPosition(refPosition, underlyingPosition);
		}
		
		
		@Override
		public long getPositionStart(final long refPosition, final long position) {
			return super.getPositionStart(position, position);
		}
		
		@Override
		public int getPositionSize(final long refPosition, final long position) {
			return super.getPositionSize(position, position);
		}
		
	}
	
	
	protected final CompositeLayer layer;
	
	protected final Orientation orientation;
	
	private final ILayerDim[][] childDims;
	
	
	public CompositeDim(final CompositeLayer layer, final Orientation orientation) {
		if (layer == null) {
			throw new NullPointerException("layer"); //$NON-NLS-1$
		}
		if (orientation == null) {
			throw new NullPointerException("orientation"); //$NON-NLS-1$
		}
		this.layer = layer;
		this.orientation = orientation;
		if (orientation == HORIZONTAL) {
			this.childDims = new ILayerDim[layer.layoutXCount][layer.layoutYCount];
		}
		else {
			this.childDims = new ILayerDim[layer.layoutYCount][layer.layoutXCount];
		}
	}
	
	
	@Override
	public CompositeLayer getLayer() {
		return this.layer;
	}
	
	@Override
	public Orientation getOrientation() {
		return this.orientation;
	}
	
	
	void updateChild(final int layout, final int layout2, final ILayer childLayer) {
		this.childDims[layout][layout2] = childLayer.getDim(this.orientation);
	}
	
	
	protected final int getLayoutByPosition(final long position) {
		if (position >= 0) {
			for (int layout = 0, offset = 0; layout < this.childDims.length; layout++) {
				if (/*position >= offset & */
						position < (offset += this.childDims[layout][0].getPositionCount()) ) {
					return layout;
				}
			}
		}
		return -1;
	}
	
	protected final int getLayoutByPixel(final long pixel) {
		if (pixel >= 0) {
			for (int layout = 0, offset = 0; layout < this.childDims.length; layout++) {
				if (/*x >= offset & */ pixel < (offset += this.childDims[layout][0].getSize())) {
					return layout;
				}
			}
		}
		return -1;
	}
	
	protected final int getLayoutByDim(final ILayerDim childDim) {
		for (int layout = 0; layout < this.childDims.length; layout++) {
			final ILayerDim[] layoutDims = this.childDims[layout];
			for (int layout2 = 0; layout2 < layoutDims.length; layout2++) {
				if (layoutDims[layout2] == childDim) {
					return layout;
				}
			}
		}
		return -1;
	}
	
	protected final long getLayoutPosition(final int layout) {
		long offset = 0;
		for (int i = 0; i < layout; i++) {
			offset += this.childDims[i][0].getPositionCount();
		}
		return offset;
	}
	
	protected final long getLayoutStart(final int layout) {
		long start = 0;
		for (int i = 0; i < layout; i++) {
			start += this.childDims[i][0].getSize();
		}
		return start;
	}
	
	
	@Override
	public long getPositionIndex(final long refPosition, final long position) {
		final int layout = getLayoutByPosition(refPosition);
		if (layout < 0) {
			throw new IndexOutOfBoundsException("refPosition: " + refPosition); //$NON-NLS-1$
		}
		
		final long layoutPosition = getLayoutPosition(layout);
		return this.childDims[layout][0].getPositionIndex(
				refPosition - layoutPosition,
				position - layoutPosition );
	}
	
	
	@Override
	public long getPositionCount() {
		long count = 0;
		for (int layout = 0; layout < this.childDims.length; layout++) {
			count += this.childDims[layout][0].getPositionCount();
		}
		return count;
	}
	
	@Override
	public long localToUnderlyingPosition(final long refPosition, final long position) {
		final int layout = getLayoutByPosition(refPosition);
		if (layout < 0) {
			throw new IndexOutOfBoundsException("refPosition: " + refPosition); //$NON-NLS-1$
		}
		
		return position - getLayoutPosition(layout);
	}
	
	@Override
	public long underlyingToLocalPosition(final long refPosition,
			final long underlyingPosition) {
		final int layout = getLayoutByPosition(refPosition);
		if (layout < 0) {
			throw new IndexOutOfBoundsException("refPosition:" + refPosition); //$NON-NLS-1$
		}
		
		final long layoutPosition = getLayoutPosition(layout);
		return layoutPosition + underlyingPosition;
	}
	
	@Override
	public long underlyingToLocalPosition(final ILayerDim sourceUnderlyingDim,
			final long underlyingPosition) {
		final int layout = getLayoutByDim(sourceUnderlyingDim);
		if (layout < 0) {
			throw new IllegalArgumentException("underlyingLayer"); //$NON-NLS-1$
		}
		
		final long layoutPosition = getLayoutPosition(layout);
		return layoutPosition + underlyingPosition;
	}
	
	@Override
	public List<Range> underlyingToLocalPositions(final ILayerDim sourceUnderlyingDim,
			final Collection<Range> underlyingPositionRanges) {
		final int layout = getLayoutByDim(sourceUnderlyingDim);
		if (layout < 0) {
			throw new IllegalArgumentException("underlyingLayer"); //$NON-NLS-1$
		}
		
		final List<Range> localPositionRanges = new ArrayList<Range>();
		
		final long layoutPosition = getLayoutPosition(layout);
		for (final Range underlyingPositionRange : underlyingPositionRanges) {
			localPositionRanges.add(new Range(
					layoutPosition + underlyingPositionRange.start,
					layoutPosition + underlyingPositionRange.end ));
		}
		
		return localPositionRanges;
	}
	
	@Override
	public List<ILayerDim> getUnderlyingDimsByPosition(final long position) {
		final int layout = getLayoutByPosition(position);
		if (layout < 0) {
			return null;
		}
		
		final List<ILayerDim> underlyingDims = new ArrayList<ILayerDim>(this.childDims.length);
		
		final ILayerDim[] layoutDims = this.childDims[layout];
		for (int layout2 = 0; layout2 < layoutDims.length; layout2++) {
			underlyingDims.add(layoutDims[layout2]);
		}
		
		return underlyingDims;
	}
	
	
	@Override
	public long getSize() {
		long size = 0;
		for (int layout = 0; layout < this.childDims.length; layout++) {
			size += this.childDims[layout][0].getSize();
		}
		return size;
	}
	
	@Override
	public long getPreferredSize() {
		long size = 0;
		for (int layout = 0; layout < this.childDims.length; layout++) {
			size += this.childDims[layout][0].getPreferredSize();
		}
		return size;
	}
	
	@Override
	public long getPositionByPixel(final long pixel) {
		final int layout = getLayoutByPixel(pixel);
		if (layout < 0) {
			throw new IndexOutOfBoundsException("pixel: " + pixel); //$NON-NLS-1$
		}
		
		final long childPosition = this.childDims[layout][0].getPositionByPixel(
				pixel - getLayoutStart(layout) );
		return getLayoutPosition(layout) + childPosition;
	}
	
	@Override
	public long getPositionStart(final long refPosition, final long position) {
		final int layout = getLayoutByPosition(refPosition);
		if (layout < 0) {
			throw new IndexOutOfBoundsException("refPosition: " + refPosition); //$NON-NLS-1$
		}
		
		final long layoutPosition = getLayoutPosition(layout);
		final long childStart = this.childDims[layout][0].getPositionStart(
				refPosition - layoutPosition, position - layoutPosition );
		return getLayoutStart(layout) + childStart;
	}
	
	@Override
	public int getPositionSize(final long refPosition, final long position) {
		final int layout = getLayoutByPosition(refPosition);
		if (layout < 0) {
			throw new IndexOutOfBoundsException("refPosition: " + refPosition); //$NON-NLS-1$
		}
		
		final long layoutPosition = getLayoutPosition(layout);
		return this.childDims[layout][0].getPositionSize(
				refPosition - layoutPosition, position - layoutPosition );
	}
	
	@Override
	public boolean isPositionResizable(final long position) {
		final int layout = getLayoutByPosition(position);
		if (layout < 0) {
			return false;
		}
		
		final long layoutPosition = getLayoutPosition(layout);
		return this.childDims[layout][0].isPositionResizable(
				position - layoutPosition );
	}
	
}
