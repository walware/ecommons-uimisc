/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.reorder.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.coordinate.PositionUtil;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.event.ColumnStructuralChangeEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.StructuralDiff;
import org.eclipse.nebula.widgets.nattable.layer.event.StructuralDiff.DiffTypeEnum;


public class ColumnReorderEvent extends ColumnStructuralChangeEvent {

	private Collection<Range> beforeFromColumnPositionRanges;

	private long beforeToColumnPosition;
	private boolean reorderToLeftEdge;

	public ColumnReorderEvent(ILayer layer, long beforeFromColumnPosition, long beforeToColumnPosition, boolean reorderToLeftEdge) {
		this(layer, Arrays.asList(new Long[] { Long.valueOf(beforeFromColumnPosition) }), beforeToColumnPosition, reorderToLeftEdge);
	}

	public ColumnReorderEvent(ILayer layer, List<Long> beforeFromColumnPositions, long beforeToColumnPosition, boolean reorderToLeftEdge) {
		super(layer);
		this.beforeFromColumnPositionRanges = PositionUtil.getRanges(beforeFromColumnPositions);
		this.reorderToLeftEdge = reorderToLeftEdge;
		this.beforeToColumnPosition = beforeToColumnPosition;

		List<Long> allColumnPositions = new ArrayList<Long>(beforeFromColumnPositions);
		allColumnPositions.add(Long.valueOf(beforeToColumnPosition));
		setColumnPositionRanges(PositionUtil.getRanges(allColumnPositions));
	}

	/**
	 * Constructor for internal use to clone this event.
	 * @param event The event out of which the new one should be created
	 */
	public ColumnReorderEvent(ColumnReorderEvent event) {
		super(event);
		this.beforeFromColumnPositionRanges = event.beforeFromColumnPositionRanges;
		this.beforeToColumnPosition = event.beforeToColumnPosition;
		this.reorderToLeftEdge = event.reorderToLeftEdge;
	}

	public Collection<Range> getBeforeFromColumnPositionRanges() {
		return beforeFromColumnPositionRanges;
	}

	public long getBeforeToColumnPosition() {
		return beforeToColumnPosition;
	}
	
	public boolean isReorderToLeftEdge() {
		return reorderToLeftEdge;
	}

	@Override
	public Collection<StructuralDiff> getColumnDiffs() {
		Collection<StructuralDiff> columnDiffs = new ArrayList<StructuralDiff>();

		Collection<Range> beforeFromColumnPositionRanges = getBeforeFromColumnPositionRanges();

		final long beforeToColumnPosition = (this.reorderToLeftEdge) ?
				this.beforeToColumnPosition : (this.beforeToColumnPosition + 1);
		long afterAddColumnPosition = beforeToColumnPosition;
		for (Range beforeFromColumnPositionRange : beforeFromColumnPositionRanges) {
			if (beforeFromColumnPositionRange.start < beforeToColumnPosition) {
				afterAddColumnPosition -= Math.min(beforeFromColumnPositionRange.end, beforeToColumnPosition) - beforeFromColumnPositionRange.start;
			} else {
				break;
			}
		}
		long cumulativeAddSize = 0;
		for (Range beforeFromColumnPositionRange : beforeFromColumnPositionRanges) {
			cumulativeAddSize += beforeFromColumnPositionRange.size();
		}

		long offset = 0;
		for (Range beforeFromColumnPositionRange : beforeFromColumnPositionRanges) {
			long afterDeleteColumnPosition = beforeFromColumnPositionRange.start - offset;
			if (afterAddColumnPosition < afterDeleteColumnPosition) {
				afterDeleteColumnPosition += cumulativeAddSize;
			}
			columnDiffs.add(new StructuralDiff(DiffTypeEnum.DELETE, beforeFromColumnPositionRange, new Range(afterDeleteColumnPosition, afterDeleteColumnPosition)));
			offset += beforeFromColumnPositionRange.size();
		}
		Range beforeAddRange = new Range(beforeToColumnPosition, beforeToColumnPosition);
		offset = 0;
		for (Range beforeFromColumnPositionRange : beforeFromColumnPositionRanges) {
			long size = beforeFromColumnPositionRange.size();
			columnDiffs.add(new StructuralDiff(DiffTypeEnum.ADD, beforeAddRange, new Range(afterAddColumnPosition + offset, afterAddColumnPosition + offset + size)));
			offset += size;
		}

		return columnDiffs;
	}

	@Override
	public boolean convertToLocal(ILayer targetLayer) {
		beforeFromColumnPositionRanges = targetLayer.underlyingToLocalColumnPositions(getLayer(), beforeFromColumnPositionRanges);
		beforeToColumnPosition = targetLayer.underlyingToLocalColumnPosition(getLayer(), beforeToColumnPosition);

		if (beforeToColumnPosition >= 0) {
			return super.convertToLocal(targetLayer);
		} else {
			return false;
		}
	}

	@Override
	public ColumnReorderEvent cloneEvent() {
		return new ColumnReorderEvent(this);
	}

}
