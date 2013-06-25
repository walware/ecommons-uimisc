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
import org.eclipse.nebula.widgets.nattable.layer.event.ColumnStructuralRefreshEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.IStructuralChangeEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.StructuralDiff;
import org.eclipse.nebula.widgets.nattable.reorder.command.ColumnReorderCommandHandler;
import org.eclipse.nebula.widgets.nattable.reorder.command.ColumnReorderEndCommandHandler;
import org.eclipse.nebula.widgets.nattable.reorder.command.ColumnReorderStartCommandHandler;
import org.eclipse.nebula.widgets.nattable.reorder.command.MultiColumnReorderCommandHandler;
import org.eclipse.nebula.widgets.nattable.reorder.config.DefaultColumnReorderLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.reorder.event.ColumnReorderEvent;


/**
 * Adds functionality for reordering column(s)
 * Also responsible for saving/loading the column order state.
 * 
 * @see DefaultColumnReorderLayerConfiguration
 */
public class ColumnReorderLayer extends AbstractLayerTransform implements IUniqueIndexLayer {

	public static final String PERSISTENCE_KEY_COLUMN_INDEX_ORDER = ".columnIndexOrder"; //$NON-NLS-1$

	private final IUniqueIndexLayer underlyingLayer;

	// Position X in the List contains the index of column at position X
	private final List<Long> columnIndexOrder = new ArrayList<Long>();

	private final Map<Long, Long> startXCache = new HashMap<Long, Long>();

	private long reorderFromColumnPosition;

	public ColumnReorderLayer(IUniqueIndexLayer underlyingLayer) {
		this(underlyingLayer, true);
	}

	public ColumnReorderLayer(IUniqueIndexLayer underlyingLayer, boolean useDefaultConfiguration) {
		super(underlyingLayer);
		this.underlyingLayer = underlyingLayer;

		populateIndexOrder();

		registerCommandHandlers();

		if (useDefaultConfiguration) {
			addConfiguration(new DefaultColumnReorderLayerConfiguration());
		}
	}

	@Override
	public void handleLayerEvent(ILayerEvent event) {
		if (event instanceof IStructuralChangeEvent) {
			IStructuralChangeEvent structuralChangeEvent = (IStructuralChangeEvent) event;
			if (structuralChangeEvent.isHorizontalStructureChanged()) {
				Collection<StructuralDiff> structuralDiffs = structuralChangeEvent.getColumnDiffs();
				if (structuralDiffs == null) {
					// Assume everything changed
					columnIndexOrder.clear();
					populateIndexOrder();
				} else {
					for (StructuralDiff structuralDiff : structuralDiffs) {
						switch (structuralDiff.getDiffType()) {
						case ADD:
							columnIndexOrder.clear();
							populateIndexOrder();
							break;
						case DELETE:
							columnIndexOrder.clear();
							populateIndexOrder();
							break;
						}
					}
				}
				invalidateCache();
			}
		}
		super.handleLayerEvent(event);
	}
	
	// Configuration
	
	@Override
	protected void registerCommandHandlers() {
		registerCommandHandler(new ColumnReorderCommandHandler(this));
		registerCommandHandler(new ColumnReorderStartCommandHandler(this));
		registerCommandHandler(new ColumnReorderEndCommandHandler(this));
		registerCommandHandler(new MultiColumnReorderCommandHandler(this));
	}

	// Persistence

	@Override
	public void saveState(String prefix, Properties properties) {
		super.saveState(prefix, properties);
		if (columnIndexOrder.size() > 0) {
			StringBuilder strBuilder = new StringBuilder();
			for (Long index : columnIndexOrder) {
				strBuilder.append(index);
				strBuilder.append(',');
			}
			properties.setProperty(prefix + PERSISTENCE_KEY_COLUMN_INDEX_ORDER, strBuilder.toString());
		}
	}

	@Override
	public void loadState(String prefix, Properties properties) {
		super.loadState(prefix, properties);
		String property = properties.getProperty(prefix + PERSISTENCE_KEY_COLUMN_INDEX_ORDER);

		if (property != null) {
			List<Long> newColumnIndexOrder = new ArrayList<Long>();
			StringTokenizer tok = new StringTokenizer(property, ","); //$NON-NLS-1$
			while (tok.hasMoreTokens()) {
				String index = tok.nextToken();
				newColumnIndexOrder.add(Long.valueOf(index));
			}
			
			if(isRestoredStateValid(newColumnIndexOrder)){
				columnIndexOrder.clear();
				columnIndexOrder.addAll(newColumnIndexOrder);
			}
		}
		fireLayerEvent(new ColumnStructuralRefreshEvent(this));
	}

