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
package de.walware.ecommons.waltable.selection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.LRangeList;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;


/**
 * Tracks the selections made in the table. All selections are tracked in terms of
 * Rectangles.
 *  
 * For example if the table has 10 rows and column 2 is selected, the 
 * LRectangle tracked is (0, 2, 10, 1)
 * 
 * Coordinates are in <i>Selection Layer positions</i>
 * 
 * @see SelectionLayer
 */
public class SelectionModel implements ISelectionModel {
	
	
	//-- Utility --//
	
	private static final LRectangle getLeftSelection(final LRectangle intersection, final LRectangle selection) {
		if (intersection.x > selection.x) {
			return new LRectangle(selection.x, selection.y,
					intersection.x - selection.x, selection.height);
		}
		return null;
	}
	
	private static final LRectangle getRightSelection(final LRectangle intersection, final LRectangle selection) {
		final long newX= intersection.x + intersection.width;
		if (newX < selection.x + selection.width) {
			return new LRectangle(newX, selection.y,
					selection.x + selection.width - newX, selection.height);
		}
		return null;
	}
	
	private static final LRectangle getTopSelection(final LRectangle intersection, final LRectangle selection) {
		if (intersection.y > selection.y) {
			return new LRectangle(selection.x, selection.y,
					selection.width, intersection.y - selection.y);
		}
		return null;
	}
	
	private static final LRectangle getBottomSelection(final LRectangle intersection, final LRectangle selection) {
		final long newY= intersection.y + intersection.height;
		if (newY < selection.y + selection.height) {
			return new LRectangle(selection.x, newY,
					selection.width, selection.y + selection.height - newY);
		}
		return null;
	}
	
	
	private final ILayer selectionLayer;
	
	private boolean multipleSelectionAllowed;
	
	private final List<LRectangle> selections;
	private final ReadWriteLock selectionsLock;
	
	
	public SelectionModel(/*@NotNull*/ final ILayer selectionLayer) {
		this(selectionLayer, true);
	}
	
	public SelectionModel(/*@NotNull*/ final ILayer selectionLayer, final boolean multipleSelectionAllowed) {
		if (selectionLayer == null) {
			throw new NullPointerException("selectionLayer"); //$NON-NLS-1$
		}
		this.selectionLayer= selectionLayer;
		this.multipleSelectionAllowed= multipleSelectionAllowed;
		
		this.selections= new LinkedList<>();
		this.selectionsLock= new ReentrantReadWriteLock();
	}
	
	
	@Override
	public boolean isMultipleSelectionAllowed() {
		return this.multipleSelectionAllowed;
	}
	
	public void setMultipleSelectionAllowed(final boolean multipleSelectionAllowed) {
		this.multipleSelectionAllowed= multipleSelectionAllowed;
	}
	
	
	@Override
	public void addSelection(final long columnPosition, final long rowPosition) {
		addSelectionIntoList(new LRectangle(columnPosition, rowPosition, 1, 1));
	}
	
	@Override
	public void addSelection(final LRectangle positions) {
		if (positions != null) {
			addSelectionIntoList(positions);
		}
	}
	
	private void addSelectionIntoList(final LRectangle selection) {
		this.selectionsLock.writeLock().lock();
		try {
			if (this.multipleSelectionAllowed) {
				ArrayList<LRectangle> itemsToRemove= null;
				for (final LRectangle r : this.selections) {
					if (selection.intersects(r)) {
						if (r.equals(selection)) {
							break;
						}
						
						final LRectangle intersection= selection.intersection(r);
						if (intersection.equals(r)) {
							// r is a subset of intersection
							if (itemsToRemove == null) {
								itemsToRemove= new ArrayList<>();
							}
							
							itemsToRemove.add(r);
						} else if (intersection.equals(selection)) {
							// selection is a subset of r
							break;
						}
					}
				}
				
				if (itemsToRemove != null) {
					this.selections.removeAll(itemsToRemove);
				}
			} else {
				this.selections.clear();
				//as no multiple selection is allowed, ensure that only one column 
				//and one row will be selected
				selection.height= 1;
				selection.width= 1;
			}
			
			this.selections.add(selection);
		} finally {
			this.selectionsLock.writeLock().unlock();
		}
	}
	
