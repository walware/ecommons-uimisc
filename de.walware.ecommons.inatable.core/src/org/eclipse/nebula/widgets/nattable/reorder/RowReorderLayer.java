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
package org.eclipse.nebula.widgets.nattable.reorder;

import static org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell.NO_INDEX;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.nebula.widgets.nattable.coordinate.PositionUtil;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.IStructuralChangeEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.RowStructuralRefreshEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.StructuralChangeEventHelper;
import org.eclipse.nebula.widgets.nattable.layer.event.StructuralDiff;
import org.eclipse.nebula.widgets.nattable.persistence.IPersistable;
import org.eclipse.nebula.widgets.nattable.reorder.command.MultiRowReorderCommandHandler;
import org.eclipse.nebula.widgets.nattable.reorder.command.RowReorderCommandHandler;
import org.eclipse.nebula.widgets.nattable.reorder.command.RowReorderEndCommandHandler;
import org.eclipse.nebula.widgets.nattable.reorder.command.RowReorderStartCommandHandler;
import org.eclipse.nebula.widgets.nattable.reorder.config.DefaultRowReorderLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.reorder.event.RowReorderEvent;


/**
 * Adds functionality for reordering rows(s)
 * Also responsible for saving/loading the row order state.
 * 
 * @see DefaultRowReorderLayerConfiguration
 */
public class RowReorderLayer extends AbstractLayerTransform implements IUniqueIndexLayer {

	public static final String PERSISTENCE_KEY_ROW_INDEX_ORDER = ".rowIndexOrder"; //$NON-NLS-1$

	private final IUniqueIndexLayer underlyingLayer;

	/**
	 * The local cache of the row index order. Used to track the reordering performed by this layer.
	 * Position Y in the List contains the index of row at position Y.
	 */
	private final List<Long> rowIndexOrder = new ArrayList<Long>();

	/**
	 * Caching of the starting y positions of the rows.
	 * Used to reduce calculation time on rendering
	 */
	private final Map<Long, Long> startYCache = new HashMap<Long, Long>();

	/**
	 * Local cached position of the row that is currently reordered.
	 */
	private long reorderFromRowPosition;

	public RowReorderLayer(IUniqueIndexLayer underlyingLayer) {
		this(underlyingLayer, true);
	}

	public RowReorderLayer(IUniqueIndexLayer underlyingLayer, boolean useDefaultConfiguration) {
		super(underlyingLayer);
		this.underlyingLayer = underlyingLayer;

		populateIndexOrder();

		registerCommandHandlers();

		if (useDefaultConfiguration) {
			addConfiguration(new DefaultRowReorderLayerConfiguration());
		}
	}

	@Override
	public void handleLayerEvent(ILayerEvent event) {
		if (event instanceof IStructuralChangeEvent) {
			IStructuralChangeEvent structuralChangeEvent = (IStructuralChangeEvent) event;
			if (structuralChangeEvent.isVerticalStructureChanged()) {
				Collection<StructuralDiff> structuralDiffs = structuralChangeEvent.getRowDiffs();
				if (structuralDiffs == null) {
					// Assume everything changed
					rowIndexOrder.clear();
					populateIndexOrder();
				} 
				else {
					// only react on ADD or DELETE and not on CHANGE
					StructuralChangeEventHelper.handleRowDelete(structuralDiffs, underlyingLayer, rowIndexOrder, true);
					StructuralChangeEventHelper.handleRowInsert(structuralDiffs, underlyingLayer, rowIndexOrder, true);
				}
				invalidateCache();
			}
		}
		super.handleLayerEvent(event);
	}
	
	// Configuration
	
	@Override
	protected void registerCommandHandlers() {
		registerCommandHandler(new RowReorderCommandHandler(this));
		registerCommandHandler(new RowReorderStartCommandHandler(this));
		registerCommandHandler(new RowReorderEndCommandHandler(this));
		registerCommandHandler(new MultiRowReorderCommandHandler(this));
	}

	// Persistence

	@Override
	public void saveState(String prefix, Properties properties) {
		super.saveState(prefix, properties);
		if (rowIndexOrder.size() > 0) {
			StringBuilder strBuilder = new StringBuilder();
			for (Long index : rowIndexOrder) {
				strBuilder.append(index);
				strBuilder.append(IPersistable.VALUE_SEPARATOR);
			}
			properties.setProperty(prefix + PERSISTENCE_KEY_ROW_INDEX_ORDER, strBuilder.toString());
		}
	}

	@Override
	public void loadState(String prefix, Properties properties) {
		super.loadState(prefix, properties);
		String property = properties.getProperty(prefix + PERSISTENCE_KEY_ROW_INDEX_ORDER);

		if (property != null) {
			List<Long> newRowIndexOrder = new ArrayList<Long>();
			StringTokenizer tok = new StringTokenizer(property, IPersistable.VALUE_SEPARATOR);
			while (tok.hasMoreTokens()) {
				String index = tok.nextToken();
				newRowIndexOrder.add(Long.valueOf(index));
			}
			
			if(isRestoredStateValid(newRowIndexOrder)){
				rowIndexOrder.clear();
				rowIndexOrder.addAll(newRowIndexOrder);
			}
		}
		fireLayerEvent(new RowStructuralRefreshEvent(this));
	}