	/**
	 * Ensure that columns haven't changed in the underlying data source
	 * @param newColumnIndexOrder restored from the properties file.
	 */
	protected boolean isRestoredStateValid(List<Long> newColumnIndexOrder) {
		if (newColumnIndexOrder.size() != getColumnCount()){
			System.err.println(
				"Number of persisted columns (" + newColumnIndexOrder.size() + ") " + //$NON-NLS-1$ //$NON-NLS-2$
				"is not the same as the number of columns in the data source (" + getColumnCount() + ").\n" + //$NON-NLS-1$ //$NON-NLS-2$
				"Skipping restore of column ordering"); //$NON-NLS-1$
			return false;
		}
		
		for (Long index : newColumnIndexOrder) {
			if(!columnIndexOrder.contains(index)){
				System.err.println(
					"Column index: " + index + " being restored, is not a available in the data soure.\n" + //$NON-NLS-1$ //$NON-NLS-2$
					"Skipping restore of column ordering"); //$NON-NLS-1$
				return false;
			}
		}
		return true;
	}

	// Columns

	public List<Long> getColumnIndexOrder() {
		return columnIndexOrder;
	}

	@Override
	public long getColumnIndexByPosition(long columnPosition) {
		if (columnPosition >= 0 && columnPosition < columnIndexOrder.size()) {
			return columnIndexOrder.get((int) columnPosition).longValue();
		} else {
			return NO_INDEX;
		}
	}

