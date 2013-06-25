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

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;


public class HorizontalLayerDim<T extends ILayer> implements ILayerDim {
	
	
	private final T layer;
	
	
	public HorizontalLayerDim(final T layer) {
		if (layer == null) {
			throw new NullPointerException("layer"); //$NON-NLS-1$
		}
		this.layer = layer;
	}
	
	
	@Override
	public T getLayer() {
		return this.layer;
	}
	
	@Override
	public Orientation getOrientation() {
		return Orientation.HORIZONTAL;
	}
	
	
	@Override
	public long getPositionIndex(final long refPosition, final long position) {
		return this.layer.getColumnIndexByPosition(position);
	}
	
	
	@Override
	public long getPositionCount() {
		return this.layer.getColumnCount();
	}
	
	@Override
	public long getPreferredPositionCount() {
		return this.layer.getPreferredColumnCount();
	}
	
	@Override
	public long localToUnderlyingPosition(final long refPosition, final long position) {
		return this.layer.localToUnderlyingColumnPosition(position);
	}
	
	@Override
	public long underlyingToLocalPosition(final long refPosition,
			final long underlyingPosition) {
		final Collection<ILayer> underlyingLayers = getUnderlyingLayersByPosition(refPosition);
		if (underlyingLayers != null) {
			for (final ILayer underlyingLayer : underlyingLayers) {
				final long position = underlyingToLocalPosition(underlyingLayer, underlyingPosition);
				if (position != Long.MIN_VALUE) {
					return position;
				}
			}
		}
		return Long.MIN_VALUE;
	}
	
	@Override
	public long underlyingToLocalPosition(final ILayer sourceUnderlyingLayer,
			final long underlyingPosition) {
		return this.layer.underlyingToLocalColumnPosition(sourceUnderlyingLayer, underlyingPosition);
	}
	
	@Override
	public Collection<Range> underlyingToLocalPositions(final ILayer sourceUnderlyingLayer,
			final Collection<Range> underlyingPositionRanges) {
		return this.layer.underlyingToLocalColumnPositions(sourceUnderlyingLayer, underlyingPositionRanges);
	}
	
	@Override
	public Collection<ILayer> getUnderlyingLayersByPosition(final long position) {
		return this.layer.getUnderlyingLayersByColumnPosition(position);
	}
	
	
	@Override
	public long getSize() {
		return this.layer.getWidth();
	}
	
	@Override
	public long getPreferredSize() {
		return this.layer.getPreferredWidth();
	}
	
	@Override
	public long getPositionByPixel(final long pixel) {
		return this.layer.getColumnPositionByX(pixel);
	}
	
	@Override
	public long getPositionStart(final long refPosition, final long position) {
		return this.layer.getStartXOfColumnPosition(position);
	}
	
	@Override
	public int getPositionSize(final long refPosition, final long position) {
		return this.layer.getColumnWidthByPosition(position);
	}
	
	
	@Override
	public boolean isPositionResizable(final long position) {
		return this.layer.isColumnPositionResizable(position);
	}
	
}
