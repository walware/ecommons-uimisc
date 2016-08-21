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

import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.ILayer;


public abstract class RowVisualChangeEvent implements IVisualChangeEvent {
	
	private ILayer layer;
	
	private Collection<LRange> rowPositionRanges= new ArrayList<>();
	
	public RowVisualChangeEvent(final ILayer layer, final LRange...rowPositionRanges) {
		this(layer, Arrays.asList(rowPositionRanges));
	}
	
	public RowVisualChangeEvent(final ILayer layer, final Collection<LRange> rowPositionRanges) {
		this.layer= layer;
		this.rowPositionRanges= rowPositionRanges;
	}
	
	// Copy constructor
	protected RowVisualChangeEvent(final RowVisualChangeEvent event) {
		this.layer= event.layer;
		this.rowPositionRanges= event.rowPositionRanges;
	}
	
	@Override
	public ILayer getLayer() {
		return this.layer;
	}
	
	public Collection<LRange> getRowPositionRanges() {
		return this.rowPositionRanges;
	}
	
	@Override
	public boolean convertToLocal(final ILayer localLayer) {
		this.rowPositionRanges= localLayer.getDim(VERTICAL).underlyingToLocalPositions(
				this.layer.getDim(VERTICAL), this.rowPositionRanges );
		this.layer= localLayer;
		
		return this.rowPositionRanges != null && this.rowPositionRanges.size() > 0;
	}
	
	@Override
	public Collection<LRectangle> getChangedPositionRectangles() {
		final Collection<LRectangle> changedPositionRectangles= new ArrayList<>();
		
		final long columnCount= this.layer.getColumnCount();
		for (final LRange lRange : this.rowPositionRanges) {
			changedPositionRectangles.add(new LRectangle(0, lRange.start, columnCount, lRange.end - lRange.start));
		}
		
		return changedPositionRectangles;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
}
