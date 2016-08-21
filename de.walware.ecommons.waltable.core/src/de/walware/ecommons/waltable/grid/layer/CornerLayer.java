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
// ~
package de.walware.ecommons.waltable.grid.layer;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.layer.cell.LayerCell;
import de.walware.ecommons.waltable.layer.cell.LayerCellDim;
import de.walware.ecommons.waltable.painter.layer.CellLayerPainter;
import de.walware.ecommons.waltable.painter.layer.ILayerPainter;


/**
 * Layer for the top left header corner of the grid layer
 */
public class CornerLayer extends DimensionallyDependentLayer {
	
	
	/**
	 * Creates a corner header layer using the default configuration and painter
	 * 
	 * @param baseLayer
	 *            The data provider for this layer
	 * @param horizontalLayerDependency
	 *            The layer to link the horizontal dimension to, typically the row header layer
	 * @param verticalLayerDependency
	 *            The layer to link the vertical dimension to, typically the column header layer
	 */
	public CornerLayer(final ILayer baseLayer,
			final ILayer horizontalLayerDependency, final ILayer verticalLayerDependency) {
		this(baseLayer, horizontalLayerDependency, verticalLayerDependency, true, new CellLayerPainter());
	}
	
	/**
	 * @param baseLayer
	 *            The data provider for this layer
	 * @param horizontalLayerDependency
	 *            The layer to link the horizontal dimension to, typically the row header layer
	 * @param verticalLayerDependency
	 *            The layer to link the vertical dimension to, typically the column header layer
	 * @param useDefaultConfiguration
	 *            If default configuration should be applied to this layer (at moment none)
	 * @param layerPainter
	 *            The painter for this layer or <code>null</code> to use the painter of the base layer
	 */
	public CornerLayer(final ILayer baseLayer,
			final ILayer horizontalLayerDependency, final ILayer verticalLayerDependency,
			final boolean useDefaultConfiguration, final ILayerPainter layerPainter) {
		super(baseLayer, horizontalLayerDependency, verticalLayerDependency);
		
		this.layerPainter= layerPainter;
	}
	
	
	@Override
	public ILayerCell getCellByPosition(final long columnPosition, final long rowPosition) {
		final ILayerDim hDim= getDim(HORIZONTAL);
		final ILayerDim vDim= getDim(VERTICAL);
		final long columnId= hDim.getPositionId(columnPosition, columnPosition);
		final long rowId= vDim.getPositionId(rowPosition, rowPosition);
		
		return new LayerCell(this,
				new LayerCellDim(HORIZONTAL, columnId,
						columnPosition, 0, hDim.getPositionCount() ),
				new LayerCellDim(VERTICAL, rowId,
						rowPosition, 0, getVerticalLayerDependency().getRowCount()) );
	}
	
}
