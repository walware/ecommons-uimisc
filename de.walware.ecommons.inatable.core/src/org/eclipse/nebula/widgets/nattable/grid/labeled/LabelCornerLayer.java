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
import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;
import static org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell.NO_INDEX;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCellDim;
import org.eclipse.nebula.widgets.nattable.painter.layer.ILayerPainter;


public class LabelCornerLayer extends CornerLayer {


	public static final String COLUMN_HEADER_LABEL = GridRegion.COLUMN_HEADER + "_LABEL"; //$NON-NLS-1$
	public static final String ROW_HEADER_LABEL = GridRegion.ROW_HEADER + "_LABEL"; //$NON-NLS-1$


	private final IDataProvider columnHeaderLabelProvider;
	private final IDataProvider rowHeaderLabelProvider;


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
	public LabelCornerLayer(final IUniqueIndexLayer baseLayer,
			final ILayer horizontalLayerDependency, final ILayer verticalLayerDependency,
			final IDataProvider columnHeaderLabelProvider, final IDataProvider rowHeaderLabelProvider,
			final boolean useDefaultConfiguration, final ILayerPainter layerPainter) {
		super(baseLayer, horizontalLayerDependency, verticalLayerDependency,
				useDefaultConfiguration, layerPainter );
		
		this.columnHeaderLabelProvider = columnHeaderLabelProvider;
		this.rowHeaderLabelProvider = rowHeaderLabelProvider;
	}


	@Override
	public ILayerCell getCellByPosition(final long columnPosition, final long rowPosition) {
		final long columnCount = getColumnCount();
		final long rowCount = getRowCount();
		if (rowPosition < rowCount - 1) {
			return new LayerCell(this,
					new LayerCellDim(HORIZONTAL, NO_INDEX, columnPosition, 0, columnCount),
					new LayerCellDim(VERTICAL, NO_INDEX, rowPosition) );
		}
		else {
			return new LayerCell(this,
					new LayerCellDim(HORIZONTAL, NO_INDEX, columnPosition),
					new LayerCellDim(VERTICAL, NO_INDEX, rowPosition) );
		}
	}

	@Override
	public LabelStack getConfigLabelsByPosition(final long columnPosition, final long rowPosition) {
		final LabelStack labelStack = super.getConfigLabelsByPosition(columnPosition, rowPosition);
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
	public Object getDataValueByPosition(final long columnPosition, final long rowPosition) {
		if (rowPosition < getRowCount() - 1) {
			return (this.columnHeaderLabelProvider != null) ? 
					this.columnHeaderLabelProvider.getDataValue(0, rowPosition) : ""; //$NON-NLS-1$
		}
		if (columnPosition < getColumnCount() - 1) {
			return (this.rowHeaderLabelProvider != null) ? 
					this.rowHeaderLabelProvider.getDataValue(columnPosition, 0) : ""; //$NON-NLS-1$
		}
		return null;
	}

}