	/**
	 * Ensure that rows haven't changed in the underlying data source
	 * @param newRowIndexOrder restored from the properties file.
	 */
	protected boolean isRestoredStateValid(List<Long> newRowIndexOrder) {
		if (newRowIndexOrder.size() != getRowCount()){
			System.err.println(
				"Number of persisted rows (" + newRowIndexOrder.size() + ") " + //$NON-NLS-1$ //$NON-NLS-2$
				"is not the same as the number of rows in the data source (" + getRowCount() + ").\n" + //$NON-NLS-1$ //$NON-NLS-2$
				"Skipping restore of row ordering"); //$NON-NLS-1$
			return false;
		}
		
		for (Long index : newRowIndexOrder) {
			if(!rowIndexOrder.contains(index)){
				System.err.println(
					"Row index: " + index + " being restored, is not a available in the data soure.\n" + //$NON-NLS-1$ //$NON-NLS-2$
					"Skipping restore of row ordering"); //$NON-NLS-1$
				return false;
			}
		}
		return true;
	}

	// Columns

	@Override
	public long getColumnPositionByIndex(long columnIndex) {
		return underlyingLayer.getColumnPositionByIndex(columnIndex);
	}
	
	// Y

	@Override
	public long getRowPositionByY(long y) {
		return LayerUtil.getRowPositionByY(this, y);
	}

	@Override
	public long getStartYOfRowPosition(long targetRowPosition) {
		Long cachedStartY = startYCache.get(targetRowPosition);
		if (cachedStartY != null) {
			return cachedStartY;
		}

		long aggregateWidth = 0;
		for (long rowPosition = 0; rowPosition < targetRowPosition; rowPosition++) {
			aggregateWidth += underlyingLayer.getRowHeightByPosition(localToUnderlyingRowPosition(rowPosition));
		}

		startYCache.put(targetRowPosition, aggregateWidth);
		return aggregateWidth;
	}

	/**
	 * Initially populate the index order to the local cache.
	 */
	private void populateIndexOrder() {
		ILayer underlyingLayer = getUnderlyingLayer();
		for (long rowPosition = 0; rowPosition < underlyingLayer.getRowCount(); rowPosition++) {
			rowIndexOrder.add(underlyingLayer.getRowIndexByPosition(rowPosition));
		}
	}

	// Vertical features

	// Rows
	/**
	 * @return The local cache of the row index order.
	 */
	public List<Long> getRowIndexOrder() {
		return rowIndexOrder;
	}
	
	@Override
	public long getRowIndexByPosition(long rowPosition) {
		if (rowPosition >= 0 && rowPosition < rowIndexOrder.size()) {
			return rowIndexOrder.get((int) rowPosition).longValue();
		} else {
			return NO_INDEX;
		}
	}

	@Override
	public long getRowPositionByIndex(long rowIndex) {
		return rowIndexOrder.indexOf(Long.valueOf(rowIndex));
	}

	@Override
	public long localToUnderlyingRowPosition(long localRowPosition) {
		long rowIndex = getRowIndexByPosition(localRowPosition);
		return underlyingLayer.getRowPositionByIndex(rowIndex);
	}

	@Override
	public long underlyingToLocalRowPosition(ILayer sourceUnderlyingLayer, long underlyingRowPosition) {
		long rowIndex = underlyingLayer.getRowIndexByPosition(underlyingRowPosition);
		return getRowPositionByIndex(rowIndex);
	}

	@Override
	public Collection<Range> underlyingToLocalRowPositions(ILayer sourceUnderlyingLayer, Collection<Range> underlyingRowPositionRanges) {
		List<Long> reorderedRowPositions = new ArrayList<Long>();
		for (Range underlyingRowPositionRange : underlyingRowPositionRanges) {
			for (long underlyingRowPosition = underlyingRowPositionRange.start; underlyingRowPosition < underlyingRowPositionRange.end; underlyingRowPosition++) {
				long localRowPosition = underlyingToLocalRowPosition(sourceUnderlyingLayer, underlyingRowPositionRange.start);
				reorderedRowPositions.add(localRowPosition);
			}
		}
		Collections.sort(reorderedRowPositions);
		
		return PositionUtil.getRanges(reorderedRowPositions);
	}

