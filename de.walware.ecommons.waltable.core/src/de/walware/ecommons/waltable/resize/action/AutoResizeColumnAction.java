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
// -GC

package de.walware.ecommons.waltable.resize.action;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;

import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.coordinate.LPoint;
import de.walware.ecommons.waltable.resize.InitializeAutoResizeCommand;
import de.walware.ecommons.waltable.ui.action.IMouseAction;
import de.walware.ecommons.waltable.ui.util.CellEdgeDetectUtil;


public class AutoResizeColumnAction implements IMouseAction {
	
	
	public AutoResizeColumnAction() {
	}
	
	
	@Override
	public void run(final NatTable natTable, final MouseEvent event) {
		final long position= CellEdgeDetectUtil.getPositionToResize(natTable,
				new LPoint(event.x, event.y), HORIZONTAL );
		if (position < 0) {
			return;
		}
		
		final InitializeAutoResizeCommand command= new InitializeAutoResizeCommand(
				natTable.getDim(HORIZONTAL), position );
		natTable.doCommand(command);
	}
	
}
