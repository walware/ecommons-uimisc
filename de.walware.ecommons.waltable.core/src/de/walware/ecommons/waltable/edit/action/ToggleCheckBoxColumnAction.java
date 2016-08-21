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
import de.walware.ecommons.waltable.edit.UpdateDataCommand;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.LayerUtil;
import de.walware.ecommons.waltable.painter.cell.ColumnHeaderCheckBoxPainter;
import de.walware.ecommons.waltable.ui.action.IMouseAction;


public class ToggleCheckBoxColumnAction implements IMouseAction {

	private final ColumnHeaderCheckBoxPainter columnHeaderCheckBoxPainter;
	private final ILayer bodyDataLayer;

	public ToggleCheckBoxColumnAction(final ColumnHeaderCheckBoxPainter columnHeaderCheckBoxPainter, final ILayer bodyDataLayer) {
		this.columnHeaderCheckBoxPainter= columnHeaderCheckBoxPainter;
		this.bodyDataLayer= bodyDataLayer;
	}
	
	@Override
	public void run(final NatTable natTable, final MouseEvent event) {
		final long sourceColumnPosition= natTable.getColumnPositionByX(event.x);
		final long columnPosition= LayerUtil.convertColumnPosition(natTable, sourceColumnPosition, this.bodyDataLayer);
		
		final long checkedCellsCount= this.columnHeaderCheckBoxPainter.getCheckedCellsCount(columnPosition, natTable.getConfigRegistry());
		final boolean targetState= checkedCellsCount < this.bodyDataLayer.getRowCount();
		
		for (long rowPosition= 0; rowPosition < this.bodyDataLayer.getRowCount(); rowPosition++) {
			this.bodyDataLayer.doCommand(new UpdateDataCommand(this.bodyDataLayer, columnPosition, rowPosition, targetState));
		}
	}

}
