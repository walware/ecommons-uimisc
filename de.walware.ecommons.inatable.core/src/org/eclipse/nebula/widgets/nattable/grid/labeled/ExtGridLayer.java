/*******************************************************************************
 * Copyright (c) 2012 Stephan Wahlbrink and others.
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


	public ExtGridLayer(ILayer bodyLayer, ILayer columnHeaderLayer, ILayer rowHeaderLayer, ILayer cornerLayer, boolean useDefaultConfiguration) {
		super(bodyLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer, useDefaultConfiguration);
	}


	@Override
	public void setColumnHeaderLayer(ILayer columnHeaderLayer) {
		setChildLayer("EXT_" + GridRegion.COLUMN_HEADER, columnHeaderLayer, 1, 0);
	}

	public void setRowHeaderLayer(ILayer rowHeaderLayer) {
		setChildLayer("EXT_" + GridRegion.ROW_HEADER, rowHeaderLayer, 0, 1);
	}

}
