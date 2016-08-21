/*******************************************************************************
 * Copyright (c) 2013-2016 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package de.walware.ecommons.waltable.layer;

import de.walware.ecommons.waltable.coordinate.Orientation;


/**
 * Abstract implementation of layer dimension.
 *
 * @param <TLayer> the type of the layer
 */
public abstract class AbstractLayerDim<TLayer extends ILayer> implements ILayerDim {
	
	
	protected final TLayer layer;
	
	protected final Orientation orientation;
	
	
	public AbstractLayerDim(/*@NonNull*/ final TLayer layer, /*@NonNull*/ final Orientation orientation) {
		if (layer == null) {
			throw new NullPointerException("layer"); //$NON-NLS-1$
		}
		if (orientation == null) {
			throw new NullPointerException("orientation"); //$NON-NLS-1$
		}
		this.layer= layer;
		this.orientation= orientation;
	}
	
	
	@Override
	public ILayer getLayer() {
		return this.layer;
	}
	
	@Override
	public final Orientation getOrientation() {
		return this.orientation;
	}
	
	
	@Override
	public String toString() {
		final StringBuilder sb= new StringBuilder("LayerDim"); //$NON-NLS-1$
		sb.append(" ").append(this.orientation); //$NON-NLS-1$
		sb.append(" of \n").append(this.layer); //$NON-NLS-1$
		return sb.toString();
	}
	
}
