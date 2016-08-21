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

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;

import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.grid.GridRegion;
import de.walware.ecommons.waltable.grid.layer.PlaceholderLayer;
import de.walware.ecommons.waltable.layer.CompositeLayer;
import de.walware.ecommons.waltable.layer.ILayer;


public class ExtRowHeaderLayer extends CompositeLayer {
	
	
	public ExtRowHeaderLayer(final ILayer rowHeaderLayer) {
		super(2, 1);
		
		setChildLayer(GridRegion.ROW_HEADER, rowHeaderLayer, 0, 0);
		setChildLayer(GridRegion.HEADER_PLACEHOLDER, new PlaceholderLayer(null, rowHeaderLayer,
				false, rowHeaderLayer.getLayerPainter() ), 1, 0);
	}
	
	
	@Override
	protected boolean ignoreRef(final Orientation orientation) {
		return (orientation == HORIZONTAL);
	}
	
	
	public void setSpaceSize(final int pixel) {
		((PlaceholderLayer) getChildByLabel(GridRegion.HEADER_PLACEHOLDER).layer)
				.setSize(pixel);
	}
	
}
