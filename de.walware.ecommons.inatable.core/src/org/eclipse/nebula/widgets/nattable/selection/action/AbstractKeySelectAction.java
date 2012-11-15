/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// ~Direction
package org.eclipse.nebula.widgets.nattable.selection.action;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.coordinate.Direction;
import org.eclipse.nebula.widgets.nattable.ui.action.IKeyAction;

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
