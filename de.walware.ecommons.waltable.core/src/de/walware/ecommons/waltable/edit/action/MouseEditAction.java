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

package de.walware.ecommons.waltable.edit.action;

import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.edit.EditCellCommand;
import de.walware.ecommons.waltable.ui.action.IMouseClickAction;


/**
 * Action that will execute an {@link EditCellCommand}.
 * It determines the cell to edit by mouse pointer coordinates
 * instead of using a SelectionLayer. So this action is also
 * working in NatTables that doesn't have a SelectionLayer in
 * its composition of layers.
 */
public class MouseEditAction implements IMouseClickAction {
	
	
	public MouseEditAction() {
	}
	
	
	@Override
	public void run(final NatTable natTable, final MouseEvent event) {
		final long columnPosition= natTable.getColumnPositionByX(event.x);
		final long rowPosition= natTable.getRowPositionByY(event.y);
		
		if (columnPosition < 0 || rowPosition < 0) {
			return;
		}
		
		natTable.doCommand(new EditCellCommand(natTable,
				natTable.getConfigRegistry(),
				natTable.getCellByPosition(columnPosition, rowPosition) ));
	}
	
	@Override
	public boolean isExclusive() {
		return true;
	}
	
}
