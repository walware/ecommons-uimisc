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

package de.walware.ecommons.waltable.sort;

import java.util.Collection;

import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.layer.event.ColumnVisualChangeEvent;


public class SortColumnEvent extends ColumnVisualChangeEvent {
	
	
	public SortColumnEvent(final ILayerDim layer, final long columnPosition) {
		super(layer.getLayer(), new LRange(columnPosition));
	}
	
	public SortColumnEvent(final ILayerDim layer, final Collection<LRange> columnPositions) {
		super(layer.getLayer(), columnPositions);
	}
	
	protected SortColumnEvent(final SortColumnEvent event) {
		super(event);
	}
	
	@Override
	public SortColumnEvent cloneEvent() {
		return new SortColumnEvent(this);
	}
	
}
