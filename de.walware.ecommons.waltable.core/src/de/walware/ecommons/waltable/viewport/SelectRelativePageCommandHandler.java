package de.walware.ecommons.waltable.viewport;

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.selection.SelectRelativeCellCommand;

public class SelectRelativePageCommandHandler extends AbstractLayerCommandHandler<SelectRelativePageCommand> {


	private final ILayer viewportLayer;


	public SelectRelativePageCommandHandler(final ILayer viewportLayer) {
		this.viewportLayer= viewportLayer;
	}


	@Override
	public Class<SelectRelativePageCommand> getCommandClass() {
		return SelectRelativePageCommand.class;
	}

	@Override
	protected boolean doCommand(final SelectRelativePageCommand command) {
		if (command.convertToTargetLayer(this.viewportLayer)) {
			long stepCount;
			switch (command.getDirection()) {
			case UP:
			case DOWN:
				stepCount= this.viewportLayer.getRowCount();
				break;
			case LEFT:
			case RIGHT:
				stepCount= this.viewportLayer.getColumnCount();
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
