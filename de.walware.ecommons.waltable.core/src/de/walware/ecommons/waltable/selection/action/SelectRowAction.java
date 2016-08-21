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

package de.walware.ecommons.waltable.selection.action;

import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.selection.SelectDimPositionsCommand;
import de.walware.ecommons.waltable.selection.SelectionFlags;
import de.walware.ecommons.waltable.ui.action.IMouseAction;


/**
 * Action executed when the user selects any row in the grid.
 */
public class SelectRowAction implements IMouseAction {
	
	
	public SelectRowAction() {
	}
	
	
	@Override
	public void run(final NatTable natTable, final MouseEvent event) {
		natTable.doCommand(new SelectDimPositionsCommand(natTable.getDim(VERTICAL),
				natTable.getRowPositionByY(event.y),
				natTable.getColumnPositionByX(event.x),
				SelectionFlags.swt2Flags(event.stateMask) ));
	}
	
}
