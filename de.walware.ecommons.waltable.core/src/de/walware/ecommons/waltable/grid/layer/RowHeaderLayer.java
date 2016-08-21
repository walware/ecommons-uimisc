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

package de.walware.ecommons.waltable.grid.layer;

import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.config.DefaultRowHeaderLayerConfiguration;
import de.walware.ecommons.waltable.painter.layer.ILayerPainter;
import de.walware.ecommons.waltable.selection.SelectionLayer;
import de.walware.ecommons.waltable.style.SelectionStyleLabels;


/**
 * Layer for the row headers of the grid layer
 */
public class RowHeaderLayer extends AbstractPositionHeaderLayer {
	
	
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
	public RowHeaderLayer(final ILayer baseLayer, final ILayer verticalLayerDependency, final SelectionLayer selectionLayer) {
		this(baseLayer, verticalLayerDependency, selectionLayer, true);
	}
	
	public RowHeaderLayer(final ILayer baseLayer, final ILayer verticalLayerDependency, final SelectionLayer selectionLayer,
			final boolean useDefaultConfiguration) {
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
	public RowHeaderLayer(final ILayer baseLayer, final ILayer verticalLayerDependency,
			final SelectionLayer selectionLayer, final boolean useDefaultConfiguration, final ILayerPainter layerPainter) {
		super(baseLayer, VERTICAL, verticalLayerDependency,
				selectionLayer, SelectionStyleLabels.ROW_FULLY_SELECTED_STYLE,
				layerPainter );
		
		registerCommandHandlers();
		
		if (useDefaultConfiguration) {
			addConfiguration(new DefaultRowHeaderLayerConfiguration());
		}
	}
	
	
}
