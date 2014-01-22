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

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.coordinate.RangeList;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectColumnsCommand;
import org.eclipse.nebula.widgets.nattable.selection.event.ColumnSelectionEvent;


public class SelectColumnCommandHandler extends AbstractLayerCommandHandler<SelectColumnsCommand> {


	private final SelectionLayer selectionLayer;


	public SelectColumnCommandHandler(final SelectionLayer selectionLayer) {
		this.selectionLayer = selectionLayer;
	}


	public Class<SelectColumnsCommand> getCommandClass() {
		return SelectColumnsCommand.class;
	}

	@Override
	protected boolean doCommand(final SelectColumnsCommand command) {
		toggleOrSelectColumn(RangeList.toRangeList(command.getPositions()), command.getRowPosition(),
				command.getSelectionFlags(), command.getColumnPositionToReveal() );
		return true;
	}

	protected void toggleOrSelectColumn(final RangeList columnPositions, final long rowPosition,
			final int selectionFlags, final long columnPositionToReveal) {
		long singleColumnPosition;
		if ((selectionFlags & (SelectionFlags.RETAIN_SELECTION | SelectionFlags.RANGE_SELECTION)) == SelectionFlags.RETAIN_SELECTION
				&& columnPositions.values().size() == 1
				&& this.selectionLayer.isColumnPositionFullySelected(
						singleColumnPosition = columnPositions.values().first() )) {
			final Rectangle columnRegion = new Rectangle(
					singleColumnPosition, 0, 1, this.selectionLayer.getRowCount());
			this.selectionLayer.clearSelection(columnRegion);
			this.selectionLayer.fireLayerEvent(new ColumnSelectionEvent(this.selectionLayer,
					singleColumnPosition, (columnPositionToReveal == singleColumnPosition) ));
			return;
		}
		selectColumn(columnPositions, rowPosition, selectionFlags, columnPositionToReveal );
	}

	protected void selectColumn(final RangeList columnPositions, final long rowPosition,
			final int selectionFlags, final long columnPositionToReveal) {
		final long rowCount = this.selectionLayer.getRowCount();
		long lastPosition = Long.MIN_VALUE;
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
				this.selectionLayer.lastSelectedRegion = new Rectangle(0, 0, 0, 0);
			}
			
			final long position = columnPositions.values().first();
			this.selectionLayer.lastSelectedRegion.x = Math.min(this.selectionLayer.selectionAnchor.columnPosition, position);
			this.selectionLayer.lastSelectedRegion.width = Math.abs(this.selectionLayer.selectionAnchor.columnPosition - position) + 1;
			this.selectionLayer.lastSelectedRegion.y = 0;
			this.selectionLayer.lastSelectedRegion.height = rowCount;
			
			lastPosition = position;
			
			this.selectionLayer.addSelection(this.selectionLayer.lastSelectedRegion);
		}
		else {
			for (final Range range : columnPositions) {
				if (range.contains(columnPositionToReveal)) {
					lastPosition = columnPositionToReveal;
				}
				this.selectionLayer.addSelection(new Rectangle(range.start, 0, range.size(), rowCount));
			}
			
			if (lastPosition == Long.MIN_VALUE) {
				lastPosition = columnPositions.values().last();
			}
			this.selectionLayer.selectionAnchor.columnPosition = lastPosition;
			this.selectionLayer.selectionAnchor.rowPosition = rowPosition;
		}
		
		if (lastPosition >= 0) {
			this.selectionLayer.lastSelectedCell.columnPosition = lastPosition;
			this.selectionLayer.lastSelectedCell.rowPosition = rowCount - 1;
		}
		
		// TODO correct change set
		this.selectionLayer.fireLayerEvent(new ColumnSelectionEvent(this.selectionLayer,
				columnPositions, columnPositionToReveal ));
	}

}
