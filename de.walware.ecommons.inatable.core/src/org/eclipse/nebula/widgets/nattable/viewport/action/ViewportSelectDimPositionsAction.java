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
package org.eclipse.nebula.widgets.nattable.viewport.action;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.events.MouseEvent;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.ILayerDim;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCellDim;
import org.eclipse.nebula.widgets.nattable.selection.SelectionFlags;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.viewport.command.ViewportSelectDimPositionsCommand;


/**
 * Action to select the column/row at the mouse position.
 */
public class ViewportSelectDimPositionsAction implements IMouseAction {
	
	
	private final Orientation orientation;
	
	
	public ViewportSelectDimPositionsAction(final Orientation orientation) {
		this.orientation = orientation;
	}
	
	
	@Override
	public void run(final NatTable natTable, final MouseEvent event) {
		final ILayerCell cell = natTable.getCellByPosition(
				natTable.getColumnPositionByX(event.x),
				natTable.getRowPositionByY(event.y) );
		if (cell == null) {
			return;
		}
		final ILayerDim layerDim = natTable.getDim(this.orientation);
		final LayerCellDim cellDim = cell.getDim(this.orientation);
		if (cellDim.getPositionSpan() > 1) {
			final List<Range> positions = Collections.singletonList(
					new Range(cellDim.getOriginPosition(), cellDim.getOriginPosition() + cellDim.getPositionSpan()) );
			natTable.doCommand(new ViewportSelectDimPositionsCommand(layerDim,
					cellDim.getPosition(), positions, cellDim.getPosition(),
					SelectionFlags.swt2Flags(event.stateMask) ));
		}
		else {
			natTable.doCommand(new ViewportSelectDimPositionsCommand(layerDim,
					cellDim.getPosition(),
					SelectionFlags.swt2Flags(event.stateMask) ));
		}
	}

}
