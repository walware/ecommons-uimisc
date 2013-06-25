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

package org.eclipse.nebula.widgets.nattable.viewport;

import static org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer.PAGE_INTERSECTION_SIZE;

import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;

import org.eclipse.nebula.widgets.nattable.coordinate.SWTUtil;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerDim;
import org.eclipse.nebula.widgets.nattable.layer.TransformDim;


/**
 * Implementation of {@link IViewportDim} for {@link ViewportLayer}.
 */
public class ViewportDim extends TransformDim<ViewportLayer> implements IViewportDim {
	
	
	private long minimumOriginPosition;
	private long minimumOriginPixel;
	
	private long originPixel;
	
	private long cachedClientAreaSize;
	private long cachedPositionCount;
	private long cachedSize;
	
	private ScrollBarHandlerTemplate scrollBarHandler;
	
	
	public ViewportDim(final ViewportLayer layer, final ILayerDim underlyingDim) {
		super(layer, underlyingDim);
		
		invalidateStructure();
	}
	
	
	protected void dispose() {
		if (this.scrollBarHandler != null) {
			this.scrollBarHandler.dispose();
			this.scrollBarHandler = null;
		}
	}
	
	/**
	 * @return The size of the visible client area. Will recalculate horizontal dimension
	 *     information if the size has changed.
	 */
	protected long getClientAreaSize() {
		final long clientAreaWidth = getLayer().getClientAreaProvider().getClientArea()
				.getRange(this.orientation).size();
		if (clientAreaWidth != this.cachedClientAreaSize) {
			invalidateStructure();
			this.cachedClientAreaSize = clientAreaWidth;
		}
		return this.cachedClientAreaSize;
	}
	
	/**
	 * Clear horizontal caches
	 */
	protected void invalidateStructure() {
		this.cachedClientAreaSize = 0;
		this.cachedPositionCount = -1;
		this.cachedSize = -1;
	}
	
	
	@Override
	public long getPositionCount() {
		if (getLayer().isViewportOff()) {
			return Math.max(this.underlyingDim.getPositionCount() - getMinimumOriginPosition(), 0);
		} else {
			if (this.cachedPositionCount < 0) {
				recalculateAvailableSizeAndPositionCount();
			}
			
			return this.cachedPositionCount;
		}
	}
	
	
	@Override
	public long localToUnderlyingPosition(final long refPosition, final long position) {
		if (refPosition < 0) {
			throw new IndexOutOfBoundsException("refPosition: " + refPosition); //$NON-NLS-1$
		}
		
		return getOriginPosition() + position;
	}
	
	@Override
	public long underlyingToLocalPosition(final long refPosition,
			final long underlyingPosition) {
		if (refPosition < 0 || refPosition >= getPositionCount()) {
			throw new IndexOutOfBoundsException("refPosition: " + refPosition); //$NON-NLS-1$
		}
		
		return underlyingPosition - getOriginPosition();
	}
	
	@Override
	public long underlyingToLocalPosition(final ILayer sourceUnderlyingLayer,
			final long underlyingPosition) {
		if (sourceUnderlyingLayer != getLayer().getScrollableLayer()) {
			throw new IllegalArgumentException("underlyingLayer"); //$NON-NLS-1$
		}
		
		return underlyingPosition - getOriginPosition();
	}
	
	
	/**
	 * Recalculate dimension properties.
	 */
	protected void recalculateAvailableSizeAndPositionCount() {
		long availableSize = getClientAreaSize();
		
		this.cachedSize = Math.min(getUnderlyingSize(), availableSize);
		
		this.originPixel = boundsCheckOrigin(this.originPixel);
		
		this.cachedPositionCount = 0;
		{	final long positionBound = this.underlyingDim.getPositionCount();
			long position = getOriginPosition();
			if (position >= 0 && position < positionBound) {
				availableSize += getOriginPixel() - this.underlyingDim.getPositionStart(position, position);
			}
			while (position >= 0 && position < positionBound && availableSize > 0) {
				final long positionSize = this.underlyingDim.getPositionSize(position, position);
				availableSize -= positionSize;
				position++;
				this.cachedPositionCount++;
			}
		}
	}
	
