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
package org.eclipse.nebula.widgets.nattable.hideshow;

import static org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell.NO_INDEX;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.IStructuralChangeEvent;


public abstract class AbstractRowHideShowLayer extends AbstractLayerTransform implements IUniqueIndexLayer {

	private Map<Long, Long> cachedVisibleRowIndexOrder;
	private Map<Long, Long> cachedVisibleRowPositionOrder;
	
	private Map<Long, Long> cachedHiddenRowIndexToPositionMap;

	private final Map<Long, Long> startYCache = new HashMap<Long, Long>();	
	
	
	public AbstractRowHideShowLayer(IUniqueIndexLayer underlyingLayer) {
		super(underlyingLayer);
	}
	
	@Override
	public void handleLayerEvent(ILayerEvent event) {
		if (event instanceof IStructuralChangeEvent) {
			IStructuralChangeEvent structuralChangeEvent = (IStructuralChangeEvent) event;
			if (structuralChangeEvent.isVerticalStructureChanged()) {
				invalidateCache();
			}
		}
		super.handleLayerEvent(event);
	}
	
	// Horizontal features

	// Columns
	
	@Override
	public long getColumnPositionByIndex(long columnIndex) {
		return ((IUniqueIndexLayer) getUnderlyingLayer()).getColumnPositionByIndex(columnIndex);
	}
	
	// Vertical features

	// Rows
	
	@Override
	public long getRowCount() {
		return getCachedVisibleRowIndexes().size();
	}
	
	@Override
	public long getRowIndexByPosition(long rowPosition) {
		if (rowPosition < 0 || rowPosition >= getRowCount()) {
			return NO_INDEX;
		}

		Long rowIndex = getCachedVisibleRowPositons().get(rowPosition);
		if (rowIndex != null) {
			return rowIndex.longValue();
		} else {
			return NO_INDEX;
		}
	}
	
	@Override
	public long getRowPositionByIndex(long rowIndex) {
		final Long position = getCachedVisibleRowIndexes().get(Long.valueOf(rowIndex));
		return position != null ? position : Long.MIN_VALUE;
	}
	
	public Collection<Long> getRowPositionsByIndexes(Collection<Long> rowIndexes) {
		Collection<Long> rowPositions = new HashSet<Long>();
		for (long rowIndex : rowIndexes) {
			rowPositions.add(getRowPositionByIndex(rowIndex));
		}
		return rowPositions;
	}
	
	@Override
	public long localToUnderlyingRowPosition(long localRowPosition) {
		long rowIndex = getRowIndexByPosition(localRowPosition);
		return ((IUniqueIndexLayer) getUnderlyingLayer()).getRowPositionByIndex(rowIndex);
	}
	
	@Override
	public long underlyingToLocalRowPosition(ILayer sourceUnderlyingLayer, long underlyingRowPosition) {
		long  rowIndex = getUnderlyingLayer().getRowIndexByPosition(underlyingRowPosition);
		long  rowPosition = getRowPositionByIndex(rowIndex);
		if (rowPosition >= 0) {
			return rowPosition;
		} else {
			Long hiddenRowPosition = cachedHiddenRowIndexToPositionMap.get(Long.valueOf(rowIndex));
			if (hiddenRowPosition != null) {
				return hiddenRowPosition.longValue();
			} else {
				return Long.MIN_VALUE;
			}
		}
	}

	@Override
	public Collection<Range> underlyingToLocalRowPositions(ILayer sourceUnderlyingLayer, Collection<Range> underlyingRowPositionRanges) {
		Collection<Range> localRowPositionRanges = new ArrayList<Range>();

		for (Range underlyingRowPositionRange : underlyingRowPositionRanges) {
			long  startRowPosition = getAdjustedUnderlyingToLocalStartPosition(sourceUnderlyingLayer, underlyingRowPositionRange.start, underlyingRowPositionRange.end);
			long  endRowPosition = getAdjustedUnderlyingToLocalEndPosition(sourceUnderlyingLayer, underlyingRowPositionRange.end, underlyingRowPositionRange.start);

			// teichstaedt: fixes the problem that ranges where added even if the
			// corresponding startPosition weren't found in the underlying layer.
			// Without that fix a bunch of ranges of kind Range [-1, 180] which
			// causes strange behaviour in Freeze- and other Layers were returned.
			if (startRowPosition > -1) {
				localRowPositionRanges.add(new Range(startRowPosition, endRowPosition));
			}
		}

		return localRowPositionRanges;
	}
	
	private long  getAdjustedUnderlyingToLocalStartPosition(ILayer sourceUnderlyingLayer, long  startUnderlyingPosition, long  endUnderlyingPosition) {
		long localStartRowPosition = underlyingToLocalRowPosition(sourceUnderlyingLayer, startUnderlyingPosition);
		long offset = 0;
		while (localStartRowPosition < 0 && (startUnderlyingPosition + offset < endUnderlyingPosition)) {
			localStartRowPosition = underlyingToLocalRowPosition(sourceUnderlyingLayer, startUnderlyingPosition + offset++);
		}
		return localStartRowPosition;
	}

