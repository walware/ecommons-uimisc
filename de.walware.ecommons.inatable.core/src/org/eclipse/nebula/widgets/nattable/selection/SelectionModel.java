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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.coordinate.RangeList;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;


/**
 * Tracks the selections made in the table. All selections are tracked in terms of
 * Rectangles.
 *  
 * For example if the table has 10 rows and column 2 is selected, the 
 * Rectangle tracked is (0, 2, 10, 1)
 * 
 * Coordinates are in <i>Selection Layer positions</i>
 * 
 * @see SelectionLayer
 */
public class SelectionModel implements ISelectionModel {


	private final ILayer selectionLayer;

	private boolean multipleSelectionAllowed;
	
	private final List<Rectangle> selections;

	private final ReadWriteLock selectionsLock;


	public SelectionModel(ILayer selectionLayer) {
		this(selectionLayer, true);
	}

	public SelectionModel(ILayer selectionLayer, boolean multipleSelectionAllowed) {
		this.selectionLayer = selectionLayer;
		this.multipleSelectionAllowed = multipleSelectionAllowed;
		
		selections = new LinkedList<Rectangle>();
		selectionsLock = new ReentrantReadWriteLock();
	}


	public boolean isMultipleSelectionAllowed() {
		return multipleSelectionAllowed;
	}
	
	public void setMultipleSelectionAllowed(boolean multipleSelectionAllowed) {
		this.multipleSelectionAllowed = multipleSelectionAllowed;
	}
	
	public void addSelection(long columnPosition, long rowPosition) {
		addSelectionIntoList(new Rectangle(columnPosition, rowPosition, 1, 1));
	}

	public void addSelection(final Rectangle range) {
		if (range != null) {
			addSelectionIntoList(range);
		}
	}

	private void addSelectionIntoList(Rectangle selection) {
		selectionsLock.writeLock().lock();
		try {
			if (multipleSelectionAllowed) {
				ArrayList<Rectangle> itemsToRemove = null;
				for (Rectangle r : selections) {
					if (selection.intersects(r)) {
						if (r.equals(selection)) {
							break;
						}
						
						Rectangle intersection = selection.intersection(r);
						if (intersection.equals(r)) {
							// r is a subset of intersection
							if (itemsToRemove == null)
								itemsToRemove = new ArrayList<Rectangle>();
	
							itemsToRemove.add(r);
						} else if (intersection.equals(selection)) {
							// selection is a subset of r
							break;
						}
					}
				}
				
				if (itemsToRemove != null) {
					selections.removeAll(itemsToRemove);
				}
			} else {
				selections.clear();
			}

			selections.add(selection);
		} finally {
			selectionsLock.writeLock().unlock();
		}
	}

	public void clearSelection() {
		selectionsLock.writeLock().lock();
		try {
			selections.clear();
		} finally {
			selectionsLock.writeLock().unlock();
		}
	}

	public void clearSelection(long columnPosition, long rowPosition) {
		clearSelection(new Rectangle(columnPosition, rowPosition, 1, 1));
	}

	public void clearSelection(Rectangle removedSelection) {

		List<Rectangle> removedItems = new LinkedList<Rectangle>();
		List<Rectangle> addedItems = new LinkedList<Rectangle>();

		selectionsLock.readLock().lock();

		try {
			for (Rectangle r : selections) {
				if (r.intersects(removedSelection)) {
					Rectangle intersection = removedSelection.intersection(r);
					removedItems.add(r);

					Rectangle topSelection = getTopSelection(intersection, r);
					if (topSelection != null) {
						addedItems.add(topSelection);
					}

					Rectangle rightSelection = getRightSelection(intersection, r);
					if (rightSelection != null)
						addedItems.add(rightSelection);

					Rectangle leftSelection = getLeftSelection(intersection, r);
					if (leftSelection != null)
						addedItems.add(leftSelection);

					Rectangle bottomSelection = getBottomSelection(intersection, r);
					if (bottomSelection != null)
						addedItems.add(bottomSelection);
				}
			}
		} finally {
			selectionsLock.readLock().unlock();
		}

		if (removedItems.size() > 0) {
			selectionsLock.writeLock().lock();
			try {
				selections.removeAll(removedItems);
			} finally {
				selectionsLock.writeLock().unlock();
			}

			removedItems.clear();
		}

		if (addedItems.size() > 0) {
			selectionsLock.writeLock().lock();
			try {
				selections.addAll(addedItems);
			} finally {
				selectionsLock.writeLock().unlock();
			}

			addedItems.clear();
		}

	}

