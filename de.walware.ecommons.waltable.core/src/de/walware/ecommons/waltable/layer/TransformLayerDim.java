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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.coordinate.PixelOutOfBoundsException;


/**
 * Dim implementation for {@link TransformLayer}.
 *
 * @param <T> the type of the layer
 */
public abstract class TransformLayerDim<T extends ILayer> extends ForwardLayerDim<T> {
	
	
	public TransformLayerDim(/*@NonNull*/ final T layer, /*@NonNull*/ final ILayerDim underlyingDim) {
		super(layer, underlyingDim.getOrientation(), underlyingDim);
	}
	
	public TransformLayerDim(/*@NonNull*/ final T layer, /*@NonNull*/ final Orientation orientation,
			/*@NonNull*/ final ILayerDim underlyingDim) {
		super(layer, orientation, underlyingDim);
	}
	
	
	@Override
	public long getPositionId(final long refPosition, final long position) {
		final long underlyingRefPosition= localToUnderlyingPosition(refPosition, refPosition);
		return this.underlyingDim.getPositionId(underlyingRefPosition,
				(refPosition == position) ?
						underlyingRefPosition :
						localToUnderlyingPosition(refPosition, position) );
	}
	
	@Override
	public long getPositionById(final long id) {
		final long underlyingPosition= this.underlyingDim.getPositionById(id);
		if (underlyingPosition == POSITION_NA) {
			return POSITION_NA;
		}
		final long position= doUnderlyingToLocalPosition(underlyingPosition);
		if (position < 0 || position >= getPositionCount()) {
			return POSITION_NA;
		}
		return position;
	}
	
	
	@Override
	public abstract long localToUnderlyingPosition(final long refPosition, final long position);
	
	@Override
	public long underlyingToLocalPosition(final ILayerDim sourceUnderlyingDim,
			final long underlyingPosition) {
		if (sourceUnderlyingDim != this.underlyingDim) {
			throw new IllegalArgumentException("underlyingLayer"); //$NON-NLS-1$
		}
		
		return doUnderlyingToLocalPosition(underlyingPosition);
	}
	
	@Override
	public List<LRange> underlyingToLocalPositions(final ILayerDim sourceUnderlyingDim,
			final Collection<LRange> underlyingPositions) {
		if (sourceUnderlyingDim != this.underlyingDim) {
			throw new IllegalArgumentException("underlyingLayer"); //$NON-NLS-1$
		}
		
		final List<LRange> localPositions= new ArrayList<>(underlyingPositions.size());
		
		for (final LRange underlyingPositionRange : underlyingPositions) {
			if (underlyingPositionRange.start == underlyingPositionRange.end) {
				final long position= doUnderlyingToLocalPosition(underlyingPositionRange.start);
				localPositions.add(new LRange(position, position));
			}
			else {
				final long first= doUnderlyingToLocalPosition(underlyingPositionRange.start);
				final long last= doUnderlyingToLocalPosition(underlyingPositionRange.end - 1);
				if (first <= last) {
					localPositions.add(new LRange(first, last + 1));
				}
			}
		}
		return localPositions;
	}
	
	protected abstract long doUnderlyingToLocalPosition(final long underlyingPosition);
	
	
	@Override
	public long getPositionByPixel(final long pixel) {
		final long position= doUnderlyingToLocalPosition(this.underlyingDim.getPositionByPixel(pixel));
		if (position < 0 || position >= getPositionCount()) {
			throw PixelOutOfBoundsException.pixel(pixel, getOrientation());
		}
		return position;
	}
	
	@Override
	public long getPositionStart(final long refPosition, final long position) {
		final long underlyingRefPosition= localToUnderlyingPosition(refPosition, refPosition);
		return this.underlyingDim.getPositionStart(underlyingRefPosition,
				(refPosition == position) ?
						underlyingRefPosition : 
						localToUnderlyingPosition(refPosition, position) );
	}
	
	@Override
	public long getPositionStart(final long position) {
		return this.underlyingDim.getPositionStart(
				localToUnderlyingPosition(position, position) );
	}
	
	@Override
	public int getPositionSize(final long refPosition, final long position) {
		final long underlyingRefPosition= localToUnderlyingPosition(refPosition, refPosition);
		return this.underlyingDim.getPositionSize(underlyingRefPosition,
				(refPosition == position) ?
						underlyingRefPosition : 
						localToUnderlyingPosition(refPosition, position) );
	}
	
	@Override
	public int getPositionSize(final long position) {
		return this.underlyingDim.getPositionSize(
				localToUnderlyingPosition(position, position) );
	}
	
	@Override
	public boolean isPositionResizable(final long position) {
		final long underlyingPosition= localToUnderlyingPosition(position, position);
		return this.underlyingDim.isPositionResizable(underlyingPosition);
	}
	
}
