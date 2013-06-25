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

import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;


public class ExtGridLayer extends GridLayer {
	
	
	public static final String EXT_COLUMN_HEADER = "EXT_" + GridRegion.COLUMN_HEADER; //$NON-NLS-1$
	public static final String EXT_ROW_HEADER = "EXT_" + GridRegion.ROW_HEADER; //$NON-NLS-1$
	
	
	public ExtGridLayer(final ILayer bodyLayer, final ILayer columnHeaderLayer, final ILayer rowHeaderLayer, final ILayer cornerLayer, final boolean useDefaultConfiguration) {
		super(bodyLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer, useDefaultConfiguration);
	}
	
	
	@Override
	public void setColumnHeaderLayer(final ILayer columnHeaderLayer) {
		setChildLayer(EXT_COLUMN_HEADER, columnHeaderLayer, 1, 0);
	}
	
	@Override
	public void setRowHeaderLayer(final ILayer rowHeaderLayer) {
		setChildLayer(EXT_ROW_HEADER, rowHeaderLayer, 0, 1);
	}
	
}
