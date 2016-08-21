/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// ~

package de.walware.ecommons.waltable.grid.layer;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.layer.LayerUtil;
import de.walware.ecommons.waltable.layer.cell.ForwardLayerCell;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.layer.cell.ILayerCellDim;
import de.walware.ecommons.waltable.painter.layer.ILayerPainter;
import de.walware.ecommons.waltable.selection.SelectionLayer;
import de.walware.ecommons.waltable.style.DisplayMode;


/**
 * Responsible for rendering, event handling etc on the column/row headers.
 */
public class AbstractPositionHeaderLayer extends DimensionallyDependentLayer {
	
	
	private final Orientation headerOrientation;
	
	private final SelectionLayer selectionLayer;
	private final String fullySelectedLabel;
	
	
	/**
	 * @param baseLayer
	 *            The data provider for this layer
	 * @param contentLayerDependency
	 *            The layer to link the horizontal dimension to, typically the body layer
	 * @param selectionLayer
	 *            The selection layer required to respond to selection events
	 * @param layerPainter
	 *            The painter for this layer or <code>null</code> to use the painter of the base layer
	 */
	public AbstractPositionHeaderLayer(final ILayer baseLayer, final Orientation orientation,
			final ILayer contentLayerDependency,
			final SelectionLayer selectionLayer, final String fullySelectedLabel,
			final ILayerPainter layerPainter) {
		super(baseLayer,
				(orientation == HORIZONTAL) ? contentLayerDependency : baseLayer,
				(orientation == VERTICAL) ? contentLayerDependency : baseLayer );
		if (selectionLayer == null) {
			throw new NullPointerException("selectionLayer"); //$NON-NLS-1$
		}
		
		this.headerOrientation= orientation;
		this.selectionLayer= selectionLayer;
		this.fullySelectedLabel= fullySelectedLabel;
		
		this.layerPainter= layerPainter;
	}
	
	
	@Override
	protected ILayerCell createCell(final ILayerCellDim hDim, final ILayerCellDim vDim, final ILayerCell underlyingCell) {
		return new ForwardLayerCell(this, hDim, vDim, underlyingCell) {
			
			@Override
			public DisplayMode getDisplayMode() {
				if (isSelected(getDim(AbstractPositionHeaderLayer.this.headerOrientation))) {
					return DisplayMode.SELECT;
				}
				return super.getDisplayMode();
			}
			
			@Override
			public LabelStack getConfigLabels() {
				final LabelStack configLabels= super.getConfigLabels();
				
				if (isFullySelected(getDim(AbstractPositionHeaderLayer.this.headerOrientation))) {
					configLabels.addLabel(AbstractPositionHeaderLayer.this.fullySelectedLabel);
				}
				
				return configLabels;
			}
			
		};
	}
	
	protected boolean isSelected(final ILayerCellDim dim) {
		final ILayerDim layerDim= getDim(this.headerOrientation);
		final long position= dim.getPosition();
		if (this.selectionLayer.isPositionSelected(this.headerOrientation,
				LayerUtil.convertPosition(layerDim, position, position,
						this.selectionLayer.getDim(this.headerOrientation) ) )) {
			return true;
		}
		if (dim.getPositionSpan() > 1) {
			long iPosition= dim.getOriginPosition();
			final long endPosition= iPosition + dim.getPositionSpan();
			for (; iPosition < endPosition; iPosition++) {
				if (iPosition != position
						&& this.selectionLayer.isPositionSelected(this.headerOrientation,
								LayerUtil.convertPosition(layerDim, position, iPosition,
										this.selectionLayer.getDim(this.headerOrientation) ))) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected boolean isFullySelected(final ILayerCellDim dim) {
		final ILayerDim layerDim= getDim(this.headerOrientation);
		final long position= dim.getPosition();
		if (!this.selectionLayer.isPositionFullySelected(this.headerOrientation,
				LayerUtil.convertPosition(layerDim, position, position,
						this.selectionLayer.getDim(this.headerOrientation)) )) {
			return false;
		}
		if (dim.getPositionSpan() > 1) {
			long iPosition= dim.getOriginPosition();
			final long endPosition= iPosition + dim.getPositionSpan();
			for (; iPosition < endPosition; iPosition++) {
				if (iPosition != position
						&& !this.selectionLayer.isPositionFullySelected(this.headerOrientation,
						LayerUtil.convertPosition(layerDim, position, iPosition,
								this.selectionLayer.getDim(this.headerOrientation) ))) {
					return false;
				}
			}
		}
		return true;
	}
	
}
