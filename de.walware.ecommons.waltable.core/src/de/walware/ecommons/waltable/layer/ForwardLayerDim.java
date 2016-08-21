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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.LRangeList;
import de.walware.ecommons.waltable.coordinate.Orientation;


/**
 * Dim implementation for {@link TransformLayer}.
 *
 * @param <T> the type of the layer
 */
public class ForwardLayerDim<T extends ILayer> extends AbstractLayerDim<T> {
	
	
	protected final ILayerDim underlyingDim;
	
	
	public ForwardLayerDim(/*@NonNull*/ final T layer, /*@NonNull*/ final ILayerDim underlyingDim) {
		this(layer, underlyingDim.getOrientation(), underlyingDim);
	}
	
	public ForwardLayerDim(/*@NonNull*/ final T layer, /*@NonNull*/ final Orientation orientation,
			/*@NonNull*/ final ILayerDim underlyingDim) {
		super(layer, orientation);
		if (underlyingDim == null) {
			throw new NullPointerException("underlyingDim"); //$NON-NLS-1$
		}
		this.underlyingDim= underlyingDim;
	}
	
	
	@Override
	public long getPositionId(final long refPosition, final long position) {
		return this.underlyingDim.getPositionId(refPosition, position);
	}
	
	@Override
	public long getPositionById(final long id) {
		return this.underlyingDim.getPositionById(id);
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
	public long underlyingToLocalPosition(final ILayerDim sourceUnderlyingDim,
			final long underlyingPosition) {
		if (sourceUnderlyingDim != this.underlyingDim) {
			throw new IllegalArgumentException("underlyingLayer"); //$NON-NLS-1$
		}
		
		return underlyingPosition;
	}
	
	@Override
	public List<LRange> underlyingToLocalPositions(final ILayerDim sourceUnderlyingDim,
			final Collection<LRange> underlyingPositions) {
		if (sourceUnderlyingDim != this.underlyingDim) {
			throw new IllegalArgumentException("underlyingLayer"); //$NON-NLS-1$
		}
		
		return LRangeList.toRangeList(underlyingPositions);
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
		return this.underlyingDim.getPositionByPixel(pixel);
	}
	
	@Override
	public long getPositionStart(final long refPosition, final long position) {
		return this.underlyingDim.getPositionStart(refPosition, position);
	}
	
	@Override
	public long getPositionStart(final long position) {
		return this.underlyingDim.getPositionStart(position);
	}
	
	@Override
	public int getPositionSize(final long refPosition, final long position) {
		return this.underlyingDim.getPositionSize(refPosition, position);
	}
	
	@Override
	public int getPositionSize(final long position) {
		return this.underlyingDim.getPositionSize(position);
	}
	
	@Override
	public boolean isPositionResizable(final long position) {
		return this.underlyingDim.isPositionResizable(position);
	}
	
}
