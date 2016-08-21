/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// ~Selection
package de.walware.ecommons.waltable.selection.action;

import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.selection.SelectCellCommand;
import de.walware.ecommons.waltable.selection.SelectionFlags;
import de.walware.ecommons.waltable.ui.action.IMouseAction;


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
				SelectionFlags.swt2Flags(event.stateMask),
				true ));
	}
	
}
