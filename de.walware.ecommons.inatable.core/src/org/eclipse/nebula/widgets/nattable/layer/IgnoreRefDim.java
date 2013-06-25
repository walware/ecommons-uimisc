/*******************************************************************************
 * Copyright (c) 2013 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.layer;


public class IgnoreRefDim extends TransformDim<ILayer> {
	
	
	public IgnoreRefDim(final ILayer layer, final ILayerDim underlyingDim) {
		super(layer, underlyingDim);
	}
	
	public IgnoreRefDim(final ILayerDim dim) {
		super(dim.getLayer(), dim);
	}
	
	
	@Override
	public int getPositionIndex(final int refPosition, final int position) {
		return super.getPositionIndex(position, position);
	}
	
	
	@Override
	public int localToUnderlyingPosition(final int refPosition, final int position) {
		return super.localToUnderlyingPosition(refPosition, position);
	}
	
	@Override
	public int underlyingToLocalPosition(final int refPosition, final int underlyingPosition) {
		return super.underlyingToLocalPosition(refPosition, underlyingPosition);
	}
	
	
	@Override
	public int getPositionStart(final int refPosition, final int position) {
		return super.getPositionStart(position, position);
	}
	
	@Override
	public int getPositionSize(final int refPosition, final int position) {
		return super.getPositionSize(position, position);
	}
	
}
