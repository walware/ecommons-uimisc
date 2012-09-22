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

import org.eclipse.nebula.widgets.nattable.config.LayoutSizeConfig;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.PlaceholderLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;


public class ExtColumnHeaderLayer extends CompositeLayer implements IUniqueIndexLayer {


	private final IUniqueIndexLayer headerLayer;


	public ExtColumnHeaderLayer(IUniqueIndexLayer columnHeaderLayer, LayoutSizeConfig sizeConfig) {
		super(1, 2);
		
		headerLayer = columnHeaderLayer;
		
		setChildLayer(GridRegion.COLUMN_HEADER, columnHeaderLayer, 0, 0);
		PlaceholderLayer placeholderLayer = new PlaceholderLayer(columnHeaderLayer, null,
				false, columnHeaderLayer.getLayerPainter() );
		placeholderLayer.setSize(sizeConfig.getRowHeight());
		setChildLayer("PLACEHOLDER", placeholderLayer, 0, 1);
	}

	@Override
	public int getColumnIndexByPosition(int compositeColumnPosition) {
		if (compositeColumnPosition < headerLayer.getColumnCount()) {
			return headerLayer.getRowIndexByPosition(compositeColumnPosition);
		}
		return -1;
	}

	@Override
	public int getColumnPositionByIndex(int columnIndex) {
		return headerLayer.getColumnPositionByIndex(columnIndex);
	}

	@Override
	public int getRowIndexByPosition(int compositeRowPosition) {
		if (compositeRowPosition < headerLayer.getRowCount()) {
			return headerLayer.getRowIndexByPosition(compositeRowPosition);
		}
		return -1;
	}

	@Override
	public int getRowPositionByIndex(int rowIndex) {
		return headerLayer.getRowPositionByIndex(rowIndex);
	}

}
