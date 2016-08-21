/*******************************************************************************
 * Copyright (c) 2010-2016 Stephan Wahlbrink (WalWare.de) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/
// +
package de.walware.ecommons.waltable.selection;

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;
import de.walware.ecommons.waltable.coordinate.PositionCoordinate;


public class SelectRelativeCommandHandler extends AbstractLayerCommandHandler<SelectRelativeCellCommand> {


	protected final SelectionLayer selectionLayer;


	public SelectRelativeCommandHandler(final SelectionLayer selectionLayer) {
		this.selectionLayer= selectionLayer;
	}


	@Override
	public Class<SelectRelativeCellCommand> getCommandClass() {
		return SelectRelativeCellCommand.class;
	}


	@Override
	protected boolean doCommand(final SelectRelativeCellCommand command) {
		select(command);
		return true;
	}

	protected boolean select(final SelectRelativeCellCommand command) {
		long row;
		long column;
		PositionCoordinate lastSelectedCell= null;
		if ((command.getSelectionFlags() & SelectionFlags.RANGE_SELECTION) == SelectionFlags.RANGE_SELECTION) {
			lastSelectedCell= this.selectionLayer.getLastSelectedCellPosition();
		}
		else {
			lastSelectedCell= this.selectionLayer.getSelectionAnchor();
		}
		
		switch (command.getDirection()) {
		case UP:
			if (command.getStepCount() == SelectionLayer.MOVE_ALL) {
				row= 0;
			}
			else {
				if (lastSelectedCell.rowPosition >= 0) {
					row= lastSelectedCell.rowPosition - command.getStepCount();
					if (row < 0) {
						row= 0;
					}
				}
				else {
					row= 0;
				}
			}
			column= lastSelectedCell.columnPosition;
			if (column < 0) {
				column= 0;
			}
			break;
		case DOWN:
			if (command.getStepCount() == SelectionLayer.MOVE_ALL) {
				row= this.selectionLayer.getRowCount() - 1;
			}
			else {
				if (lastSelectedCell.rowPosition >= 0) {
					row= lastSelectedCell.rowPosition + command.getStepCount();
					if (row >= this.selectionLayer.getRowCount()) {
						row= this.selectionLayer.getRowCount() - 1;
					}
				}
				else {
					row= 0;
				}
			}
			column= lastSelectedCell.columnPosition;
			if (column < 0) {
				column= 0;
			}
			break;
		case LEFT:
			if (command.getStepCount() == SelectionLayer.MOVE_ALL) {
				column= 0;
			}
			else {
				if (lastSelectedCell.columnPosition >= 0) {
					column= lastSelectedCell.columnPosition - command.getStepCount();
					if (column < 0) {
						column= 0;
					}
				}
				else {
					column= 0;
				}
			}
			row= lastSelectedCell.rowPosition;
			if (row < 0) {
				row= 0;
			}
			break;
		case RIGHT:
			if (command.getStepCount() == SelectionLayer.MOVE_ALL) {
				column= this.selectionLayer.getColumnCount() - 1;
			}
			else {
				if (lastSelectedCell.columnPosition >= 0) {
					column= lastSelectedCell.columnPosition + command.getStepCount();
					if (column >= this.selectionLayer.getColumnCount()) {
						column= this.selectionLayer.getColumnCount() - 1;
					}
				}
				else {
					column= 0;
				}
			}
			row= lastSelectedCell.rowPosition;
			if (row < 0) {
				row= 0;
			}
			break;
		default:
			return false;
		}
		
		this.selectionLayer.selectCell(column, row, command.getSelectionFlags());
		this.selectionLayer.fireCellSelectionEvent(column, row, true);
		return true;
	}

}
