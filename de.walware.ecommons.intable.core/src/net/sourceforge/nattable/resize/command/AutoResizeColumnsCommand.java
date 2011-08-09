package net.sourceforge.nattable.resize.command;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.command.AbstractMultiColumnCommand;
import net.sourceforge.nattable.command.ILayerCommand;

/**
 * Command indicating that all selected columns have to be auto resized i.e made
 * wide enough to just fit the widest cell. This should also take the column
 * header into account
 * 
 * Note: The {@link InitializeAutoResizeColumnsCommand} has to be fired first
 * when autoresizing columns.
 */

public class AutoResizeColumnsCommand extends AbstractMultiColumnCommand {
	
	
	private final NatTable natTable;
	
	
	public AutoResizeColumnsCommand(InitializeAutoResizeColumnsCommand initCommand,
			NatTable natTable) {
		super(initCommand.getLayer(), initCommand.getColumnPositions());
		
		this.natTable = natTable;
	}
	
	protected AutoResizeColumnsCommand(AutoResizeColumnsCommand command) {
		super(command);
		
		this.natTable = command.natTable;
	}
	
	public ILayerCommand cloneCommand() {
		return new AutoResizeColumnsCommand(this);
	}
	
	
	public NatTable getNatTable() {
		return natTable;
	}
	
}
