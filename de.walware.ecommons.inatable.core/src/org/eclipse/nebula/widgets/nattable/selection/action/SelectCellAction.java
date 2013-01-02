/*******************************************************************************
 * Copyright (c) 2012-2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// ~Selection
package org.eclipse.nebula.widgets.nattable.selection.action;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;


/**
 * Action executed when the user selects any cell in the grid.
 */
public class SelectCellAction implements IMouseAction {
	
	
	public SelectCellAction() {
	}
	
	
	@Override
	public void run(final NatTable natTable, final MouseEvent event) {
		natTable.doCommand(new SelectCellCommand(natTable,
				natTable.getColumnPositionByX(event.x),
				natTable.getRowPositionByY(event.y),
				event.stateMask & (SWT.SHIFT | SWT.CONTROL),
				true ));
	}
	
}
