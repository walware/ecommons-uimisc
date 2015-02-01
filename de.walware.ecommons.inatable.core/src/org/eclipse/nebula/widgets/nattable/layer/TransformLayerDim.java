/*******************************************************************************
 * Copyright (c) 2013-2015 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.layer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;


/**
 * Dim implementation for {@link TransformLayer}.
 *
 * @param <T> the type of the layer
 */
public class TransformLayerDim<T extends ILayer> extends AbstractLayerDim<T> {
	
	
	protected final ILayerDim underlyingDim;
	
	
	public TransformLayerDim(/*@NonNull*/ final T layer, /*@NonNull*/ final ILayerDim underlyingDim) {
		this(layer, underlyingDim.getOrientation(), underlyingDim);
	}
	
	public TransformLayerDim(/*@NonNull*/ final T layer, /*@NonNull*/ final Orientation orientation,
			/*@NonNull*/ final ILayerDim underlyingDim) {
		super(layer, orientation);
		if (underlyingDim == null) {
			throw new NullPointerException("underlyingDim"); //$NON-NLS-1$
		}
		this.underlyingDim = underlyingDim;
	}
	
	
	@Override
	public long getPositionIndex(final long refPosition, final long position) {
		final long underlyingRefPosition = localToUnderlyingPosition(refPosition, refPosition);
		return this.underlyingDim.getPositionIndex(underlyingRefPosition,
				(refPosition == position) ?
						underlyingRefPosition :
						localToUnderlyingPosition(refPosition, position) );
	}
	
	
	@Override
	public long getPositionCount() {
		return this.underlyingDim.getPositionCount();
	}
	
	@Override
	public long localToUnderlyingPosition(final long refPosition, final long position) {
		return position;
	}
	
	@Override
	public long underlyingToLocalPosition(final long refPosition,
			final long underlyingPosition) {
		
		return underlyingPosition;
	}
	
	@Override
	public long underlyingToLocalPosition(final ILayerDim sourceUnderlyingDim,
			final long underlyingPosition) {
		if (sourceUnderlyingDim != this.underlyingDim) {
			throw new IllegalArgumentException("underlyingLayer"); //$NON-NLS-1$
		}
		
		return underlyingPosition;
	}
	
	@Override
	public List<Range> underlyingToLocalPositions(final ILayerDim sourceUnderlyingDim,
			final Collection<Range> underlyingPositions) {
		final List<Range> localPositions = new ArrayList<Range>(underlyingPositions.size());
		
		for (final Range underlyingPositionRange : underlyingPositions) {
			if (underlyingPositionRange.start == underlyingPositionRange.end) {
				final long position = underlyingToLocalPosition(sourceUnderlyingDim, underlyingPositionRange.start);
				localPositions.add(new Range(position, position));
			}
			else {
				final long first = underlyingToLocalPosition(sourceUnderlyingDim, underlyingPositionRange.start);
				final long last = underlyingToLocalPosition(sourceUnderlyingDim, underlyingPositionRange.end - 1);
				if (first <= last) {
					localPositions.add(new Range(first, last + 1));
				}
			}
		}
		return localPositions;
	}
	
	@Override
	public List<ILayerDim> getUnderlyingDimsByPosition(final long position) {
		return Collections.singletonList(this.underlyingDim);
	}
	
	
	@Override
	public long getSize() {
		return this.underlyingDim.getSize();
	}
	
	@Override
	public long getPreferredSize() {
		return this.underlyingDim.getPreferredSize();
	}
	
	@Override
	public long getPositionByPixel(final long pixel) {
		return underlyingToLocalPosition(this.underlyingDim,
				this.underlyingDim.getPositionByPixel(pixel) );
	}
	
	@Override
	public long getPositionStart(final long refPosition, final long position) {
		final long underlyingRefPosition = localToUnderlyingPosition(refPosition, refPosition);
		return this.underlyingDim.getPositionStart(underlyingRefPosition,
				(refPosition == position) ?
						underlyingRefPosition : 
						localToUnderlyingPosition(refPosition, position) );
	}
	
	@Override
	public int getPositionSize(final long refPosition, final long position) {
		final long underlyingRefPosition = localToUnderlyingPosition(refPosition, refPosition);
		return this.underlyingDim.getPositionSize(underlyingRefPosition,
				(refPosition == position) ?
						underlyingRefPosition : 
						localToUnderlyingPosition(refPosition, position) );
	}
	
	@Override
	public boolean isPositionResizable(final long position) {
		final long underlyingPosition = localToUnderlyingPosition(position, position);
		return this.underlyingDim.isPositionResizable(underlyingPosition);
	}
	
}
