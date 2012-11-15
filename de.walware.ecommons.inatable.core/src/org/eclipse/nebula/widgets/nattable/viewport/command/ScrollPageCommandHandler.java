package org.eclipse.nebula.widgets.nattable.viewport.command;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;

public class ScrollPageCommandHandler extends AbstractLayerCommandHandler<ScrollPageCommand> {


	private ILayer viewportLayer;


	public ScrollPageCommandHandler(ILayer viewportLayer) {
		this.viewportLayer = viewportLayer;
	}


	@Override
	public Class<ScrollPageCommand> getCommandClass() {
		return ScrollPageCommand.class;
	}

	@Override
	protected boolean doCommand(ScrollPageCommand command) {
		if (command.convertToTargetLayer(this.viewportLayer)) {
			int stepCount;
			switch (command.getDirection()) {
			case UP:
			case DOWN:
				stepCount = this.viewportLayer.getRowCount();
				break;
			case LEFT:
			case RIGHT:
				stepCount = this.viewportLayer.getColumnCount();
				break;
			default:
				return false;
			}
			this.viewportLayer.doCommand(new ScrollCellCommand(
					command.getDirection(), stepCount ));
		}
		return true;
	}

}
