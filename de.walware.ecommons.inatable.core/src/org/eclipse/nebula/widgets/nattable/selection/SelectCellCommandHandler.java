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

import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;


public class SelectCellCommandHandler extends AbstractLayerCommandHandler<SelectCellCommand> {


	private final SelectionLayer selectionLayer;


	public SelectCellCommandHandler(SelectionLayer selectionLayer) {
		this.selectionLayer = selectionLayer;
	}


	public Class<SelectCellCommand> getCommandClass() {
		return SelectCellCommand.class;
	}

	protected boolean doCommand(SelectCellCommand command) {
		toggleOrSelectCell(command.getColumnPosition(), command.getRowPosition(), 
				command.getSelectionFlags(), command.getRevealCell());
		this.selectionLayer.fireCellSelectionEvent(command.getColumnPosition(), command.getRowPosition(),
				command.getRevealCell());
		return true;
	}

	/**
	 * Toggles the selection state of the given row and column.
	 * @return <code>false</code> if the cell was unselected.
	 */
	protected void toggleOrSelectCell(final int columnPosition, final int rowPosition,
			final int selectionFlags, final boolean showCell) {
		if ((selectionFlags & (RETAIN_SELECTION | RANGE_SELECTION)) == RETAIN_SELECTION) {
			if (this.selectionLayer.isCellPositionSelected(columnPosition, rowPosition)) {
				this.selectionLayer.clearSelection(columnPosition, rowPosition);
				return;
			}
		}
		
		selectCell(columnPosition, rowPosition, selectionFlags, showCell);
	}

	/**
	 * Selects a cell, optionally clearing current selection
	 */
	protected void selectCell(final int columnPosition, final int rowPosition,
			final int selectionFlags, final boolean show) {
		if ((selectionFlags & (RETAIN_SELECTION | RANGE_SELECTION)) == 0) {
			this.selectionLayer.clearSelections();
		}
		this.selectionLayer.lastSelectedCell.columnPosition = columnPosition;
		this.selectionLayer.lastSelectedCell.rowPosition = rowPosition;
		
		if ((selectionFlags & RANGE_SELECTION) != 0 && this.selectionLayer.lastSelectedRegion != null
				&& this.selectionLayer.selectionAnchor.columnPosition >= 0) {
			if ((selectionFlags & RETAIN_SELECTION) != 0) {
				this.selectionLayer.lastSelectedRegion = new Rectangle(0, 0, 0, 0);
			}
			
			this.selectionLayer.lastSelectedRegion.x = Math.min(selectionLayer.selectionAnchor.columnPosition, columnPosition);
			this.selectionLayer.lastSelectedRegion.width = Math.abs(selectionLayer.selectionAnchor.columnPosition - columnPosition) + 1;
			this.selectionLayer.lastSelectedRegion.y = Math.min(selectionLayer.selectionAnchor.rowPosition, rowPosition);
			this.selectionLayer.lastSelectedRegion.height = Math.abs(selectionLayer.selectionAnchor.rowPosition - rowPosition) + 1;
			
			this.selectionLayer.addSelection(selectionLayer.lastSelectedRegion);
		}
		else {
			this.selectionLayer.addSelection(new Rectangle(columnPosition, rowPosition, 1, 1));
		}
	}

}