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
import de.walware.ecommons.waltable.coordinate.LPoint;
import de.walware.ecommons.waltable.selection.SelectCellCommand;
import de.walware.ecommons.waltable.selection.SelectionFlags;
import de.walware.ecommons.waltable.ui.action.IDragMode;


/**
 * Fires commands to select a range of cells when the mouse is dragged in the viewport.
 */
public class CellSelectionDragMode implements IDragMode {
	
	
	private LPoint lastDragInCellPosition= null;
	
	
	public CellSelectionDragMode() {
	}
	
	
	@Override
	public void mouseDown(final NatTable natTable, final MouseEvent event) {
		natTable.forceFocus();
		this.lastDragInCellPosition= new LPoint(natTable.getColumnPositionByX(event.x), natTable.getRowPositionByY(event.y));
	}
	
	@Override
	public void mouseMove(final NatTable natTable, final MouseEvent event) {
		if (event.x > natTable.getWidth()) {
			return;
		}
		final long selectedColumnPosition= natTable.getColumnPositionByX(event.x);
		final long selectedRowPosition= natTable.getRowPositionByY(event.y);
		
		if (selectedColumnPosition > -1 && selectedRowPosition > -1) {
			final LPoint dragInCellPosition= new LPoint(selectedColumnPosition, selectedRowPosition);
			if (this.lastDragInCellPosition == null || !dragInCellPosition.equals(this.lastDragInCellPosition)) {
				this.lastDragInCellPosition= dragInCellPosition;
				
				fireSelectionCommand(natTable, selectedColumnPosition, selectedRowPosition, SelectionFlags.RANGE_SELECTION);
			}
		}
	}
	
	protected void fireSelectionCommand(final NatTable natTable, final long columnPosition,	final long rowPosition, final int selectionFlags) {
		natTable.doCommand(new SelectCellCommand(natTable, columnPosition, rowPosition, selectionFlags ));
	}
	
	@Override
	public void mouseUp(final NatTable natTable, final MouseEvent event) {
		endDrag();
	}
	
	private void endDrag(){
		this.lastDragInCellPosition= null;
	}
	
}
