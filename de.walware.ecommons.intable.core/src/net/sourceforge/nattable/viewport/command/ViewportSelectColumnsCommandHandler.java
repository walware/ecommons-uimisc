package net.sourceforge.nattable.viewport.command;

import net.sourceforge.nattable.command.AbstractLayerCommandHandler;
import net.sourceforge.nattable.layer.AbstractLayer;
import net.sourceforge.nattable.selection.command.SelectColumnsCommand;


public class ViewportSelectColumnsCommandHandler extends AbstractLayerCommandHandler<ViewportSelectColumnsCommand> {
	
	
	private final AbstractLayer viewportLayer;
	
	
	public ViewportSelectColumnsCommandHandler(final AbstractLayer viewportLayer) {
		this.viewportLayer = viewportLayer;
	}
	
	
	public Class<ViewportSelectColumnsCommand> getCommandClass() {
		return ViewportSelectColumnsCommand.class;
	}
	
	protected boolean doCommand(ViewportSelectColumnsCommand command) {
		viewportLayer.doCommand(new SelectColumnsCommand(viewportLayer,
				command.getColumnPositions(), 0, command.getSelectionFlags() ));
		return true;
	}
	
}
