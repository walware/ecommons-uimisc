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

import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;


public class CompositeDim implements ILayerDim {
	
	
	public static class IgnoreRef extends CompositeDim {
		
		
		public IgnoreRef(final CompositeLayer layer, final Orientation orientation) {
			super(layer, orientation);
		}
		
		
		@Override
		public int getPositionIndex(final int refPosition, final int position) {
			return super.getPositionIndex(position, position);
		}
		
		
		@Override
		public int localToUnderlyingPosition(final int refPosition, final int position) {
			return super.localToUnderlyingPosition(refPosition, position);
		}
		
		@Override
		public int underlyingToLocalPosition(final int refPosition, final int underlyingPosition) {
			return super.underlyingToLocalPosition(refPosition, underlyingPosition);
		}
		
		
		@Override
		public int getPositionStart(final int refPosition, final int position) {
			return super.getPositionStart(position, position);
		}
		
		@Override
		public int getPositionSize(final int refPosition, final int position) {
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
		this.childDims[layout][layout2] = childLayer.getDim(getOrientation());
	}
	
	
	protected final int getLayoutByPosition(final int position) {
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
	
	protected final int getLayoutByPixel(final int pixel) {
		if (pixel >= 0) {
			for (int layout = 0, offset = 0; layout < this.childDims.length; layout++) {
				if (/*x >= offset & */ pixel < (offset += this.childDims[layout][0].getSize())) {
					return layout;
				}
			}
		}
		return -1;
	}
	
	protected final int getLayoutByChildLayer(final ILayer childLayer) {
		for (int layout = 0; layout < this.childDims.length; layout++) {
			final ILayerDim[] layoutDims = this.childDims[layout];
			for (int layout2 = 0; layout2 < layoutDims.length; layout2++) {
				if (layoutDims[layout2].getLayer() == childLayer) {
					return layout;
				}
			}
		}
		return -1;
	}
	
	protected final int getLayoutPosition(final int layout) {
		int offset = 0;
		for (int x = 0; x < layout; x++) {
			offset += this.childDims[x][0].getPositionCount();
		}
		return offset;
	}
	
	protected final int getLayoutStart(final int layout) {
		int start = 0;
		for (int x = 0; x < layout; x++) {
			start += this.childDims[x][0].getSize();
		}
		return start;
	}
	
	
	@Override
	public int getPositionIndex(final int refPosition, final int position) {
		final int layout = getLayoutByPosition(refPosition);
		if (layout < 0) {
			throw new IndexOutOfBoundsException("refPosition: " + refPosition); //$NON-NLS-1$
		}
		
		final int layoutPosition = getLayoutPosition(layout);
		return this.childDims[layout][0].getPositionIndex(
				refPosition - layoutPosition,
				position - layoutPosition );
	}
	
	
	@Override
	public int getPositionCount() {
		int count = 0;
		for (int layout = 0; layout < this.childDims.length; layout++) {
			count += this.childDims[layout][0].getPositionCount();
		}
		return count;
	}
	
	@Override
	public int getPreferredPositionCount() {
		int count = 0;
		for (int layout = 0; layout < this.childDims.length; layout++) {
			count += this.childDims[layout][0].getPreferredPositionCount();
		}
		return count;
	}
	
	@Override
	public int localToUnderlyingPosition(final int refPosition, final int position) {
		final int layout = getLayoutByPosition(refPosition);
		if (layout < 0) {
			throw new IndexOutOfBoundsException("refPosition: " + refPosition); //$NON-NLS-1$
		}
		
		return position - getLayoutPosition(layout);
	}
	
	@Override
	public int underlyingToLocalPosition(final int refPosition,
			final int underlyingPosition) {
		final int layout = getLayoutByPosition(refPosition);
		if (layout < 0) {
			throw new IndexOutOfBoundsException("refPosition:" + refPosition); //$NON-NLS-1$
		}
		
		final int layoutPosition = getLayoutPosition(layout);
		return layoutPosition + underlyingPosition;
	}
	
	@Override
	public int underlyingToLocalPosition(final ILayer sourceUnderlyingLayer,
			final int underlyingPosition) {
		final int layout = getLayoutByChildLayer(sourceUnderlyingLayer);
		if (layout < 0) {
			throw new IllegalArgumentException("underlyingLayer"); //$NON-NLS-1$
		}
		
		final int layoutPosition = getLayoutPosition(layout);
		return layoutPosition + underlyingPosition;
	}
	
	@Override
	public Collection<Range> underlyingToLocalPositions(final ILayer sourceUnderlyingLayer,
			final Collection<Range> underlyingPositionRanges) {
		final int layout = getLayoutByChildLayer(sourceUnderlyingLayer);
		if (layout < 0) {
			throw new IllegalArgumentException("underlyingLayer"); //$NON-NLS-1$
		}
		
		final Collection<Range> localPositionRanges = new ArrayList<Range>();
		
		final int layoutPosition = getLayoutPosition(layout);
		for (final Range underlyingPositionRange : underlyingPositionRanges) {
			localPositionRanges.add(new Range(
					layoutPosition + underlyingPositionRange.start,
					layoutPosition + underlyingPositionRange.end ));
		}
		
		return localPositionRanges;
	}
	
	@Override
	public Collection<ILayer> getUnderlyingLayersByPosition(final int position) {
		final int layout = getLayoutByPosition(position);
		if (layout < 0) {
			return null;
		}
		
		final Collection<ILayer> underlyingLayers = new ArrayList<ILayer>(this.childDims.length);
		
		final ILayerDim[] layoutDims = this.childDims[layout];
		for (int layout2 = 0; layout2 < layoutDims.length; layout2++) {
			underlyingLayers.add(layoutDims[layout2].getLayer());
		}
		
		return underlyingLayers;
	}
	
	
	@Override
	public int getSize() {
		int size = 0;
		for (int layout = 0; layout < this.childDims.length; layout++) {
			size += this.childDims[layout][0].getSize();
		}
		return size;
	}
	
	@Override
	public int getPreferredSize() {
		int size = 0;
		for (int layout = 0; layout < this.childDims.length; layout++) {
			size += this.childDims[layout][0].getPreferredSize();
		}
		return size;
	}
	
	@Override
	public int getPositionByPixel(final int pixel) {
		final int layout = getLayoutByPixel(pixel);
		if (layout < 0) {
			throw new IndexOutOfBoundsException("pixel: " + pixel); //$NON-NLS-1$
		}
		
		final int childPosition = this.childDims[layout][0].getPositionByPixel(
				pixel - getLayoutStart(layout) );
		return getLayoutPosition(layout) + childPosition;
	}
	
	@Override
	public int getPositionStart(final int refPosition, final int position) {
		final int layout = getLayoutByPosition(refPosition);
		if (layout < 0) {
			throw new IndexOutOfBoundsException("refPosition: " + refPosition); //$NON-NLS-1$
		}
		
		final int layoutPosition = getLayoutPosition(layout);
		final int childStart = this.childDims[layout][0].getPositionStart(
				refPosition - layoutPosition, position - layoutPosition );
		return getLayoutStart(layout) + childStart;
	}
	
	@Override
	public int getPositionSize(final int refPosition, final int position) {
		final int layout = getLayoutByPosition(refPosition);
		if (layout < 0) {
			throw new IndexOutOfBoundsException("refPosition: " + refPosition); //$NON-NLS-1$
		}
		
		final int layoutPosition = getLayoutPosition(layout);
		return this.childDims[layout][0].getPositionSize(
				refPosition - layoutPosition, position - layoutPosition );
	}
	
	@Override
	public boolean isPositionResizable(final int position) {
		final int layout = getLayoutByPosition(position);
		if (layout < 0) {
			return false;
		}
		
		final int layoutPosition = getLayoutPosition(layout);
		return this.childDims[layout][0].isPositionResizable(
				position - layoutPosition );
	}
	
}
