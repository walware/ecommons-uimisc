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
// -GC
package org.eclipse.nebula.widgets.nattable.resize.action;

import org.eclipse.swt.events.MouseEvent;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.coordinate.Point;
import org.eclipse.nebula.widgets.nattable.resize.command.InitializeAutoResizeRowsCommand;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeDetectUtil;


public class AutoResizeRowAction implements IMouseAction {
	
	public void run(NatTable natTable, MouseEvent event) {
		Point clickPoint = new Point(event.x, event.y);
		long row = CellEdgeDetectUtil.getRowPositionToResize(natTable, clickPoint);

		InitializeAutoResizeRowsCommand command = new InitializeAutoResizeRowsCommand(natTable, row);
		natTable.doCommand(command);
	}
	
}
