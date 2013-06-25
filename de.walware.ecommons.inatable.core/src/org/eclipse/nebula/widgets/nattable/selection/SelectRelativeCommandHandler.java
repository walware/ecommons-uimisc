/*******************************************************************************
 * Copyright (c) 2010, 2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/
// +
package org.eclipse.nebula.widgets.nattable.selection;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectRelativeCellCommand;


public class SelectRelativeCommandHandler extends AbstractLayerCommandHandler<SelectRelativeCellCommand> {


	protected final SelectionLayer selectionLayer;


	public SelectRelativeCommandHandler(SelectionLayer selectionLayer) {
		this.selectionLayer = selectionLayer;
	}


	public Class<SelectRelativeCellCommand> getCommandClass() {
		return SelectRelativeCellCommand.class;
	}


	protected boolean doCommand(SelectRelativeCellCommand command) {
		select(command);
		return true;
	}

	protected boolean select(SelectRelativeCellCommand command) {
		int row;
		int column;
		PositionCoordinate lastSelectedCell = null;
		if ((command.getSelectionFlags() & SelectionFlags.RANGE_SELECTION) == SelectionFlags.RANGE_SELECTION) {
			lastSelectedCell = selectionLayer.getLastSelectedCellPosition();
		}
		else {
			lastSelectedCell = selectionLayer.getSelectionAnchor();
		}
		
		switch (command.getDirection()) {
		case UP:
			if (command.getStepCount() == SelectionLayer.MOVE_ALL) {
				row = 0;
			}
			else {
				if (lastSelectedCell.rowPosition >= 0) {
					row = lastSelectedCell.rowPosition - command.getStepCount();
					if (row < 0) {
						row = 0;
					}
				}
				else {
					row = 0;
				}
			}
			column = lastSelectedCell.columnPosition;
			if (column < 0) {
				column = 0;
			}
			break;
		case DOWN:
			if (command.getStepCount() == SelectionLayer.MOVE_ALL) {
				row = selectionLayer.getRowCount() - 1;
			}
			else {
				if (lastSelectedCell.rowPosition >= 0) {
					row = lastSelectedCell.rowPosition + command.getStepCount();
					if (row >= selectionLayer.getRowCount()) {
						row = selectionLayer.getRowCount() - 1;
					}
				}
				else {
					row = 0;
				}
			}
			column = lastSelectedCell.columnPosition;
			if (column < 0) {
				column = 0;
			}
			break;
		case LEFT:
			if (command.getStepCount() == SelectionLayer.MOVE_ALL) {
				column = 0;
			}
			else {
				if (lastSelectedCell.columnPosition >= 0) {
					column = lastSelectedCell.columnPosition - command.getStepCount();
					if (column < 0) {
						column = 0;
					}
				}
				else {
					column = 0;
				}
			}
			row = lastSelectedCell.rowPosition;
			if (row < 0) {
				row = 0;
			}
			break;
		case RIGHT:
			if (command.getStepCount() == SelectionLayer.MOVE_ALL) {
				column = selectionLayer.getColumnCount() - 1;
			}
			else {
				if (lastSelectedCell.columnPosition >= 0) {
					column = lastSelectedCell.columnPosition + command.getStepCount();
					if (column >= selectionLayer.getColumnCount()) {
						column = selectionLayer.getColumnCount() - 1;
					}
				}
				else {
					column = 0;
				}
			}
			row = lastSelectedCell.rowPosition;
			if (row < 0) {
				row = 0;
			}
			break;
		default:
			return false;
		}
		
		selectionLayer.selectCell(column, row, command.getSelectionFlags());
		selectionLayer.fireCellSelectionEvent(column, row, true);
		return true;
	}

}
