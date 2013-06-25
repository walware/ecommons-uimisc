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

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;

import org.eclipse.nebula.widgets.nattable.config.LayoutSizeConfig;
import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.PlaceholderLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;


public class ExtColumnHeaderLayer extends CompositeLayer {
	
	
	private final ILayer headerLayer;
	
	
	public ExtColumnHeaderLayer(final ILayer columnHeaderLayer, final LayoutSizeConfig sizeConfig) {
		super(1, 2);
		
		this.headerLayer = columnHeaderLayer;
		
		setChildLayer(GridRegion.COLUMN_HEADER, columnHeaderLayer, 0, 0);
		final PlaceholderLayer placeholderLayer = new PlaceholderLayer(columnHeaderLayer, null,
				false, columnHeaderLayer.getLayerPainter() );
		placeholderLayer.setSize(sizeConfig.getRowHeight());
		setChildLayer("PLACEHOLDER", placeholderLayer, 0, 1);
	}
	
	
	@Override
	protected boolean ignoreRef(final Orientation orientation) {
		return (orientation == VERTICAL);
	}
	
}
