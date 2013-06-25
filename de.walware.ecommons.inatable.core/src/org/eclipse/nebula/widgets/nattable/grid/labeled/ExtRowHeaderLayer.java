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

package org.eclipse.nebula.widgets.nattable.grid.labeled;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;

import org.eclipse.nebula.widgets.nattable.config.LayoutSizeConfig;
import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.PlaceholderLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;


public class ExtRowHeaderLayer extends CompositeLayer {
	
	
	private final ILayer headerLayer;
	
	
	public ExtRowHeaderLayer(final ILayer rowHeaderLayer, final LayoutSizeConfig sizeConfig) {
		super(2, 1);
		
		this.headerLayer = rowHeaderLayer;
		
		setChildLayer(GridRegion.ROW_HEADER, rowHeaderLayer, 0, 0);
		final PlaceholderLayer placeholderLayer = new PlaceholderLayer(null, rowHeaderLayer,
				false, rowHeaderLayer.getLayerPainter() );
		placeholderLayer.setSize(sizeConfig.getRowHeight());
		setChildLayer("PLACEHOLDER", placeholderLayer, 1, 0);
	}
	
	
	@Override
	protected boolean ignoreRef(final Orientation orientation) {
		return (orientation == HORIZONTAL);
	}
	
}
