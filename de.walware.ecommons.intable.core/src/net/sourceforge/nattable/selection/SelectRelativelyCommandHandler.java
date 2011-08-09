package net.sourceforge.nattable.selection;

import net.sourceforge.nattable.command.AbstractLayerCommandHandler;
import net.sourceforge.nattable.coordinate.PositionCoordinate;
import net.sourceforge.nattable.selection.command.ISelectionCommand.SelectionFlag;
import net.sourceforge.nattable.selection.command.SelectRelativelyCommand;


public class SelectRelativelyCommandHandler extends AbstractLayerCommandHandler<SelectRelativelyCommand> {
	
	
	protected final SelectionLayer fSelectionLayer;
	
	
	public SelectRelativelyCommandHandler(SelectionLayer selectionLayer) {
		fSelectionLayer = selectionLayer;
	}
	
	
	public Class<SelectRelativelyCommand> getCommandClass() {
		return SelectRelativelyCommand.class;
	}
	
	
	protected boolean doCommand(SelectRelativelyCommand command) {
		select(command);
		return true;
	}
	
	protected boolean select(SelectRelativelyCommand command) {
		int row;
		int column;
		PositionCoordinate lastSelectedCell = null;
		if (command.getSelectionFlags().contains(SelectionFlag.RANGE_SELECTION)) {
			lastSelectedCell = fSelectionLayer.getLastSelectedCellPosition();
		}
		if (lastSelectedCell == null) {
			lastSelectedCell = fSelectionLayer.getSelectionAnchor();
		}
		
		switch (command.getDirection()) {
		case UP:
			switch (command.getScale()) {
			case CELL:
				if (lastSelectedCell.rowPosition >= 0) {
					row = lastSelectedCell.rowPosition - command.getStepCount();
					if (row < 0) {
						row = 0;
					}
				}
				else {
					row = 0;
				}
				break;
			case TABLE:
				row = 0;
				break;
			default:
				return false;
			}
			column = lastSelectedCell.columnPosition;
			if (column < 0) {
				column = 0;
			}
			break;
		case DOWN:
			switch (command.getScale()) {
			case CELL:
				if (lastSelectedCell.rowPosition >= 0) {
					row = lastSelectedCell.rowPosition + command.getStepCount();
					if (row >= fSelectionLayer.getRowCount()) {
						row = fSelectionLayer.getRowCount() - 1;
					}
				}
				else {
					row = 0;
				}
				break;
			case TABLE:
				row = fSelectionLayer.getRowCount() - 1;
				break;
			default:
				return false;
			}
			column = lastSelectedCell.columnPosition;
			if (column < 0) {
				column = 0;
			}
			break;
		case LEFT:
			switch (command.getScale()) {
			case CELL:
				if (lastSelectedCell.columnPosition >= 0) {
					column = lastSelectedCell.columnPosition - command.getStepCount();
					if (column < 0) {
						column = 0;
					}
				}
				else {
					column = 0;
				}
				break;
			case TABLE:
				column = 0;
				break;
			default:
				return false;
			}
			row = lastSelectedCell.rowPosition;
			if (row < 0) {
				row = 0;
			}
			break;
		case RIGHT:
			switch (command.getScale()) {
			case CELL:
				if (lastSelectedCell.columnPosition >= 0) {
					column = lastSelectedCell.columnPosition + command.getStepCount();
					if (column >= fSelectionLayer.getColumnCount()) {
						column = fSelectionLayer.getColumnCount() - 1;
					}
				}
				else {
					column = 0;
				}
				break;
			case TABLE:
				column = fSelectionLayer.getColumnCount() - 1;
				break;
			default:
				return false;
			}
			row = lastSelectedCell.rowPosition;
			if (row < 0) {
				row = 0;
			}
			break;
		default:
			return false;
		}
		
		fSelectionLayer.selectCell(column, row, command.getSelectionFlags());
		fSelectionLayer.fireCellSelectionEvent(column, row, true);
		return true;
	}
	
}
