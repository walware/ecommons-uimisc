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
package org.eclipse.nebula.widgets.nattable.viewport.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.viewport.command.ViewportSelectColumnCommand;


/**
 * Action to select the column at the mouse position.
 */
public class ViewportSelectColumnAction implements IMouseAction {


	public ViewportSelectColumnAction() {
	}


	@Override
	public void run(NatTable natTable, MouseEvent event) {
		int colPosition = natTable.getColumnPositionByX(event.x);
		int rowPosition = natTable.getRowPositionByY(event.y);
		ILayerCell cell = natTable.getCellByPosition(colPosition, rowPosition);
		if (cell.isSpannedCell()) {
			int span = cell.getColumnSpan();
			int position = cell.getOriginColumnPosition();
			List<Integer> positions = new ArrayList<Integer>(span);
			for (int i = 0; i < span; i++, position++) {
				positions.add(position);
			}
			natTable.doCommand(new ViewportSelectColumnCommand(natTable, positions,
					event.stateMask & (SWT.SHIFT | SWT.CONTROL), colPosition ));
		}
		else {
			natTable.doCommand(new ViewportSelectColumnCommand(natTable, colPosition,
					event.stateMask & (SWT.SHIFT | SWT.CONTROL) ));
		}
	}

}
