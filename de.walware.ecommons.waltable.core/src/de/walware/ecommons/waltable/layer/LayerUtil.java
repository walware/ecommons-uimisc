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

import de.walware.ecommons.waltable.coordinate.PositionOutOfBoundsException;


public class LayerUtil {
	
	
	/**
	 * Convert column/row position from the source layer to the target layer
	 * @param sourceLayer source layer
	 * @param sourceColumnPosition column position in the source layer
	 * @param targetLayer layer to convert the from position to 
	 * @return converted position, or ILayerDim.POSITION_NA if conversion not possible
	 */
	public static final long convertPosition(final ILayerDim sourceDim,
			final long sourceRefPosition, final long sourcePosition,
			final ILayerDim targetDim) {
		if (targetDim == sourceDim) {
			return sourcePosition;
		}
		
		try {
			final long id= sourceDim.getPositionId(sourceRefPosition, sourcePosition);
			return targetDim.getPositionById(id);
		}
		catch (final PositionOutOfBoundsException e) {
			return ILayerDim.POSITION_NA;
		}
	}
	
	/**
	 * Convert column position from the source layer to the target layer
	 * @param sourceLayer source layer
	 * @param sourceColumnPosition column position in the source layer
	 * @param targetLayer layer to convert the from position to 
	 * @return converted column position, or -1 if conversion not possible
	 */
	public static final long convertColumnPosition(final ILayer sourceLayer,
			final long sourceColumnPosition, final ILayer targetLayer) {
		return convertPosition(sourceLayer.getDim(HORIZONTAL),
				sourceColumnPosition, sourceColumnPosition,
				targetLayer.getDim(HORIZONTAL) );
	}
	
	/**
	 * Convert row position from the source layer to the target layer
	 * @param sourceLayer source layer
	 * @param sourceRowPosition position in the source layer
	 * @param targetLayer layer to convert the from position to 
	 * @return converted row position, or -1 if conversion not possible
	 */
	public static final long convertRowPosition(final ILayer sourceLayer,
			final long sourceRowPosition, final ILayer targetLayer) {
		return convertPosition(sourceLayer.getDim(VERTICAL),
				sourceRowPosition, sourceRowPosition,
				targetLayer.getDim(VERTICAL) );
	}
	
}
