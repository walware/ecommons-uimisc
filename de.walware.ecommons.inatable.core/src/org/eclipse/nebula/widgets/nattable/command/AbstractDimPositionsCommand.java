/*******************************************************************************
 * Copyright (c) 2012, 2013 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
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
	
	protected static final long NO_REF = Long.MIN_VALUE + 1;
	
	
	private final Orientation orientation;
	
	private ILayerDim layerDim;
	
	private long refPosition;
	
	private Collection<Range> positions;
	
	
	protected AbstractDimPositionsCommand(
			final ILayerDim layerDim, final long refPosition, final Collection<Range> positions) {
		this.orientation = layerDim.getOrientation();
		this.layerDim = layerDim;
		this.refPosition = refPosition;
		this.positions = positions;
	}
	
	protected AbstractDimPositionsCommand(
			final ILayerDim layerDim, final Collection<Range> positions) {
		this(layerDim, NO_REF, positions);
	}
	
	protected AbstractDimPositionsCommand(final AbstractDimPositionsCommand command) {
		this.orientation = command.orientation;
		this.layerDim = command.layerDim;
		this.refPosition = command.refPosition;
		this.positions = command.positions;
	}
	
	
	public final Orientation getOrientation() {
		return this.orientation;
	}
	
	protected final ILayerDim getDim() {
		return this.layerDim;
	}
	
	public long getRefPosition() {
		return this.refPosition;
	}
	
	public Collection<Range> getPositions() {
		return this.positions;
	}
	
	
	public boolean convertToTargetLayer(final ILayer targetLayer) {
		final ILayerDim targetDim = targetLayer.getDim(this.orientation);
		if (this.layerDim == targetDim) {
			return true;
		}
		
		return convertToTargetLayer(this.layerDim, this.refPosition, targetDim);
	}
	
	protected boolean convertToTargetLayer(final ILayerDim dim, final long refPosition,
			ILayerDim targetDim) {
		final long targetRefPosition;
		final RangeList targetPositions = new RangeList();
		if (refPosition == NO_REF) {
			targetRefPosition = NO_REF;
			for (final Range range : this.positions) {
				for (long position = range.start; position < range.end; position++) {
					final long targetPosition = LayerCommandUtil.convertPositionToTargetContext(dim,
							position, position, targetDim );
					if (targetPosition != Long.MIN_VALUE) {
						targetPositions.values().add(targetPosition);
					}
				}
			}
		}
		else if (refPosition != Long.MIN_VALUE) {
			targetRefPosition = LayerCommandUtil.convertPositionToTargetContext(dim,
					refPosition, refPosition, targetDim );
			if (targetRefPosition == Long.MIN_VALUE) {
				return false;
			}
			for (final Range range : positions) {
				for (long position = range.start; position < range.end; position++) {
					long targetPosition = LayerCommandUtil.convertPositionToTargetContext(dim,
							refPosition, position, targetDim );
					if (targetPosition != Long.MIN_VALUE) {
						targetPositions.values().add(targetPosition);
					}
				}
			}
		}
		else {
			targetRefPosition = Long.MIN_VALUE;
		}
		
		if (!targetPositions.isEmpty()) {
			this.layerDim = targetDim;
			this.refPosition = targetRefPosition;
			this.positions = targetPositions;
			return true;
		}
		return false;
	}
	
}
