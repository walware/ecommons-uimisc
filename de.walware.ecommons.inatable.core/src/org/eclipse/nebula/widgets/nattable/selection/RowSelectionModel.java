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
// ~Selection
package org.eclipse.nebula.widgets.nattable.selection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.coordinate.RangeList;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IRowIdAccessor;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

public class RowSelectionModel<R> implements IRowSelectionModel<R> {

	protected final SelectionLayer selectionLayer;
	protected final IRowDataProvider<R> rowDataProvider;
	protected final IRowIdAccessor<R> rowIdAccessor;
	private boolean multipleSelectionAllowed;
	
	protected Map<Serializable, R> selectedRows;
	protected Rectangle lastSelectedRange;  // *live* reference to last range parameter used in addSelection(range)
	protected Set<Serializable> lastSelectedRowIds;
	protected final ReadWriteLock selectionsLock;

	public RowSelectionModel(SelectionLayer selectionLayer, IRowDataProvider<R> rowDataProvider, IRowIdAccessor<R> rowIdAccessor) {
		this(selectionLayer, rowDataProvider, rowIdAccessor, true);
	}
	
	public RowSelectionModel(SelectionLayer selectionLayer, IRowDataProvider<R> rowDataProvider, IRowIdAccessor<R> rowIdAccessor, boolean multipleSelectionAllowed) {
		this.selectionLayer = selectionLayer;
		this.rowDataProvider = rowDataProvider;
		this.rowIdAccessor = rowIdAccessor;
		this.multipleSelectionAllowed = multipleSelectionAllowed;
		
		selectedRows = new HashMap<Serializable, R>();
		selectionsLock = new ReentrantReadWriteLock();
	}
	
	public boolean isMultipleSelectionAllowed() {
		return multipleSelectionAllowed;
	}
	
	public void setMultipleSelectionAllowed(boolean multipleSelectionAllowed) {
		this.multipleSelectionAllowed = multipleSelectionAllowed;
	}

	public void addSelection(long columnPosition, long rowPosition) {
		selectionsLock.writeLock().lock();
		
		try {
			if (!multipleSelectionAllowed) {
				selectedRows.clear();
			}
			
			R rowObject = getRowObjectByPosition(rowPosition);
			if (rowObject != null) {
				Serializable rowId = rowIdAccessor.getRowId(rowObject);
				selectedRows.put(rowId, rowObject);
			}
		} finally {
			selectionsLock.writeLock().unlock();
		}
	}

	public void addSelection(Rectangle range) {
		selectionsLock.writeLock().lock();
		
		try {
			if (multipleSelectionAllowed) {
				if (range.equals(lastSelectedRange)) {
					// Unselect all previously selected rowIds
					if (lastSelectedRowIds != null) {
						for (Serializable rowId : lastSelectedRowIds) {
							selectedRows.remove(rowId);
						}
					}
				}
			} else {
				selectedRows.clear();
			}
			
			Map<Serializable, R> rowsToSelect = new HashMap<Serializable, R>();
			
			long maxY = Math.min(range.y + range.height, selectionLayer.getRowCount());
			for (long rowPosition = range.y; rowPosition < maxY; rowPosition++) {
				R rowObject = getRowObjectByPosition(rowPosition);
				if (rowObject != null) {
					Serializable rowId = rowIdAccessor.getRowId(rowObject);
					rowsToSelect.put(rowId, rowObject);
				}
			}
			
			selectedRows.putAll(rowsToSelect);
			
			if (range.equals(lastSelectedRange)) {
				lastSelectedRowIds = rowsToSelect.keySet();
			} else {
				lastSelectedRowIds = null;
			}
			
			lastSelectedRange = range;
		} finally {
			selectionsLock.writeLock().unlock();
		}
	}
	
	public void clearSelection() {
		selectionsLock.writeLock().lock();
		try {
			selectedRows.clear();
		} finally {
			selectionsLock.writeLock().unlock();
		}
	}

	public void clearSelection(long columnPosition, long rowPosition) {
		selectionsLock.writeLock().lock();
		
		try {
			Serializable rowId = getRowIdByPosition(rowPosition);
			selectedRows.remove(rowId);
		} finally {
			selectionsLock.writeLock().unlock();
		}
	}

	public void clearSelection(Rectangle removedSelection) {
		selectionsLock.writeLock().lock();
		
		try {
			long maxY = Math.min(removedSelection.y + removedSelection.height, selectionLayer.getRowCount());
			for (long rowPosition = removedSelection.y; rowPosition < maxY; rowPosition++) {
				clearSelection(0, rowPosition);
			}
		} finally {
			selectionsLock.writeLock().unlock();
		}
	}

	public void clearSelection(R rowObject) {
		selectionsLock.writeLock().lock();
		
		try {
			selectedRows.values().remove(rowObject);
		} finally {
			selectionsLock.writeLock().unlock();
		}
	};
	
	public boolean isEmpty() {
		selectionsLock.readLock().lock();
		
		try {
			return selectedRows.isEmpty();
		} finally {
			selectionsLock.readLock().unlock();
		}
	}