	/**
	 * Moves the row at the given from position to the <i>TOP</i> of the of the given to position.
	 * This is the internal implementation for reordering a row.
	 * @param fromRowPosition row position to move
	 * @param toRowPosition position to move the row to
	 * @param reorderToTopEdge whether the move should be done above the given to position or not
	 */
	private void moveRow(int fromRowPosition, int toRowPosition, boolean reorderToTopEdge) {
		if (!reorderToTopEdge) {
			toRowPosition++;
		}
		
		Long fromRowIndex = rowIndexOrder.get(fromRowPosition);
		rowIndexOrder.add(toRowPosition, fromRowIndex);

		rowIndexOrder.remove(fromRowPosition + (fromRowPosition > toRowPosition ? 1 : 0));
		invalidateCache();
	}

	/**
	 * Reorders the row at the given from position to the <i>TOP</i> of the of the given to position.
	 * Will calculate whether the move is done above the to position or not regarding the position in
	 * the NatTable.
	 * @param fromRowPosition row position to move
	 * @param toRowPosition position to move the row to
	 */
	public void reorderRowPosition(int fromRowPosition, int toRowPosition) {
		boolean reorderToTopEdge;
		if (toRowPosition < getRowCount()) {
 			reorderToTopEdge = true;
		} else {
			reorderToTopEdge = false;
			toRowPosition--;
		}
		reorderRowPosition(fromRowPosition, toRowPosition, reorderToTopEdge);
	}
	
	/**
	 * Reorders the row at the given from position to the <i>TOP</i> of the of the given to position.
	 * @param fromRowPosition row position to move
	 * @param toRowPosition position to move the row to
	 * @param reorderToTopEdge whether the move should be done above the given to position or not
	 */
	public void reorderRowPosition(long fromRowPosition, long toRowPosition, boolean reorderToTopEdge) {
		if (fromRowPosition >= Integer.MAX_VALUE || toRowPosition >= Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		moveRow((int) fromRowPosition, (int) toRowPosition, reorderToTopEdge);
		fireLayerEvent(new RowReorderEvent(this, fromRowPosition, toRowPosition, reorderToTopEdge));
	}

	/**
	 * Reorders the rows at the given from positions to the <i>TOP</i> of the of the given to position.
	 * Will calculate whether the move is done above the to position or not regarding the position in
	 * the NatTable.
	 * @param fromRowPositions row positions to move
	 * @param toRowPosition position to move the rows to
	 */
	public void reorderMultipleRowPositions(List<Long> fromRowPositions, long toRowPosition) {
		boolean reorderToTopEdge;
		if (toRowPosition < getRowCount()) {
 			reorderToTopEdge = true;
		} else {
			reorderToTopEdge = false;
			toRowPosition--;
		}
		reorderMultipleRowPositions(fromRowPositions, toRowPosition, reorderToTopEdge);
	}
	
	/**
	 * Reorders the rows at the given from positions to the <i>TOP</i> of the of the given to position.
	 * @param fromRowPositions row positions to move
	 * @param toRowPosition position to move the rows to
	 * @param reorderToTopEdge whether the move should be done above the given to position or not
	 */
	public void reorderMultipleRowPositions(List<Long> fromRowPositions, long toRowPosition, boolean reorderToTopEdge) {
		if (toRowPosition >= Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		final int fromRowPositionsCount = fromRowPositions.size();

		if (toRowPosition > fromRowPositions.get(fromRowPositionsCount - 1)) {
			// Moving from top to bottom
			long firstRowPosition = fromRowPositions.get(0).longValue();

			for (long rowCount = 0; rowCount < fromRowPositionsCount; rowCount++) {
				final long fromRowPosition = fromRowPositions.get(0).longValue();
				if (fromRowPosition >= Integer.MAX_VALUE) {
					throw new IndexOutOfBoundsException();
				}
				moveRow((int) fromRowPosition, (int) toRowPosition, reorderToTopEdge);
				if (fromRowPosition < firstRowPosition) {
					firstRowPosition = fromRowPosition;
				}
			}
		} 
		else if (toRowPosition < fromRowPositions.get(fromRowPositionsCount - 1)) {
			// Moving from bottom to top
			int targetRowPosition = (int) toRowPosition;
			for (Long fromRowPosition : fromRowPositions) {
				final int fromRowPositionInt = fromRowPosition.intValue();
				moveRow(fromRowPositionInt, targetRowPosition++, reorderToTopEdge);
			}
		}

		fireLayerEvent(new RowReorderEvent(this, fromRowPositions, toRowPosition, reorderToTopEdge));
	}

	/**
	 * Clear the caching of the starting Y positions
	 */
	private void invalidateCache() {
		startYCache.clear();
	}

	/**
	 * @return Local cached position of the row that is currently reordered.
	 */
	public long getReorderFromRowPosition() {
		return reorderFromRowPosition;
	}
	
	/**
	 * Locally cache the position of the row that is currently reordered.
	 * @param fromRowPosition Position of the row that is currently reordered.
	 */
	public void setReorderFromRowPosition(long fromRowPosition) {
		this.reorderFromRowPosition = fromRowPosition;
	}

}
