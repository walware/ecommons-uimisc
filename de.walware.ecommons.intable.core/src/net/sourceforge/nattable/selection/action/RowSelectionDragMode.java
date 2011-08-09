package net.sourceforge.nattable.selection.action;

import java.util.Set;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.selection.command.ISelectionCommand.SelectionFlag;
import net.sourceforge.nattable.selection.command.SelectRowsCommand;


/**
 * Selects the entire row when the mouse is dragged on the body.
 * <i>Multiple</i> rows are selected as the user drags.
 *
 * @see RowOnlySelectionBindings
 */
public class RowSelectionDragMode extends CellSelectionDragMode {
	
	
	public RowSelectionDragMode() {
	}
	
	
	@Override
	protected void fireSelectionCommand(final NatTable natTable,
			final int columnPosition, final int rowPosition, Set<SelectionFlag> selectionFlags) {
		natTable.doCommand(new SelectRowsCommand(natTable, 1, rowPosition, selectionFlags));
	}
	
}
