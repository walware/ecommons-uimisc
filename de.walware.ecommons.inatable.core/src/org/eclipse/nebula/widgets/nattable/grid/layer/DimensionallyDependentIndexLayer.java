/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation (DimensionallyDependentLayer)
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.grid.layer;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;

import java.util.Collections;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerDim;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.TransformLayer;
import org.eclipse.nebula.widgets.nattable.layer.TransformLayerDim;


/**
 * <p>A DimensionallyDependentIndexLayer is a layer whose horizontal and vertical dimensions are dependent on the 
 * horizontal and vertical dimensions of other layers. A DimensionallyDependentIndexLayer takes three constructor 
 * parameters: the horizontal layer that the DimensionallyDependentIndexLayer's horizontal dimension is linked to, the 
 * vertical layer that the DimensionallyDependentIndexLayer is linked to, and a base layer to which all 
 * non-dimensionally related ILayer method calls will be delegated to (e.g. command, event methods)
 * </p>
 * <p>Prime examples of dimensionally dependent layers are the column header and row header layers. For example, the
 * column header layer's horizontal dimension is linked to the body layer's horizontal dimension. This means that
 * whatever columns are shown in the body area will also be shown in the column header area, and vice versa. Note that
 * the column header layer maintains its own vertical dimension, however, so it's vertical layer dependency would be a
 * separate data layer. The same is true for the row header layer, only with the vertical instead of the horizontal
 * dimension. The constructors for the column header and row header layers would therefore look something like this:
 * </p>
 * <pre>
 * ILayer columnHeaderLayer = new DimensionallyDependentIndexLayer(columnHeaderRowDataLayer, bodyLayer, columnHeaderRowDataLayer);
 * ILayer rowHeaderLayer = new DimensionallyDependentIndexLayer(rowHeaderColumnDataLayer, bodyLayer, rowHeaderColumnDataLayer);
 * </pre>
 */
public class DimensionallyDependentIndexLayer extends TransformLayer {
	
	
	protected static class Dim extends TransformLayerDim<DimensionallyDependentIndexLayer> {
		
		
		public Dim(final DimensionallyDependentIndexLayer layer, final ILayerDim underlyingDim) {
			super(layer, underlyingDim);
		}
		
		
		protected ILayerDim getBaseDim() {
			return this.layer.getBaseLayer().getDim(this.orientation);
		}
		
		
		@Override
		public long getPositionIndex(final long refPosition, final long position) {
			return this.underlyingDim.getPositionIndex(refPosition, position);
		}
		
		
		@Override
		public long localToUnderlyingPosition(final long refPosition, final long position) {
			if (this.underlyingDim == getBaseDim()) {
				return position;
			}
			
			return LayerUtil.convertPosition(this, refPosition, position, this.layer.getBaseLayer());
		}
		
		@Override
		public long underlyingToLocalPosition(final long refPosition,
				final long underlyingPosition) {
			if (this.underlyingDim == getBaseDim()) {
				return underlyingPosition;
			}
			
			if (refPosition < 0 || refPosition >= getPositionCount()) {
				throw new IndexOutOfBoundsException("refPosition: " + refPosition); //$NON-NLS-1$
			}
			
			final long index = getBaseDim().getPositionIndex(underlyingPosition,
					underlyingPosition );
			if (this.underlyingDim.getLayer() instanceof IUniqueIndexLayer) {
				return getPositionByIndex((IUniqueIndexLayer) this.underlyingDim.getLayer(), index);
			}
//				final long count = getPositionCount();
//				for (long position = 0; position < count; position++) {
//					if (getPositionIndex(refPosition, position) == index) {
//						return position;
//					}
//				}
			return searchIndex(this.underlyingDim, refPosition, index);
		}
		
		@Override
		public long underlyingToLocalPosition(final ILayerDim sourceUnderlyingDim,
				final long underlyingPosition) {
			if (sourceUnderlyingDim != getBaseDim()) {
				throw new IllegalArgumentException("underlyingLayer"); //$NON-NLS-1$
			}
			
			final long index = getBaseDim().getPositionIndex(underlyingPosition,
					underlyingPosition );
			
			final long count = getPositionCount();
			for (long position = 0; position < count; position++) {
				if (getPositionIndex(position, position) == index) {
					return position;
				}
			}
			return Long.MIN_VALUE;
		}
		
		private long searchIndex(final ILayerDim dim, final long refPosition, final long index) {
			final List<ILayerDim> uDims = dim.getUnderlyingDimsByPosition(refPosition);
			if (uDims != null) {
				final long uRefPosition = dim.localToUnderlyingPosition(refPosition, refPosition);
				for (final ILayerDim uDim : uDims) {
					final long underlyingPosition;
					if (uDim.getLayer() instanceof IUniqueIndexLayer) {
						underlyingPosition = getPositionByIndex((IUniqueIndexLayer) uDim.getLayer(), index);
					}
					else {
						underlyingPosition = searchIndex(uDim, uRefPosition, index);
					}
					if (underlyingPosition != Long.MIN_VALUE) {
						final long position = dim.underlyingToLocalPosition(refPosition, underlyingPosition);
						if (position != Long.MIN_VALUE) {
							return position;
						}
					}
				}
			}
			return Long.MIN_VALUE;
		}
		