	public boolean isEmpty() {
		selectionsLock.readLock().lock();
		try {
			return selections.isEmpty();
		} finally {
			selectionsLock.readLock().unlock();
		}
	}

	public List<Rectangle> getSelections() {
		return selections;
	}
	
	// Cell features

	public boolean isCellPositionSelected(final ILayerCell cell) {
		selectionsLock.readLock().lock();
		try {
			final Rectangle cellRectangle = new Rectangle(
					cell.getOriginColumnPosition(),
					cell.getOriginRowPosition(),
					cell.getColumnSpan(),
					cell.getRowSpan());
			
			for (Rectangle selectionRectangle : selections) {
				if (selectionRectangle.intersects(cellRectangle))
					return true;
			}
		} finally {
			selectionsLock.readLock().unlock();
		}
		
		return false;
	}
	
	// Column features

	public RangeList getSelectedColumnPositions() {
		selectionsLock.readLock().lock();
		try {
			final RangeList selected = new RangeList();
			
			final long columnCount = selectionLayer.getColumnCount();
			for (final Rectangle r : selections) {
				if (r.x < columnCount) {
					selected.add(new Range(r.x, Math.min(r.x + r.width, columnCount)));
				}
			}
			
			return selected;
		} finally {
			selectionsLock.readLock().unlock();
		}
	}
	
	public boolean isColumnPositionSelected(long columnPosition) {
		selectionsLock.readLock().lock();
		try {
			long columnCount = selectionLayer.getColumnCount();
			if (columnPosition < 0 || columnPosition >= columnCount) {
				return false;
			}
			
			for (final Rectangle r : selections) {
				if (columnPosition >= r.x && columnPosition < r.x + r.width) {
					return true;
				}
			}
			
			return false;
		} finally {
			selectionsLock.readLock().unlock();
		}
	}

	public List<Range> getFullySelectedColumnPositions() {
		selectionsLock.readLock().lock();
		try {
			final RangeList selected = new RangeList();
			
			final RangeList selectedColumns = getSelectedColumnPositions();
			final long rowCount = selectionLayer.getRowCount();
			for (final Range range : selectedColumns) {
				for (long position = range.start; position < range.end; position++) {
					if (isColumnPositionFullySelected(position, rowCount)) {
						selected.addValue(position);
					}
				}
			}
			
			return selected;
		} finally {
			selectionsLock.readLock().unlock();
		}
	}

	/**
	 * Are all cells in this column selected?
	 * Different selection rectangles might aggregate to cover the entire column.
	 * We need to take into account any overlapping selections or any selection rectangles
	 * contained within each other.
	 * 
	 * See the related tests for a better understanding.
	 */
	public boolean isColumnPositionFullySelected(final long columnPosition) {
		selectionsLock.readLock().lock();
		try {
			final long rowCount = selectionLayer.getRowCount();
			return isColumnPositionFullySelected(columnPosition, rowCount);
		} finally {
			selectionsLock.readLock().unlock();
		}
	}
	
	private boolean isColumnPositionFullySelected(final long columnPosition, final long rowCount) {
		// Aggregate all rows of selection rectangles including the column
		final RangeList selectedRangesInColumn = new RangeList();
		
		for (final Rectangle r : selections) {
			if (columnPosition >= r.x && columnPosition < r.x + r.width) {
				selectedRangesInColumn.add(new Range(r.y, r.y + r.height));
			}
		}
		
		final Range range = selectedRangesInColumn.getRange(0);
		return (range != null && range.end >= rowCount);
	}
	
	// Row features

	public long getSelectedRowCount() {
		List<Range> selectedRows = getSelectedRowPositions();
		long count = 0;
		for (Range range : selectedRows) {
			count += range.size();
		}
		return count;
	}

	public RangeList getSelectedRowPositions() {
		selectionsLock.readLock().lock();
		try {
			final RangeList selected = new RangeList();
			
			final long rowCount = selectionLayer.getRowCount();
			for (Rectangle r : selections) {
				if (r.y < rowCount) {
					selected.add(new Range(r.y, Math.min(r.y + r.height, rowCount)));
				}
			}
			
			return selected;
		} finally {
			selectionsLock.readLock().unlock();
		}
	}