	private long getUnderlyingSize() {
		final long offsetPosition = getMinimumOriginPosition();
		return this.underlyingDim.getSize() - this.underlyingDim.getPositionStart(offsetPosition, offsetPosition);
	}
	
	@Override
	public long getSize() {
		if (getLayer().isViewportOff()) {
			return getUnderlyingSize();
		}
		if (this.cachedSize < 0) {
			recalculateAvailableSizeAndPositionCount();
		}
		return this.cachedSize;
	}
	
	@Override
	public long getPositionByPixel(final long pixel) {
		return super.getPositionByPixel(getOriginPixel() + pixel);
	}
	
	@Override
	public long getPositionStart(final long refPosition, final long position) {
		return super.getPositionStart(refPosition, position) - getOriginPixel();
	}
	
	
	@Override
	public final ILayerDim getScrollable() {
		return this.underlyingDim;
	}
	
	
	@Override
	public long getMinimumOriginPixel() {
		return this.minimumOriginPixel;
	}
	
	@Override
	public long getMinimumOriginPosition() {
		return this.minimumOriginPosition;
	}
	
	@Override
	public void setMinimumOriginPosition(final long scrollablePosition) {
		final long pixel = (scrollablePosition < this.underlyingDim.getPositionCount()) ?
				this.underlyingDim.getPositionStart(scrollablePosition, scrollablePosition) :
				this.underlyingDim.getSize();
		if (pixel < 0) {
			return;
		}
		
		final long delta = pixel - this.minimumOriginPixel;
		
		this.minimumOriginPosition = scrollablePosition;
		this.minimumOriginPixel = pixel;
		
		setOriginPixel(getOriginPixel() - delta);
		
		recalculateScrollBar();
	}
	
	
	@Override
	public long getOriginPixel() {
		if (getLayer().isViewportOff()) {
			return this.minimumOriginPixel;
		}
		return this.originPixel;
	}
	
	@Override
	public long getOriginPosition() {
		return this.underlyingDim.getPositionByPixel(getOriginPixel());
	}
	
	/**
	 * If the client area size is greater than the content size, move origin to fill as much content
	 * as possible.
	 */
	protected long adjustOrigin(final long pixel) {
		if (getPositionCount() == 0) {
			return 0;
		}
		
		final long availableWidth = getClientAreaSize() - (this.underlyingDim.getSize() - pixel);
		if (availableWidth <= 0) {
			return pixel;
		}
		return pixel - availableWidth;
	}
	
	/**
	 * Range checking for origin pixel position.
	 * @param pixel
	 * @return A valid x value within bounds: minimum origin x < x < max x (= column 0 x + width)
	 */
	private long boundsCheckOrigin(final long pixel) {
		final long min = getMinimumOriginPixel();
		if (pixel <= min) {
			return min;
		}
		final long max = Math.max(this.underlyingDim.getPositionStart(0, 0) + this.underlyingDim.getSize(), min);
		if (pixel > max) {
			return max;
		}
		return pixel;
	}
	
	@Override
	public void setOriginPixel(final long scrollablePixel) {
		if (doSetOriginPixel(scrollablePixel)) {
			getLayer().fireScrollEvent();
		}
	}
	
	protected boolean doSetOriginPixel(long scrollablePixel) {
		scrollablePixel = boundsCheckOrigin(scrollablePixel);
		scrollablePixel = boundsCheckOrigin(adjustOrigin(scrollablePixel));
		
		if (this.originPixel != scrollablePixel) {
			invalidateStructure();
			this.originPixel = scrollablePixel;
			return true;
		}
		return false;
	}
	
	@Override
	public void setOriginPosition(final long scrollablePosition) {
		final long pixel = this.underlyingDim.getPositionStart(scrollablePosition, scrollablePosition);
		if (pixel >= 0) {
			setOriginPixel(pixel);
		}
	}
	
	
	public void reset(final long scrollablePosition) {
		this.minimumOriginPosition = 0;
		this.minimumOriginPixel = 0;
		
		this.originPixel = 0;
		setOriginPosition(scrollablePosition);
	}
	
