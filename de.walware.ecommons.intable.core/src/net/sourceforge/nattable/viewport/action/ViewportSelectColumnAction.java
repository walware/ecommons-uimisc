package net.sourceforge.nattable.viewport.action;

import java.util.Set;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.selection.action.AbstractDefaultMouseSelectionAction;
import net.sourceforge.nattable.selection.command.ISelectionCommand.SelectionFlag;
import net.sourceforge.nattable.viewport.command.ViewportSelectColumnsCommand;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;


/**
 * Action to select the column at the mouse position
 */
public class ViewportSelectColumnAction extends AbstractDefaultMouseSelectionAction {
	
	
	public ViewportSelectColumnAction() {
		super(SWT.CTRL | SWT.SHIFT);
	}
	
	
	public void run(final NatTable natTable, final MouseEvent event,
			final Set<SelectionFlag> selectionFlags) {
		natTable.doCommand(new ViewportSelectColumnsCommand(natTable,
				natTable.getColumnPositionByX(event.x), selectionFlags ));
	}
	
}

