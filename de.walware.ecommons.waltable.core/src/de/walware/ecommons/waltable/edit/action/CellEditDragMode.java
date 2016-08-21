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
import de.walware.ecommons.waltable.selection.action.CellSelectionDragMode;


/**
 * Specialisation of CellSelectionDragMode that is used in the context of editing.
 * If a drag&amp;drop operation is executed on the same cell, the corresponding editor
 * will be activated, just as if you performed a click into that cell.
 * <p>
 * This is needed to treat minimal (not intended) drag&amp;drop operations like clicks.
 * It sometimes happens that on performing a click, the mouse moves a bit. So between
 * mouseDown and mouseUp there is a movement registered, so it is not interpreted as
 * a click anymore, but as a drag&amp;drop operation. With this implementation registered
 * the described behaviour is avoided.
 */
public class CellEditDragMode extends CellSelectionDragMode {
	
	
	private long originalColumnPosition;
	private long originalRowPosition;
	
	
	public CellEditDragMode() {
	}
	
	
	@Override
	public void mouseDown(final NatTable natTable, final MouseEvent event) {
		super.mouseDown(natTable, event);
		
		final long columnPosition= natTable.getColumnPositionByX(event.x);
		final long rowPosition= natTable.getRowPositionByY(event.y);
		
		this.originalColumnPosition= columnPosition;
		this.originalRowPosition= rowPosition;
	}
	
	@Override
	public void mouseMove(final NatTable natTable, final MouseEvent event) {
		super.mouseMove(natTable, event);
		
		final long columnPosition= natTable.getColumnPositionByX(event.x);
		final long rowPosition= natTable.getRowPositionByY(event.y);
		
		if (columnPosition != this.originalColumnPosition
				|| rowPosition != this.originalRowPosition) {
			// Left original cell, cancel edit
			this.originalColumnPosition= -1;
			this.originalRowPosition= -1;
		}
	}
	
	@Override
	public void mouseUp(final NatTable natTable, final MouseEvent event) {
		super.mouseUp(natTable, event);
		
		final long columnPosition= natTable.getColumnPositionByX(event.x);
		final long rowPosition= natTable.getRowPositionByY(event.y);
		
		if (columnPosition < 0 || columnPosition != this.originalColumnPosition
				|| rowPosition < 0 || rowPosition != this.originalRowPosition) {
			return;
		}
		
		natTable.doCommand(new EditCellCommand(natTable,
				natTable.getConfigRegistry(),
				natTable.getCellByPosition(columnPosition, rowPosition) ));
	}
	
}
