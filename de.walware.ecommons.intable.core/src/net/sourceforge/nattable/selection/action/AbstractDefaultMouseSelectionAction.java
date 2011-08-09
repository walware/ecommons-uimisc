package net.sourceforge.nattable.selection.action;

import java.util.Set;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.selection.command.ISelectionCommand.SelectionFlag;
import net.sourceforge.nattable.ui.action.IMouseAction;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;


public abstract class AbstractDefaultMouseSelectionAction implements IMouseAction {
	
	
	protected final int enabledStateMask;
	
	
	protected AbstractDefaultMouseSelectionAction(final int enabledStateMask) {
		this.enabledStateMask = enabledStateMask;
	}
	
	
	public void run(NatTable natTable, MouseEvent event) {
		final Set<SelectionFlag> selectionFlags = SelectionFlag.newSet();
		if ((event.stateMask & this.enabledStateMask & SWT.CTRL) == SWT.CTRL) {
			selectionFlags.add(SelectionFlag.KEEP_SELECTION);
		}
		if ((event.stateMask & this.enabledStateMask & SWT.SHIFT) == SWT.SHIFT) {
			selectionFlags.add(SelectionFlag.RANGE_SELECTION);
		}
		
		run(natTable, event, selectionFlags);
	}
	
	protected abstract void run(NatTable natTable, MouseEvent event,
			Set<SelectionFlag> selectionFlags);
	
}
