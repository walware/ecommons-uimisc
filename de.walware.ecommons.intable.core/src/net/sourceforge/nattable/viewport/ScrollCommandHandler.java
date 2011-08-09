package net.sourceforge.nattable.viewport;

import net.sourceforge.nattable.command.AbstractLayerCommandHandler;
import net.sourceforge.nattable.viewport.command.ScrollCommand;


public class ScrollCommandHandler extends AbstractLayerCommandHandler<ScrollCommand> {
	
	
	private final ViewportLayer fViewportLayer;
	
	
	public ScrollCommandHandler(final ViewportLayer viewportLayer) {
		fViewportLayer = viewportLayer;
	}
	
	
	public Class<ScrollCommand> getCommandClass() {
		return ScrollCommand.class;
	}
	
	
	protected boolean doCommand(ScrollCommand command) {
		scroll(command);
		return true;
	}
	
	protected boolean scroll(ScrollCommand command) {
		int pos;
		switch (command.getDirection()) {
		case UP:
			switch (command.getScale()) {
			case CELL:
				pos = fViewportLayer.getOriginRowPosition() - command.getStepCount();
				break;
			case TABLE:
				pos = 0;
				break;
			default:
				return false;
			}
			fViewportLayer.setOriginRowPosition(pos);
			return true;
		case DOWN:
			if (fViewportLayer.isLastRowCompletelyDisplayed()) {
				return true;
			}
			switch (command.getScale()) {
			case CELL:
				pos = fViewportLayer.getOriginRowPosition() + command.getStepCount();
				break;
			case TABLE:
				pos = Integer.MAX_VALUE;
				break;
			default:
				return false;
			}
			fViewportLayer.setOriginRowPosition(pos);
			return true;
		case LEFT:
			switch (command.getScale()) {
			case CELL:
				pos = fViewportLayer.getOriginColumnPosition() - command.getStepCount();
				break;
			case TABLE:
				pos = 0;
				break;
			default:
				return false;
			}
			fViewportLayer.setOriginColumnPosition(pos);
			return true;
		case RIGHT:
			if (fViewportLayer.isLastColumnCompletelyDisplayed()) {
				return true;
			}
			switch (command.getScale()) {
			case CELL:
				pos = fViewportLayer.getOriginColumnPosition() + command.getStepCount();
				break;
			case TABLE:
				pos = Integer.MAX_VALUE;
				break;
			default:
				return false;
			}
			fViewportLayer.setOriginColumnPosition(pos);
			return true;
		default:
			return false;
		}
	}
	
}