	public List<Rectangle> getSelections() {
		List<Rectangle> selectionRectangles = new ArrayList<Rectangle>();
		
		selectionsLock.readLock().lock();
		
		try {
			long width = selectionLayer.getColumnCount();
			for (Serializable rowId : selectedRows.keySet()) {
				long rowPosition = getRowPositionById(rowId);
				selectionRectangles.add(new Rectangle(0, rowPosition, width, 1));
			}
		} finally {
			selectionsLock.readLock().unlock();
		}
		
		return selectionRectangles;
	}
	
	// Cell features

	public boolean isCellPositionSelected(final ILayerCell cell) {
		long cellOriginRowPosition = cell.getOriginRowPosition();
		for (long testRowPosition = cellOriginRowPosition; testRowPosition < cellOriginRowPosition + cell.getRowSpan(); testRowPosition++) {
			if (isRowPositionSelected(testRowPosition)) {
				return true;
			}
		}
		return false;
	}
	
	// Column features

	public List<Range> getSelectedColumnPositions() {
		selectionsLock.readLock().lock();
		
		try {
			RangeList selected = new RangeList();
			if (!selectedRows.isEmpty()) {
				selected.add(new Range(0, selectionLayer.getColumnCount()));
			}
			return selected;
		}
		finally {
			selectionsLock.readLock().unlock();
		}
	}
	
	public boolean isColumnPositionSelected(long columnPosition) {
		selectionsLock.readLock().lock();
		
		try {
			return !selectedRows.isEmpty();
		} finally {
			selectionsLock.readLock().unlock();
		}
	}

	public List<Range> getFullySelectedColumnPositions() {
		selectionsLock.readLock().lock();
		
		try {
			if (isColumnPositionFullySelected(0)) {
				return getSelectedColumnPositions();
			}
		} finally {
			selectionsLock.readLock().unlock();
		}
		
		return Collections.emptyList();
	}

	public boolean isColumnPositionFullySelected(long columnPosition) {
		selectionsLock.readLock().lock();
		long fullySelectedColumnRowCount = selectionLayer.getRowCount();
		try {
			long selectedRowCount = selectedRows.size();
			
			if (selectedRowCount == 0) {
				return false;
			}
			
			return selectedRowCount == fullySelectedColumnRowCount;
		} finally {
			selectionsLock.readLock().unlock();
		}
	}
	
	// Row features
	
	public List<R> getSelectedRowObjects() {
		final List<R> rowObjects = new ArrayList<R>();

		this.selectionsLock.readLock().lock();
		try {
			rowObjects.addAll(this.selectedRows.values());
		} finally {
			this.selectionsLock.readLock().unlock();
		}

		return rowObjects;
	}

	public long getSelectedRowCount() {
		selectionsLock.readLock().lock();
		
		try {
			return selectedRows.size();
		} finally {
			selectionsLock.readLock().unlock();
		}
	}

	public List<Range> getSelectedRowPositions() {
		
		selectionsLock.readLock().lock();
		
		try {
			RangeList selected = new RangeList();
			
			for (Serializable rowId : selectedRows.keySet()) {
				long rowPosition = getRowPositionById(rowId);
				selected.addValue(rowPosition);
			}
			
			return selected;
		} finally {
			selectionsLock.readLock().unlock();
		}
	}

	public boolean isRowPositionSelected(long rowPosition) {
		selectionsLock.readLock().lock();
		
		try {
			Serializable rowId = getRowIdByPosition(rowPosition);
			return selectedRows.containsKey(rowId);
		} finally {
			selectionsLock.readLock().unlock();
		}
	}

	public List<Range> getFullySelectedRowPositions() {
		return getSelectedRowPositions();
	}

	public boolean isRowPositionFullySelected(long rowPosition) {
		return isRowPositionSelected(rowPosition);
	}

	private Serializable getRowIdByPosition(long rowPosition) {
		R rowObject = getRowObjectByPosition(rowPosition);
		if (rowObject != null) {
			Serializable rowId = rowIdAccessor.getRowId(rowObject);
			return rowId;
		}
		return null;
	}

	private R getRowObjectByPosition(long rowPosition) {
		selectionsLock.readLock().lock();
		
		try {
			long rowIndex = selectionLayer.getRowIndexByPosition(rowPosition);
			if (rowIndex >= 0) {
				try {
					R rowObject = rowDataProvider.getRowObject(rowIndex);
					return rowObject;
				} catch (Exception e) {
					// row index is invalid for the data provider
				}
			}
		} finally {
			selectionsLock.readLock().unlock();
		}
		
		return null;
	}
	
	private long getRowPositionById(Serializable rowId) {
		selectionsLock.readLock().lock();
		
		try {
			R rowObject = selectedRows.get(rowId);
			long rowIndex = rowDataProvider.indexOfRowObject(rowObject);
			if(rowIndex < 0){
				return Long.MIN_VALUE;
			}
			long rowPosition = selectionLayer.getRowPositionByIndex(rowIndex);
			return rowPosition;
		} finally {
			selectionsLock.readLock().unlock();
		}
	}
	
}
