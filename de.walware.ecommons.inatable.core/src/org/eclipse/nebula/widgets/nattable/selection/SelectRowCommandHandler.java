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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
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

	protected void toggleOrSelectRows(final int columnPosition, final Collection<Integer> rowPositions,
			final int selectionFlags, final int rowPositionToShow) {
		int singleRowPosition;
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

	protected void selectRows(final int columnPosition, Collection<Integer> rowPositions,
			final int selectionFlags, final int rowPositionToShow) {
		if (!(rowPositions instanceof Set)) {
			rowPositions = new HashSet<Integer>(rowPositions);
		}
		
		final Set<Range> changedRowRanges = new HashSet<Range>();
		
		int lastPosition = Integer.MIN_VALUE;
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
			
			final int position = rowPositions.iterator().next();
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
			int position = Integer.MIN_VALUE;
			for (final Iterator<Integer> iterator = rowPositions.iterator(); iterator.hasNext();) {
				position = iterator.next();
				if (position == rowPositionToShow) {
					lastPosition = position;
				}
				changedRowRanges.add(new Range(position));
				this.selectionLayer.addSelection(new Rectangle(0, position, this.selectionLayer.getColumnCount(), 1));
			}
			
			if (lastPosition == Integer.MIN_VALUE) {
				lastPosition = position;
			}
			this.selectionLayer.selectionAnchor.columnPosition = columnPosition;
			this.selectionLayer.selectionAnchor.rowPosition = lastPosition;
		}
		
		if (lastPosition >= 0) {
			this.selectionLayer.lastSelectedCell.columnPosition = this.selectionLayer.getColumnCount() - 1;
			this.selectionLayer.lastSelectedCell.rowPosition = lastPosition;
		}
		
		final List<Integer> changedRowPositions = new ArrayList<Integer>(changedRowRanges.size());
		for (final Range rowRange : changedRowRanges) {
			for (int i = rowRange.start; i < rowRange.end; i++) {
				changedRowPositions.add(Integer.valueOf(i));
			}
		}
		this.selectionLayer.fireLayerEvent(new RowSelectionEvent(this.selectionLayer, changedRowPositions, rowPositionToShow));
	}

}
