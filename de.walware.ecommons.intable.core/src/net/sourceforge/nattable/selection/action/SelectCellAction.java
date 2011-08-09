package net.sourceforge.nattable.selection.action;

import java.util.Set;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.selection.command.ISelectionCommand.SelectionFlag;
import net.sourceforge.nattable.selection.command.SelectCellCommand;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;


/**
 * Action executed when the user selects any cell in the grid.
 */
public class SelectCellAction extends AbstractDefaultMouseSelectionAction {
	
	
	public SelectCellAction() {
		super(SWT.CTRL | SWT.SHIFT);
	}
	
	
	@Override
	protected void run(final NatTable natTable, final MouseEvent event,
			final Set<SelectionFlag> selectionFlags) {
		natTable.doCommand(new SelectCellCommand(natTable,
				natTable.getColumnPositionByX(event.x),natTable.getRowPositionByY(event.y),
				selectionFlags ));
	}
	
}
