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

package de.walware.ecommons.waltable.layer;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import java.util.Properties;

import de.walware.ecommons.waltable.command.ILayerCommand;
import de.walware.ecommons.waltable.config.ConfigRegistry;
import de.walware.ecommons.waltable.layer.cell.ForwardLayerCell;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.layer.cell.ILayerCellDim;
import de.walware.ecommons.waltable.layer.event.IStructuralChangeEvent;
import de.walware.ecommons.waltable.painter.layer.ILayerPainter;
import de.walware.ecommons.waltable.ui.IClientAreaProvider;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;


public abstract class ForwardLayer extends AbstractLayer {
	
	
	private final ILayer underlyingLayer;
	
	
	public ForwardLayer(/*@NonNull*/ final ILayer underlyingLayer) {
		if (underlyingLayer == null) {
			throw new NullPointerException("underlyingLayer"); //$NON-NLS-1$
		}
		this.underlyingLayer= underlyingLayer;
		this.underlyingLayer.setClientAreaProvider(getClientAreaProvider());
		this.underlyingLayer.addLayerListener(this);
		
		initDims();
	}
	
	
	@Override
	protected void initDims() {
		final ILayer underlying= getUnderlyingLayer();
		if (underlying == null) {
			return;
		}
		setDim(new ForwardLayerDim<>(this, underlying.getDim(HORIZONTAL)));
		setDim(new ForwardLayerDim<>(this, underlying.getDim(VERTICAL)));
	}
	
	protected void setUnderlyingLayer(/*@NonNull*/ final ILayer underlyingLayer) {
	}
	
	protected final ILayer getUnderlyingLayer() {
		return this.underlyingLayer;
	}
	
	// Dispose
	
	@Override
	public void dispose() {
		this.underlyingLayer.dispose();
	}
	
	// Persistence
	
	@Override
	public void saveState(final String prefix, final Properties properties) {
		this.underlyingLayer.saveState(prefix, properties);
		super.saveState(prefix, properties);
	}
	
	/**
	 * Underlying layers <i>must</i> load state first.
	 * If this is not done, {@link IStructuralChangeEvent} from underlying
	 * layers will reset caches after state has been loaded
	 */
	@Override
	public void loadState(final String prefix, final Properties properties) {
		this.underlyingLayer.loadState(prefix, properties);
		super.loadState(prefix, properties);
	}
	
	// Configuration
	
	@Override
	public void configure(final ConfigRegistry configRegistry, final UiBindingRegistry uiBindingRegistry) {
		this.underlyingLayer.configure(configRegistry, uiBindingRegistry);
		super.configure(configRegistry, uiBindingRegistry);
	}

	@Override
	public ILayerPainter getLayerPainter() {
		return (this.layerPainter != null) ? this.layerPainter : this.underlyingLayer.getLayerPainter();
	}
	
	// Command
	
	@Override
	public boolean doCommand(final ILayerCommand command) {
		if (super.doCommand(command)) {
			return true;
		}
		
		if (this.underlyingLayer != null) {
			return this.underlyingLayer.doCommand(command);
		}
		
		return false;
	}
	
	// Client area
	
	@Override
	public void setClientAreaProvider(final IClientAreaProvider clientAreaProvider) {
		super.setClientAreaProvider(clientAreaProvider);
		if (getUnderlyingLayer() != null) {
			getUnderlyingLayer().setClientAreaProvider(clientAreaProvider);
		}
	}
	
	
	// Cell features
	
	@Override
	public ILayerCell getCellByPosition(final long columnPosition, final long rowPosition) {
		final ILayerCell underlyingCell= this.underlyingLayer.getCellByPosition(
				columnPosition, rowPosition );
		
		return createCell(
				underlyingCell.getDim(HORIZONTAL),
				underlyingCell.getDim(VERTICAL),
				underlyingCell );
	}
	
	protected ILayerCell createCell(final ILayerCellDim hDim, final ILayerCellDim vDim,
			final ILayerCell underlyingCell) {
		return new ForwardLayerCell(this, hDim, vDim, underlyingCell);
	}
	
	// IRegionResolver
	
	@Override
	public LabelStack getRegionLabelsByXY(final long x, final long y) {
		return this.underlyingLayer.getRegionLabelsByXY(x, y);
	}
	
	@Override
	public ILayer getUnderlyingLayerByPosition(final long columnPosition, final long rowPosition) {
		return this.underlyingLayer;
	}
	
}
