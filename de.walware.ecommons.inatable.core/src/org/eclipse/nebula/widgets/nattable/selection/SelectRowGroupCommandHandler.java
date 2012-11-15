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
package org.eclipse.nebula.widgets.nattable.selection;

import static org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.RANGE_SELECTION;
import static org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.RETAIN_SELECTION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
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
		final List<Integer> rowIndexes = RowGroupUtils.getRowIndexesInGroup(model, rowGroupHeaderLayer.getRowIndexByPosition( command.getRowPosition() ) );
		final List<Integer> rowPositions = RowGroupUtils.getRowPositionsInGroup( selectionLayer, rowIndexes );
		selectRows(command.getColumnPosition(), rowPositions, command.getSelectionFlags(), command.getRowPositionToReveal(), command.isMoveAnchorToTopOfGroup());
		return true;
	}
	
	protected void selectRows(int columnPosition, List<Integer> rowPositions, int selectionFlags, int rowPositionToMoveIntoViewport, boolean moveAnchorToTopOfGroup) {
		Set<Range> changedRowRanges = new HashSet<Range>();
		
		if( rowPositions.size() > 0 ) {
			changedRowRanges.addAll(internalSelectRow(columnPosition, rowPositions.get(0), rowPositions.size(), selectionFlags, moveAnchorToTopOfGroup));
		}

		Set<Integer> changedRows = new HashSet<Integer>();
		for (Range range : changedRowRanges) {
			for (int i = range.start; i < range.end; i++) {
				changedRows.add(Integer.valueOf(i));
			}
		}
		selectionLayer.fireLayerEvent(new RowSelectionEvent(selectionLayer, changedRows, rowPositionToMoveIntoViewport));
	}
	
	private Set<Range> internalSelectRow(int columnPosition, int rowPosition, int rowCount, int selectionFlags, boolean moveAnchorToTopOfGroup) {
		Set<Range> changedRowRanges = new HashSet<Range>();
		
		if ((selectionFlags & (RETAIN_SELECTION | RANGE_SELECTION)) == 0) {
			changedRowRanges.addAll(selectionLayer.getSelectedRowPositions());
			selectionLayer.clear();
			selectionLayer.selectCell(0, rowPosition, selectionFlags);
			selectionLayer.selectRegion(0, rowPosition, selectionLayer.getColumnCount(), rowCount);
			
			changedRowRanges.add(new Range(rowPosition, rowPosition + rowCount));
		} else if ((selectionFlags & (RETAIN_SELECTION | RANGE_SELECTION)) == RETAIN_SELECTION) {
			changedRowRanges.add(selectRowWithCtrlKey(columnPosition, rowPosition, rowCount));
		} else if ((selectionFlags & (RETAIN_SELECTION | RANGE_SELECTION)) == RANGE_SELECTION) {
			changedRowRanges.add(selectRowWithShiftKey(columnPosition, rowPosition, rowCount));
		}
		if (moveAnchorToTopOfGroup) {
			selectionLayer.selectionAnchor.columnPosition = columnPosition;
			selectionLayer.selectionAnchor.rowPosition = rowPosition;
		}	
		selectionLayer.lastSelectedCell.columnPosition = selectionLayer.getColumnCount() - 1;
		selectionLayer.lastSelectedCell.rowPosition = rowPosition;
		
		return changedRowRanges;
	}
	
	private Range selectRowWithCtrlKey(int columnPosition, int rowPosition, int rowCount) {
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
		
		return new Range(rowPosition, rowPosition + 1);
	}

	private Range selectRowWithShiftKey(int columnPosition, int rowPosition, int rowCount) {
		if (selectionLayer.lastSelectedRegion != null) {
			int start = Math.min(selectionLayer.lastSelectedRegion.y, rowPosition);
			int end = Math.max(selectionLayer.lastSelectedRegion.y, rowPosition);
		
			for(int i = start; i <= end; i++){
				int index = selectionLayer.getRowIndexByPosition(i);
				if(RowGroupUtils.isPartOfAGroup(model, index) && !selectionLayer.isRowPositionFullySelected(i)){
					List <Integer> rowPositions = new ArrayList<Integer>(RowGroupUtils.getRowPositionsInGroup(selectionLayer, RowGroupUtils.getRowIndexesInGroup(model, index)));
					Collections.sort(rowPositions);
					selectionLayer.selectRegion(0, rowPositions.get(0), selectionLayer.getColumnCount(), rowPositions.size());
					i=ObjectUtils.getLastElement(rowPositions);
				}
			}
		}
		return new Range(rowPosition, rowPosition + 1);
	}

}
