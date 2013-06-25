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

package org.eclipse.nebula.widgets.nattable.grid.layer;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;
import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;
import static org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell.NO_INDEX;

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.DimBasedLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerDim;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCellDim;
import org.eclipse.nebula.widgets.nattable.painter.cell.BackgroundPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.layer.CellLayerPainter;
import org.eclipse.nebula.widgets.nattable.painter.layer.ILayerPainter;


public class PlaceholderLayer extends DimensionallyDependentIndexLayer {
	
	
	private static class DummyLayer extends DimBasedLayer implements IUniqueIndexLayer {
		
		private class Dim implements ILayerDim {
			
			
			private final Orientation orientation;
			
			
			public Dim(final Orientation orientation) {
				this.orientation = orientation;
			}
			
			
			@Override
			public ILayer getLayer() {
				return DummyLayer.this;
			}
			
			@Override
			public Orientation getOrientation() {
				return this.orientation;
			}
			
			
			@Override
			public int getPositionIndex(final int refPosition, final int position) {
				return NO_INDEX;
			}
			
			
			@Override
			public int getPositionCount() {
				return 1;
			}
			
			@Override
			public int getPreferredPositionCount() {
				return 1;
			}
			
			@Override
			public int localToUnderlyingPosition(final int refPosition, final int position) {
				return position;
			}
			
			@Override
			public int underlyingToLocalPosition(final int refPosition,
					final int underlyingPosition) {
				return underlyingPosition;
			}
			
			@Override
			public int underlyingToLocalPosition(final ILayer sourceUnderlyingLayer,
					final int underlyingPosition) {
				return underlyingPosition;
			}
			
			@Override
			public Collection<Range> underlyingToLocalPositions(final ILayer sourceUnderlyingLayer,
					final Collection<Range> underlyingPositionRanges) {
				return null;
			}
			
			@Override
			public Collection<ILayer> getUnderlyingLayersByPosition(final int position) {
				return null;
			}
			
			
			@Override
			public int getSize() {
				return DummyLayer.this.size;
			}
			
			@Override
			public int getPreferredSize() {
				return DummyLayer.this.size;
			}
			
			@Override
			public int getPositionByPixel(final int pixel) {
				if (pixel < 0 || pixel >= DummyLayer.this.size) {
					throw new IndexOutOfBoundsException("pixel: " + pixel); //$NON-NLS-1$
				}
				return 0;
			}
			
			@Override
			public int getPositionStart(final int refPosition, final int position) {
				if (refPosition != 0) {
					throw new IndexOutOfBoundsException("refPosition: " + refPosition); //$NON-NLS-1$
				}
				if (position != 0) {
					throw new IndexOutOfBoundsException("position: " + position); //$NON-NLS-1$
				}
				return 0;
			}
			
			@Override
			public int getPositionSize(final int refPosition, final int position) {
				if (refPosition != 0) {
					throw new IndexOutOfBoundsException("refPosition: " + refPosition); //$NON-NLS-1$
				}
				if (position != 0) {
					throw new IndexOutOfBoundsException("position: " + position); //$NON-NLS-1$
				}
				return DummyLayer.this.size;
			}
			
			@Override
			public boolean isPositionResizable(final int position) {
				return false;
			}
			
		}
		
		
		private int size = 10;
		
		
		public DummyLayer() {
		}
		
		
		@Override
		protected void updateDims() {
			for (final Orientation orientation : Orientation.values()) {
				setDim(orientation, new Dim(orientation));
			}
		}
		
		
		@Override
		public Object getDataValueByPosition(final int columnPosition, final int rowPosition) {
			return null;
		}
		
		@Override
		public ILayer getUnderlyingLayerByPosition(final int columnPosition, final int rowPosition) {
			return null;
		}
		
		@Override
		public int getColumnPositionByIndex(final int columnIndex) {
			return columnIndex;
		}
		
		@Override
		public int getRowPositionByIndex(final int rowIndex) {
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
	public PlaceholderLayer(final ILayer horizontalLayerDependency, final ILayer verticalLayerDependency) {
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
	public PlaceholderLayer(final ILayer horizontalLayerDependency, final ILayer verticalLayerDependency,
			final boolean useDefaultConfiguration, final ILayerPainter layerPainter) {
		super(new DummyLayer());
		
		setHorizontalLayerDependency((horizontalLayerDependency != null) ?
				horizontalLayerDependency : getBaseLayer() );
		setVerticalLayerDependency((verticalLayerDependency != null) ?
				verticalLayerDependency : getBaseLayer() );
		
		this.layerPainter = layerPainter;
	}
	
	
	public void setSize(final int size) {
		((DummyLayer) getBaseLayer()).size = size;
	}
	
	@Override
	public ICellPainter getCellPainter(final int columnPosition, final int rowPosition,
			final ILayerCell cell, final IConfigRegistry configRegistry) {
		return CELL_PAINTER;
	}
	
	@Override
	public ILayerCell getCellByPosition(final int columnPosition, final int rowPosition) {
		return new LayerCell(this,
				new LayerCellDim(HORIZONTAL, NO_INDEX,
						columnPosition, 0, getHorizontalLayerDependency().getColumnCount() ),
				new LayerCellDim(VERTICAL, NO_INDEX,
						rowPosition, 0, getVerticalLayerDependency().getRowCount()) );
	}
	
}
