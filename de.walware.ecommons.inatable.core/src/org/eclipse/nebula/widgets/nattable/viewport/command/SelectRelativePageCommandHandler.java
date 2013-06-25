package org.eclipse.nebula.widgets.nattable.viewport.command;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectRelativeCellCommand;

public class SelectRelativePageCommandHandler extends AbstractLayerCommandHandler<SelectRelativePageCommand> {


	private ILayer viewportLayer;


	public SelectRelativePageCommandHandler(ILayer viewportLayer) {
		this.viewportLayer = viewportLayer;
	}


	@Override
	public Class<SelectRelativePageCommand> getCommandClass() {
		return SelectRelativePageCommand.class;
	}

	@Override
	protected boolean doCommand(SelectRelativePageCommand command) {
		if (command.convertToTargetLayer(this.viewportLayer)) {
			long stepCount;
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
			this.viewportLayer.doCommand(new SelectRelativeCellCommand(
					command.getDirection(), stepCount, command.getSelectionFlags() ));
		}
		return true;
	}

}
