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


public class VerticalLayerDim<T extends ILayer> implements ILayerDim {
	
	
	private final T layer;
	
	
	public VerticalLayerDim(final T layer) {
		if (layer == null) {
			throw new NullPointerException("layer"); //$NON-NLS-1$
		}
		this.layer = layer;
	}
	
	
	@Override
	public T getLayer() {
		return this.layer;
	}
	
	public Orientation getOrientation() {
		return Orientation.VERTICAL;
	}
	
	
	@Override
	public int getPositionIndex(final int refPosition, final int position) {
		return this.layer.getRowIndexByPosition(position);
	}
	
	
	@Override
	public int getPositionCount() {
		return this.layer.getRowCount();
	}
	
	@Override
	public int getPreferredPositionCount() {
		return this.layer.getPreferredRowCount();
	}
	
	@Override
	public int localToUnderlyingPosition(final int refPosition, final int position) {
		return this.layer.localToUnderlyingRowPosition(position);
	}
	
	@Override
	public int underlyingToLocalPosition(final int refPosition,
			final int underlyingPosition) {
		final Collection<ILayer> underlyingLayers = getUnderlyingLayersByPosition(refPosition);
		if (underlyingLayers != null) {
			for (final ILayer underlyingLayer : underlyingLayers) {
				final int position = underlyingToLocalPosition(underlyingLayer, underlyingPosition);
				if (position != Integer.MIN_VALUE) {
					return position;
				}
			}
		}
		return Integer.MIN_VALUE;
	}
	
	@Override
	public int underlyingToLocalPosition(final ILayer sourceUnderlyingLayer,
			final int underlyingPosition) {
		return this.layer.underlyingToLocalRowPosition(sourceUnderlyingLayer, underlyingPosition);
	}
	
	@Override
	public Collection<Range> underlyingToLocalPositions(final ILayer sourceUnderlyingLayer,
			final Collection<Range> underlyingPositionRanges) {
		return this.layer.underlyingToLocalRowPositions(sourceUnderlyingLayer, underlyingPositionRanges);
	}
	
	@Override
	public Collection<ILayer> getUnderlyingLayersByPosition(final int position) {
		return this.layer.getUnderlyingLayersByRowPosition(position);
	}
	
	
	@Override
	public int getSize() {
		return this.layer.getHeight();
	}
	
	@Override
	public int getPreferredSize() {
		return this.layer.getPreferredHeight();
	}
	
	@Override
	public int getPositionByPixel(final int pixel) {
		return this.layer.getRowPositionByY(pixel);
	}
	
	@Override
	public int getPositionStart(final int refPosition, final int position) {
		return this.layer.getStartYOfRowPosition(position);
	}
	
	@Override
	public int getPositionSize(final int refPosition, final int position) {
		return this.layer.getRowHeightByPosition(position);
	}
	
	@Override
	public boolean isPositionResizable(final int position) {
		return this.layer.isRowPositionResizable(position);
	}
	
}