		private long getPositionByIndex(final IUniqueIndexLayer indexLayer, final long index) {
			return (this.orientation == HORIZONTAL) ?
					indexLayer.getColumnPositionByIndex(index) :
					indexLayer.getRowPositionByIndex(index);
		}
		
		@Override
		public List<ILayerDim> getUnderlyingDimsByPosition(final long position) {
			return Collections.<ILayerDim>singletonList(getBaseDim());
		}
		
		
		@Override
		public long getPositionByPixel(final long pixel) {
			return this.underlyingDim.getPositionByPixel(pixel);
		}
		
		@Override
		public long getPositionStart(final long refPosition, final long position) {
			return this.underlyingDim.getPositionStart(refPosition, position);
		}
		
		@Override
		public int getPositionSize(final long refPosition, final long position) {
			return this.underlyingDim.getPositionSize(refPosition, position);
		}
		
		
		@Override
		public boolean isPositionResizable(final long position) {
			return this.underlyingDim.isPositionResizable(position);
		}
		
	}
	
	
	private ILayer horizontalLayerDependency;
	private ILayer verticalLayerDependency;
	
	
	/**
	 * Creates a new DimensionallyDependentIndexLayer.
	 * 
	 * @param baseLayer the underlying base layer
	 * @param horizontalLayerDependency the layer, the horizontal dimension is linked to
	 * @param verticalLayerDependency the layer, the vertical dimension is linked to
	 */
	public DimensionallyDependentIndexLayer(final IUniqueIndexLayer baseLayer,
			final ILayer horizontalLayerDependency, final ILayer verticalLayerDependency) {
		super(baseLayer);
		
		setHorizontalLayerDependency(horizontalLayerDependency);
		setVerticalLayerDependency(verticalLayerDependency);
	}

	/**
	 * Creates a new DimensionallyDependentIndexLayer. The horizontal and vertical layer dependency must be set
	 * by calling {@link #init(IUniqueIndexLayer, IUniqueIndexLayer)} before the layer is used.
	 * 
	 * @param baseLayer the underlying base layer
	 */
	protected DimensionallyDependentIndexLayer(final ILayer baseLayer) {
		super(baseLayer);
	}
	
	
	@Override
	protected void updateDims() {
		if (this.horizontalLayerDependency == null || this.verticalLayerDependency == null) {
			return;
		}
		
		for (final Orientation orientation : Orientation.values()) {
			final ILayerDim dependency = getLayerDependency(orientation).getDim(orientation);
			setDim(new Dim(this, dependency));
		}
	}
	
	
	// Dependent layer accessors
	
	protected void setHorizontalLayerDependency(final ILayer horizontalLayerDependency) {
		this.horizontalLayerDependency = horizontalLayerDependency;
		
//		horizontalLayerDependency.addLayerListener(new ILayerListener() {
//
//			public void handleLayerEvent(ILayerEvent event) {
//				if (event instanceof IStructuralChangeEvent) {
//					// TODO refresh horizontal structure
//				}
//			}
//
//		});
		
		updateDims();
	}

	protected void setVerticalLayerDependency(final ILayer verticalLayerDependency) {
		this.verticalLayerDependency = verticalLayerDependency;

//		verticalLayerDependency.addLayerListener(new ILayerListener() {
//
//			public void handleLayerEvent(ILayerEvent event) {
//				if (event instanceof IStructuralChangeEvent) {
//					// TODO refresh vertical structure
//				}
//			}
//
//		});
		
		updateDims();
	}
	
	public ILayer getHorizontalLayerDependency() {
		return this.horizontalLayerDependency;
	}
	
	public ILayer getVerticalLayerDependency() {
		return this.verticalLayerDependency;
	}
	
	public ILayer getLayerDependency(final Orientation orientation) {
		return (orientation == HORIZONTAL) ? this.horizontalLayerDependency : this.verticalLayerDependency;
	}
	
	public IUniqueIndexLayer getBaseLayer() {
		return (IUniqueIndexLayer) getUnderlyingLayer();
	}
	
	// Commands
	
	@Override
	public boolean doCommand(final ILayerCommand command) {
		// Invoke command handler(s) on the Dimensionally dependent layer
		if (super.doCommand(command.cloneCommand())) {
			return true;
		}
		
		if (this.horizontalLayerDependency.doCommand(command.cloneCommand())) {
			return true;
		}
		
		if (this.verticalLayerDependency.doCommand(command.cloneCommand())) {
			return true;
		}
		
		return false;
	}
	
}
