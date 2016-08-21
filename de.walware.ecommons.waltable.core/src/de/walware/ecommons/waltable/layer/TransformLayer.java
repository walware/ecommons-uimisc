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

package de.walware.ecommons.waltable.layer;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.layer.cell.ILayerCellDim;
import de.walware.ecommons.waltable.layer.cell.LayerCellDim;


/**
 * Abstract base class for layers that expose transformed views of an underlying layer.
 * 
 * By default the layer behaves as an identity transform of its underlying layer; that is, it
 * exposes its underlying layer as is without any changes.  Subclasses are expected to override
 * methods in this class to implement specific kinds of layer transformations.
 * 
 * The layer is similar to {@link AbstractLayerTransform}, but is {@link DimBasedLayer dim-based}.
 */
public abstract class TransformLayer extends ForwardLayer {
	
	
	public TransformLayer(/*@NonNull*/ final ILayer underlyingLayer) {
		super(underlyingLayer);
	}
	
	
	@Override
	protected abstract void initDims();
	
	
	@Override
	public ILayerCell getCellByPosition(final long columnPosition, final long rowPosition) {
		final ILayerCell underlyingCell= getUnderlyingLayer().getCellByPosition(
				getDim(HORIZONTAL).localToUnderlyingPosition(columnPosition, columnPosition),
				getDim(VERTICAL).localToUnderlyingPosition(rowPosition, rowPosition) );
		
		return createCell(
				transformCellDim(underlyingCell.getDim(HORIZONTAL), columnPosition),
				transformCellDim(underlyingCell.getDim(VERTICAL), rowPosition),
				underlyingCell );
	}
	
	protected ILayerCellDim transformCellDim(final ILayerCellDim underlyingDim, final long position) {
		if (underlyingDim.getPosition() == position) {
			return underlyingDim;
		}
		
		final long originPosition= position -
				(underlyingDim.getPosition() - underlyingDim.getOriginPosition());
		return new LayerCellDim(underlyingDim.getOrientation(), underlyingDim.getId(),
				position, originPosition, underlyingDim.getPositionSpan() );
	}
	
}
