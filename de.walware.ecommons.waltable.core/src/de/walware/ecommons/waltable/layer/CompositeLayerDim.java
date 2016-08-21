/*******************************************************************************
 * Copyright (c) 2013-2016 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package de.walware.ecommons.waltable.layer;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.coordinate.PixelOutOfBoundsException;
import de.walware.ecommons.waltable.coordinate.PositionOutOfBoundsException;


public class CompositeLayerDim extends AbstractLayerDim<CompositeLayer> {
	
	
	public static class IgnoreRef extends CompositeLayerDim {
		
		
		public IgnoreRef(final CompositeLayer layer, final Orientation orientation) {
			super(layer, orientation);
		}
		
		
		@Override
		public long getPositionId(final long refPosition, final long position) {
			return super.getPositionId(position, position);
		}
		
		
		@Override
		public long localToUnderlyingPosition(final long refPosition, final long position) {
			return super.localToUnderlyingPosition(position, position);
		}
		
		
		@Override
		public long getPositionStart(final long refPosition, final long position) {
			return super.getPositionStart(position);
		}
		
		@Override
		public int getPositionSize(final long refPosition, final long position) {
			return super.getPositionSize(position);
		}
		
	}
	
	
	private final ILayerDim[][] childDims;
	
	
	public CompositeLayerDim(final CompositeLayer layer, final Orientation orientation) {
		super(layer, orientation);
		
		this.childDims= (orientation == HORIZONTAL) ?
				new ILayerDim[layer.layoutXCount][layer.layoutYCount] :
				new ILayerDim[layer.layoutYCount][layer.layoutXCount];
	}
	
	
	@Override
	public CompositeLayer getLayer() {
		return this.layer;
	}
	
	
	void updateChild(final int layout, final int layout2, final ILayer childLayer) {
		this.childDims[layout][layout2]= childLayer.getDim(this.orientation);
	}
	
	
	protected final int getLayoutByPosition(final long position) {
		if (position >= 0) {
			for (int layout= 0, offset= 0; layout < this.childDims.length; layout++) {
				if (/*position >= offset & */
						position < (offset+= this.childDims[layout][0].getPositionCount()) ) {
					return layout;
				}
			}
		}
		return -1;
	}
	
	protected final int getLayoutByPixel(final long pixel) {
		if (pixel >= 0) {
			for (int layout= 0, offset= 0; layout < this.childDims.length; layout++) {
				if (/*x >= offset & */ pixel < (offset+= this.childDims[layout][0].getSize())) {
					return layout;
				}
			}
		}
		return -1;
	}
	
	protected final int getLayoutByDim(final ILayerDim childDim) {
		for (int layout= 0; layout < this.childDims.length; layout++) {
			final ILayerDim[] layoutDims= this.childDims[layout];
			for (int layout2= 0; layout2 < layoutDims.length; layout2++) {
				if (layoutDims[layout2] == childDim) {
					return layout;
				}
			}
		}
		return -1;
	}
	
	protected final long getLayoutPosition(final int layout) {
		long offset= 0;
		for (int i= 0; i < layout; i++) {
			offset+= this.childDims[i][0].getPositionCount();
		}
		return offset;
	}
	
	protected final long getLayoutStart(final int layout) {
		long start= 0;
		for (int i= 0; i < layout; i++) {
			start+= this.childDims[i][0].getSize();
		}
		return start;
	}
	
	
	@Override
	public long getPositionId(final long refPosition, final long position) {
		final int layout= getLayoutByPosition(refPosition);
		if (layout < 0) {
			throw PositionOutOfBoundsException.refPosition(refPosition, getOrientation());
		}
		
		final long layoutPosition= getLayoutPosition(layout);
		return this.childDims[layout][0].getPositionId(
				refPosition - layoutPosition,
				position - layoutPosition );
	}
	
	@Override
	public long getPositionById(final long id) {
		for (int i= 0; i < this.childDims.length; i++) {
			final long underlyingPosition= this.childDims[i][0].getPositionById(id);
			if (underlyingPosition >= 0 && underlyingPosition < this.childDims[i][0].getPositionCount()) {
				final long layoutPosition= getLayoutPosition(i);
				return layoutPosition + underlyingPosition;
			}
		}
		
		return POSITION_NA;
	}
	
	
	@Override
	public long getPositionCount() {
		long count= 0;
		for (int layout= 0; layout < this.childDims.length; layout++) {
			count+= this.childDims[layout][0].getPositionCount();
		}
		return count;
	}
	
	@Override
	public long localToUnderlyingPosition(final long refPosition, final long position) {
		final int layout= getLayoutByPosition(refPosition);
		if (layout < 0) {
			throw PositionOutOfBoundsException.refPosition(refPosition, getOrientation());
		}
		
		return position - getLayoutPosition(layout);
	}
	
	public long underlyingToLocalPosition(final long refPosition,
			final long underlyingPosition) {
		final int layout= getLayoutByPosition(refPosition);
		if (layout < 0) {
			throw PositionOutOfBoundsException.refPosition(refPosition, getOrientation());
		}
		
		final long layoutPosition= getLayoutPosition(layout);
		return layoutPosition + underlyingPosition;
	}
	
	@Override
	public long underlyingToLocalPosition(final ILayerDim sourceUnderlyingDim,
			final long underlyingPosition) {
		final int layout= getLayoutByDim(sourceUnderlyingDim);
		if (layout < 0) {
			throw new IllegalArgumentException("underlyingLayer"); //$NON-NLS-1$
		}
		
		final long layoutPosition= getLayoutPosition(layout);
		return layoutPosition + underlyingPosition;
	}
	
	@Override
	public List<LRange> underlyingToLocalPositions(final ILayerDim sourceUnderlyingDim,
			final Collection<LRange> underlyingPositionRanges) {
		final int layout= getLayoutByDim(sourceUnderlyingDim);
		if (layout < 0) {
			throw new IllegalArgumentException("underlyingLayer"); //$NON-NLS-1$
		}
		
		final List<LRange> localPositionRanges= new ArrayList<>();
		
		final long layoutPosition= getLayoutPosition(layout);
		for (final LRange underlyingPositionRange : underlyingPositionRanges) {
			localPositionRanges.add(new LRange(
					layoutPosition + underlyingPositionRange.start,
					layoutPosition + underlyingPositionRange.end ));
		}
		
		return localPositionRanges;
	}
	
	@Override
	public List<ILayerDim> getUnderlyingDimsByPosition(final long position) {
		final int layout= getLayoutByPosition(position);
		if (layout < 0) {
			return null;
		}
		
		final List<ILayerDim> underlyingDims= new ArrayList<>(this.childDims.length);
		
		final ILayerDim[] layoutDims= this.childDims[layout];
		for (int layout2= 0; layout2 < layoutDims.length; layout2++) {
			underlyingDims.add(layoutDims[layout2]);
		}
		
		return underlyingDims;
	}
	
	
	@Override
	public long getSize() {
		long size= 0;
		for (int layout= 0; layout < this.childDims.length; layout++) {
			size+= this.childDims[layout][0].getSize();
		}
		return size;
	}
	
	@Override
	public long getPreferredSize() {
		long size= 0;
		for (int layout= 0; layout < this.childDims.length; layout++) {
			size+= this.childDims[layout][0].getPreferredSize();
		}
		return size;
	}
	
	@Override
	public long getPositionByPixel(final long pixel) {
		final int layout= getLayoutByPixel(pixel);
		if (layout < 0) {
			throw PixelOutOfBoundsException.pixel(pixel, getOrientation());
		}
		
		final long childPosition= this.childDims[layout][0].getPositionByPixel(
				pixel - getLayoutStart(layout) );
		return getLayoutPosition(layout) + childPosition;
	}
	
	@Override
	public long getPositionStart(final long refPosition, final long position) {
		final int layout= getLayoutByPosition(refPosition);
		if (layout < 0) {
			throw PositionOutOfBoundsException.refPosition(refPosition, getOrientation());
		}
		
		final long layoutPosition= getLayoutPosition(layout);
		return getLayoutStart(layout) + this.childDims[layout][0].getPositionStart(
				refPosition - layoutPosition, position - layoutPosition );
	}
	
	@Override
	public long getPositionStart(final long position) {
		final int layout= getLayoutByPosition(position);
		if (layout < 0) {
			throw PositionOutOfBoundsException.position(position, getOrientation());
		}
		
		final long layoutPosition= getLayoutPosition(layout);
		return getLayoutStart(layout) + this.childDims[layout][0].getPositionStart(
				position - layoutPosition );
	}
	
	@Override
	public int getPositionSize(final long refPosition, final long position) {
		final int layout= getLayoutByPosition(refPosition);
		if (layout < 0) {
			throw PositionOutOfBoundsException.refPosition(refPosition, getOrientation());
		}
		
		final long layoutPosition= getLayoutPosition(layout);
		return this.childDims[layout][0].getPositionSize(
				refPosition - layoutPosition, position - layoutPosition );
	}
	
	@Override
	public int getPositionSize(final long position) {
		final int layout= getLayoutByPosition(position);
		if (layout < 0) {
			throw PositionOutOfBoundsException.position(position, getOrientation());
		}
		
		final long layoutPosition= getLayoutPosition(layout);
		return this.childDims[layout][0].getPositionSize(
				position - layoutPosition );
	}
	
	@Override
	public boolean isPositionResizable(final long position) {
		final int layout= getLayoutByPosition(position);
		if (layout < 0) {
			return false;
		}
		
		final long layoutPosition= getLayoutPosition(layout);
		return this.childDims[layout][0].isPositionResizable(
				position - layoutPosition );
	}
	
}
