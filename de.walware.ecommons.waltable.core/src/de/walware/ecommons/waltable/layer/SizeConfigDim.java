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

import de.walware.ecommons.waltable.coordinate.Orientation;


public abstract class SizeConfigDim<TLayer extends ILayer> extends DataDim<ILayer> {
	
	
	private final SizeConfig sizeConfig;
	
	
	public SizeConfigDim(final ILayer layer, final Orientation orientation,
			final long idCat, final SizeConfig sizeConfig) {
		super(layer, orientation, idCat);
		
		this.sizeConfig= sizeConfig;
	}
	
	
	public void setResizableByDefault(final boolean resizableByDefault) {
		this.sizeConfig.setResizableByDefault(resizableByDefault);
	}
	
	public void setPositionResizable(final long rowPosition, final boolean resizable) {
		this.sizeConfig.setPositionResizable(rowPosition, resizable);
	}
	
	
	@Override
	public long getSize() {
		return this.sizeConfig.getAggregateSize(getPositionCount());
	}
	
	@Override
	public long getPositionStart(final long position) {
		return this.sizeConfig.getAggregateSize(position);
	}
	
	@Override
	public int getPositionSize(final long position) {
		return this.sizeConfig.getSize(position);
	}
	
	@Override
	public boolean isPositionResizable(final long position) {
		return this.sizeConfig.isPositionResizable(position);
	}
	
}
