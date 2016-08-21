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
package de.walware.ecommons.waltable.layer.event;

import java.util.Arrays;
import java.util.Collection;

import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.ILayer;

public class VisualRefreshEvent implements IVisualChangeEvent {

	private ILayer layer;

	public VisualRefreshEvent(final ILayer layer) {
		this.layer= layer;
	}
	
	protected VisualRefreshEvent(final VisualRefreshEvent event) {
		this.layer= event.layer;
	}
	
	@Override
	public ILayer getLayer() {
		return this.layer;
	}

	@Override
	public boolean convertToLocal(final ILayer localLayer) {
		this.layer= localLayer;
		
		return true;
	}
	
	@Override
	public Collection<LRectangle> getChangedPositionRectangles() {
		return Arrays.asList(new LRectangle[] { new LRectangle(0, 0, this.layer.getColumnCount(), this.layer.getRowCount()) });
	}
	
	@Override
	public ILayerEvent cloneEvent() {
		return new VisualRefreshEvent(this);
	}
	
}
