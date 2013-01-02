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
package org.eclipse.nebula.widgets.nattable.selection;

import static org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.RANGE_SELECTION;
import static org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.RETAIN_SELECTION;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectColumnsCommand;
import org.eclipse.nebula.widgets.nattable.selection.event.ColumnSelectionEvent;


public class SelectColumnCommandHandler extends AbstractLayerCommandHandler<SelectColumnsCommand> {


	private final SelectionLayer selectionLayer;


	public SelectColumnCommandHandler(SelectionLayer selectionLayer) {
		this.selectionLayer = selectionLayer;
	}


	public Class<SelectColumnsCommand> getCommandClass() {
		return SelectColumnsCommand.class;
	}

	protected boolean doCommand(final SelectColumnsCommand command) {
		toggleOrSelectColumn(command.getColumnPositions(), command.getRowPosition(),
				command.getSelectionFlags(), command.getColumnPositionToReveal() );
		return true;
	}

	protected void toggleOrSelectColumn(Collection<Integer> columnPositions, int rowPosition,
			final int selectionFlags, final int columnPositionToReveal) {
		int singleColumnPosition;
		if ((selectionFlags & (RETAIN_SELECTION | RANGE_SELECTION)) == RETAIN_SELECTION
				&& columnPositions.size() == 1
				&& this.selectionLayer.isColumnPositionFullySelected(
						singleColumnPosition = columnPositions.iterator().next()) ) {
			final Rectangle columnRegion = new Rectangle(
					singleColumnPosition, 0, 1, selectionLayer.getRowCount());
			this.selectionLayer.clearSelection(columnRegion);
			this.selectionLayer.fireLayerEvent(new ColumnSelectionEvent(selectionLayer,
					singleColumnPosition, (columnPositionToReveal == singleColumnPosition) ));
			return;
		}
		selectColumn(columnPositions, rowPosition, selectionFlags, columnPositionToReveal );
	}

	protected void selectColumn(Collection<Integer> columnPositions, int rowPosition,
			final int selectionFlags, final int columnPositionToReveal) {
		int columnPosition = -1;
		if ((selectionFlags & (RETAIN_SELECTION | RANGE_SELECTION)) == 0) {
			this.selectionLayer.clearSelections();
		}
		if (columnPositions.isEmpty() || ((selectionFlags & RANGE_SELECTION) != 0 && columnPositions.size() > 1)) {
		}
		if ((selectionFlags & RANGE_SELECTION) != 0 && this.selectionLayer.lastSelectedRegion != null
				&& this.selectionLayer.selectionAnchor.columnPosition >= 0) {
			if ((selectionFlags & RETAIN_SELECTION) != 0) {
				this.selectionLayer.lastSelectedRegion = new Rectangle(0, 0, 0, 0);
			}
			
			columnPosition = columnPositions.iterator().next();
			this.selectionLayer.lastSelectedRegion.x = Math.min(this.selectionLayer.selectionAnchor.columnPosition, columnPosition);
			this.selectionLayer.lastSelectedRegion.width = Math.abs(this.selectionLayer.selectionAnchor.columnPosition - columnPosition) + 1;
			this.selectionLayer.lastSelectedRegion.y = 0;
			this.selectionLayer.lastSelectedRegion.height = this.selectionLayer.getRowCount();
			
			this.selectionLayer.addSelection(this.selectionLayer.lastSelectedRegion);
		}
		else {
			for (Iterator<Integer> iterator = columnPositions.iterator(); iterator.hasNext();) {
				columnPosition = iterator.next();
				this.selectionLayer.addSelection(new Rectangle(columnPosition, 0,
						1, this.selectionLayer.getRowCount() ));
			}
			
			this.selectionLayer.selectionAnchor.columnPosition = columnPosition;
			this.selectionLayer.selectionAnchor.rowPosition = rowPosition;
		}
		
		if (columnPosition >= 0) {
			this.selectionLayer.lastSelectedCell.columnPosition = columnPosition;
			this.selectionLayer.lastSelectedCell.rowPosition = this.selectionLayer.getRowCount() - 1;
		}
		
		// TODO orrect change set
		this.selectionLayer.fireLayerEvent(new ColumnSelectionEvent(this.selectionLayer,
				columnPositions, columnPositionToReveal ));
	}

}
