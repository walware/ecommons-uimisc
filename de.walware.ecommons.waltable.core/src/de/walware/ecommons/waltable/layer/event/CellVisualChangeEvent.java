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

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import java.util.Arrays;
import java.util.Collection;

import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.ILayer;

public class CellVisualChangeEvent implements IVisualChangeEvent {

	protected ILayer layer;
	
	protected long columnPosition;
	
	protected long rowPosition;
	
	public CellVisualChangeEvent(final ILayer layer, final long columnPosition, final long rowPosition) {
		this.layer= layer;
		this.columnPosition= columnPosition;
		this.rowPosition= rowPosition;
	}
	
	protected CellVisualChangeEvent(final CellVisualChangeEvent event) {
		this.layer= event.layer;
		this.columnPosition= event.columnPosition;
		this.rowPosition= event.rowPosition;
	}
	
	@Override
	public ILayer getLayer() {
		return this.layer;
	}
	
	public long getColumnPosition() {
		return this.columnPosition;
	}
	
	public long getRowPosition() {
		return this.rowPosition;
	}
	
	@Override
	public boolean convertToLocal(final ILayer localLayer) {
		this.columnPosition= localLayer.getDim(HORIZONTAL).underlyingToLocalPosition(
				this.layer.getDim(HORIZONTAL), this.columnPosition );
		this.rowPosition= localLayer.getDim(VERTICAL).underlyingToLocalPosition(
				this.layer.getDim(VERTICAL), this.rowPosition );
		
		this.layer= localLayer;
		
		return this.columnPosition >= 0 && this.rowPosition >= 0
			&& this.columnPosition < this.layer.getColumnCount() && this.rowPosition < this.layer.getRowCount();
	}
	
	@Override
	public Collection<LRectangle> getChangedPositionRectangles() {
		return Arrays.asList(new LRectangle[] { new LRectangle(this.columnPosition, this.rowPosition, 1, 1) });
	}
	
	@Override
	public CellVisualChangeEvent cloneEvent() {
		return new CellVisualChangeEvent(this);
	}

}
