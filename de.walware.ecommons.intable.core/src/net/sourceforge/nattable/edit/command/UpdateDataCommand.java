package net.sourceforge.nattable.edit.command;

import net.sourceforge.nattable.command.AbstractPositionCommand;
import net.sourceforge.nattable.layer.ILayer;

public class UpdateDataCommand extends AbstractPositionCommand {

	private final Object newValue;

	public UpdateDataCommand(ILayer layer, int columnPosition, int rowPosition, Object newValue) {
		super(layer, columnPosition, rowPosition);
		this.newValue = newValue;
	}
	
	protected UpdateDataCommand(UpdateDataCommand command) {
		super(command);
		this.newValue = command.newValue;
	}
	
	public Object getNewValue() {
		return newValue;
	}
	
	public UpdateDataCommand cloneCommand() {
		return new UpdateDataCommand(this);
	}

}
