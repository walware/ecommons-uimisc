/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/

package de.walware.ecommons.waltable.layer;

import java.util.Collection;
import java.util.List;

import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.coordinate.PixelOutOfBoundsException;
import de.walware.ecommons.waltable.coordinate.PositionId;
import de.walware.ecommons.waltable.coordinate.PositionOutOfBoundsException;


public abstract class DataDim<TLayer extends ILayer> extends AbstractLayerDim<TLayer> {
	
	
	private final long idCat;
	
	
	public DataDim(final TLayer layer, final Orientation orientation,
			final long idCat) {
		super(layer, orientation);
		
		this.idCat= idCat;
	}
	
	
	@Override
	public long getPositionId(final long refPosition, final long position) {
		if (position < 0 || position >= getPositionCount()) {
			throw PositionOutOfBoundsException.position(position, getOrientation());
		}
		return (this.idCat | position);
	}
	
	@Override
	public long getPositionById(final long id) {
		if ((id & PositionId.CAT_MASK) == this.idCat) {
			final long position= (id & PositionId.NUM_MASK);
			if (position < getPositionCount()) {
				return position;
			}
		}
		return POSITION_NA;
	}
	
	
	@Override
	public long localToUnderlyingPosition(final long refPosition, final long position) {
		return position;
	}
	
	@Override
	public long underlyingToLocalPosition(final ILayerDim sourceUnderlyingDim, final long underlyingPosition) {
		return underlyingPosition;
	}
	
	@Override
	public List<LRange> underlyingToLocalPositions(final ILayerDim sourceUnderlyingDim,
			final Collection<LRange> underlyingPositions) {
		return null;
	}
	
	@Override
	public List<ILayerDim> getUnderlyingDimsByPosition(final long position) {
		return null;
	}
	
	
	@Override
	public long getPreferredSize() {
		return getSize();
	}
	
	@Override
	public long getPositionByPixel(final long pixel) {
		long startPixel= 0;
		long endPixel= getSize();
		
		if (pixel < startPixel || pixel >= endPixel) {
			throw PixelOutOfBoundsException.pixel(pixel, getOrientation());
		}
		
		long startPosition= 0;
		long endPosition= getPositionCount();
		
		while (true) {
			final double size= (double) (endPixel - startPixel) / (endPosition - startPosition);
			final long position= startPosition + (long) ((pixel - startPixel) / size);
			
			final long start= getPositionStart(position);
			final long end= start + getPositionSize(position);
			if (pixel < start) {
				endPosition= position;
				endPixel= start;
			}
			else if (pixel >= end) {
				startPosition= position + 1;
				startPixel= end;
			}
			else {
				return position;
			}
		}
	}
	
	@Override
	public long getPositionStart(final long refPosition, final long position) {
		return getPositionStart(position);
	}
	
	@Override
	public int getPositionSize(final long refPosition, final long position) {
		return getPositionSize(position);
	}
	
}
