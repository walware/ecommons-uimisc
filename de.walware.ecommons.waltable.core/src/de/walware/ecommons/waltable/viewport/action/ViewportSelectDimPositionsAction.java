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
package de.walware.ecommons.waltable.viewport.action;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.layer.cell.ILayerCellDim;
import de.walware.ecommons.waltable.selection.SelectionFlags;
import de.walware.ecommons.waltable.ui.action.IMouseAction;
import de.walware.ecommons.waltable.viewport.ViewportSelectDimPositionsCommand;


/**
 * Action to select the column/row at the mouse position.
 */
public class ViewportSelectDimPositionsAction implements IMouseAction {
	
	
	private final Orientation orientation;
	
	
	public ViewportSelectDimPositionsAction(final Orientation orientation) {
		this.orientation= orientation;
	}
	
	
	@Override
	public void run(final NatTable natTable, final MouseEvent event) {
		final ILayerCell cell= natTable.getCellByPosition(
				natTable.getColumnPositionByX(event.x),
				natTable.getRowPositionByY(event.y) );
		if (cell == null) {
			return;
		}
		final ILayerDim layerDim= natTable.getDim(this.orientation);
		final ILayerCellDim cellDim= cell.getDim(this.orientation);
		if (cellDim.getPositionSpan() > 1) {
			final List<LRange> positions= Collections.singletonList(
					new LRange(cellDim.getOriginPosition(), cellDim.getOriginPosition() + cellDim.getPositionSpan()) );
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
