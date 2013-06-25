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
package org.eclipse.nebula.widgets.nattable.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.coordinate.RangeList;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.group.RowGroupHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.RowGroupUtils;
import org.eclipse.nebula.widgets.nattable.group.model.IRowGroupModel;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectRowGroupsCommand;
import org.eclipse.nebula.widgets.nattable.selection.event.RowSelectionEvent;
import org.eclipse.nebula.widgets.nattable.util.ObjectUtils;


public class SelectRowGroupCommandHandler<T> extends AbstractLayerCommandHandler<SelectRowGroupsCommand> {

	private final IRowGroupModel<T> model;
	private final RowGroupHeaderLayer<T> rowGroupHeaderLayer;
	private final SelectionLayer selectionLayer;


	public SelectRowGroupCommandHandler(IRowGroupModel<T> model, SelectionLayer selectionLayer, RowGroupHeaderLayer<T> rowGroupHeaderLayer) {
		this.model = model;
		this.selectionLayer = selectionLayer;
		this.rowGroupHeaderLayer = rowGroupHeaderLayer;
	}


	public Class<SelectRowGroupsCommand> getCommandClass() {
		return SelectRowGroupsCommand.class;
	}

	@Override
	protected boolean doCommand(SelectRowGroupsCommand command) {
		final List<Long> rowIndexes = RowGroupUtils.getRowIndexesInGroup(model, rowGroupHeaderLayer.getRowIndexByPosition( command.getRowPosition() ) );
		final List<Long> rowPositions = RowGroupUtils.getRowPositionsInGroup( selectionLayer, rowIndexes );
		selectRows(command.getColumnPosition(), rowPositions, command.getSelectionFlags(), command.getRowPositionToReveal(), command.isMoveAnchorToTopOfGroup());
		return true;
	}
	
	protected void selectRows(long columnPosition, List<Long> rowPositions, int selectionFlags, long rowPositionToMoveIntoViewport, boolean moveAnchorToTopOfGroup) {
		final RangeList changedRowRanges = new RangeList();
		
		if( rowPositions.size() > 0 ) {
			internalSelectRow(columnPosition, rowPositions.get(0), rowPositions.size(),
					selectionFlags, moveAnchorToTopOfGroup, changedRowRanges );
		}
		
		selectionLayer.fireLayerEvent(new RowSelectionEvent(selectionLayer, changedRowRanges, rowPositionToMoveIntoViewport));
	}
	
	private void internalSelectRow(long columnPosition, long rowPosition, long rowCount,
			int selectionFlags, boolean moveAnchorToTopOfGroup, final RangeList changedRowRanges) {
		if ((selectionFlags & (SelectionFlags.RETAIN_SELECTION | SelectionFlags.RANGE_SELECTION)) == 0) {
			changedRowRanges.addAll(selectionLayer.getSelectedRowPositions());
			selectionLayer.clear();
			selectionLayer.selectCell(0, rowPosition, selectionFlags);
			selectionLayer.selectRegion(0, rowPosition, selectionLayer.getColumnCount(), rowCount);
			
			changedRowRanges.add(new Range(rowPosition, rowPosition + rowCount));
		} else if ((selectionFlags & (SelectionFlags.RETAIN_SELECTION | SelectionFlags.RANGE_SELECTION)) == SelectionFlags.RETAIN_SELECTION) {
			changedRowRanges.add(selectRowWithCtrlKey(columnPosition, rowPosition, rowCount));
		} else if ((selectionFlags & (SelectionFlags.RETAIN_SELECTION | SelectionFlags.RANGE_SELECTION)) == SelectionFlags.RANGE_SELECTION) {
			changedRowRanges.add(selectRowWithShiftKey(columnPosition, rowPosition, rowCount));
		}
		if (moveAnchorToTopOfGroup) {
			selectionLayer.selectionAnchor.columnPosition = columnPosition;
			selectionLayer.selectionAnchor.rowPosition = rowPosition;
		}	
		selectionLayer.lastSelectedCell.columnPosition = selectionLayer.getColumnCount() - 1;
		selectionLayer.lastSelectedCell.rowPosition = rowPosition;
	}
	
	private Range selectRowWithCtrlKey(long columnPosition, long rowPosition, long rowCount) {
		Rectangle selectedRowRectangle = new Rectangle(0, rowPosition, selectionLayer.getColumnCount(), rowCount);

		if (selectionLayer.isRowPositionFullySelected(rowPosition)) {
			selectionLayer.clearSelection(selectedRowRectangle);
			if (selectionLayer.lastSelectedRegion != null && selectionLayer.lastSelectedRegion.equals(selectedRowRectangle)) {
				selectionLayer.lastSelectedRegion = null;
			}
		} else {
			// Preserve last selected region
			if (selectionLayer.lastSelectedRegion != null) {
				selectionLayer.selectionModel.addSelection(
						new Rectangle(selectionLayer.lastSelectedRegion.x,
								selectionLayer.lastSelectedRegion.y,
								selectionLayer.lastSelectedRegion.width,
								selectionLayer.lastSelectedRegion.height));
			}
			selectionLayer.selectRegion(0, rowPosition, selectionLayer.getColumnCount(), rowCount);
		}
		
		return new Range(rowPosition);
	}

	private Range selectRowWithShiftKey(long columnPosition, long rowPosition, long rowCount) {
		if (selectionLayer.lastSelectedRegion != null) {
			long start = Math.min(selectionLayer.lastSelectedRegion.y, rowPosition);
			long end = Math.max(selectionLayer.lastSelectedRegion.y, rowPosition);
		
			for(long i = start; i <= end; i++){
				long index = selectionLayer.getRowIndexByPosition(i);
				if(RowGroupUtils.isPartOfAGroup(model, index) && !selectionLayer.isRowPositionFullySelected(i)){
					List <Long> rowPositions = new ArrayList<Long>(RowGroupUtils.getRowPositionsInGroup(selectionLayer, RowGroupUtils.getRowIndexesInGroup(model, index)));
					Collections.sort(rowPositions);
					selectionLayer.selectRegion(0, rowPositions.get(0), selectionLayer.getColumnCount(), rowPositions.size());
					i=ObjectUtils.getLastElement(rowPositions);
				}
			}
		}
		return new Range(rowPosition);
	}

}
