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
package de.walware.ecommons.waltable.freeze;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.coordinate.PositionCoordinate;
import de.walware.ecommons.waltable.coordinate.PositionOutOfBoundsException;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.layer.TransformLayer;
import de.walware.ecommons.waltable.layer.TransformLayerDim;


public class FreezeLayer extends TransformLayer {
	
	
	public static final String PERSISTENCE_TOP_LEFT_POSITION= ".freezeTopLeftPosition"; //$NON-NLS-1$
	public static final String PERSISTENCE_BOTTOM_RIGHT_POSITION= ".freezeBottomRightPosition"; //$NON-NLS-1$
	
	
	static class Dim extends TransformLayerDim<FreezeLayer> {
		
		
		private long startPosition;
		private long endPosition;
		
		
		public Dim(final FreezeLayer layer, final ILayerDim underlyingDim) {
			super(layer, underlyingDim);
		}
		
		
		public void setFreeze(final long start, final long end) {
			if (start < 0 || start > end) {
				throw new IllegalArgumentException();
			}
			this.startPosition= start;
			this.endPosition= end;
		}
		
		public long getStartPosition() {
			return this.startPosition;
		}
		
		public long getEndPosition() {
			return this.endPosition;
		}
		
		private long getStartPixel() {
			return this.underlyingDim.getPositionStart(this.startPosition);
		}
		
		
		@Override
		public long getPositionCount() {
			return this.endPosition - this.startPosition;
		}
		
		@Override
		public long localToUnderlyingPosition(final long refPosition, final long position) {
			if (refPosition < 0 || refPosition >= getPositionCount()) {
				throw PositionOutOfBoundsException.refPosition(refPosition, getOrientation()); 
			}
			
			return this.startPosition + position;
		}
		
		@Override
		protected long doUnderlyingToLocalPosition(final long underlyingPosition) {
			return underlyingPosition - this.startPosition;
		}
		
		
		@Override
		public long getSize() {
			long size= 0;
			for (long position= this.startPosition; position < this.endPosition; position++) {
				size+= this.underlyingDim.getPositionSize(position, position);
			}
			return size;
		}
		
		@Override
		public long getPreferredSize() {
			return getSize();
		}
		
		@Override
		public long getPositionByPixel(final long pixel) {
			final long underlyingPosition= this.underlyingDim.getPositionByPixel(getStartPixel() + pixel);
			return underlyingToLocalPosition(this.underlyingDim, underlyingPosition);
		}
		
		@Override
		public long getPositionStart(final long refPosition, final long position) {
			return super.getPositionStart(refPosition, position) - getStartPixel();
		}
		
		@Override
		public long getPositionStart(final long position) {
			return super.getPositionStart(position) - getStartPixel();
		}
		
	}
	
	
	public FreezeLayer(final ILayer underlyingLayer) {
		super(underlyingLayer);
		
		registerEventHandler(new FreezeEventHandler(this));
	}
	
	
	@Override
	protected void initDims() {
		final ILayer underlying= getUnderlyingLayer();
		if (underlying == null) {
			return;
		}
		setDim(new Dim(this, underlying.getDim(HORIZONTAL)));
		setDim(new Dim(this, underlying.getDim(VERTICAL)));
	}
	
	final Dim get(final Orientation orientation) {
		return (Dim) getDim(orientation);
	}
	
	
	public boolean isFrozen() {
		return getColumnCount() > 0 || getRowCount() > 0;
	}
	
	public PositionCoordinate getTopLeftPosition() {
		return new PositionCoordinate(this,
				get(HORIZONTAL).startPosition,
				get(VERTICAL).startPosition );
	}
	
	public PositionCoordinate getBottomRightPosition() {
		return new PositionCoordinate(this,
				get(HORIZONTAL).endPosition - 1,
				get(VERTICAL).endPosition - 1 );
	}
	
	public void setFreeze(final long leftColumnPosition, final long topRowPosition,
			final long rightColumnPosition, final long bottomRowPosition) {
		if (rightColumnPosition < leftColumnPosition || bottomRowPosition < topRowPosition) {
			throw new IllegalArgumentException();
		}
		if (leftColumnPosition < 0) {
			get(HORIZONTAL).setFreeze(0, 0);
		}
		else {
			get(HORIZONTAL).setFreeze(leftColumnPosition, rightColumnPosition + 1);
		}
		if (topRowPosition < 0) {
			get(VERTICAL).setFreeze(0, 0);
		}
		else {
			get(VERTICAL).setFreeze(topRowPosition, bottomRowPosition + 1);
		}
	}
	
}
