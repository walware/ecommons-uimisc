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
// ~Selection
package org.eclipse.nebula.widgets.nattable.selection;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectColumnsCommand;
import org.eclipse.nebula.widgets.nattable.selection.event.ColumnSelectionEvent;


public class SelectColumnCommandHandler extends AbstractLayerCommandHandler<SelectColumnsCommand> {


	private final SelectionLayer selectionLayer;


	public SelectColumnCommandHandler(final SelectionLayer selectionLayer) {
		this.selectionLayer = selectionLayer;
	}


	public Class<SelectColumnsCommand> getCommandClass() {
		return SelectColumnsCommand.class;
	}

	@Override
	protected boolean doCommand(final SelectColumnsCommand command) {
		toggleOrSelectColumn(command.getColumnPositions(), command.getRowPosition(),
				command.getSelectionFlags(), command.getColumnPositionToReveal() );
		return true;
	}

	protected void toggleOrSelectColumn(final Collection<Long> columnPositions, final long rowPosition,
			final int selectionFlags, final long columnPositionToReveal) {
		long singleColumnPosition;
		if ((selectionFlags & (SelectionFlags.RETAIN_SELECTION | SelectionFlags.RANGE_SELECTION)) == SelectionFlags.RETAIN_SELECTION
				&& columnPositions.size() == 1
				&& this.selectionLayer.isColumnPositionFullySelected(
						singleColumnPosition = columnPositions.iterator().next()) ) {
			final Rectangle columnRegion = new Rectangle(
					singleColumnPosition, 0, 1, this.selectionLayer.getRowCount());
			this.selectionLayer.clearSelection(columnRegion);
			this.selectionLayer.fireLayerEvent(new ColumnSelectionEvent(this.selectionLayer,
					singleColumnPosition, (columnPositionToReveal == singleColumnPosition) ));
			return;
		}
		selectColumn(columnPositions, rowPosition, selectionFlags, columnPositionToReveal );
	}

	protected void selectColumn(final Collection<Long> columnPositions, final long rowPosition,
			final int selectionFlags, final long columnPositionToReveal) {
		long lastPosition = Long.MIN_VALUE;
		if ((selectionFlags & (SelectionFlags.RETAIN_SELECTION | SelectionFlags.RANGE_SELECTION)) == 0) {
			this.selectionLayer.clearSelections();
		}
		if (columnPositions.isEmpty() || ((selectionFlags & SelectionFlags.RANGE_SELECTION) != 0 && columnPositions.size() > 1)) {
		}
		if ((selectionFlags & SelectionFlags.RANGE_SELECTION) != 0 && this.selectionLayer.lastSelectedRegion != null
				&& this.selectionLayer.selectionAnchor.columnPosition >= 0) {
			if ((selectionFlags & SelectionFlags.RETAIN_SELECTION) != 0) {
				this.selectionLayer.lastSelectedRegion = new Rectangle(0, 0, 0, 0);
			}
			
			final long position = columnPositions.iterator().next();
			this.selectionLayer.lastSelectedRegion.x = Math.min(this.selectionLayer.selectionAnchor.columnPosition, position);
			this.selectionLayer.lastSelectedRegion.width = Math.abs(this.selectionLayer.selectionAnchor.columnPosition - position) + 1;
			this.selectionLayer.lastSelectedRegion.y = 0;
			this.selectionLayer.lastSelectedRegion.height = this.selectionLayer.getRowCount();
			
			lastPosition = position;
			
			this.selectionLayer.addSelection(this.selectionLayer.lastSelectedRegion);
		}
		else {
			long position = Long.MIN_VALUE;
			for (final Iterator<Long> iterator = columnPositions.iterator(); iterator.hasNext();) {
				position = iterator.next();
				if (position == columnPositionToReveal) {
					lastPosition = position;
				}
				this.selectionLayer.addSelection(new Rectangle(position, 0, 1, this.selectionLayer.getRowCount()));
			}
			
			if (lastPosition == Long.MIN_VALUE) {
				lastPosition = position;
			}
			this.selectionLayer.selectionAnchor.columnPosition = lastPosition;
			this.selectionLayer.selectionAnchor.rowPosition = rowPosition;
		}
		
		if (lastPosition >= 0) {
			this.selectionLayer.lastSelectedCell.columnPosition = lastPosition;
			this.selectionLayer.lastSelectedCell.rowPosition = this.selectionLayer.getRowCount() - 1;
		}
		
		// TODO correct change set
		this.selectionLayer.fireLayerEvent(new ColumnSelectionEvent(this.selectionLayer,
				columnPositions, columnPositionToReveal ));
	}

}
