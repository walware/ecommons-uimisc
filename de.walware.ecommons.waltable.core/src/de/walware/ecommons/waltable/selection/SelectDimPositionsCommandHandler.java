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
// ~Selection
package de.walware.ecommons.waltable.selection;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;
import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.LRangeList;
import de.walware.ecommons.waltable.coordinate.LRectangle;


public class SelectDimPositionsCommandHandler extends AbstractLayerCommandHandler<SelectDimPositionsCommand> {
	
	
	private final SelectionLayer selectionLayer;
	
	
	public SelectDimPositionsCommandHandler(final SelectionLayer selectionLayer) {
		this.selectionLayer= selectionLayer;
	}
	
	
	@Override
	public Class<SelectDimPositionsCommand> getCommandClass() {
		return SelectDimPositionsCommand.class;
	}
	
	@Override
	protected boolean doCommand(final SelectDimPositionsCommand command) {
		if (command.getOrientation() == HORIZONTAL) {
			toggleOrSelectColumn(LRangeList.toRangeList(command.getPositions()),
					command.getOrthogonalPosition(),
					command.getSelectionFlags(), command.getPositionToReveal() );
		}
		else {
			toggleOrSelectRows(command.getOrthogonalPosition(),
					LRangeList.toRangeList(command.getPositions()),
					command.getSelectionFlags(), command.getPositionToReveal() );
		}
		return true;
	}
	
	
	protected void toggleOrSelectColumn(final LRangeList columnPositions, final long rowPosition,
			final int selectionFlags, final long columnPositionToReveal) {
		long singleColumnPosition;
		if ((selectionFlags & (SelectionFlags.RETAIN_SELECTION | SelectionFlags.RANGE_SELECTION)) == SelectionFlags.RETAIN_SELECTION
				&& columnPositions.values().size() == 1
				&& this.selectionLayer.isColumnPositionFullySelected(
						singleColumnPosition= columnPositions.values().first() )) {
			final LRectangle columnRegion= new LRectangle(
					singleColumnPosition, 0, 1, this.selectionLayer.getRowCount());
			this.selectionLayer.clearSelection(columnRegion);
			this.selectionLayer.fireLayerEvent(new ColumnSelectionEvent(this.selectionLayer,
					singleColumnPosition, (columnPositionToReveal == singleColumnPosition) ));
			return;
		}
		selectColumn(columnPositions, rowPosition, selectionFlags, columnPositionToReveal );
	}
	
	protected void selectColumn(final LRangeList columnPositions, final long rowPosition,
			final int selectionFlags, final long columnPositionToReveal) {
		final long rowCount= this.selectionLayer.getRowCount();
		long lastPosition= Long.MIN_VALUE;
		if ((selectionFlags & (SelectionFlags.RETAIN_SELECTION | SelectionFlags.RANGE_SELECTION)) == 0) {
			this.selectionLayer.clearSelections();
		}
		if (columnPositions.isEmpty()
				|| ((selectionFlags & SelectionFlags.RANGE_SELECTION) != 0 && columnPositions.values().size() > 1)) {
		}
		else if (this.selectionLayer.getSelectionModel().isMultipleSelectionAllowed()
				&& (selectionFlags & SelectionFlags.RANGE_SELECTION) != 0 && this.selectionLayer.lastSelectedRegion != null
				&& this.selectionLayer.selectionAnchor.columnPosition >= 0) {
			if ((selectionFlags & SelectionFlags.RETAIN_SELECTION) != 0) {
				this.selectionLayer.lastSelectedRegion= new LRectangle(0, 0, 0, 0);
			}
			
			final long position= columnPositions.values().first();
			this.selectionLayer.lastSelectedRegion.x= Math.min(this.selectionLayer.selectionAnchor.columnPosition, position);
			this.selectionLayer.lastSelectedRegion.width= Math.abs(this.selectionLayer.selectionAnchor.columnPosition - position) + 1;
			this.selectionLayer.lastSelectedRegion.y= 0;
			this.selectionLayer.lastSelectedRegion.height= rowCount;
			
			lastPosition= position;
			
			this.selectionLayer.addSelection(this.selectionLayer.lastSelectedRegion);
		}
		else {
			for (final LRange lRange : columnPositions) {
				if (lRange.contains(columnPositionToReveal)) {
					lastPosition= columnPositionToReveal;
				}
				this.selectionLayer.addSelection(new LRectangle(lRange.start, 0, lRange.size(), rowCount));
			}
			
			if (lastPosition == Long.MIN_VALUE) {
				lastPosition= columnPositions.values().last();
			}
			this.selectionLayer.selectionAnchor.columnPosition= lastPosition;
			this.selectionLayer.selectionAnchor.rowPosition= rowPosition;
		}
		
		if (lastPosition >= 0) {
			this.selectionLayer.lastSelectedCell.columnPosition= lastPosition;
			this.selectionLayer.lastSelectedCell.rowPosition= rowCount - 1;
		}
		
		// TODO correct change set
		this.selectionLayer.fireLayerEvent(new ColumnSelectionEvent(this.selectionLayer,
				columnPositions, columnPositionToReveal ));
	}
	
	
	protected void toggleOrSelectRows(final long columnPosition, final LRangeList rowPositions,
			final int selectionFlags, final long rowPositionToShow) {
		long singleRowPosition;
		if ((selectionFlags & (SelectionFlags.RETAIN_SELECTION | SelectionFlags.RANGE_SELECTION)) == SelectionFlags.RETAIN_SELECTION
				&& rowPositions.values().size() == 1
				&& this.selectionLayer.isRowPositionFullySelected(
						singleRowPosition= rowPositions.values().first()) ) {
			final LRectangle columnRegion= new LRectangle(
					0, singleRowPosition, this.selectionLayer.getColumnCount(), 1);
			this.selectionLayer.clearSelection(columnRegion);
			this.selectionLayer.fireLayerEvent(new RowSelectionEvent(this.selectionLayer, singleRowPosition, false));
			return;
		}
		selectRows(columnPosition, rowPositions, selectionFlags, rowPositionToShow);
	}
	
