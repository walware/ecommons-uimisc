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
			public long getPositionIndex(final long refPosition, final long position) {
				return NO_INDEX;
			}
			
			
			@Override
			public long getPositionCount() {
				return 1;
			}
			
			@Override
			public long getPreferredPositionCount() {
				return 1;
			}
			
			@Override
			public long localToUnderlyingPosition(final long refPosition, final long position) {
				return position;
			}
			
			@Override
			public long underlyingToLocalPosition(final long refPosition,
					final long underlyingPosition) {
				return underlyingPosition;
			}
			
			@Override
			public long underlyingToLocalPosition(final ILayer sourceUnderlyingLayer,
					final long underlyingPosition) {
				return underlyingPosition;
			}
			
			@Override
			public Collection<Range> underlyingToLocalPositions(final ILayer sourceUnderlyingLayer,
					final Collection<Range> underlyingPositionRanges) {
				return null;
			}
			
			@Override
			public Collection<ILayer> getUnderlyingLayersByPosition(final long position) {
				return null;
			}
			
			
			@Override
			public long getSize() {
				return DummyLayer.this.size;
			}
			
			@Override
			public long getPreferredSize() {
				return DummyLayer.this.size;
			}
			
			@Override
			public long getPositionByPixel(final long pixel) {
				if (pixel < 0 || pixel >= DummyLayer.this.size) {
					throw new IndexOutOfBoundsException("pixel: " + pixel); //$NON-NLS-1$
				}
				return 0;
			}
			
			@Override
			public long getPositionStart(final long refPosition, final long position) {
				if (refPosition != 0) {
					throw new IndexOutOfBoundsException("refPosition: " + refPosition); //$NON-NLS-1$
				}
				if (position != 0) {
					throw new IndexOutOfBoundsException("position: " + position); //$NON-NLS-1$
				}
				return 0;
			}
			
			@Override
			public int getPositionSize(final long refPosition, final long position) {
				if (refPosition != 0) {
					throw new IndexOutOfBoundsException("refPosition: " + refPosition); //$NON-NLS-1$
				}
				if (position != 0) {
					throw new IndexOutOfBoundsException("position: " + position); //$NON-NLS-1$
				}
				return DummyLayer.this.size;
			}
			
			@Override
			public boolean isPositionResizable(final long position) {
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
		public Object getDataValueByPosition(final long columnPosition, final long rowPosition) {
			return null;
		}
		
		@Override
		public ILayer getUnderlyingLayerByPosition(final long columnPosition, final long rowPosition) {
			return null;
		}
		
		@Override
		public long getColumnPositionByIndex(final long columnIndex) {
			return columnIndex;
		}
		
		@Override
		public long getRowPositionByIndex(final long rowIndex) {
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
	public ICellPainter getCellPainter(final long columnPosition, final long rowPosition,
			final ILayerCell cell, final IConfigRegistry configRegistry) {
		return CELL_PAINTER;
	}
	
	@Override
	public ILayerCell getCellByPosition(final long columnPosition, final long rowPosition) {
		return new LayerCell(this,
				new LayerCellDim(HORIZONTAL, NO_INDEX,
						columnPosition, 0, getHorizontalLayerDependency().getColumnCount() ),
				new LayerCellDim(VERTICAL, NO_INDEX,
						rowPosition, 0, getVerticalLayerDependency().getRowCount()) );
	}
	
}
