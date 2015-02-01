/*******************************************************************************
 * Copyright (c) 2013-2015 Dirk Fauth and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Dirk Fauth <dirk.fauth@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.nattable.reorder.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.coordinate.PositionUtil;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.event.RowStructuralChangeEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.StructuralDiff;
import org.eclipse.nebula.widgets.nattable.layer.event.StructuralDiff.DiffTypeEnum;

public class RowReorderEvent extends RowStructuralChangeEvent {

	private Collection<Range> beforeFromRowPositionRanges;

	private long beforeToRowPosition;
	private boolean reorderToTopEdge;

	/**
	 * @param layer
	 * @param beforeFromRowPosition
	 * @param beforeToRowPosition
	 * @param reorderToTopEdge
	 */
	public RowReorderEvent(ILayer layer, 
			long beforeFromRowPosition, long beforeToRowPosition, 
			boolean reorderToTopEdge) {
		
		this(layer, Arrays.asList(
				new Long[] { Long.valueOf(beforeFromRowPosition) }), beforeToRowPosition, reorderToTopEdge);
	}

	/**
	 * @param layer
	 * @param beforeFromRowPositions
	 * @param beforeToRowPosition
	 * @param reorderToTopEdge
	 */
	public RowReorderEvent(ILayer layer, List<Long> beforeFromRowPositions, 
			long beforeToRowPosition, boolean reorderToTopEdge) {
		super(layer, PositionUtil.getRanges(
				PositionUtil.concat(beforeFromRowPositions, Long.valueOf(beforeToRowPosition) )));
		this.beforeFromRowPositionRanges = PositionUtil.getRanges(beforeFromRowPositions);
		this.reorderToTopEdge = reorderToTopEdge;
		this.beforeToRowPosition = beforeToRowPosition;
	}

	/**
	 * Constructor for internal use to clone this event.
	 * @param event The event out of which the new one should be created
	 */
	public RowReorderEvent(RowReorderEvent event) {
		super(event);
		this.beforeFromRowPositionRanges = event.beforeFromRowPositionRanges;
		this.beforeToRowPosition = event.beforeToRowPosition;
		this.reorderToTopEdge = event.reorderToTopEdge;
	}

	public Collection<Range> getBeforeFromRowPositionRanges() {
		return this.beforeFromRowPositionRanges;
	}

	public long getBeforeToRowPosition() {
		return this.beforeToRowPosition;
	}
	
	public boolean isReorderToTopEdge() {
		return reorderToTopEdge;
	}

	@Override
	public Collection<StructuralDiff> getRowDiffs() {
		Collection<StructuralDiff> rowDiffs = new ArrayList<StructuralDiff>();

		Collection<Range> beforeFromRowPositionRanges = getBeforeFromRowPositionRanges();

		final long beforeToRowPosition = (this.reorderToTopEdge) ?
				this.beforeToRowPosition : (this.beforeToRowPosition + 1);
		long afterAddRowPosition = beforeToRowPosition;
		for (Range beforeFromRowPositionRange : beforeFromRowPositionRanges) {
			if (beforeFromRowPositionRange.start < beforeToRowPosition) {
				afterAddRowPosition -= Math.min(beforeFromRowPositionRange.end, beforeToRowPosition) - beforeFromRowPositionRange.start;
			} else {
				break;
			}
		}
		long cumulativeAddSize = 0;
		for (Range beforeFromRowPositionRange : beforeFromRowPositionRanges) {
			cumulativeAddSize += beforeFromRowPositionRange.size();
		}

		long offset = 0;
		for (Range beforeFromRowPositionRange : beforeFromRowPositionRanges) {
			long afterDeleteRowPosition = beforeFromRowPositionRange.start - offset;
			if (afterAddRowPosition < afterDeleteRowPosition) {
				afterDeleteRowPosition += cumulativeAddSize;
			}
			rowDiffs.add(new StructuralDiff(DiffTypeEnum.DELETE, beforeFromRowPositionRange, new Range(afterDeleteRowPosition, afterDeleteRowPosition)));
			offset += beforeFromRowPositionRange.size();
		}
		Range beforeAddRange = new Range(beforeToRowPosition, beforeToRowPosition);
		offset = 0;
		for (Range beforeFromRowPositionRange : beforeFromRowPositionRanges) {
			long size = beforeFromRowPositionRange.size();
			rowDiffs.add(new StructuralDiff(DiffTypeEnum.ADD, beforeAddRange, new Range(afterAddRowPosition + offset, afterAddRowPosition + offset + size)));
			offset += size;
		}

		return rowDiffs;
	}

	@Override
	public boolean convertToLocal(ILayer targetLayer) {
		beforeFromRowPositionRanges = targetLayer.underlyingToLocalRowPositions(getLayer(), beforeFromRowPositionRanges);
		beforeToRowPosition = targetLayer.underlyingToLocalRowPosition(getLayer(), beforeToRowPosition);

		if (beforeToRowPosition >= 0) {
			return super.convertToLocal(targetLayer);
		} else {
			return false;
		}
	}

	@Override
	public RowReorderEvent cloneEvent() {
		return new RowReorderEvent(this);
	}

}
