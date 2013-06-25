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

import java.util.Collection;
import java.util.Collections;

import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.layer.AbstractTransformLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerDim;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.TransformDim;


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
public class DimensionallyDependentIndexLayer extends AbstractTransformLayer {
	
	
	protected static class Dim extends TransformDim<DimensionallyDependentIndexLayer> {
		
		
		public Dim(final DimensionallyDependentIndexLayer layer, final ILayerDim underlyingDim) {
			super(layer, underlyingDim);
		}
		
		
		protected ILayerDim getBaseDim() {
			return getLayer().getBaseLayer().getDim(getOrientation());
		}
		
		
		@Override
		public int getPositionIndex(final int refPosition, final int position) {
			return this.underlyingDim.getPositionIndex(refPosition, position);
		}
		
		
		@Override
		public int localToUnderlyingPosition(final int refPosition, final int position) {
			if (this.underlyingDim == getBaseDim()) {
				return position;
			}
			
			return LayerUtil.convertPosition(this, refPosition, position, getLayer().getBaseLayer());
		}
		
		@Override
		public int underlyingToLocalPosition(final int refPosition,
				final int underlyingPosition) {
			if (this.underlyingDim == getBaseDim()) {
				return underlyingPosition;
			}
			
			if (refPosition < 0 || refPosition >= getPositionCount()) {
				throw new IndexOutOfBoundsException("refPosition: " + refPosition); //$NON-NLS-1$
			}
			
			final int index = getBaseDim().getPositionIndex(underlyingPosition,
					underlyingPosition );
			if (this.underlyingDim.getLayer() instanceof IUniqueIndexLayer) {
				return getPositionByIndex((IUniqueIndexLayer) this.underlyingDim.getLayer(), index);
			}
//				final int count = getPositionCount();
//				for (int position = 0; position < count; position++) {
//					if (getPositionIndex(refPosition, position) == index) {
//						return position;
//					}
//				}
			return searchIndex(this.underlyingDim, refPosition, index);
		}
		
		@Override
		public int underlyingToLocalPosition(final ILayer sourceUnderlyingLayer,
				final int underlyingPosition) {
			if (sourceUnderlyingLayer != getBaseDim().getLayer()) {
				throw new IllegalArgumentException("underlyingLayer"); //$NON-NLS-1$
			}
			
			final int index = getBaseDim().getPositionIndex(underlyingPosition,
					underlyingPosition );
			
			final int count = getPositionCount();
			for (int position = 0; position < count; position++) {
				if (getPositionIndex(position, position) == index) {
					return position;
				}
			}
			return Integer.MIN_VALUE;
		}
		
		private int searchIndex(final ILayerDim dim, final int refPosition, final int index) {
			final Collection<ILayer> underlyingLayers = dim.getUnderlyingLayersByPosition(refPosition);
			final int underlyingRefPosition = dim.localToUnderlyingPosition(refPosition, refPosition);
			for (final ILayer underlyingLayer : underlyingLayers) {
				final int underlyingPosition;
				if (underlyingLayer instanceof IUniqueIndexLayer) {
					underlyingPosition = getPositionByIndex((IUniqueIndexLayer) underlyingLayer, index);
				}
				else {
					underlyingPosition = searchIndex(underlyingLayer.getDim(getOrientation()),
							underlyingRefPosition, index );
				}
				if (underlyingPosition != Integer.MIN_VALUE) {
					final int position = dim.underlyingToLocalPosition(refPosition, underlyingPosition);
					if (position != Integer.MIN_VALUE) {
						return position;
					}
				}
			}
			return Integer.MIN_VALUE;
		}
		
		private int getPositionByIndex(final IUniqueIndexLayer indexLayer, final int index) {
			return (getOrientation() == HORIZONTAL) ?
					indexLayer.getColumnPositionByIndex(index) :
					indexLayer.getRowPositionByIndex(index);
		}
		
		@Override
		public Collection<ILayer> getUnderlyingLayersByPosition(final int position) {
			return Collections.<ILayer>singletonList(getLayer().getBaseLayer());
		}
		
		
		@Override
		public int getPositionByPixel(final int pixel) {
			return this.underlyingDim.getPositionByPixel(pixel);
		}
		
		@Override
		public int getPositionStart(final int refPosition, final int position) {
			return this.underlyingDim.getPositionStart(refPosition, position);
		}
		
		@Override
		public int getPositionSize(final int refPosition, final int position) {
			return this.underlyingDim.getPositionSize(refPosition, position);
		}
		
		
		@Override
		public boolean isPositionResizable(final int position) {
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
			setDim(orientation, new Dim(this, dependency));
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
