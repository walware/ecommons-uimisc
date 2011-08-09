package net.sourceforge.nattable.viewport.command;

import java.util.Set;

import net.sourceforge.nattable.command.AbstractRowCommand;
import net.sourceforge.nattable.layer.ILayer;
import net.sourceforge.nattable.selection.command.ISelectionCommand;

/**
 * Command to select a row.
 * Note: The row position is in top level composite Layer (NatTable) coordinates
 */
public class ViewportSelectRowCommand extends AbstractRowCommand implements ISelectionCommand {
	
	
	private final Set<SelectionFlag> selectionFlags;
	
	
	public ViewportSelectRowCommand(final ILayer layer, final int rowPosition,
			final Set<SelectionFlag> selectionFlags) {
		super(layer, rowPosition);
		
		this.selectionFlags = selectionFlags;
	}
	
	protected ViewportSelectRowCommand(ViewportSelectRowCommand command) {
		super(command);
		
		this.selectionFlags = command.selectionFlags;
	}
	
	public ViewportSelectRowCommand cloneCommand() {
		return new ViewportSelectRowCommand(this);
	}
	
	
	public Set<SelectionFlag> getSelectionFlags() {
		return this.selectionFlags;
	}
	
}
