/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.selection.action;

import org.eclipse.swt.events.MouseEvent;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.selection.SelectionFlags;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectRowsCommand;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;


/**
 * Action executed when the user selects any row in the grid.
 */
public class SelectRowAction implements IMouseAction {
	
	
	@Override
	public void run(NatTable natTable, MouseEvent event) {
		natTable.doCommand(new SelectRowsCommand(natTable,
				natTable.getColumnPositionByX(event.x),
				natTable.getRowPositionByY(event.y),
				SelectionFlags.swt2Flags(event.stateMask) ));
	}
	
}
