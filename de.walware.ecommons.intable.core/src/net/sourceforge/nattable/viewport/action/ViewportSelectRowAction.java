package net.sourceforge.nattable.viewport.action;

import java.util.Set;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.selection.action.AbstractDefaultMouseSelectionAction;
import net.sourceforge.nattable.selection.command.ISelectionCommand.SelectionFlag;
import net.sourceforge.nattable.viewport.command.ViewportSelectRowCommand;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;


/**
 * Action to select the row at the mouse position
 */
public class ViewportSelectRowAction extends AbstractDefaultMouseSelectionAction {
	
	
	public ViewportSelectRowAction() {
		super(SWT.CTRL | SWT.SHIFT);
	}
	
	
	@Override
	protected void run(final NatTable natTable, final MouseEvent event,
			final Set<SelectionFlag> selectionFlags) {
		natTable.doCommand(new ViewportSelectRowCommand(natTable, 
				natTable.getRowPositionByY(event.y), selectionFlags ));
	}
	
}
