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

import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.config.DefaultColumnHeaderLayerConfiguration;
import de.walware.ecommons.waltable.painter.layer.CellLayerPainter;
import de.walware.ecommons.waltable.painter.layer.ILayerPainter;
import de.walware.ecommons.waltable.selection.SelectionLayer;
import de.walware.ecommons.waltable.style.SelectionStyleLabels;


/**
 * Responsible for rendering, event handling etc on the column headers.
 */
public class ColumnHeaderLayer extends AbstractPositionHeaderLayer {
	
	
	/**
	 * Creates a column header layer using the default configuration and painter
	 * 
	 * @param baseLayer
	 *            The data provider for this layer
	 * @param horizontalLayerDependency
	 *            The layer to link the horizontal dimension to, typically the body layer
	 * @param selectionLayer
	 *            The selection layer required to respond to selection events
	 */
	public ColumnHeaderLayer(final ILayer baseLayer, final ILayer horizontalLayerDependency, final SelectionLayer selectionLayer) {
		this(baseLayer, horizontalLayerDependency, selectionLayer, true);
	}
	
	public ColumnHeaderLayer(final ILayer baseLayer, final ILayer horizontalLayerDependency, final SelectionLayer selectionLayer, final boolean useDefaultConfiguration) {
		this(baseLayer, horizontalLayerDependency, selectionLayer, useDefaultConfiguration, new CellLayerPainter());
	}
	
	/**
	 * @param baseLayer
	 *            The data provider for this layer
	 * @param horizontalLayerDependency
	 *            The layer to link the horizontal dimension to, typically the body layer
	 * @param selectionLayer
	 *            The selection layer required to respond to selection events
	 * @param useDefaultConfiguration
	 *            If default configuration should be applied to this layer
	 * @param layerPainter
	 *            The painter for this layer or <code>null</code> to use the painter of the base layer
	 */
	public ColumnHeaderLayer(final ILayer baseLayer, final ILayer horizontalLayerDependency,
			final SelectionLayer selectionLayer, final boolean useDefaultConfiguration, final ILayerPainter layerPainter) {
		super(baseLayer, HORIZONTAL, horizontalLayerDependency,
				selectionLayer, SelectionStyleLabels.COLUMN_FULLY_SELECTED_STYLE,
				layerPainter );
		
		registerCommandHandlers();
		
		if (useDefaultConfiguration) {
			addConfiguration(new DefaultColumnHeaderLayerConfiguration());
		}
	}
	
	
}
