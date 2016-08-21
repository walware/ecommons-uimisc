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
package de.walware.ecommons.waltable.resize;

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;
import de.walware.ecommons.waltable.coordinate.LRangeList;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.layer.LayerUtil;
import de.walware.ecommons.waltable.selection.SelectionLayer;


public class InitializeAutoResizeCommandHandler extends AbstractLayerCommandHandler<InitializeAutoResizeCommand> {
	
	
	private final SelectionLayer selectionLayer;
	
	
	public InitializeAutoResizeCommandHandler(final SelectionLayer selectionLayer) {
		this.selectionLayer= selectionLayer;
	}
	
	@Override
	public Class<InitializeAutoResizeCommand> getCommandClass() {
		return InitializeAutoResizeCommand.class;
	}
	
	
	@Override
	protected boolean doCommand(final InitializeAutoResizeCommand initCommand) {
		final ILayerDim layerDim= initCommand.getDim();
		final long position= initCommand.getPosition();
		final LRangeList positions;
		
		final long selectionPosition= LayerUtil.convertPosition(layerDim,
				position, position, this.selectionLayer.getDim(layerDim.getOrientation()) );
		if (selectionPosition != ILayerDim.POSITION_NA
				&& this.selectionLayer.isPositionFullySelected(layerDim.getOrientation(), position) ) {
			positions= this.selectionLayer.getFullySelectedPositions(layerDim.getOrientation());
			
			this.selectionLayer.doCommand(new AutoResizePositionsCommand(
					this.selectionLayer.getDim(layerDim.getOrientation()), positions));
		}
		else {
			positions= new LRangeList(position);
			
			layerDim.getLayer().doCommand(new AutoResizePositionsCommand(layerDim, positions));
		}
		
		// Fire command carrying the selected columns
		return true;
	}
	
}
