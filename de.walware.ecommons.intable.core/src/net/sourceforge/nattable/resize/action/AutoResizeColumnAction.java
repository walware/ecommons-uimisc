package net.sourceforge.nattable.resize.action;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.resize.command.InitializeAutoResizeColumnsCommand;
import net.sourceforge.nattable.ui.action.IMouseAction;
import net.sourceforge.nattable.ui.util.CellEdgeDetectUtil;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;


public class AutoResizeColumnAction implements IMouseAction {
	
	
	public void run(NatTable natTable, MouseEvent event) {
		Point clickPoint = new Point(event.x, event.y);
		int column = CellEdgeDetectUtil.getColumnPositionToResize(natTable, clickPoint);
		
		InitializeAutoResizeColumnsCommand command = new InitializeAutoResizeColumnsCommand(natTable,
				column, natTable);
		natTable.doCommand(command);
	}

}