	public void movePositionIntoViewport(final long scrollablePosition) {
		if (this.underlyingDim.getPositionIndex(scrollablePosition, scrollablePosition) >= 0
				&& scrollablePosition >= getMinimumOriginPosition() ) {
			if (scrollablePosition <= getOriginPosition()) {
				// Move backward
				setOriginPosition(scrollablePosition);
			} else {
				// Move forward
				final long scrollableStart = this.underlyingDim.getPositionStart(scrollablePosition, scrollablePosition);
				final long scrollableEnd = scrollableStart + this.underlyingDim.getPositionSize(scrollablePosition, scrollablePosition);
				final long clientAreaHeight = getClientAreaSize();
				final long originPosition = getOriginPosition();
				final long viewportEnd = this.underlyingDim.getPositionStart(originPosition, originPosition) + clientAreaHeight;
				
				if (viewportEnd < scrollableEnd) {
					setOriginPixel(Math.min(scrollableEnd - clientAreaHeight, scrollableStart));
				}
			}
			
			// TEE: at least adjust scrollbar to reflect new position
			adjustScrollBar();
		}
	}
	
	@Override
	public void scrollBackwardByStep() {
		if (getPositionCount() == 0 || getLayer().isViewportOff()) {
			return;
		}
		
		final long origin = getOriginPixel();
		final long maxSize = origin - getMinimumOriginPixel();
		final long stepSize = Math.min(getWindowStepSize(), maxSize);
		long position = getOriginPosition();
		long sizeForPosition = origin - this.underlyingDim.getPositionStart(position, position);
		if (sizeForPosition <= 0 && position > getMinimumOriginPosition()) {
			sizeForPosition += this.underlyingDim.getPositionSize(position - 1, position - 1);
			position--;
		}
		if (sizeForPosition > 0 && sizeForPosition <= stepSize) {
			setOriginPixel(origin - sizeForPosition);
			return;
		}
//		if (stepSize == maxSize) {
//			setOriginPixel(origin - stepSize);
//			return;
//		}
		setOriginPixel(origin - stepSize / 2);
	}
	
	@Override
	public void scrollForwardByStep() {
		if (getPositionCount() == 0 || getLayer().isViewportOff()) {
			return;
		}
		
		final long origin = getOriginPixel();
		final long maxSize = this.underlyingDim.getSize() - origin;
		final long stepSize = Math.min(getWindowStepSize(), maxSize);
		final long position = getOriginPosition();
		if (position + 1 < this.underlyingDim.getPositionCount()) {
			final long sizeForPosition = this.underlyingDim.getPositionStart(position + 1, position + 1) - origin;
			if (sizeForPosition > 0 && sizeForPosition <= stepSize) {
				setOriginPixel(origin + sizeForPosition);
				return;
			}
		}
//		if (stepSize == maxSize) {
//			setOriginPixel(origin + stepSize);
//			return;
//		}
		setOriginPixel(origin + stepSize / 2);
		return;
	}
	
	private int getWindowStepSize() {
		final int size = (int) getSize();
		final int hint = size / 4 - PAGE_INTERSECTION_SIZE;
		if (hint < 3 * PAGE_INTERSECTION_SIZE) {
			return size;
		}
		return hint;
	}
	
	@Override
	public void scrollBackwardByPosition() {
		if (getPositionCount() == 0 || getLayer().isViewportOff()) {
			return;
		}
		
		final long position = getOriginPosition();
		final long positionPixel = getPositionStart(position, position);
		if (positionPixel < getOriginPixel()) { // first show start
			setOriginPixel(positionPixel);
			return;
		}
		setOriginPosition(position - 1);
	}
	
	@Override
	public void scrollForwardByPosition() {
		if (getPositionCount() == 0 || getLayer().isViewportOff()) {
			return;
		}
		
		setOriginPosition(getOriginPosition() + 1);
	}
	
