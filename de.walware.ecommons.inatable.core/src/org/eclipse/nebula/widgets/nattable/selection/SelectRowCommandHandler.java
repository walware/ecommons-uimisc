/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
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

import static org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.RANGE_SELECTION;
import static org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.RETAIN_SELECTION;

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


	public SelectRowCommandHandler(SelectionLayer selectionLayer) {
		this.selectionLayer = selectionLayer;
	}


	public Class<SelectRowsCommand> getCommandClass() {
		return SelectRowsCommand.class;
	}

	protected boolean doCommand(SelectRowsCommand command) {
		toggleOrSelectRows(command.getColumnPosition(), command.getRowPositions(),
				command.getSelectionFlags(), command.getRowPositionToReveal() );
		return true;
	}

	protected void toggleOrSelectRows(final int columnPosition, final Collection<Integer> rowPositions,
			final int selectionFlags, final int rowPositionToShow) {
		int singleRowPosition;
		if ((selectionFlags & (RETAIN_SELECTION | RANGE_SELECTION)) == RETAIN_SELECTION
				&& rowPositions.size() == 1
				&& selectionLayer.isRowPositionFullySelected(
						singleRowPosition = rowPositions.iterator().next()) ) {
			final Rectangle columnRegion = new Rectangle(
					0, singleRowPosition, selectionLayer.getColumnCount(), 1);
			selectionLayer.clearSelection(columnRegion);
			selectionLayer.fireLayerEvent(new RowSelectionEvent(selectionLayer, singleRowPosition, false));
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
		
		int rowPosition = -1;
		if ((selectionFlags & (RETAIN_SELECTION | RANGE_SELECTION)) == 0) {
			changedRowRanges.addAll(selectionLayer.getSelectedRowPositions());
			selectionLayer.clearSelections();
		}
		if (rowPositions.isEmpty() || ((selectionFlags & RANGE_SELECTION) != 0 && rowPositions.size() > 1)) {
		}
		else if ((selectionFlags & RANGE_SELECTION) != 0 && selectionLayer.lastSelectedRegion != null
				&& selectionLayer.selectionAnchor.columnPosition >= 0) {
			if ((selectionFlags & RETAIN_SELECTION) != 0) {
				selectionLayer.lastSelectedRegion = new Rectangle(0, 0, 0, 0);
			}
			
			rowPosition = rowPositions.iterator().next();
			selectionLayer.lastSelectedRegion.x = 0;
			selectionLayer.lastSelectedRegion.width = selectionLayer.getColumnCount();
			selectionLayer.lastSelectedRegion.y = Math.min(selectionLayer.selectionAnchor.rowPosition, rowPosition);
			selectionLayer.lastSelectedRegion.height = Math.abs(selectionLayer.selectionAnchor.rowPosition - rowPosition) + 1;
			
			selectionLayer.addSelection(selectionLayer.lastSelectedRegion);
			changedRowRanges.add(new Range(selectionLayer.lastSelectedRegion.y,
					selectionLayer.lastSelectedRegion.y + selectionLayer.lastSelectedRegion.height ));
		}
		else {
			for (Iterator<Integer> iterator = rowPositions.iterator(); iterator.hasNext();) {
				rowPosition = iterator.next();
				changedRowRanges.add(new Range(rowPosition, rowPosition + 1));
				selectionLayer.addSelection(new Rectangle(0, rowPosition,
						selectionLayer.getColumnCount(), 1 ));
			}
			
			selectionLayer.selectionAnchor.columnPosition = columnPosition;
			selectionLayer.selectionAnchor.rowPosition = rowPosition;
		}
		
		if (rowPosition >= 0) {
			selectionLayer.lastSelectedCell.columnPosition = selectionLayer.getColumnCount() - 1;
			selectionLayer.lastSelectedCell.rowPosition = rowPosition;
		}
		
		final List<Integer> changedRowPositions = new ArrayList<Integer>(changedRowRanges.size());
		for (Range rowRange : changedRowRanges) {
			for (int i = rowRange.start; i < rowRange.end; i++) {
				changedRowPositions.add(Integer.valueOf(i));
			}
		}
		selectionLayer.fireLayerEvent(new RowSelectionEvent(selectionLayer, changedRowPositions, rowPositionToShow));
	}

}
