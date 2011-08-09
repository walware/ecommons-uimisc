package net.sourceforge.nattable.selection.action;

import java.util.EnumSet;
import java.util.Set;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.selection.command.ISelectionCommand.SelectionFlag;
import net.sourceforge.nattable.selection.command.SelectCellCommand;
import net.sourceforge.nattable.ui.action.IDragMode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;

/**
 * Fires commands to select a range of cells when the mouse is dragged in the viewport.
 */
public class CellSelectionDragMode implements IDragMode {
	
	
	protected final int enabledStateMask;
	
	private Set<SelectionFlag> selectionFlags;
	
	private Point lastDragInCellPosition = null;
	
	
	public CellSelectionDragMode() {
		enabledStateMask = (SWT.CTRL | SWT.SHIFT);
	}
	
	
	protected Set<SelectionFlag> getSelectionFlags() {
		return selectionFlags;
	}
	
	public void mouseDown(NatTable natTable, MouseEvent event) {
		natTable.forceFocus();
		
		selectionFlags = SelectionFlag.newSet();
		if ((event.stateMask & this.enabledStateMask & SWT.CTRL) == SWT.CTRL) {
			selectionFlags.add(SelectionFlag.KEEP_SELECTION);
		}
		if ((event.stateMask & this.enabledStateMask & SWT.SHIFT) == SWT.SHIFT) {
			selectionFlags.add(SelectionFlag.RANGE_SELECTION);
		}
		
		fireSelectionCommand(natTable, natTable.getColumnPositionByX(event.x),
				natTable.getRowPositionByY(event.y), selectionFlags);
	}
	
	public void mouseMove(NatTable natTable, MouseEvent event) {
		if (event.x > natTable.getWidth()) {
			return;
		}
		
		int selectedColumnPosition = natTable.getColumnPositionByX(event.x);
		int selectedRowPosition = natTable.getRowPositionByY(event.y);
		
		if (selectedColumnPosition > -1 && selectedRowPosition > -1) {
			Point dragInCellPosition = new Point(selectedColumnPosition, selectedRowPosition);
			if(lastDragInCellPosition == null || !dragInCellPosition.equals(lastDragInCellPosition)){
				lastDragInCellPosition = dragInCellPosition;
				
				fireSelectionCommand(natTable, selectedColumnPosition, selectedRowPosition,
						EnumSet.of(SelectionFlag.RANGE_SELECTION));
			}
		}
	}
	
	public void mouseUp(NatTable natTable, MouseEvent event) {
		lastDragInCellPosition = null;
	}
	
	protected void fireSelectionCommand(final NatTable natTable,
			final int columnPosition, final int rowPosition, Set<SelectionFlag> selectionFlags) {
		natTable.doCommand(new SelectCellCommand(natTable, columnPosition, rowPosition,
				selectionFlags ));
	}
	
}
