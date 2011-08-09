package net.sourceforge.nattable.edit.action;

import java.util.Set;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.edit.command.EditCellCommand;
import net.sourceforge.nattable.selection.action.AbstractDefaultMouseSelectionAction;
import net.sourceforge.nattable.selection.command.ISelectionCommand.SelectionFlag;
import net.sourceforge.nattable.selection.command.SelectCellCommand;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;


public class MouseEditAction extends AbstractDefaultMouseSelectionAction {
	
	
	public MouseEditAction() {
		super(SWT.CTRL | SWT.SHIFT);
	}
	
	
	@Override
	protected void run(final NatTable natTable, final MouseEvent event,
			Set<SelectionFlag> selectionFlags) {
		final int columnPosition = natTable.getColumnPositionByX(event.x);
		final int rowPosition = natTable.getRowPositionByY(event.y);
		
		natTable.doCommand(new SelectCellCommand(natTable, columnPosition, rowPosition,
				selectionFlags ));
		
		natTable.doCommand(new EditCellCommand(natTable, natTable.getConfigRegistry(),
				natTable.getCellByPosition(columnPosition, rowPosition) ));
	}
	
}