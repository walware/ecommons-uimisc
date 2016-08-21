/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation (DimensionallyDependentLayer)
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package de.walware.ecommons.waltable.grid.layer;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.walware.ecommons.waltable.command.ILayerCommand;
import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.coordinate.PositionOutOfBoundsException;
import de.walware.ecommons.waltable.layer.ForwardLayerDim;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.layer.TransformLayer;


/**
 * <p>A DimensionallyDependentLayer is a layer whose horizontal and vertical dimensions are dependent on the 
 * horizontal and vertical dimensions of other layers. A DimensionallyDependentLayer takes three constructor 
 * parameters: the horizontal layer that the DimensionallyDependentLayer's horizontal dimension is linked to, the 
 * vertical layer that the DimensionallyDependentLayer is linked to, and a base layer to which all 
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
 * ILayer columnHeaderLayer= new DimensionallyDependentLayer(columnHeaderRowDataLayer, bodyLayer, columnHeaderRowDataLayer);
 * ILayer rowHeaderLayer= new DimensionallyDependentLayer(rowHeaderColumnDataLayer, bodyLayer, rowHeaderColumnDataLayer);
 * </pre>
 */
public class DimensionallyDependentLayer extends TransformLayer {
	
	
	protected static class Dim extends ForwardLayerDim<DimensionallyDependentLayer> {
		
		
		public Dim(final DimensionallyDependentLayer layer, final ILayerDim underlyingDim) {
			super(layer, underlyingDim);
		}
		
		
		protected ILayerDim getBaseDim() {
			return this.layer.getBaseLayer().getDim(this.orientation);
		}
		
		
		@Override
		public long localToUnderlyingPosition(final long refPosition, final long position) {
			final long id= this.underlyingDim.getPositionId(refPosition, position);
			final long underlyingPosition= getBaseDim().getPositionById(id);
			
			if (underlyingPosition == POSITION_NA) {
				throw PositionOutOfBoundsException.position(position, getOrientation());
			}
			
			return underlyingPosition;
		}
		
		@Override
		public long underlyingToLocalPosition(final ILayerDim sourceUnderlyingDim,
				final long underlyingPosition) {
			if (sourceUnderlyingDim != getBaseDim()) {
				throw new IllegalArgumentException("underlyingLayer"); //$NON-NLS-1$
			}
			
			return doUnderlyingToLocalPosition(underlyingPosition);
		}
		
		@Override
		public List<LRange> underlyingToLocalPositions(final ILayerDim sourceUnderlyingDim,
				final Collection<LRange> underlyingPositions) {
			if (sourceUnderlyingDim != getBaseDim()) {
				throw new IllegalArgumentException("underlyingLayer"); //$NON-NLS-1$
			}
			
			final List<LRange> localPositions= new ArrayList<>(underlyingPositions.size());
			
			for (final LRange underlyingPositionRange : underlyingPositions) {
				if (underlyingPositionRange.start == underlyingPositionRange.end) {
					final long position= doUnderlyingToLocalPosition(underlyingPositionRange.start);
					localPositions.add(new LRange(position, position));
				}
				else {
					final long first= doUnderlyingToLocalPosition(underlyingPositionRange.start);
					final long last= doUnderlyingToLocalPosition(underlyingPositionRange.end - 1);
					if (first <= last) {
						localPositions.add(new LRange(first, last + 1));
					}
				}
			}
			return localPositions;
		}
		
		protected long doUnderlyingToLocalPosition(final long underlyingPosition) {
			final long id= getBaseDim().getPositionId(underlyingPosition,
					underlyingPosition );
			final long position= this.underlyingDim.getPositionById(id);
			
			if (underlyingPosition == POSITION_NA) {
				throw PositionOutOfBoundsException.underlyingPosition(underlyingPosition);
			}
			
			return position;
		}
		
		@Override
		public List<ILayerDim> getUnderlyingDimsByPosition(final long position) {
			return Collections.<ILayerDim>singletonList(getBaseDim());
		}
		
	}
	
	
	private ILayer horizontalLayerDependency;
	private ILayer verticalLayerDependency;
	
	
	/**
	 * Creates a new DimensionallyDependentLayer.
	 * 
	 * @param baseLayer the underlying base layer
	 * @param horizontalLayerDependency the layer, the horizontal dimension is linked to
	 * @param verticalLayerDependency the layer, the vertical dimension is linked to
	 */
	public DimensionallyDependentLayer(final ILayer baseLayer,
			final ILayer horizontalLayerDependency, final ILayer verticalLayerDependency) {
		super(baseLayer);
		
		setHorizontalLayerDependency(horizontalLayerDependency);
		setVerticalLayerDependency(verticalLayerDependency);
	}
	
	/**
	 * Creates a new DimensionallyDependentLayer. The horizontal and vertical layer dependency must be set
	 * by calling {@link #init(ILayer, ILayer)} before the layer is used.
	 * 
	 * @param baseLayer the underlying base layer
	 */
	protected DimensionallyDependentLayer(final ILayer baseLayer) {
		super(baseLayer);
	}
	
	
	@Override
	protected void initDims() {
		if (this.horizontalLayerDependency == null || this.verticalLayerDependency == null) {
			return;
		}
		
		for (final Orientation orientation : Orientation.values()) {
			final ILayer dependency= getLayerDependency(orientation);
			final ILayerDim dim;
			if (dependency == getBaseLayer()) {
				dim= new ForwardLayerDim<>(this, dependency.getDim(orientation));
			}
			else {
				dim= new Dim(this, dependency.getDim(orientation));
			}
			setDim(dim);
		}
	}
	
	
	// Dependent layer accessors
	
	protected void setHorizontalLayerDependency(final ILayer horizontalLayerDependency) {
		this.horizontalLayerDependency= horizontalLayerDependency;
		
//		horizontalLayerDependency.addLayerListener(new ILayerListener() {
//
//			public void handleLayerEvent(ILayerEvent event) {
//				if (event instanceof IStructuralChangeEvent) {
//					// TODO refresh horizontal structure
//				}
//			}
//
//		});
		
		initDims();
	}

	protected void setVerticalLayerDependency(final ILayer verticalLayerDependency) {
		this.verticalLayerDependency= verticalLayerDependency;

//		verticalLayerDependency.addLayerListener(new ILayerListener() {
//
//			public void handleLayerEvent(ILayerEvent event) {
//				if (event instanceof IStructuralChangeEvent) {
//					// TODO refresh vertical structure
//				}
//			}
//
//		});
		
		initDims();
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
	
	public ILayer getBaseLayer() {
		return getUnderlyingLayer();
	}
	
	// Commands
	
	@Override
	public boolean doCommand(final ILayerCommand command) {
		if (super.doCommand(command)) {
			return true;
		}
		
		// Invoke command handler(s) on the Dimensionally dependent layer ?
		if (getBaseLayer() != this.horizontalLayerDependency
				&& this.horizontalLayerDependency.doCommand(command)) {
			return true;
		}
		if (getBaseLayer() != this.verticalLayerDependency
				&& this.verticalLayerDependency.doCommand(command)) {
			return true;
		}
		
		return false;
	}
	
}
