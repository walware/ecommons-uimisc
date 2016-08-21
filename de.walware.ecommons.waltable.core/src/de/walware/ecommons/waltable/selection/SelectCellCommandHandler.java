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

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;
import de.walware.ecommons.waltable.coordinate.LRectangle;


public class SelectCellCommandHandler extends AbstractLayerCommandHandler<SelectCellCommand> {


	private final SelectionLayer selectionLayer;


	public SelectCellCommandHandler(final SelectionLayer selectionLayer) {
		this.selectionLayer= selectionLayer;
	}


	@Override
	public Class<SelectCellCommand> getCommandClass() {
		return SelectCellCommand.class;
	}
	
	@Override
	protected boolean doCommand(final SelectCellCommand command) {
		toggleOrSelectCell(command.getColumnPosition(), command.getRowPosition(), 
				command.getSelectionFlags(), command.getRevealCell());
		this.selectionLayer.fireCellSelectionEvent(command.getColumnPosition(), command.getRowPosition(),
				command.getRevealCell());
		return true;
	}

	/**
	 * Toggles the selection state of the given row and column.
	 */
	protected void toggleOrSelectCell(final long columnPosition, final long rowPosition,
			final int selectionFlags, final boolean showCell) {
		if ((selectionFlags & (SelectionFlags.RETAIN_SELECTION | SelectionFlags.RANGE_SELECTION)) == SelectionFlags.RETAIN_SELECTION) {
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
	protected void selectCell(final long columnPosition, final long rowPosition,
			final int selectionFlags, final boolean show) {
		if ((selectionFlags & (SelectionFlags.RETAIN_SELECTION | SelectionFlags.RANGE_SELECTION)) == 0) {
			this.selectionLayer.clearSelections();
		}
		this.selectionLayer.lastSelectedCell.columnPosition= columnPosition;
		this.selectionLayer.lastSelectedCell.rowPosition= rowPosition;
		
		if (this.selectionLayer.getSelectionModel().isMultipleSelectionAllowed()
				&& (selectionFlags & SelectionFlags.RANGE_SELECTION) != 0 && this.selectionLayer.lastSelectedRegion != null
				&& this.selectionLayer.selectionAnchor.columnPosition >= 0) {
			if ((selectionFlags & SelectionFlags.RETAIN_SELECTION) != 0) {
				this.selectionLayer.lastSelectedRegion= new LRectangle(0, 0, 0, 0);
			}
			
			this.selectionLayer.lastSelectedRegion.x= Math.min(this.selectionLayer.selectionAnchor.columnPosition, columnPosition);
			this.selectionLayer.lastSelectedRegion.width= Math.abs(this.selectionLayer.selectionAnchor.columnPosition - columnPosition) + 1;
			this.selectionLayer.lastSelectedRegion.y= Math.min(this.selectionLayer.selectionAnchor.rowPosition, rowPosition);
			this.selectionLayer.lastSelectedRegion.height= Math.abs(this.selectionLayer.selectionAnchor.rowPosition - rowPosition) + 1;
			
			this.selectionLayer.addSelection(this.selectionLayer.lastSelectedRegion);
		}
		else {
			this.selectionLayer.addSelection(new LRectangle(columnPosition, rowPosition, 1, 1));
		}
	}

}
