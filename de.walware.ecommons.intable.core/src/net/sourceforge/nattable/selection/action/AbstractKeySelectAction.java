package net.sourceforge.nattable.selection.action;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.coordinate.IRelative.Direction;
import net.sourceforge.nattable.ui.action.IKeyAction;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public abstract class AbstractKeySelectAction implements IKeyAction {

	private boolean shiftMask = false;
	private boolean controlMask = false;
	private boolean isStateMaskSpecified = false;
	private final Direction direction;

	public AbstractKeySelectAction(Direction direction) {
		this.direction = direction;
	}

	public AbstractKeySelectAction(Direction direction, boolean shiftMask, boolean ctrlMask) {
		this.direction = direction;
		this.shiftMask = shiftMask;
		this.controlMask = ctrlMask;
		this.isStateMaskSpecified = true;
	}

	public void run(NatTable natTable, KeyEvent event) {
		if (!isStateMaskSpecified) {
			this.shiftMask = (event.stateMask & SWT.SHIFT) != 0;
			this.controlMask = (event.stateMask & SWT.CTRL) != 0;
		}
	}

	protected boolean isShiftMask() {
		return shiftMask;
	}

	protected boolean isControlMask() {
		return controlMask;
	}

	public void setShiftMask(boolean shiftMask) {
		this.shiftMask = shiftMask;
	}

	public void setControlMask(boolean controlMask) {
		this.controlMask = controlMask;
	}

	public Direction getDirection() {
		return direction;
	}

}
