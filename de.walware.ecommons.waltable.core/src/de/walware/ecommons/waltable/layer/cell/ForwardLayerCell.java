/*******************************************************************************
 * Copyright (c) 2012-2016 Edwin Park and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Edwin Park - initial API and implementation
 ******************************************************************************/

package de.walware.ecommons.waltable.layer.cell;

import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.style.DisplayMode;


public class ForwardLayerCell extends LayerCell {
	
	
	private final ILayerCell underlyingCell;
	
	
	public ForwardLayerCell(final ILayer layer,
			final ILayerCellDim horizontalDim, final ILayerCellDim verticalDim,
			final ILayerCell underlyingCell) {
		super(layer, horizontalDim, verticalDim);
		
		this.underlyingCell= underlyingCell;
	}
	
	
	public ILayerCell getUnderlyingCell() {
		return this.underlyingCell;
	}
	
	@Override
	public DisplayMode getDisplayMode() {
		return this.underlyingCell.getDisplayMode();
	}
	
	@Override
	public LabelStack getConfigLabels() {
		return this.underlyingCell.getConfigLabels();
	}
	
	
	@Override
	public Object getDataValue(final int flags, final IProgressMonitor monitor) {
		return this.underlyingCell.getDataValue(flags, monitor);
	}
	
}
