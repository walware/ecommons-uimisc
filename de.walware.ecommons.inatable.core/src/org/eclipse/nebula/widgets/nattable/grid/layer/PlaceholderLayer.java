/*******************************************************************************
 * Copyright (c) 2012-2013 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.grid.layer;

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.BackgroundPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.layer.CellLayerPainter;
import org.eclipse.nebula.widgets.nattable.painter.layer.ILayerPainter;


public class PlaceholderLayer extends DimensionallyDependentLayer {


	private static class DummyLayer extends AbstractLayer implements IUniqueIndexLayer {


		private int size = 10;


		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public int getPreferredColumnCount() {
			return 1;
		}

		@Override
		public int getColumnIndexByPosition(int columnPosition) {
			if (columnPosition == 0) {
				return 0;
			}
			return -1;
		}

		@Override
		public int localToUnderlyingColumnPosition(int localColumnPosition) {
			return localColumnPosition;
		}

		@Override
		public int underlyingToLocalColumnPosition(ILayer sourceUnderlyingLayer, int underlyingColumnPosition) {
			return underlyingColumnPosition;
		}

		@Override
		public Collection<Range> underlyingToLocalColumnPositions(ILayer sourceUnderlyingLayer, Collection<Range> underlyingColumnPositionRanges) {
			return null;
		}

		@Override
		public int getWidth() {
			return size;
		}

		@Override
		public int getPreferredWidth() {
			return size;
		}

		@Override
		public int getColumnWidthByPosition(int columnPosition) {
			return size;
		}

		@Override
		public boolean isColumnPositionResizable(int columnPosition) {
			return false;
		}

		@Override
		public int getColumnPositionByX(int x) {
			if (x >= 0 && x < size) {
				return 0;
			}
			return -1;
		}

		@Override
		public int getStartXOfColumnPosition(int columnPosition) {
			if (columnPosition < 0) {
				return -1;
			}
			return 0;
		}

		@Override
		public Collection<ILayer> getUnderlyingLayersByColumnPosition(int columnPosition) {
			return null;
		}


		@Override
		public int getRowCount() {
			return 1;
		}

		@Override
		public int getPreferredRowCount() {
			return 1;
		}

		@Override
		public int getRowIndexByPosition(int rowPosition) {
			if (rowPosition == 0) {
				return 0;
			}
			return -1;
		}

		@Override
		public int localToUnderlyingRowPosition(int localRowPosition) {
			return localRowPosition;
		}

		@Override
		public int underlyingToLocalRowPosition(ILayer sourceUnderlyingLayer, int underlyingRowPosition) {
			return underlyingRowPosition;
		}

		@Override
		public Collection<Range> underlyingToLocalRowPositions(ILayer sourceUnderlyingLayer, Collection<Range> underlyingRowPositionRanges) {
			return null;
		}

		@Override
		public int getHeight() {
			return size;
		}

		@Override
		public int getPreferredHeight() {
			return size;
		}

		@Override
		public int getRowHeightByPosition(int rowPosition) {
			return size;
		}

		@Override
		public boolean isRowPositionResizable(int rowPosition) {
			return false;
		}

		@Override
		public int getRowPositionByY(int y) {
			if (y >= 0 && y < size) {
				return 0;
			}
			return -1;
		}

		@Override
		public int getStartYOfRowPosition(int rowPosition) {
			return 0;
		}

		@Override
		public Collection<ILayer> getUnderlyingLayersByRowPosition(int rowPosition) {
			return null;
		}

		@Override
		public Object getDataValueByPosition(int columnPosition, int rowPosition) {
			return null;
		}

		@Override
		public ILayer getUnderlyingLayerByPosition(int columnPosition, int rowPosition) {
			return null;
		}

		@Override
		public int getColumnPositionByIndex(int columnIndex) {
			return columnIndex;
		}

		@Override
		public int getRowPositionByIndex(int rowIndex) {
			return rowIndex;
		}

	}

	private static final ICellPainter CELL_PAINTER = new BackgroundPainter();


	/**
	 * Creates a corner header layer using the default configuration and painter
	 * 
	 * @param horizontalLayerDependency
	 *            The layer to link the horizontal dimension to, typically the row header layer
	 * @param verticalLayerDependency
	 *            The layer to link the vertical dimension to, typically the column header layer
	 */
	public PlaceholderLayer(IUniqueIndexLayer horizontalLayerDependency, IUniqueIndexLayer verticalLayerDependency) {
		this(horizontalLayerDependency, verticalLayerDependency, true, new CellLayerPainter());
	}

	/**
	 * @param horizontalLayerDependency
	 *            The layer to link the horizontal dimension to, typically the row header layer
	 * @param verticalLayerDependency
	 *            The layer to link the vertical dimension to, typically the column header layer
	 * @param useDefaultConfiguration
	 *            If default configuration should be applied to this layer (at moment none)
	 * @param layerPainter
	 *            The painter for this layer or <code>null</code> to use the painter of the base layer
	 */
	public PlaceholderLayer(IUniqueIndexLayer horizontalLayerDependency, IUniqueIndexLayer verticalLayerDependency,
			boolean useDefaultConfiguration, ILayerPainter layerPainter) {
		super(new DummyLayer());

		init((horizontalLayerDependency != null) ? horizontalLayerDependency : getBaseLayer(),
				(verticalLayerDependency != null) ? verticalLayerDependency : getBaseLayer() );
		
		this.layerPainter = layerPainter;
	}

	public void setSize(int size) {
		((DummyLayer) getBaseLayer()).size = size;
	}

	@Override
	public ICellPainter getCellPainter(int columnPosition, int rowPosition,
			ILayerCell cell, IConfigRegistry configRegistry) {
		return CELL_PAINTER;
	}

	@Override
	public ILayerCell getCellByPosition(int columnPosition, int rowPosition) {
		return new LayerCell(this, 0, 0, columnPosition, rowPosition,
				getHorizontalLayerDependency().getColumnCount(),
				getVerticalLayerDependency().getRowCount());
	}

}
