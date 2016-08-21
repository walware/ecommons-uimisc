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
package de.walware.ecommons.waltable.selection;

import java.util.Collection;

import de.walware.ecommons.waltable.command.AbstractDimPositionsCommand;
import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.LRangeList;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.layer.LayerUtil;


public class SelectDimPositionsCommand extends AbstractDimPositionsCommand {
	
	
	private final int selectionFlags;
	
	private long orthogonalPosition;
	
	private long positionToReveal;
	
	
	public SelectDimPositionsCommand(final ILayerDim layerDim,
			final long position, final long orthogonalPosition,
			final int selectionFlags) {
		this(layerDim, position, new LRangeList(position), orthogonalPosition, selectionFlags,
				position );
	}
	
	public SelectDimPositionsCommand(final ILayerDim layerDim,
			final long refPosition, final Collection<LRange> positions,
			final long orthogonalPosition, final int selectionFlags, final long positionToReveal) {
		super(layerDim, refPosition, positions);
		
		this.orthogonalPosition= orthogonalPosition;
		this.selectionFlags= selectionFlags;
		this.positionToReveal= positionToReveal;
	}
	
	protected SelectDimPositionsCommand(final SelectDimPositionsCommand command) {
		super(command);
		
		this.orthogonalPosition= command.orthogonalPosition;
		this.selectionFlags= command.selectionFlags;
		this.positionToReveal= command.positionToReveal;
	}
	
	@Override
	public SelectDimPositionsCommand cloneCommand() {
		return new SelectDimPositionsCommand(this);
	}
	
	
	@Override
	public boolean convertToTargetLayer(final ILayer targetLayer) {
		final ILayerDim layerDim= getDim();
		final long targetOrthogonalPosition= LayerUtil.convertPosition(
				layerDim.getLayer().getDim(getOrientation().getOrthogonal()),
				this.orthogonalPosition, this.orthogonalPosition,
				targetLayer.getDim(getOrientation().getOrthogonal()) );
		if (targetOrthogonalPosition != ILayerDim.POSITION_NA
				&& super.convertToTargetLayer(targetLayer) ) {
			this.orthogonalPosition= targetOrthogonalPosition;
			this.positionToReveal= LayerUtil.convertPosition(layerDim,
					this.positionToReveal, this.positionToReveal,
					targetLayer.getDim(getOrientation()) );
			return true;
		}
		return false;
	}
	
	
	public long getOrthogonalPosition() {
		return this.orthogonalPosition;
	}
	
	public int getSelectionFlags() {
		return this.selectionFlags;
	}
	
	public long getPositionToReveal() {
		return this.positionToReveal;
	}
	
}
