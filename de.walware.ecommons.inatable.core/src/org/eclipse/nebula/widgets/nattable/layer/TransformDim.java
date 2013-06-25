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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;


/**
 * Dim implementation for {@link AbstractTransformLayer}
 *
 * @param <T>
 */
public class TransformDim<T extends ILayer> implements ILayerDim {
	
	
	protected final T layer;
	
	protected final Orientation orientation;
	
	protected final ILayerDim underlyingDim;
	
	
	public TransformDim(final T layer, final ILayerDim underlyingDim) {
		this(layer, underlyingDim.getOrientation(), underlyingDim);
	}
	
	public TransformDim(final T layer, final Orientation orientation,
			final ILayerDim underlyingDim) {
		if (layer == null) {
			throw new NullPointerException("layer"); //$NON-NLS-1$
		}
		if (orientation == null) {
			throw new NullPointerException("orientation"); //$NON-NLS-1$
		}
		if (underlyingDim == null) {
			throw new NullPointerException("underlyingDim"); //$NON-NLS-1$
		}
		this.layer = layer;
		this.orientation = orientation;
		this.underlyingDim = underlyingDim;
	}
	
	
	@Override
	public T getLayer() {
		return this.layer;
	}
	
	@Override
	public Orientation getOrientation() {
		return this.orientation;
	}
	
	
	@Override
	public int getPositionIndex(final int refPosition, final int position) {
		final int underlyingRefPosition = localToUnderlyingPosition(refPosition, refPosition);
		return this.underlyingDim.getPositionIndex(underlyingRefPosition,
				(refPosition == position) ?
						underlyingRefPosition :
						localToUnderlyingPosition(refPosition, position) );
	}
	
	
	@Override
	public int getPositionCount() {
		return this.underlyingDim.getPositionCount();
	}
	
	@Override
	public int getPreferredPositionCount() {
		return this.underlyingDim.getPreferredPositionCount();
	}
	
	@Override
	public int localToUnderlyingPosition(final int refPosition, final int position) {
		return position;
	}
	
	@Override
	public int underlyingToLocalPosition(final int refPosition,
			final int underlyingPosition) {
		
		return underlyingPosition;
	}
	
	@Override
	public int underlyingToLocalPosition(final ILayer sourceUnderlyingLayer,
			final int underlyingPosition) {
		if (sourceUnderlyingLayer != this.underlyingDim.getLayer()) {
			throw new IllegalArgumentException("underlyingLayer"); //$NON-NLS-1$
		}
		
		return underlyingPosition;
	}
	
	@Override
	public Collection<Range> underlyingToLocalPositions(final ILayer sourceUnderlyingLayer,
			final Collection<Range> underlyingPositionRanges) {
		final Collection<Range> localPositionRanges = new ArrayList<Range>();
		
		for (final Range underlyingPositionRange : underlyingPositionRanges) {
			localPositionRanges.add(new Range(
					underlyingToLocalPosition(sourceUnderlyingLayer, underlyingPositionRange.start),
					underlyingToLocalPosition(sourceUnderlyingLayer, underlyingPositionRange.end) ));
		}
		
		return localPositionRanges;
	}
	
	@Override
	public Collection<ILayer> getUnderlyingLayersByPosition(final int position) {
		return Collections.singletonList(this.underlyingDim.getLayer());
	}
	
	
	@Override
	public int getSize() {
		return this.underlyingDim.getSize();
	}
	
	@Override
	public int getPreferredSize() {
		return this.underlyingDim.getPreferredSize();
	}
	
	@Override
	public int getPositionByPixel(final int pixel) {
		return underlyingToLocalPosition(this.underlyingDim.getLayer(),
				this.underlyingDim.getPositionByPixel(pixel) );
	}
	
	@Override
	public int getPositionStart(final int refPosition, final int position) {
		final int underlyingRefPosition = localToUnderlyingPosition(refPosition, refPosition);
		return this.underlyingDim.getPositionStart(underlyingRefPosition,
				(refPosition == position) ?
						underlyingRefPosition : 
						localToUnderlyingPosition(refPosition, position) );
	}
	
	@Override
	public int getPositionSize(final int refPosition, final int position) {
		final int underlyingRefPosition = localToUnderlyingPosition(refPosition, refPosition);
		return this.underlyingDim.getPositionSize(underlyingRefPosition,
				(refPosition == position) ?
						underlyingRefPosition : 
						localToUnderlyingPosition(refPosition, position) );
	}
	
	@Override
	public boolean isPositionResizable(final int position) {
		return this.underlyingDim.isPositionResizable(
				localToUnderlyingPosition(position, position) );
	}
	
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("LayerDim"); //$NON-NLS-1$
		sb.append(" ").append(this.orientation); //$NON-NLS-1$
		sb.append(" of \n").append(this.layer); //$NON-NLS-1$
		return sb.toString();
	}
	
}
