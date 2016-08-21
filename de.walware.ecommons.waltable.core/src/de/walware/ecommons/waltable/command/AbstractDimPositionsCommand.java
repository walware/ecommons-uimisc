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
// +(Collection args)
package de.walware.ecommons.waltable.command;

import java.util.Collection;

import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.LRangeList;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.layer.LayerUtil;


public abstract class AbstractDimPositionsCommand implements ILayerCommand {
	
	protected static final long NO_REF= Long.MIN_VALUE + 1;
	
	
	private ILayerDim layerDim;
	
	private long refPosition;
	
	private Collection<LRange> positions;
	
	
	protected AbstractDimPositionsCommand(final ILayerDim layerDim,
			final long refPosition, final Collection<LRange> positions) {
		this.layerDim= layerDim;
		this.refPosition= refPosition;
		this.positions= positions;
	}
	
	protected AbstractDimPositionsCommand(final ILayerDim layerDim,
			final Collection<LRange> positions) {
		this(layerDim, NO_REF, positions);
	}
	
	protected AbstractDimPositionsCommand(final AbstractDimPositionsCommand command) {
		this.layerDim= command.layerDim;
		this.refPosition= command.refPosition;
		this.positions= command.positions;
	}
	
	
	public final Orientation getOrientation() {
		return this.layerDim.getOrientation();
	}
	
	public final ILayerDim getDim() {
		return this.layerDim;
	}
	
	public long getRefPosition() {
		return this.refPosition;
	}
	
	public Collection<LRange> getPositions() {
		return this.positions;
	}
	
	
	@Override
	public boolean convertToTargetLayer(final ILayer targetLayer) {
		final ILayerDim targetDim= targetLayer.getDim(getOrientation());
		if (this.layerDim == targetDim) {
			return true;
		}
		
		return convertToTargetLayer(this.layerDim, this.refPosition, targetDim);
	}
	
	protected boolean convertToTargetLayer(final ILayerDim dim,
			final long refPosition, final ILayerDim targetDim) {
		final long targetRefPosition;
		final LRangeList targetPositions= new LRangeList();
		if (refPosition == NO_REF) {
			targetRefPosition= NO_REF;
			for (final LRange lRange : this.positions) {
				for (long position= lRange.start; position < lRange.end; position++) {
					final long targetPosition= LayerUtil.convertPosition(dim,
							position, position, targetDim );
					if (targetPosition != ILayerDim.POSITION_NA) {
						targetPositions.values().add(targetPosition);
					}
				}
			}
		}
		else if (refPosition != ILayerDim.POSITION_NA) {
			targetRefPosition= LayerUtil.convertPosition(dim, refPosition, refPosition, targetDim);
			if (targetRefPosition == ILayerDim.POSITION_NA) {
				return false;
			}
			for (final LRange lRange : this.positions) {
				for (long position= lRange.start; position < lRange.end; position++) {
					final long targetPosition= LayerUtil.convertPosition(dim,
							refPosition, position, targetDim );
					if (targetPosition != ILayerDim.POSITION_NA) {
						targetPositions.values().add(targetPosition);
					}
				}
			}
		}
		else {
			targetRefPosition= ILayerDim.POSITION_NA;
		}
		
		if (targetPositions.isEmpty()) {
			return false;
		}
		
		this.layerDim= targetDim;
		this.refPosition= targetRefPosition;
		this.positions= targetPositions;
		return true;
	}
	
}