	@Override
	public long getColumnPositionByIndex(long columnIndex) {
		if (columnIndex > Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		return columnIndexOrder.indexOf(Long.valueOf((int) columnIndex));
	}

	@Override
	public long localToUnderlyingColumnPosition(long localColumnPosition) {
		long columnIndex = getColumnIndexByPosition(localColumnPosition);
		return underlyingLayer.getColumnPositionByIndex(columnIndex);
	}

	@Override
	public long underlyingToLocalColumnPosition(ILayer sourceUnderlyingLayer, long underlyingColumnPosition) {
		long columnIndex = underlyingLayer.getColumnIndexByPosition(underlyingColumnPosition);
		return getColumnPositionByIndex(columnIndex);
	}

	@Override
	public Collection<Range> underlyingToLocalColumnPositions(ILayer sourceUnderlyingLayer, Collection<Range> underlyingColumnPositionRanges) {
		List<Long> reorderedColumnPositions = new ArrayList<Long>();
		for (Range underlyingColumnPositionRange : underlyingColumnPositionRanges) {
			for (long underlyingColumnPosition = underlyingColumnPositionRange.start; underlyingColumnPosition < underlyingColumnPositionRange.end; underlyingColumnPosition++) {
				long localColumnPosition = underlyingToLocalColumnPosition(sourceUnderlyingLayer, underlyingColumnPositionRange.start);
				reorderedColumnPositions.add(Long.valueOf(localColumnPosition));
			}
		}
		Collections.sort(reorderedColumnPositions);
		
		return PositionUtil.getRanges(reorderedColumnPositions);
	}
	
	// X

	@Override
	public long getColumnPositionByX(long x) {
		return LayerUtil.getColumnPositionByX(this, x);
	}

	@Override
	public long getStartXOfColumnPosition(long targetColumnPosition) {
		if (targetColumnPosition >= Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		Long cachedStartX = startXCache.get(Long.valueOf((int) targetColumnPosition));
		if (cachedStartX != null) {
			return cachedStartX.longValue();
		}

		long aggregateWidth = 0;
		for (int columnPosition = 0; columnPosition < targetColumnPosition; columnPosition++) {
			aggregateWidth += underlyingLayer.getColumnWidthByPosition(localToUnderlyingColumnPosition(columnPosition));
		}

		startXCache.put(Long.valueOf(targetColumnPosition), Long.valueOf(aggregateWidth));
		return aggregateWidth;
	}
	
	private void populateIndexOrder() {
		ILayer underlyingLayer = getUnderlyingLayer();
		for (long columnPosition = 0; columnPosition < underlyingLayer.getColumnCount(); columnPosition++) {
			columnIndexOrder.add(Long.valueOf(underlyingLayer.getColumnIndexByPosition(columnPosition)));
		}
	}

	// Vertical features

	// Rows

	@Override
	public long getRowPositionByIndex(long rowIndex) {
		return underlyingLayer.getRowPositionByIndex(rowIndex);
	}

	/**
	 * Moves the column to the <i>LEFT</i> of the toColumnPosition
	 * @param fromColumnPosition column position to move
	 * @param toColumnPosition position to move the column to
	 */
	private void moveColumn(int fromColumnPosition, int toColumnPosition, boolean reorderToLeftEdge) {
		if (!reorderToLeftEdge) {
			toColumnPosition++;
		}
		
		Long fromColumnIndex = columnIndexOrder.get(fromColumnPosition);
		columnIndexOrder.add(toColumnPosition, fromColumnIndex);

		columnIndexOrder.remove(fromColumnPosition + (fromColumnPosition > toColumnPosition ? 1 : 0));
		invalidateCache();
	}

	public void reorderColumnPosition(long fromColumnPosition, long toColumnPosition) {
		boolean reorderToLeftEdge;
		if (toColumnPosition < getColumnCount()) {
 			reorderToLeftEdge = true;
		} else {
			reorderToLeftEdge = false;
			toColumnPosition--;
		}
		reorderColumnPosition(fromColumnPosition, toColumnPosition, reorderToLeftEdge);
	}
	
	public void reorderColumnPosition(long fromColumnPosition, long toColumnPosition, boolean reorderToLeftEdge) {
		if (fromColumnPosition >= Integer.MAX_VALUE || toColumnPosition >= Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		moveColumn((int) fromColumnPosition, (int) toColumnPosition, reorderToLeftEdge);
		fireLayerEvent(new ColumnReorderEvent(this, fromColumnPosition, toColumnPosition, reorderToLeftEdge));
	}

	public void reorderMultipleColumnPositions(List<Long> fromColumnPositions, long toColumnPosition) {
		boolean reorderToLeftEdge;
		if (toColumnPosition < getColumnCount()) {
 			reorderToLeftEdge = true;
		} else {
			reorderToLeftEdge = false;
			toColumnPosition--;
		}
		reorderMultipleColumnPositions(fromColumnPositions, toColumnPosition, reorderToLeftEdge);
	}
	
	public void reorderMultipleColumnPositions(List<Long> fromColumnPositions, long toColumnPosition, boolean reorderToLeftEdge) {
		if (toColumnPosition >= Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		// Moving from left to right
		final int fromColumnPositionsCount = fromColumnPositions.size();

		if (toColumnPosition > fromColumnPositions.get(fromColumnPositionsCount - 1).longValue()) {
			long firstColumnPosition = fromColumnPositions.get(0).longValue();

			for (int columnCount = 0; columnCount < fromColumnPositionsCount; columnCount++) {
				final long fromColumnPosition = fromColumnPositions.get(0).longValue();
				if (fromColumnPosition >= Integer.MAX_VALUE) {
					throw new IndexOutOfBoundsException();
				}
				moveColumn((int) fromColumnPosition, (int) toColumnPosition, reorderToLeftEdge);
				if (fromColumnPosition < firstColumnPosition) {
					firstColumnPosition = fromColumnPosition;
				}
			}
		} else if (toColumnPosition < fromColumnPositions.get(fromColumnPositionsCount - 1).longValue()) {
			// Moving from right to left
			int targetColumnPosition = (int) toColumnPosition;
			for (Long fromColumnPosition : fromColumnPositions) {
				final int fromColumnPositionInt = fromColumnPosition.intValue();
				moveColumn(fromColumnPositionInt, targetColumnPosition++, reorderToLeftEdge);
			}
		}

		fireLayerEvent(new ColumnReorderEvent(this, fromColumnPositions, toColumnPosition, reorderToLeftEdge));
	}

	private void invalidateCache() {
		startXCache.clear();
	}

	public long getReorderFromColumnPosition() {
		return reorderFromColumnPosition;
	}
	
	public void setReorderFromColumnPosition(long fromColumnPosition) {
		this.reorderFromColumnPosition = fromColumnPosition;
	}

}