	private long getAdjustedUnderlyingToLocalEndPosition(ILayer sourceUnderlyingLayer, long endUnderlyingPosition, long startUnderlyingPosition) {
		long localEndRowPosition = underlyingToLocalRowPosition(sourceUnderlyingLayer, endUnderlyingPosition - 1);
		long offset = 0;
		while (localEndRowPosition < 0 && (endUnderlyingPosition - offset > startUnderlyingPosition)) {
			localEndRowPosition = underlyingToLocalRowPosition(sourceUnderlyingLayer, endUnderlyingPosition - offset++);
		}
		return localEndRowPosition + 1;
	}
	
	// Height
	
	@Override
	public long getHeight() {
		long lastRowPosition = getRowCount() - 1;
		return getStartYOfRowPosition(lastRowPosition) + getRowHeightByPosition(lastRowPosition);
	}
	
	// Y
	
	@Override
	public long getRowPositionByY(long y) {
		return LayerUtil.getRowPositionByY(this, y);
	}
	
	@Override
	public long getStartYOfRowPosition(long localRowPosition) {
		Long cachedStartY = startYCache.get(Long.valueOf(localRowPosition));
		if (cachedStartY != null) {
			return cachedStartY.longValue();
		}
		
		IUniqueIndexLayer underlyingLayer = (IUniqueIndexLayer) getUnderlyingLayer();
		long underlyingPosition = localToUnderlyingRowPosition(localRowPosition);
		long underlyingStartY = underlyingLayer.getStartYOfRowPosition(underlyingPosition);

		for (Long hiddenIndex : getHiddenRowIndexes()) {
			long hiddenPosition = underlyingLayer.getRowPositionByIndex(hiddenIndex.longValue());
			//if the hidden position is -1, it is hidden in the underlying layer
			//therefore the underlying layer should handle the positioning
			if (hiddenPosition >= 0 && hiddenPosition <= underlyingPosition) {
				underlyingStartY -= underlyingLayer.getRowHeightByPosition(hiddenPosition);
			}
		}

		startYCache.put(Long.valueOf(localRowPosition), Long.valueOf(underlyingStartY));
		return underlyingStartY;
	}
	
	// Hide/show

	/**
	 * Will check if the row at the specified index is hidden or not. Checks this
	 * layer and also the sublayers for the visibility.
	 * @param rowIndex The row index of the row whose visibility state
	 * 			should be checked.
	 * @return <code>true</code> if the row at the specified index is hidden,
	 * 			<code>false</code> if it is visible.
	 */
	public abstract boolean isRowIndexHidden(long rowIndex);

	/**
	 * Will collect and return all indexes of the rows that are hidden in this layer.
	 * Note: It is not intended that it also collects the row indexes of underlying
	 * 		 layers. This would cause issues on calculating positions as every layer
	 * 		 is responsible for those calculations itself. 
	 * @return Collection of all row indexes that are hidden in this layer.
	 */
	public abstract Collection<Long> getHiddenRowIndexes();
	
	// Cache

	/**
	 * Invalidate the cache to ensure that information is rebuild.
	 */
	protected void invalidateCache() {
		cachedVisibleRowIndexOrder = null;
		cachedVisibleRowPositionOrder = null;
		cachedHiddenRowIndexToPositionMap = null;
		startYCache.clear();
	}

	private Map<Long, Long> getCachedVisibleRowIndexes() {
		if (cachedVisibleRowIndexOrder == null) {
			cacheVisibleRowIndexes();
		}
		return cachedVisibleRowIndexOrder;
	}
	
	private Map<Long, Long> getCachedVisibleRowPositons() {
		if (cachedVisibleRowPositionOrder == null) {
			cacheVisibleRowIndexes();
		}
		return cachedVisibleRowPositionOrder;
	}

	protected void cacheVisibleRowIndexes() {
		cachedVisibleRowIndexOrder = new HashMap<Long, Long>();
		cachedVisibleRowPositionOrder = new HashMap<Long, Long>();
		cachedHiddenRowIndexToPositionMap = new HashMap<Long, Long>();
		startYCache.clear();

		ILayer underlyingLayer = getUnderlyingLayer();
		long rowPosition = 0;
		for (long parentRowPosition = 0; parentRowPosition < underlyingLayer.getRowCount(); parentRowPosition++) {
			long rowIndex = underlyingLayer.getRowIndexByPosition(parentRowPosition);

			if (!isRowIndexHidden(rowIndex)) {
				cachedVisibleRowIndexOrder.put(Long.valueOf(rowIndex), rowPosition);
				cachedVisibleRowPositionOrder.put(rowPosition, Long.valueOf(rowIndex));
				rowPosition++;
			} else {
				cachedHiddenRowIndexToPositionMap.put(Long.valueOf(rowIndex), Long.valueOf(rowPosition));
			}
		}
	}

}
