package org.eclipse.nebula.widgets.nattable.viewport;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.command.ScrollCellCommand;


public class ScrollCellCommandHandler extends AbstractLayerCommandHandler<ScrollCellCommand> {


	private final ViewportLayer viewportLayer;


	public ScrollCellCommandHandler(final ViewportLayer viewportLayer) {
		this.viewportLayer = viewportLayer;
	}


	public Class<ScrollCellCommand> getCommandClass() {
		return ScrollCellCommand.class;
	}


	protected boolean doCommand(ScrollCellCommand command) {
		scroll(command);
		return true;
	}

	protected boolean scroll(ScrollCellCommand command) {
		int pos;
		switch (command.getDirection()) {
		case UP:
			if (command.getStepCount() == SelectionLayer.MOVE_ALL) {
				pos = 0;
			}
			else {
				pos = viewportLayer.getOriginRowPosition() - command.getStepCount();
			}
			viewportLayer.setOriginRowPosition(pos);
			return true;
		case DOWN:
			if (viewportLayer.isLastRowCompletelyDisplayed()) {
				return true;
			}
			if (command.getStepCount() == SelectionLayer.MOVE_ALL) {
				pos = Integer.MAX_VALUE;
			}
			else {
				pos = viewportLayer.getOriginRowPosition() + command.getStepCount();
			}
			viewportLayer.setOriginRowPosition(pos);
			return true;
		case LEFT:
			if (command.getStepCount() == SelectionLayer.MOVE_ALL) {
				pos = 0;
			}
			else {
				pos = viewportLayer.getOriginColumnPosition() - command.getStepCount();
			}
			viewportLayer.setOriginColumnPosition(pos);
			return true;
		case RIGHT:
			if (viewportLayer.isLastColumnCompletelyDisplayed()) {
				return true;
			}
			if (command.getStepCount() == SelectionLayer.MOVE_ALL) {
				pos = Integer.MAX_VALUE;
			}
			else {
				pos = viewportLayer.getOriginColumnPosition() + command.getStepCount();
			}
			viewportLayer.setOriginColumnPosition(pos);
			return true;
		default:
			return false;
		}
	}

}
