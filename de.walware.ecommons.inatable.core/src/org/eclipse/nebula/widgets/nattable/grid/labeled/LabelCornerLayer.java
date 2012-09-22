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

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCell;
import org.eclipse.nebula.widgets.nattable.painter.layer.ILayerPainter;


public class LabelCornerLayer extends CornerLayer {


	public static final String COLUMN_HEADER_LABEL = GridRegion.COLUMN_HEADER + "_LABEL"; //$NON-NLS-1$
	public static final String ROW_HEADER_LABEL = GridRegion.ROW_HEADER + "_LABEL"; //$NON-NLS-1$


	private IDataProvider columnHeaderLabelProvider;
	private IDataProvider rowHeaderLabelProvider;


	/**
	 * @param baseLayer
	 *            The data provider for this layer
	 * @param horizontalLayerDependency
	 *            The layer to link the horizontal dimension to, typically the row header layer
	 * @param verticalLayerDependency
	 *            The layer to link the vertical dimension to, typically the column header layer
	 * @param useDefaultConfiguration
	 *            If default configuration should be applied to this layer (at moment none)
	 * @param layerPainter
	 *            The painter for this layer or <code>null</code> to use the painter of the base layer
	 */
	public LabelCornerLayer(IUniqueIndexLayer baseLayer, IUniqueIndexLayer horizontalLayerDependency, IUniqueIndexLayer verticalLayerDependency,
			IDataProvider columnHeaderLabelProvider, IDataProvider rowHeaderLabelProvider,
			boolean useDefaultConfiguration, ILayerPainter layerPainter) {
		super(baseLayer, horizontalLayerDependency, verticalLayerDependency,
				useDefaultConfiguration, layerPainter );
		
		this.columnHeaderLabelProvider = columnHeaderLabelProvider;
		this.rowHeaderLabelProvider = rowHeaderLabelProvider;
	}


	@Override
	public ILayerCell getCellByPosition(int columnPosition, int rowPosition) {
		int columnCount = getColumnCount();
		int rowCount = getRowCount();
		if (rowPosition < rowCount - 1) {
			return new LayerCell(this, 0, rowPosition, columnPosition, rowPosition, columnCount, 1);
		}
		else {
			return new LayerCell(this, columnPosition, rowPosition);
		}
	}

	@Override
	public LabelStack getConfigLabelsByPosition(int columnPosition, int rowPosition) {
		LabelStack labelStack = super.getConfigLabelsByPosition(columnPosition, rowPosition);
		if (rowPosition < getRowCount() - 1) {
			labelStack.addLabelOnTop(COLUMN_HEADER_LABEL);
		}
		else if (columnPosition < getColumnCount() - 1) {
			labelStack.addLabelOnTop(ROW_HEADER_LABEL);
		}
		else {
			labelStack.addLabelOnTop("PLACEHOLDER");
		}
		return labelStack;
	}

	@Override
	public Object getDataValueByPosition(int columnPosition, int rowPosition) {
		if (rowPosition < getRowCount() - 1) {
			return (columnHeaderLabelProvider != null) ? 
					columnHeaderLabelProvider.getDataValue(0, rowPosition) : ""; //$NON-NLS-1$
		}
		if (columnPosition < getColumnCount() - 1) {
			return (rowHeaderLabelProvider != null) ? 
					rowHeaderLabelProvider.getDataValue(columnPosition, 0) : ""; //$NON-NLS-1$
		}
		return null;
	}

}
