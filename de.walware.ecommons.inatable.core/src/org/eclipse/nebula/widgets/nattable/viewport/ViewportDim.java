/*******************************************************************************
 * Copyright (c) 2013-2015 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Edwin Park - original layer-based implementation supporting smooth scrolling
 *     Stephan Wahlbrink - initial API and dim-based implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.viewport;

import static org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer.PAGE_INTERSECTION_SIZE;

import java.util.Collection;

import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;

import org.eclipse.nebula.widgets.nattable.layer.ILayerDim;
import org.eclipse.nebula.widgets.nattable.layer.TransformLayerDim;
import org.eclipse.nebula.widgets.nattable.layer.event.StructuralDiff;
import org.eclipse.nebula.widgets.nattable.swt.SWTUtil;


/**
 * Implementation of {@link IViewportDim} for {@link ViewportLayer}.
 */
public class ViewportDim extends TransformLayerDim<ViewportLayer> implements IViewportDim {
	
	
	private long minimumOriginPosition;
	private long minimumOriginPixel;
	
	private long originPixel;
	
	private long cachedClientAreaSize;
	private long cachedOriginPosition;
	private int cachedOriginPositionPixelShift;
	private long cachedPositionCount;
	private long cachedSize;
	
	private ScrollBarHandler scrollBarHandler;
	
	
	public ViewportDim(/*@NonNull*/ final ViewportLayer layer, /*@NonNull*/ final ILayerDim underlyingDim) {
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
		final long clientAreaSize = SWTUtil.getRange(this.layer.getClientAreaProvider().getClientArea(),
				this.orientation).size();
		if (clientAreaSize != this.cachedClientAreaSize) {
			invalidateStructure();
			this.cachedClientAreaSize = clientAreaSize;
		}
		return this.cachedClientAreaSize;
	}
	
