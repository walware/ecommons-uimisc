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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.coordinate.RangeList;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectRowsCommand;
import org.eclipse.nebula.widgets.nattable.selection.event.RowSelectionEvent;


public class SelectRowCommandHandler extends AbstractLayerCommandHandler<SelectRowsCommand> {


	private final SelectionLayer selectionLayer;


	public SelectRowCommandHandler(final SelectionLayer selectionLayer) {
		this.selectionLayer = selectionLayer;
	}


	public Class<SelectRowsCommand> getCommandClass() {
		return SelectRowsCommand.class;
	}

	@Override
	protected boolean doCommand(final SelectRowsCommand command) {
		toggleOrSelectRows(command.getColumnPosition(), command.getRowPositions(),
				command.getSelectionFlags(), command.getRowPositionToReveal() );
		return true;
	}

	protected void toggleOrSelectRows(final long columnPosition, final Collection<Long> rowPositions,
			final int selectionFlags, final long rowPositionToShow) {
		long singleRowPosition;
		if ((selectionFlags & (SelectionFlags.RETAIN_SELECTION | SelectionFlags.RANGE_SELECTION)) == SelectionFlags.RETAIN_SELECTION
				&& rowPositions.size() == 1
				&& this.selectionLayer.isRowPositionFullySelected(
						singleRowPosition = rowPositions.iterator().next()) ) {
			final Rectangle columnRegion = new Rectangle(
					0, singleRowPosition, this.selectionLayer.getColumnCount(), 1);
			this.selectionLayer.clearSelection(columnRegion);
			this.selectionLayer.fireLayerEvent(new RowSelectionEvent(this.selectionLayer, singleRowPosition, false));
			return;
		}
		selectRows(columnPosition, rowPositions, selectionFlags, rowPositionToShow);
	}

	protected void selectRows(final long columnPosition, Collection<Long> rowPositions,
			final int selectionFlags, final long rowPositionToShow) {
		if (!(rowPositions instanceof Set)) {
			rowPositions = new HashSet<Long>(rowPositions);
		}
		
		final RangeList changedRowRanges = new RangeList();
		
		long lastPosition = Long.MIN_VALUE;
		if ((selectionFlags & (SelectionFlags.RETAIN_SELECTION | SelectionFlags.RANGE_SELECTION)) == 0) {
			changedRowRanges.addAll(this.selectionLayer.getSelectedRowPositions());
			this.selectionLayer.clearSelections();
		}
		if (rowPositions.isEmpty() || ((selectionFlags & SelectionFlags.RANGE_SELECTION) != 0 && rowPositions.size() > 1)) {
		}
		else if ((selectionFlags & SelectionFlags.RANGE_SELECTION) != 0 && this.selectionLayer.lastSelectedRegion != null
				&& this.selectionLayer.selectionAnchor.columnPosition >= 0) {
			if ((selectionFlags & SelectionFlags.RETAIN_SELECTION) != 0) {
				this.selectionLayer.lastSelectedRegion = new Rectangle(0, 0, 0, 0);
			}
			
			final long position = rowPositions.iterator().next();
			this.selectionLayer.lastSelectedRegion.x = 0;
			this.selectionLayer.lastSelectedRegion.width = this.selectionLayer.getColumnCount();
			this.selectionLayer.lastSelectedRegion.y = Math.min(this.selectionLayer.selectionAnchor.rowPosition, position);
			this.selectionLayer.lastSelectedRegion.height = Math.abs(this.selectionLayer.selectionAnchor.rowPosition - position) + 1;
			
			lastPosition = position;
			
			this.selectionLayer.addSelection(this.selectionLayer.lastSelectedRegion);
			changedRowRanges.add(new Range(this.selectionLayer.lastSelectedRegion.y,
					this.selectionLayer.lastSelectedRegion.y + this.selectionLayer.lastSelectedRegion.height ));
		}
		else {
			long position = Long.MIN_VALUE;
			for (final Iterator<Long> iterator = rowPositions.iterator(); iterator.hasNext();) {
				position = iterator.next();
				if (position == rowPositionToShow) {
					lastPosition = position;
				}
				changedRowRanges.addValue(position);
				this.selectionLayer.addSelection(new Rectangle(0, position, this.selectionLayer.getColumnCount(), 1));
			}
			
			if (lastPosition == Long.MIN_VALUE) {
				lastPosition = position;
			}
			this.selectionLayer.selectionAnchor.columnPosition = columnPosition;
			this.selectionLayer.selectionAnchor.rowPosition = lastPosition;
		}
		
		if (lastPosition >= 0) {
			this.selectionLayer.lastSelectedCell.columnPosition = this.selectionLayer.getColumnCount() - 1;
			this.selectionLayer.lastSelectedCell.rowPosition = lastPosition;
		}
		
		this.selectionLayer.fireLayerEvent(new RowSelectionEvent(this.selectionLayer, changedRowRanges, rowPositionToShow));
	}

}
