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
import java.util.List;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel.ColumnGroup;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.IStructuralChangeEvent;


public abstract class AbstractColumnHideShowLayer extends AbstractLayerTransform implements IUniqueIndexLayer {

	private List<Long> cachedVisibleColumnIndexOrder;

	private Map<Long, Long> cachedHiddenColumnIndexToPositionMap;

	private final Map<Long, Long> startXCache = new HashMap<Long, Long>();

	public AbstractColumnHideShowLayer(IUniqueIndexLayer underlyingLayer) {
		super(underlyingLayer);
	}

	@Override
	public void handleLayerEvent(ILayerEvent event) {
		if (event instanceof IStructuralChangeEvent) {
			IStructuralChangeEvent structuralChangeEvent = (IStructuralChangeEvent) event;
			if (structuralChangeEvent.isHorizontalStructureChanged()) {
				invalidateCache();
			}
		}
		super.handleLayerEvent(event);
	}

	// Horizontal features

	// Columns

	@Override
	public long getColumnCount() {
		return getCachedVisibleColumnIndexes().size();
	}

	@Override
	public long getColumnIndexByPosition(long columnPosition) {
		if (columnPosition < 0 || columnPosition >= getColumnCount()) {
			return NO_INDEX;
		}

		Long columnIndex = getCachedVisibleColumnIndexes().get((int) columnPosition);
		if (columnIndex != null) {
			return columnIndex.longValue();
		} else {
			return NO_INDEX;
		}
	}

	public long getColumnPositionByIndex(long columnIndex) {
		return getCachedVisibleColumnIndexes().indexOf(Long.valueOf(columnIndex));
	}
	
	public Collection<Long> getColumnPositionsByIndexes(Collection<Long> columnIndexes) {
		Collection<Long> columnPositions = new HashSet<Long>();
		for (long columnIndex : columnIndexes) {
			columnPositions.add(getColumnPositionByIndex(columnIndex));
		}
		return columnPositions;
	}
	
	@Override
	public long localToUnderlyingColumnPosition(long localColumnPosition) {
		long columnIndex = getColumnIndexByPosition(localColumnPosition);
		return ((IUniqueIndexLayer) getUnderlyingLayer()).getColumnPositionByIndex(columnIndex);
	}

	@Override
	public long underlyingToLocalColumnPosition(ILayer sourceUnderlyingLayer, long underlyingColumnPosition) {
		long columnIndex = getUnderlyingLayer().getColumnIndexByPosition(underlyingColumnPosition);
		long columnPosition = getColumnPositionByIndex(columnIndex);
		if (columnPosition >= 0) {
			return columnPosition;
		} else {
			Long hiddenColumnPosition = cachedHiddenColumnIndexToPositionMap.get(Long.valueOf(columnIndex));
			if (hiddenColumnPosition != null) {
				return hiddenColumnPosition.longValue();
			} else {
				return Long.MIN_VALUE;
			}
		}
	}

	@Override
	public Collection<Range> underlyingToLocalColumnPositions(ILayer sourceUnderlyingLayer, Collection<Range> underlyingColumnPositionRanges) {
		Collection<Range> localColumnPositionRanges = new ArrayList<Range>();

		for (Range underlyingColumnPositionRange : underlyingColumnPositionRanges) {
			long startColumnPosition = getAdjustedUnderlyingToLocalStartPosition(sourceUnderlyingLayer, underlyingColumnPositionRange.start, underlyingColumnPositionRange.end);
			long endColumnPosition = getAdjustedUnderlyingToLocalEndPosition(sourceUnderlyingLayer, underlyingColumnPositionRange.end, underlyingColumnPositionRange.start);

			// teichstaedt: fixes the problem that ranges where added even if the
			// corresponding startPosition weren't found in the underlying layer.
			// Without that fix a bunch of ranges of kind Range [-1, 180] which
			// causes strange behaviour in Freeze- and other Layers were returned.
			if (startColumnPosition > -1 && startColumnPosition < endColumnPosition) {
				localColumnPositionRanges.add(new Range(startColumnPosition, endColumnPosition));
			}
		}

		return localColumnPositionRanges;
	}

	private long getAdjustedUnderlyingToLocalStartPosition(ILayer sourceUnderlyingLayer, long startUnderlyingPosition, long endUnderlyingPosition) {
		long localStartColumnPosition = underlyingToLocalColumnPosition(sourceUnderlyingLayer, startUnderlyingPosition);
		long offset = 0;
		while (localStartColumnPosition < 0 && (startUnderlyingPosition + offset < endUnderlyingPosition)) {
			localStartColumnPosition = underlyingToLocalColumnPosition(sourceUnderlyingLayer, startUnderlyingPosition + offset++);
		}
		return localStartColumnPosition;
	}

