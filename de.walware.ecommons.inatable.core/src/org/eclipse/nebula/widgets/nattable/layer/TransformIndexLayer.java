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

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;
import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;


public class TransformIndexLayer extends TransformLayer implements IUniqueIndexLayer {
	
	
	public TransformIndexLayer(/*@NonNull*/ final IUniqueIndexLayer underlyingLayer) {
		super(underlyingLayer);
	}
	
	protected TransformIndexLayer() {
		super();
	}
	
	
	@Override
	protected void setUnderlyingLayer(/*@NonNull*/ final ILayer underlyingLayer) {
		if (!(underlyingLayer instanceof IUniqueIndexLayer)) {
			throw new IllegalArgumentException("underlyingLayer: !instanceof IUniqueIndexLayer"); //$NON-NLS-1$
		}
		
		super.setUnderlyingLayer(underlyingLayer);
	}
	
	
	@Override
	public long getColumnPositionByIndex(final long columnIndex) {
		final IUniqueIndexLayer underlyingLayer = (IUniqueIndexLayer) getUnderlyingLayer();
		final long underlyingPosition = underlyingLayer.getColumnPositionByIndex(columnIndex);
		if (underlyingPosition == Long.MIN_VALUE) {
			return Long.MIN_VALUE;
		}
		return getDim(HORIZONTAL).underlyingToLocalPosition(underlyingLayer.getDim(HORIZONTAL),
				underlyingPosition );
	}
	
	@Override
	public long getRowPositionByIndex(final long rowIndex) {
		final IUniqueIndexLayer underlyingLayer = (IUniqueIndexLayer) getUnderlyingLayer();
		final long underlyingPosition = underlyingLayer.getRowPositionByIndex(rowIndex);
		if (underlyingPosition == Long.MIN_VALUE) {
			return Long.MIN_VALUE;
		}
		return getDim(VERTICAL).underlyingToLocalPosition(underlyingLayer.getDim(VERTICAL),
				underlyingPosition );
	}
	
}
