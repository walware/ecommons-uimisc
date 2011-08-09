package net.sourceforge.nattable.viewport.command;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.sourceforge.nattable.command.AbstractMultiColumnCommand;
import net.sourceforge.nattable.layer.ILayer;
import net.sourceforge.nattable.selection.command.ISelectionCommand.SelectionFlag;


public class ViewportSelectColumnsCommand extends AbstractMultiColumnCommand {
	
	
	private final Set<SelectionFlag> selectionFlags;
	
	
	public ViewportSelectColumnsCommand(final ILayer layer, int columnPosition,
			Set<SelectionFlag> selectionFlags) {
		this(layer, Collections.singleton(columnPosition), selectionFlags);
	}
	
	public ViewportSelectColumnsCommand(final ILayer layer, final Collection<Integer> columnPositions,
			Set<SelectionFlag> selectionFlags) {
		super(layer, columnPositions);
		
		this.selectionFlags = selectionFlags;
	}
	
	protected ViewportSelectColumnsCommand(ViewportSelectColumnsCommand command) {
		super(command);
		
		this.selectionFlags = command.selectionFlags;
	}
	
	public ViewportSelectColumnsCommand cloneCommand() {
		return new ViewportSelectColumnsCommand(this);
	}
	
	
	public Set<SelectionFlag> getSelectionFlags() {
		return this.selectionFlags;
	}
	
}