	private long getAdjustedUnderlyingToLocalEndPosition(ILayer sourceUnderlyingLayer, long endUnderlyingPosition, long startUnderlyingPosition) {
		long localEndColumnPosition = underlyingToLocalColumnPosition(sourceUnderlyingLayer, endUnderlyingPosition - 1);
		long offset = 0;
		while (localEndColumnPosition < 0 && (endUnderlyingPosition - offset > startUnderlyingPosition)) {
			localEndColumnPosition = underlyingToLocalColumnPosition(sourceUnderlyingLayer, endUnderlyingPosition - offset++);
		}
		return localEndColumnPosition + 1;
	}

	// Width

	@Override
	public long getWidth() {
		long lastColumnPosition = getColumnCount() - 1;
		return getStartXOfColumnPosition(lastColumnPosition) + getColumnWidthByPosition(lastColumnPosition);
	}

	// X

	@Override
	public long getColumnPositionByX(long x) {
		return LayerUtil.getColumnPositionByX(this, x);
	}

	@Override
	public long getStartXOfColumnPosition(long localColumnPosition) {
		Long cachedStartX = startXCache.get(Long.valueOf(localColumnPosition));
		if (cachedStartX != null) {
			return cachedStartX.longValue();
		}

		IUniqueIndexLayer underlyingLayer = (IUniqueIndexLayer) getUnderlyingLayer();
		long underlyingPosition = localToUnderlyingColumnPosition(localColumnPosition);
		if (underlyingPosition < 0) {
			return -1;
		}
		long underlyingStartX = underlyingLayer.getStartXOfColumnPosition(underlyingPosition);

		for (Long hiddenIndex : getHiddenColumnIndexes()) {
			long hiddenPosition = underlyingLayer.getColumnPositionByIndex(hiddenIndex.longValue());
			if (hiddenPosition <= underlyingPosition) {
				underlyingStartX -= underlyingLayer.getColumnWidthByPosition(hiddenPosition);
			}
		}

		startXCache.put(Long.valueOf(localColumnPosition), Long.valueOf(underlyingStartX));
		return underlyingStartX;
	}
	
	// Vertical features

	// Rows

	public long getRowPositionByIndex(long rowIndex) {
		return ((IUniqueIndexLayer) getUnderlyingLayer()).getRowPositionByIndex(rowIndex);
	}

	// Hide/show

	/**
	 * Will check if the column at the specified index is hidden or not. Checks this
	 * layer and also the sublayers for the visibility.
	 * Note: As the {@link ColumnGroup}s are created index based, this method only
	 * 		 works correctly with indexes rather than positions.
	 * @param columnIndex The column index of the column whose visibility state
	 * 			should be checked.
	 * @return <code>true</code> if the column at the specified index is hidden,
	 * 			<code>false</code> if it is visible.
	 */
	public abstract boolean isColumnIndexHidden(long columnIndex);

	/**
	 * Will collect and return all indexes of the columns that are hidden in this layer.
	 * Note: It is not intended that it also collects the column indexes of underlying
	 * 		 layers. This would cause issues on calculating positions as every layer
	 * 		 is responsible for those calculations itself. 
	 * @return Collection of all column indexes that are hidden in this layer.
	 */
	public abstract Collection<Long> getHiddenColumnIndexes();

	// Cache

	/**
	 * Invalidate the cache to ensure that information is rebuild.
	 */
	protected void invalidateCache() {
		cachedVisibleColumnIndexOrder = null;
		startXCache.clear();
	}

	private List<Long> getCachedVisibleColumnIndexes() {
		if (cachedVisibleColumnIndexOrder == null) {
			cacheVisibleColumnIndexes();
		}
		return cachedVisibleColumnIndexOrder;
	}

	private void cacheVisibleColumnIndexes() {
		cachedVisibleColumnIndexOrder = new ArrayList<Long>();
		cachedHiddenColumnIndexToPositionMap = new HashMap<Long, Long>();
		startXCache.clear();

		ILayer underlyingLayer = getUnderlyingLayer();
		long columnPosition = 0;
		for (long parentColumnPosition = 0; parentColumnPosition < underlyingLayer.getColumnCount(); parentColumnPosition++) {
			long columnIndex = underlyingLayer.getColumnIndexByPosition(parentColumnPosition);

			if (!isColumnIndexHidden(columnIndex)) {
				cachedVisibleColumnIndexOrder.add(Long.valueOf(columnIndex));
				columnPosition++;
			} else {
				cachedHiddenColumnIndexToPositionMap.put(Long.valueOf(columnIndex), Long.valueOf(columnPosition));
			}
		}
	}

}
