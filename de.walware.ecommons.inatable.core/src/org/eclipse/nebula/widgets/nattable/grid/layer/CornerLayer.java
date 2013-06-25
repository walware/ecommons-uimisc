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
// ~
package org.eclipse.nebula.widgets.nattable.grid.layer;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;
import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;
import static org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell.NO_INDEX;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCellDim;
import org.eclipse.nebula.widgets.nattable.painter.layer.CellLayerPainter;
import org.eclipse.nebula.widgets.nattable.painter.layer.ILayerPainter;


/**
 * Layer for the top left header corner of the grid layer
 */
public class CornerLayer extends DimensionallyDependentIndexLayer {


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
	public CornerLayer(final IUniqueIndexLayer baseLayer,
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
	public CornerLayer(final IUniqueIndexLayer baseLayer,
			final ILayer horizontalLayerDependency, final ILayer verticalLayerDependency,
			final boolean useDefaultConfiguration, final ILayerPainter layerPainter) {
		super(baseLayer, horizontalLayerDependency, verticalLayerDependency);

		this.layerPainter = layerPainter;
	}


	@Override
	public ILayerCell getCellByPosition(final long columnPosition, final long rowPosition) {
		return new LayerCell(this,
				new LayerCellDim(HORIZONTAL, NO_INDEX,
						columnPosition, 0, getHorizontalLayerDependency().getColumnCount() ),
				new LayerCellDim(VERTICAL, NO_INDEX,
						rowPosition, 0, getVerticalLayerDependency().getRowCount()) );
	}

}
