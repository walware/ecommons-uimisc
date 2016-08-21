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
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.ecommons.waltable.data.IDataProvider;
import de.walware.ecommons.waltable.grid.GridRegion;
import de.walware.ecommons.waltable.grid.layer.CornerLayer;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.layer.cell.LayerCell;
import de.walware.ecommons.waltable.layer.cell.LayerCellDim;
import de.walware.ecommons.waltable.painter.layer.ILayerPainter;


public class LabelCornerLayer extends CornerLayer {
	
	
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
	public LabelCornerLayer(final ILayer baseLayer,
			final ILayer horizontalLayerDependency, final ILayer verticalLayerDependency,
			final IDataProvider columnHeaderLabelProvider, final IDataProvider rowHeaderLabelProvider,
			final boolean useDefaultConfiguration, final ILayerPainter layerPainter) {
		super(baseLayer, horizontalLayerDependency, verticalLayerDependency,
				useDefaultConfiguration, layerPainter );
		
		this.columnHeaderLabelProvider= columnHeaderLabelProvider;
		this.rowHeaderLabelProvider= rowHeaderLabelProvider;
	}
	
	
	@Override
	public ILayerCell getCellByPosition(final long columnPosition, final long rowPosition) {
		final ILayerDim hDim= getDim(HORIZONTAL);
		final ILayerDim vDim= getDim(VERTICAL);
		final long columnId= hDim.getPositionId(columnPosition, columnPosition);
		final long rowId= vDim.getPositionId(rowPosition, rowPosition);
		
		final long columnCount= getColumnCount();
		final long rowCount= getRowCount();
		if (rowPosition < rowCount - 1) {
			return new LayerCell(this,
					new LayerCellDim(HORIZONTAL, columnId, columnPosition, 0, columnCount),
					new LayerCellDim(VERTICAL, rowId, rowPosition) ) {
				@Override
				public LabelStack getConfigLabels() {
					return new LabelStack(GridRegion.COLUMN_HEADER_LABEL);
				}
				@Override
				public Object getDataValue(final int flags, final IProgressMonitor monitor) {
					return (LabelCornerLayer.this.columnHeaderLabelProvider != null) ? 
							LabelCornerLayer.this.columnHeaderLabelProvider.getDataValue(
									0, getRowPosition(), flags, monitor ) :
							""; //$NON-NLS-1$
				}
			};
		}
		else if (columnPosition < columnCount - 1) {
			return new LayerCell(this,
					new LayerCellDim(HORIZONTAL, columnId, columnPosition),
					new LayerCellDim(VERTICAL, rowId, rowPosition) ) {
				@Override
				public LabelStack getConfigLabels() {
					return new LabelStack(GridRegion.ROW_HEADER_LABEL);
				}
				@Override
				public Object getDataValue(final int flags, final IProgressMonitor monitor) {
					return (LabelCornerLayer.this.rowHeaderLabelProvider != null) ? 
							LabelCornerLayer.this.rowHeaderLabelProvider.getDataValue(
									getColumnPosition(), 0, flags, monitor) :
							""; //$NON-NLS-1$
				}
			};
		}
		else {
			return new LayerCell(this,
					new LayerCellDim(HORIZONTAL, columnId, columnPosition),
					new LayerCellDim(VERTICAL, rowId, rowPosition) ) {
				@Override
				public LabelStack getConfigLabels() {
					return new LabelStack(GridRegion.HEADER_PLACEHOLDER);
				}
			};
		}
	}
	
}
