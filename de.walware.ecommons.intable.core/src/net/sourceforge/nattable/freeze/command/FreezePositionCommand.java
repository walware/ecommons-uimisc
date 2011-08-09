package net.sourceforge.nattable.freeze.command;

import net.sourceforge.nattable.command.AbstractPositionCommand;
import net.sourceforge.nattable.command.ILayerCommand;
import net.sourceforge.nattable.layer.ILayer;

public class FreezePositionCommand extends AbstractPositionCommand implements IFreezeCommand {
	
	public FreezePositionCommand(ILayer layer, int columnPosition, int rowPosition) {
		super(layer, columnPosition, rowPosition);
	}
	
	protected FreezePositionCommand(FreezePositionCommand command) {
		super(command);
	}
	
	public ILayerCommand cloneCommand() {
		return new FreezePositionCommand(this);
	}
	
}