	@Override
	public void clearSelection() {
		this.selectionsLock.writeLock().lock();
		try {
			this.selections.clear();
		} finally {
			this.selectionsLock.writeLock().unlock();
		}
	}
	
	@Override
	public void clearSelection(final long columnPosition, final long rowPosition) {
		clearSelection(new LRectangle(columnPosition, rowPosition, 1, 1));
	}
	
	@Override
	public void clearSelection(final LRectangle positions) {
		final List<LRectangle> removedItems= new LinkedList<>();
		final List<LRectangle> addedItems= new LinkedList<>();
		
		this.selectionsLock.readLock().lock();
		try {
			for (final LRectangle r : this.selections) {
				if (r.intersects(positions)) {
					final LRectangle intersection= positions.intersection(r);
					removedItems.add(r);
					
					final LRectangle topSelection= getTopSelection(intersection, r);
					if (topSelection != null) {
						addedItems.add(topSelection);
					}
					final LRectangle rightSelection= getRightSelection(intersection, r);
					if (rightSelection != null) {
						addedItems.add(rightSelection);
					}
					final LRectangle leftSelection= getLeftSelection(intersection, r);
					if (leftSelection != null) {
						addedItems.add(leftSelection);
					}
					final LRectangle bottomSelection= getBottomSelection(intersection, r);
					if (bottomSelection != null) {
						addedItems.add(bottomSelection);
					}
				}
			}
		} finally {
			this.selectionsLock.readLock().unlock();
		}
		
		if (removedItems.size() > 0) {
			this.selectionsLock.writeLock().lock();
			try {
				this.selections.removeAll(removedItems);
			} finally {
				this.selectionsLock.writeLock().unlock();
			}
			
			removedItems.clear();
		}
		
		if (addedItems.size() > 0) {
			this.selectionsLock.writeLock().lock();
			try {
				this.selections.addAll(addedItems);
			} finally {
				this.selectionsLock.writeLock().unlock();
			}
			
			addedItems.clear();
		}
	}
	
	
	@Override
	public boolean isEmpty() {
		this.selectionsLock.readLock().lock();
		try {
			return this.selections.isEmpty();
		} finally {
			this.selectionsLock.readLock().unlock();
		}
	}
	
	@Override
	public List<LRectangle> getSelections() {
		return this.selections;
	}
	
	// Cell features
	
	@Override
	public boolean isCellPositionSelected(final ILayerCell cell) {
		this.selectionsLock.readLock().lock();
		try {
			final LRectangle cellRectangle= new LRectangle(
					cell.getOriginColumnPosition(),
					cell.getOriginRowPosition(),
					cell.getColumnSpan(),
					cell.getRowSpan());
			
			for (final LRectangle selectionRectangle : this.selections) {
				if (selectionRectangle.intersects(cellRectangle)) {
					return true;
				}
			}
			
			return false;
		} finally {
			this.selectionsLock.readLock().unlock();
		}
	}
	
	// Column features
	
	@Override
	public LRangeList getSelectedColumnPositions() {
		this.selectionsLock.readLock().lock();
		try {
			final LRangeList selected= new LRangeList();
			final long columnCount= this.selectionLayer.getColumnCount();
			
			for (final LRectangle r : this.selections) {
				if (r.x < columnCount) {
					selected.add(new LRange(r.x, Math.min(r.x + r.width, columnCount)));
				}
			}
			
			return selected;
		} finally {
			this.selectionsLock.readLock().unlock();
		}
	}
	
	@Override
	public boolean isColumnPositionSelected(final long columnPosition) {
		this.selectionsLock.readLock().lock();
		try {
			final long columnCount= this.selectionLayer.getColumnCount();
			
			if (columnPosition >= 0 && columnPosition < columnCount) {
				for (final LRectangle r : this.selections) {
					if (columnPosition >= r.x && columnPosition < r.x + r.width) {
						return true;
					}
				}
			}
			
			return false;
		} finally {
			this.selectionsLock.readLock().unlock();
		}
	}
	
	@Override
	public LRangeList getFullySelectedColumnPositions() {
		this.selectionsLock.readLock().lock();
		try {
			final LRangeList selected= new LRangeList();
			final long rowCount= this.selectionLayer.getRowCount();
			
			if (rowCount > 0) {
				final LRangeList selectedColumns= getSelectedColumnPositions();
				for (final LRange lRange : selectedColumns) {
					for (long position= lRange.start; position < lRange.end; position++) {
						if (isColumnPositionFullySelected(position, rowCount)) {
							selected.values().add(position);
						}
					}
				}
			}
			
			return selected;
		} finally {
			this.selectionsLock.readLock().unlock();
		}
	}
	
