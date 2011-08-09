package net.sourceforge.nattable.viewport.command;

import net.sourceforge.nattable.command.AbstractLayerCommandHandler;
import net.sourceforge.nattable.layer.AbstractLayer;
import net.sourceforge.nattable.selection.command.SelectRowsCommand;


public class ViewportSelectRowCommandHandler extends AbstractLayerCommandHandler<ViewportSelectRowCommand> {
	
	
	private final AbstractLayer viewportLayer;
	
	
	public ViewportSelectRowCommandHandler(AbstractLayer viewportLayer) {
		this.viewportLayer = viewportLayer;
	}
	
	
	public Class<ViewportSelectRowCommand> getCommandClass() {
		return ViewportSelectRowCommand.class;
	}
	
	protected boolean doCommand(ViewportSelectRowCommand command) {
		viewportLayer.doCommand(new SelectRowsCommand(viewportLayer,
				0, command.getRowPosition(), command.getSelectionFlags()));
		return true;
	}
	
}
