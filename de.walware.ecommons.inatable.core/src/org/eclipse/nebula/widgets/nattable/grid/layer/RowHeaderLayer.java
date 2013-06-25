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
package org.eclipse.nebula.widgets.nattable.grid.layer;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerDim;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCellDim;
import org.eclipse.nebula.widgets.nattable.layer.config.DefaultRowHeaderLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.painter.layer.ILayerPainter;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.SelectionStyleLabels;


/**
 * Layer for the row headers of the grid layer
 */
public class RowHeaderLayer extends DimensionallyDependentIndexLayer {

	private final SelectionLayer selectionLayer;


	/**
	 * Creates a row header layer using the default configuration and painter
	 * 
	 * @param baseLayer
	 *            The data provider for this layer
	 * @param verticalLayerDependency
	 *            The layer to link the vertical dimension to, typically the body layer
	 * @param selectionLayer
	 *            The selection layer required to respond to selection events
	 */
	public RowHeaderLayer(IUniqueIndexLayer baseLayer, ILayer verticalLayerDependency, SelectionLayer selectionLayer) {
		this(baseLayer, verticalLayerDependency, selectionLayer, true);
	}
	
	public RowHeaderLayer(IUniqueIndexLayer baseLayer, ILayer verticalLayerDependency, SelectionLayer selectionLayer,
			boolean useDefaultConfiguration) {
		this(baseLayer, verticalLayerDependency, selectionLayer, useDefaultConfiguration, null);
	}

	/**
	 * @param baseLayer
	 *            The data provider for this layer
	 * @param verticalLayerDependency
	 *            The layer to link the vertical dimension to, typically the body layer
	 * @param selectionLayer
	 *            The selection layer required to respond to selection events
	 * @param useDefaultConfiguration
	 *            If default configuration should be applied to this layer
	 * @param layerPainter
	 *            The painter for this layer or <code>null</code> to use the painter of the base layer
	 */
	public RowHeaderLayer(IUniqueIndexLayer baseLayer, ILayer verticalLayerDependency,
			SelectionLayer selectionLayer, boolean useDefaultConfiguration, ILayerPainter layerPainter) {
		super(baseLayer, baseLayer, verticalLayerDependency);
		if (selectionLayer == null) {
			throw new NullPointerException("selectionLayer"); //$NON-NLS-1$
		}

		this.selectionLayer = selectionLayer;
		this.layerPainter = layerPainter;
		
		if (useDefaultConfiguration) {
			addConfiguration(new DefaultRowHeaderLayerConfiguration());
		}
	}


	@Override
	protected String getDisplayMode(final LayerCellDim hDim, final LayerCellDim vDim,
			final String displayMode) {
		return (isSelected(hDim, vDim)) ? DisplayMode.SELECT : displayMode;
	}
	
	protected boolean isSelected(final LayerCellDim hDim, final LayerCellDim vDim) {
		final ILayerDim dim = getDim(VERTICAL);
		final long rowPosition = vDim.getPosition();
		if (this.selectionLayer.isRowPositionSelected(
				LayerUtil.convertPosition(dim, rowPosition, rowPosition, this.selectionLayer) )) {
			return true;
		}
		return false;
	}
	
	@Override
	public LabelStack getConfigLabelsByPosition(long columnPosition, long rowPosition) {
		LabelStack labelStack = super.getConfigLabelsByPosition(columnPosition, rowPosition);
		
		final long selectionLayerRowPosition = LayerUtil.convertRowPosition(this, rowPosition, selectionLayer);
		if (selectionLayer.isRowPositionFullySelected(selectionLayerRowPosition)) {
			labelStack.addLabel(SelectionStyleLabels.ROW_FULLY_SELECTED_STYLE);
		}
		
		return labelStack;
	}
	
}
