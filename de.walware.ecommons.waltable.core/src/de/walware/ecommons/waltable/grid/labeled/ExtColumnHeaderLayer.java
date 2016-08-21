/*******************************************************************************
 * Copyright (c) 2012-2016 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package de.walware.ecommons.waltable.grid.labeled;

import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.grid.GridRegion;
import de.walware.ecommons.waltable.grid.layer.PlaceholderLayer;
import de.walware.ecommons.waltable.layer.CompositeLayer;
import de.walware.ecommons.waltable.layer.ILayer;


public class ExtColumnHeaderLayer extends CompositeLayer {
	
	
	public ExtColumnHeaderLayer(final ILayer columnHeaderLayer) {
		super(1, 2);
		
		setChildLayer(GridRegion.COLUMN_HEADER, columnHeaderLayer, 0, 0);
		setChildLayer(GridRegion.HEADER_PLACEHOLDER, new PlaceholderLayer(columnHeaderLayer, null,
				false, columnHeaderLayer.getLayerPainter() ), 0, 1);
	}
	
	
	@Override
	protected boolean ignoreRef(final Orientation orientation) {
		return (orientation == VERTICAL);
	}
	
	
	public void setSpaceSize(final int pixel) {
		((PlaceholderLayer) getChildByLabel(GridRegion.HEADER_PLACEHOLDER).layer)
				.setSize(pixel);
	}
	
}
