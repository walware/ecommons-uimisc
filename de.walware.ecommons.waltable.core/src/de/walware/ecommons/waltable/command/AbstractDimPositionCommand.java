/*******************************************************************************
 * Copyright (c) 2012-2016 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package de.walware.ecommons.waltable.command;

import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.layer.LayerUtil;


public abstract class AbstractDimPositionCommand implements ILayerCommand {
	
	
	private ILayerDim layerDim;
	
	private long position;
	
	
	protected AbstractDimPositionCommand(final ILayerDim layerDim, final long position) {
		this.layerDim= layerDim;
		this.position= position;
	}
	
	protected AbstractDimPositionCommand(final AbstractDimPositionCommand command) {
		this.layerDim= command.layerDim;
		this.position= command.position;
	}
	
	
	public final Orientation getOrientation() {
		return this.layerDim.getOrientation();
	}
	
	public final ILayerDim getDim() {
		return this.layerDim;
	}
	
	public long getPosition() {
		return this.position;
	}
	
	
	@Override
	public boolean convertToTargetLayer(final ILayer targetLayer) {
		final ILayerDim targetDim= targetLayer.getDim(getOrientation());
		if (this.layerDim == targetDim) {
			return true;
		}
		
		return convertToTargetLayer(this.layerDim, this.position, targetDim);
	}
	
	protected boolean convertToTargetLayer(final ILayerDim dim,
			final long position, final ILayerDim targetDim) {
		final long targetPosition= LayerUtil.convertPosition(dim, position, position, targetDim);
		if (targetPosition == ILayerDim.POSITION_NA) {
			return false;
		}
		
		this.layerDim= targetDim;
		this.position= targetPosition;
		return true;
	}
	
}