	/**
	 * Clear horizontal caches
	 */
	protected void invalidateStructure() {
		this.cachedClientAreaSize = 0;
		this.cachedOriginPosition = -1;
		this.cachedOriginPositionPixelShift = 0;
		this.cachedPositionCount = -1;
		this.cachedSize = -1;
	}
	
	
	@Override
	public long getPositionCount() {
		if (this.layer.isViewportOff()) {
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
	public long underlyingToLocalPosition(final ILayerDim sourceUnderlyingDim,
			final long underlyingPosition) {
		if (sourceUnderlyingDim != this.underlyingDim) {
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
		
		this.cachedOriginPosition = this.underlyingDim.getPositionByPixel(getOriginPixel());
		this.cachedPositionCount = 0;
		final long positionBound = this.underlyingDim.getPositionCount();
		long position = this.cachedOriginPosition;
		if (position >= 0 && position < positionBound) {
			this.cachedOriginPositionPixelShift = (int) (getOriginPixel() - this.underlyingDim.getPositionStart(position, position));
			availableSize += this.cachedOriginPositionPixelShift;
		}
		while (position >= 0 && position < positionBound && availableSize > 0) {
			final int positionSize = this.underlyingDim.getPositionSize(position, position);
			availableSize -= positionSize;
			position++;
			this.cachedPositionCount++;
		}
	}
	
	private long getUnderlyingSize() {
		final long offsetPosition = getMinimumOriginPosition();
		return this.underlyingDim.getSize() - this.underlyingDim.getPositionStart(offsetPosition, offsetPosition);
	}
	
	@Override
	public long getSize() {
		if (this.layer.isViewportOff()) {
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
		if (scrollablePosition < 0 || scrollablePosition > this.underlyingDim.getPositionCount()) {
			throw new IndexOutOfBoundsException("scrollablePosition: " + scrollablePosition); //$NON-NLS-1$
		}
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
		if (this.layer.isViewportOff()) {
			return this.minimumOriginPixel;
		}
		return this.originPixel;
	}
	
	@Override
	public long getOriginPosition() {
		if (this.cachedOriginPosition < 0) {
			recalculateAvailableSizeAndPositionCount();
		}
		return this.cachedOriginPosition;
	}
	
	/**
	 * If the client area size is greater than the content size, move origin to fill as much content
	 * as possible.
	 */
	protected long adjustOrigin(final long pixel) {
		if (getPositionCount() == 0) {
			return 0;
		}
		
		final long availableSize = getClientAreaSize() - (this.underlyingDim.getSize() - pixel);
		if (availableSize <= 0) {
			return pixel;
		}
		return pixel - availableSize;
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
			this.layer.fireScrollEvent();
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
		if (scrollablePosition < getMinimumOriginPosition()
				|| (scrollablePosition > getMinimumOriginPosition() && scrollablePosition >= this.underlyingDim.getPositionCount()) ) {
			throw new IndexOutOfBoundsException("scrollablePosition: " + scrollablePosition); //$NON-NLS-1$
		}
		setOriginPixel(this.underlyingDim.getPositionStart(scrollablePosition, scrollablePosition));
	}
	
	
	@Override
	public void reset(final long scrollablePosition) {
		if (scrollablePosition < 0
				|| (scrollablePosition > 0 && scrollablePosition >= this.underlyingDim.getPositionCount()) ) {
			throw new IndexOutOfBoundsException("scrollablePosition: " + scrollablePosition); //$NON-NLS-1$
		}
		this.minimumOriginPosition = 0;
		this.minimumOriginPixel = 0;
		
		this.originPixel = -1; // force to reset origin
		setOriginPosition(scrollablePosition);
	}
	
	@Override
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
				final long clientAreaSize = getClientAreaSize();
				final long originPosition = getOriginPosition();
				final long viewportEnd = this.underlyingDim.getPositionStart(originPosition, originPosition) + clientAreaSize;
				
				if (viewportEnd < scrollableEnd) {
					setOriginPixel(Math.min(scrollableEnd - clientAreaSize, scrollableStart));
				}
			}
			
			// TEE: at least adjust scrollbar to reflect new position
			adjustScrollBar();
		}
	}
	
	@Override
	public void scrollBackwardByStep() {
		if (getPositionCount() == 0 || this.layer.isViewportOff()) {
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
		if (getPositionCount() == 0 || this.layer.isViewportOff()) {
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
		if (getPositionCount() == 0 || this.layer.isViewportOff()) {
			return;
		}
		
		final long position = getOriginPosition();
		final long positionPixel = getPositionStart(position, position);
		if (positionPixel < getOriginPixel()) { // first show start
			setOriginPixel(positionPixel);
			return;
		}
		if (position - 1 >= getMinimumOriginPosition()) {
			setOriginPosition(position - 1);
		}
	}
	
	@Override
	public void scrollForwardByPosition() {
		if (getPositionCount() == 0 || this.layer.isViewportOff()) {
			return;
		}
		
		final long position = getOriginPosition();
		if (position + 1 < this.underlyingDim.getPositionCount()) {
			setOriginPosition(position + 1);
		}
	}
	
	@Override
	public void scrollBackwardByPage() {
		if (getPositionCount() == 0 || this.layer.isViewportOff()) {
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
		if (getPositionCount() == 0 || this.layer.isViewportOff()) {
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
		if (getPositionCount() == 0 || this.layer.isViewportOff()) {
			return;
		}
		
		setOriginPosition(getMinimumOriginPosition());
	}
	
	@Override
	public void scrollForwardToBound() {
		if (getPositionCount() == 0 || this.layer.isViewportOff()) {
			return;
		}
		
		setOriginPosition(getScrollable().getPositionCount() - 1);
	}
	
	
	protected void checkScrollBar(final Scrollable control) {
		if (this.scrollBarHandler != null) {
			return;
		}
		final ScrollBar scrollBar = SWTUtil.getScrollBar(control, this.orientation);
		if (scrollBar != null) {
			this.scrollBarHandler = new ScrollBarHandler(this, scrollBar);
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
	
	protected void handleStructuralChange(final Collection<StructuralDiff> diffs) {
		final long minimumOriginPosition = getMinimumOriginPosition();
		final long minimumOriginPixel = getMinimumOriginPixel();
		final long selectedOriginPosition = this.cachedOriginPosition;
		final int selectedOriginPositionShift = this.cachedOriginPositionPixelShift;
		final long selectedOriginPixel = this.originPixel;
		
		invalidateStructure();
		
		if (diffs != null) {
			// The handling of diffs have to be in sync with FreezeEventHandler
			int minimumPositionChange = 0;
			int selectedPositionChange = 0;
			int freezeMove = 0; // 0 = unset, 1 == true, -1 == false
			
			for (final StructuralDiff diff : diffs) {
				final long start = diff.getBeforePositionRange().start;
				switch (diff.getDiffType()) {
				case ADD:
					if (start < minimumOriginPosition
							|| (freezeMove == 1 && start == minimumOriginPosition) ) {
						minimumPositionChange += diff.getAfterPositionRange().size();
					}
					if (start < selectedOriginPosition) {
						selectedPositionChange += diff.getAfterPositionRange().size();
					}
					continue;
				case DELETE:
					if (start < minimumOriginPosition) {
						minimumPositionChange -= Math.min(diff.getBeforePositionRange().end, minimumOriginPosition + 1) - start;
						if (freezeMove == 0) {
							freezeMove = 1;
						}
					}
					else {
						freezeMove = -1;
					}
					if (start < selectedOriginPosition) {
						selectedPositionChange -= Math.min(diff.getBeforePositionRange().end, selectedOriginPosition + 1) - start;
					}
					continue;
				default:
					continue;
				}
			}
			
			setMinimumOriginPosition(minimumOriginPosition + minimumPositionChange);
			if (selectedOriginPosition >= 0) {
				final long pixel = this.underlyingDim.getPositionStart(selectedOriginPosition + selectedPositionChange, selectedOriginPosition + selectedPositionChange);
				if (pixel >= 0) {
					setOriginPixel(pixel + selectedOriginPositionShift);
				}
			}
			else {
				setOriginPixel(selectedOriginPixel + (getMinimumOriginPixel() - minimumOriginPixel));
			}
		}
	}
	
}
