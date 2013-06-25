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
// +(Collection args)
package org.eclipse.nebula.widgets.nattable.command;

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.coordinate.RangeList;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerDim;


public abstract class AbstractDimPositionsCommand implements ILayerCommand {
	
	
	private Orientation orientation;
	
	private ILayer layer;
	
	private int refPosition;
	
	private Collection<Range> positions;
	
	
	protected AbstractDimPositionsCommand(final Orientation orientation,
			final ILayer layer, final int refPosition, final Collection<Range> positions) {
		this.orientation = orientation;
		this.layer = layer;
		this.refPosition = refPosition;
		this.positions = positions;
	}
	
	protected AbstractDimPositionsCommand(final AbstractDimPositionsCommand command) {
		this.orientation = command.orientation;
		this.layer = command.layer;
		this.refPosition = command.refPosition;
		this.positions = command.positions;
	}
	
	
	protected ILayer getLayer() {
		return layer;
	}
	
	protected ILayerDim getDim() {
		return layer.getDim(orientation);
	}
	
	public Orientation getOrientation() {
		return orientation;
	}
	
	public int getRefPosition() {
		return this.refPosition;
	}
	
	public Collection<Range> getPositions() {
		return positions;
	}
	
	
	public boolean convertToTargetLayer(final ILayer targetLayer) {
		final ILayerDim dim = getDim();
		final ILayerDim targetDim = targetLayer.getDim(getOrientation());
		if (dim == targetDim) {
			return true;
		}
		
		return convertToTargetLayer(dim, this.refPosition, targetDim);
	}
	
	protected boolean convertToTargetLayer(final ILayerDim dim, final int refPosition,
			ILayerDim targetDim) {
		final int targetRefPosition;
		final RangeList targetPositions = new RangeList();
		if (refPosition != Integer.MIN_VALUE) {
			targetRefPosition = LayerCommandUtil.convertPositionToTargetContext(dim,
					refPosition, refPosition, targetDim );
			if (targetRefPosition == Integer.MIN_VALUE) {
				return false;
			}
			for (final Range range : positions) {
				for (int position = range.start; position < range.end; position++) {
					int targetPosition = LayerCommandUtil.convertPositionToTargetContext(dim,
							refPosition, position, targetDim );
					if (targetPosition != Integer.MIN_VALUE) {
						targetPositions.addValue(targetPosition);
					}
				}
			}
		}
		else {
			targetRefPosition = Integer.MIN_VALUE;
		}
		
		if (!targetPositions.isEmpty()) {
			this.layer = targetDim.getLayer();
			this.refPosition = targetRefPosition;
			this.positions = targetPositions;
			return true;
		}
		return false;
	}
	
}