	@Override
	public boolean isColumnPositionFullySelected(final long columnPosition) {
		this.selectionsLock.readLock().lock();
		try {
			final long rowCount= this.selectionLayer.getRowCount();
			
			return ((rowCount > 0)
					&& isColumnPositionFullySelected(columnPosition, rowCount) );
		} finally {
			this.selectionsLock.readLock().unlock();
		}
	}
	
	private boolean isColumnPositionFullySelected(final long columnPosition, final long rowCount) {
		// Aggregate all rows of selection rectangles including the column
		final LRangeList selectedRowsInColumn= new LRangeList();
		
		for (final LRectangle r : this.selections) {
			if (columnPosition >= r.x && columnPosition < r.x + r.width) {
				selectedRowsInColumn.add(new LRange(r.y, r.y + r.height));
			}
		}
		
		final LRange lRange= selectedRowsInColumn.values().getRangeOf(0);
		return (lRange != null && lRange.end >= rowCount);
	}
	
	// Row features
	
	@Override
	public long getSelectedRowCount() {
		return getSelectedRowPositions().values().size();
	}
	
	@Override
	public LRangeList getSelectedRowPositions() {
		this.selectionsLock.readLock().lock();
		try {
			final LRangeList selected= new LRangeList();
			final long rowCount= this.selectionLayer.getRowCount();
			
			for (final LRectangle r : this.selections) {
				if (r.y < rowCount) {
					selected.add(new LRange(r.y, Math.min(r.y + r.height, rowCount)));
				}
			}
			
			return selected;
		} finally {
			this.selectionsLock.readLock().unlock();
		}
	}
	
	@Override
	public boolean isRowPositionSelected(final long rowPosition) {
		this.selectionsLock.readLock().lock();
		try {
			final long rowCount= this.selectionLayer.getRowCount();
			
			if (rowPosition >= 0 && rowPosition < rowCount) {
				for (final LRectangle r : this.selections) {
					if (rowPosition >= r.y && rowPosition < r.y + r.height) {
						return true;
					}
				}
			}
			
			return false;
		} finally {
			this.selectionsLock.readLock().unlock();
		}
	}
	
	@Override
	public LRangeList getFullySelectedRowPositions() {
		this.selectionsLock.readLock().lock();
		try {
			final LRangeList selected= new LRangeList();
			final long columnCount= this.selectionLayer.getColumnCount();
			
			if (columnCount > 0) {
				final LRangeList selectedRows= getSelectedRowPositions();
				for (final LRange lRange : selectedRows) {
					for (long position= lRange.start; position < lRange.end; position++) {
						if (isRowPositionFullySelected(position, columnCount)) {
							selected.values().add(position);
						}
					}
				}
			}
			
			return selected;
		}
		finally {
			this.selectionsLock.readLock().unlock();
		}
	}
	
	@Override
	public boolean isRowPositionFullySelected(final long rowPosition) {
		this.selectionsLock.readLock().lock();
		try {
			final long columnCount= this.selectionLayer.getColumnCount();
			
			return ((columnCount > 0)
					&& isRowPositionFullySelected(rowPosition, columnCount) );
		} finally {
			this.selectionsLock.readLock().unlock();
		}
	}
	
	private boolean isRowPositionFullySelected(final long rowPosition, final long columnCount) {
		// Aggregate all columns of selection rectangles including the row
		final LRangeList selectedColumnsInRow= new LRangeList();
		
		for (final LRectangle r : this.selections) {
			if (rowPosition >= r.y && rowPosition < r.y + r.height) {
				selectedColumnsInRow.add(new LRange(r.x, r.x + r.width));
			}
		}
		
		final LRange lRange= selectedColumnsInRow.values().getRangeOf(0);
		return (lRange != null && lRange.end >= columnCount);
	}
	
	//-- Object methods --//
	
	@Override
	public String toString() {
		this.selectionsLock.readLock().lock();
		try {
			return this.selections.toString();
		} finally {
			this.selectionsLock.readLock().unlock();
		}
	}
	
}
