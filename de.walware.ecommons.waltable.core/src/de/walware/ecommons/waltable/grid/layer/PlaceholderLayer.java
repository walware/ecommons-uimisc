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

package de.walware.ecommons.waltable.grid.layer;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.coordinate.PositionId;
import de.walware.ecommons.waltable.coordinate.PositionOutOfBoundsException;
import de.walware.ecommons.waltable.layer.AbstractLayer;
import de.walware.ecommons.waltable.layer.DataDim;
import de.walware.ecommons.waltable.layer.DataLayer;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.layer.cell.LayerCell;
import de.walware.ecommons.waltable.layer.cell.LayerCellDim;
import de.walware.ecommons.waltable.painter.layer.CellLayerPainter;
import de.walware.ecommons.waltable.painter.layer.ILayerPainter;


public class PlaceholderLayer extends DimensionallyDependentLayer {
	
	
	private static class DummyLayer extends AbstractLayer implements ILayer {
		
		private static class Dim extends DataDim<DummyLayer> {
			
			
			public Dim(final DummyLayer layer, final Orientation orientation, final long catId) {
				super(layer, orientation, catId);
			}
			
			
			@Override
			public long getPositionCount() {
				return 1;
			}
			
			
			@Override
			public long getSize() {
				return this.layer.size;
			}
			
			@Override
			public long getPositionByPixel(final long pixel) {
				return 0;
			}
			
			@Override
			public long getPositionStart(final long position) {
				if (position != 0) {
					throw PositionOutOfBoundsException.position(position, getOrientation());
				}
				return 0;
			}
			
			@Override
			public int getPositionSize(final long position) {
				if (position != 0) {
					throw PositionOutOfBoundsException.position(position, getOrientation());
				}
				return this.layer.size;
			}
			
			@Override
			public boolean isPositionResizable(final long position) {
				return false;
			}
			
		}
		
		
		private static long idCounter;
		
		
		private int size= DataLayer.DEFAULT_ROW_HEIGHT;
		
		
		public DummyLayer() {
		}
		
		
		@Override
		protected void initDims() {
			final long id= PositionId.PLACEHOLDER_CAT + idCounter++;
			for (final Orientation orientation : Orientation.values()) {
				setDim(new Dim(this, orientation, id));
			}
		}
		
		
		@Override
		public ILayer getUnderlyingLayerByPosition(final long columnPosition, final long rowPosition) {
			return null;
		}
		
		@Override
		public ILayerCell getCellByPosition(final long columnPosition, final long rowPosition) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	
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
		
		this.layerPainter= layerPainter;
	}
	
	
	public void setSize(final int size) {
		((DummyLayer) getBaseLayer()).size= size;
	}
	
	@Override
	public ILayerCell getCellByPosition(final long columnPosition, final long rowPosition) {
		final ILayerDim hDim= getDim(HORIZONTAL);
		final ILayerDim vDim= getDim(VERTICAL);
		final long columnId= hDim.getPositionId(columnPosition, columnPosition);
		final long rowId= vDim.getPositionId(rowPosition, rowPosition);
		
		return new LayerCell(this,
				new LayerCellDim(HORIZONTAL, columnId,
						columnPosition, 0, hDim.getPositionCount() ),
				new LayerCellDim(VERTICAL, rowId,
						rowPosition, 0, vDim.getPositionCount() ));
	}
	
}