	public boolean isRowPositionSelected(final long rowPosition) {
		selectionsLock.readLock().lock();
		try {
			final long rowCount = selectionLayer.getRowCount();
			if (rowPosition < 0 || rowPosition >= rowCount) {
				return false;
			}
			
			for (final Rectangle r : selections) {
				if (rowPosition >= r.y && rowPosition < r.y + r.height) {
					return true;
				}
			}
			
			return false;
		} finally {
			selectionsLock.readLock().unlock();
		}
	}

	public List<Range> getFullySelectedRowPositions() {
		selectionsLock.readLock().lock();
		try {
			final RangeList selected = new RangeList();
			
			final RangeList selectedRows = getSelectedRowPositions();
			long columnCount = selectionLayer.getColumnCount();
			for (final Range range : selectedRows) {
				for (long position = range.start; position < range.end; position++) {
					if (isRowPositionFullySelected(position, columnCount)) {
						selected.addValue(position);
					}
				}
			}
			
			return selected;
		}
		finally {
			selectionsLock.readLock().unlock();
		}
	}

	public boolean isRowPositionFullySelected(final long rowPosition) {
		selectionsLock.readLock().lock();
		try {
			final long columnCount = selectionLayer.getColumnCount();
			return isRowPositionFullySelected(rowPosition, columnCount);
		} finally {
			selectionsLock.readLock().unlock();
		}
	}
	
	private boolean isRowPositionFullySelected(final long rowPosition, final long columnCount) {
		// Aggregate all rows of selection rectangles including the column
		final RangeList selectedRangesInRow = new RangeList();
		
		for (final Rectangle r : selections) {
			if (rowPosition >= r.y && rowPosition < r.y + r.height) {
				selectedRangesInRow.add(new Range(r.x, r.x + r.width));
			}
		}
		
		final Range range = selectedRangesInRow.getRange(0);
		return (range != null && range.end >= columnCount);
	}


	protected static final boolean contains(Rectangle containerRectangle, Rectangle rectangle) {
		Rectangle union = containerRectangle.union(rectangle);
		return union.equals(containerRectangle);
	}

	protected static final void sortByX(List<Rectangle> selectionRectanglesInRow) {
		Collections.sort(selectionRectanglesInRow, new Comparator<Rectangle>(){
			public int compare(Rectangle rectangle1, Rectangle rectangle2) {
				return (rectangle1.x < rectangle2.x) ? -1 :
						((rectangle1.x == rectangle2.x) ? 0 : 1);
			}
		});
	}

	protected static final void sortByY(List<Rectangle> selectionRectanglesInColumn) {
		Collections.sort(selectionRectanglesInColumn, new Comparator<Rectangle>(){
			public int compare(Rectangle rectangle1, Rectangle rectangle2) {
				return (rectangle1.y < rectangle2.y) ? -1 :
					((rectangle1.y == rectangle2.y) ? 0 : 1);
			}
		});
	}
	private static final Rectangle getLeftSelection(Rectangle intersection, Rectangle selection) {
		if (intersection.x > selection.x) {
			Rectangle leftSelection = new Rectangle(selection.x, selection.y,
					intersection.x - selection.x, selection.height);
			return leftSelection;
		}

		return null;
	}

	private static final Rectangle getRightSelection(Rectangle intersection, Rectangle selection) {
		long newX = intersection.x + intersection.width;

		if (newX < selection.x + selection.width) {
			Rectangle rightSelection = new Rectangle(newX, selection.y,
					selection.x + selection.width - newX, selection.height);

			return rightSelection;
		}

		return null;
	}

	private static final Rectangle getTopSelection(Rectangle intersection,
			Rectangle selectoin) {
		if (intersection.y > selectoin.y) {
			Rectangle topSelection = new Rectangle(selectoin.x, selectoin.y,
					selectoin.width, intersection.y - selectoin.y);
			return topSelection;
		}
		return null;
	}

	private static final Rectangle getBottomSelection(Rectangle intersection,
			Rectangle selection) {
		long newY = intersection.y + intersection.height;

		if (newY < selection.y + selection.height) {
			Rectangle bottomSelection = new Rectangle(selection.x, newY,
					selection.width, selection.y + selection.height - newY);
			return bottomSelection;
		}

		return null;
	}

	// Object methods

	@Override
	public String toString() {
		selectionsLock.readLock().lock();
		try {
			return selections.toString();
		} finally {
			selectionsLock.readLock().unlock();
		}
	}
	
}