	@Override
	public void scrollBackwardByPage() {
		if (getPositionCount() == 0 || getLayer().isViewportOff()) {
			return;
		}
		
		final long origin = getOriginPixel();
		final long maxSize = origin - getMinimumOriginPixel();
		final long pageSize = Math.min(getWindowPageSize(), maxSize);
		long position = getOriginPosition();
		long sizeForPosition = origin - this.underlyingDim.getPositionStart(position, position);
		for (final long bound = getMinimumOriginPosition(); position > bound; ) {
			final long size = this.underlyingDim.getPositionSize(position - 1, position - 1);
			if (sizeForPosition + size <= pageSize) {
				sizeForPosition += size;
				position--;
				continue;
			}
			else {
				break;
			}
		}
		if (sizeForPosition > PAGE_INTERSECTION_SIZE && sizeForPosition <= pageSize) {
			setOriginPixel(origin - sizeForPosition);
			return;
		}
		if (pageSize == maxSize) {
			setOriginPixel(origin - pageSize);
			return;
		}
		setOriginPixel(origin - (pageSize - PAGE_INTERSECTION_SIZE));
	}
	
	@Override
	public void scrollForwardByPage() {
		if (getPositionCount() == 0 || getLayer().isViewportOff()) {
			return;
		}
		
		final long origin = getOriginPixel();
		final long maxSize = this.underlyingDim.getSize() - origin;
		final long pageSize = Math.min(getWindowPageSize(), maxSize);
		long position = getOriginPosition();
		long sizeForPosition = this.underlyingDim.getPositionStart(position, position) - origin;
		for (final long bound = this.underlyingDim.getPositionCount(); position + 1 < bound; ) {
			final long size = this.underlyingDim.getPositionSize(position, position);
			if (sizeForPosition + size <= pageSize) {
				sizeForPosition += size;
				position++;
				continue;
			}
			else {
				break;
			}
		}
		if (sizeForPosition >= PAGE_INTERSECTION_SIZE && sizeForPosition <= pageSize) {
			setOriginPixel(origin + sizeForPosition);
			return;
		}
		if (pageSize == maxSize) {
			setOriginPixel(origin + pageSize);
			return;
		}
		setOriginPixel(origin + (pageSize - PAGE_INTERSECTION_SIZE));
	}
	
	private long getWindowPageSize() {
		final int size = (int) getSize();
		final int hint = size - PAGE_INTERSECTION_SIZE/2;
		if (hint < 4 * PAGE_INTERSECTION_SIZE) {
			return size;
		}
		return hint;
	}
	
	@Override
	public void scrollBackwardToBound() {
		if (getPositionCount() == 0 || getLayer().isViewportOff()) {
			return;
		}
		
		setOriginPosition(getMinimumOriginPosition());
	}
	
	@Override
	public void scrollForwardToBound() {
		if (getPositionCount() == 0 || getLayer().isViewportOff()) {
			return;
		}
		
		setOriginPosition(getScrollable().getPositionCount() - 1);
	}
	
	
	protected void checkScrollBar(final Scrollable control) {
		if (this.scrollBarHandler != null) {
			return;
		}
		final ScrollBar scrollBar = SWTUtil.getScrollBar(control, getOrientation());
		if (scrollBar != null) {
			this.scrollBarHandler = new ScrollBarHandlerTemplate(getLayer(), getOrientation(), scrollBar);
		}
	}
	/**
	 * Adjusts scrollbar to sync with current state of viewport.
	 */
	private void adjustScrollBar() {
		if (this.scrollBarHandler != null) {
			this.scrollBarHandler.adjustScrollBar();
		}
	}
	
	/**
	 * Recalculate scrollbar characteristics;
	 */
	private void recalculateScrollBar() {
		if (this.scrollBarHandler != null) {
			this.scrollBarHandler.recalculateScrollBarSize();
			
			if (!this.scrollBarHandler.getScrollBar().getEnabled()) {
				setOriginPosition(getMinimumOriginPosition());
			}
		}
	}
	
	protected void handleResize() {
		recalculateAvailableSizeAndPositionCount();
		setOriginPixel(getOriginPixel());
		recalculateScrollBar();
	}
	
}
