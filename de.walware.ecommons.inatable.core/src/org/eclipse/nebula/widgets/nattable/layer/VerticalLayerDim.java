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

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.coordinate.Range;


/**
 * Implementation of vertical layer dimension which forwards all method calls to the
 * corresponding "row" methods in the layer.
 */
public class VerticalLayerDim extends AbstractLayerDim<ILayer> {
	
	
	public VerticalLayerDim(/*@NonNull*/ final ILayer layer) {
		super(layer, VERTICAL);
	}
	
	
	@Override
	public long getPositionIndex(final long refPosition, final long position) {
		return this.layer.getRowIndexByPosition(position);
	}
	
	
	@Override
	public long getPositionCount() {
		return this.layer.getRowCount();
	}
	
	@Override
	public long localToUnderlyingPosition(final long refPosition, final long position) {
		return this.layer.localToUnderlyingRowPosition(position);
	}
	
	@Override
	public long underlyingToLocalPosition(final long refPosition,
			final long underlyingPosition) {
		final List<ILayerDim> underlyingDims = getUnderlyingDimsByPosition(refPosition);
		if (underlyingDims != null) {
			for (final ILayerDim underlyingDim : underlyingDims) {
				final long position = underlyingToLocalPosition(underlyingDim, underlyingPosition);
				if (position != Long.MIN_VALUE) {
					return position;
				}
			}
		}
		return Long.MIN_VALUE;
	}
	
	@Override
	public long underlyingToLocalPosition(final ILayerDim sourceUnderlyingDim,
			final long underlyingPosition) {
		return this.layer.underlyingToLocalRowPosition(sourceUnderlyingDim.getLayer(),
				underlyingPosition );
	}
	
	@Override
	public List<Range> underlyingToLocalPositions(final ILayerDim sourceUnderlyingDim,
			final Collection<Range> underlyingPositions) {
		final Collection<Range> localPositions = this.layer.underlyingToLocalRowPositions(
				sourceUnderlyingDim.getLayer(), underlyingPositions );
		return (localPositions instanceof List) ?
				(List<Range>) localPositions :
				new ArrayList<Range>(localPositions);
	}
	
	@Override
	public List<ILayerDim> getUnderlyingDimsByPosition(final long position) {
		final Collection<ILayer> underlyingLayers = this.layer.getUnderlyingLayersByRowPosition(
				position );
		if (underlyingLayers == null) {
			return null;
		}
		
		final List<ILayerDim> underlyingDims = new ArrayList<ILayerDim>(underlyingLayers.size());
		for (final ILayer underlyingLayer : underlyingLayers) {
			underlyingDims.add(underlyingLayer.getDim(this.orientation));
		}
		return underlyingDims;
	}
	
	
	@Override
	public long getSize() {
		return this.layer.getHeight();
	}
	
	@Override
	public long getPreferredSize() {
		return this.layer.getPreferredHeight();
	}
	
	@Override
	public long getPositionByPixel(final long pixel) {
		return this.layer.getRowPositionByY(pixel);
	}
	
	@Override
	public long getPositionStart(final long refPosition, final long position) {
		return this.layer.getStartYOfRowPosition(position);
	}
	
	@Override
	public int getPositionSize(final long refPosition, final long position) {
		return this.layer.getRowHeightByPosition(position);
	}
	
	@Override
	public boolean isPositionResizable(final long position) {
		return this.layer.isRowPositionResizable(position);
	}
	
}