	protected void selectRows(final long columnPosition, final LRangeList rowPositions,
			final int selectionFlags, final long rowPositionToShow) {
		final LRangeList changedRowRanges= new LRangeList();
		
		final long columnCount= this.selectionLayer.getColumnCount();
		long lastPosition= Long.MIN_VALUE;
		if ((selectionFlags & (SelectionFlags.RETAIN_SELECTION | SelectionFlags.RANGE_SELECTION)) == 0) {
			changedRowRanges.addAll(this.selectionLayer.getSelectedRowPositions());
			this.selectionLayer.clearSelections();
		}
		if (rowPositions.isEmpty()
				|| ((selectionFlags & SelectionFlags.RANGE_SELECTION) != 0 && rowPositions.values().size() > 1)) {
		}
		else if (this.selectionLayer.getSelectionModel().isMultipleSelectionAllowed()
				&& (selectionFlags & SelectionFlags.RANGE_SELECTION) != 0 && this.selectionLayer.lastSelectedRegion != null
				&& this.selectionLayer.selectionAnchor.columnPosition >= 0) {
			if ((selectionFlags & SelectionFlags.RETAIN_SELECTION) != 0) {
				this.selectionLayer.lastSelectedRegion= new LRectangle(0, 0, 0, 0);
			}
			
			final long position= rowPositions.values().first();
			this.selectionLayer.lastSelectedRegion.x= 0;
			this.selectionLayer.lastSelectedRegion.width= columnCount;
			this.selectionLayer.lastSelectedRegion.y= Math.min(this.selectionLayer.selectionAnchor.rowPosition, position);
			this.selectionLayer.lastSelectedRegion.height= Math.abs(this.selectionLayer.selectionAnchor.rowPosition - position) + 1;
			
			lastPosition= position;
			
			this.selectionLayer.addSelection(this.selectionLayer.lastSelectedRegion);
			changedRowRanges.add(new LRange(this.selectionLayer.lastSelectedRegion.y,
					this.selectionLayer.lastSelectedRegion.y + this.selectionLayer.lastSelectedRegion.height ));
		}
		else {
			for (final LRange lRange : rowPositions) {
				if (lRange.contains(rowPositionToShow)) {
					lastPosition= rowPositionToShow;
				}
				this.selectionLayer.addSelection(new LRectangle(0, lRange.start, columnCount, lRange.size()));
				changedRowRanges.add(lRange);
			}
			
			if (lastPosition == Long.MIN_VALUE) {
				lastPosition= rowPositions.values().last();
			}
			this.selectionLayer.selectionAnchor.columnPosition= columnPosition;
			this.selectionLayer.selectionAnchor.rowPosition= lastPosition;
		}
		
		if (lastPosition >= 0) {
			this.selectionLayer.lastSelectedCell.columnPosition= columnCount - 1;
			this.selectionLayer.lastSelectedCell.rowPosition= lastPosition;
		}
		
		this.selectionLayer.fireLayerEvent(new RowSelectionEvent(this.selectionLayer, changedRowRanges, rowPositionToShow));
	}
	
}
