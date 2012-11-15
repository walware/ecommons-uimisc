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
// ~Selection
package org.eclipse.nebula.widgets.nattable.viewport.action;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.viewport.command.ViewportSelectColumnCommand;


/**
 * Action to select the column at the mouse position.
 */
public class ViewportSelectColumnAction implements IMouseAction {


	public ViewportSelectColumnAction() {
	}


	public void run(NatTable natTable, MouseEvent event) {
		natTable.doCommand(new ViewportSelectColumnCommand(natTable,
				natTable.getColumnPositionByX(event.x),
				event.stateMask & (SWT.SHIFT | SWT.CONTROL) ));
	}

}
