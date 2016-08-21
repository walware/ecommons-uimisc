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
// ~
package de.walware.ecommons.waltable.selection;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.selection.SelectionLayer.NO_SELECTION;

import java.util.Collection;

import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.LRangeList;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.event.ColumnVisualChangeEvent;


public class ColumnSelectionEvent extends ColumnVisualChangeEvent implements ISelectionEvent {
	
	
	private final SelectionLayer selectionLayer;
	
	private long columnPositionToReveal;
	
	
	public ColumnSelectionEvent(final SelectionLayer selectionLayer,
			final long columnPosition, final boolean revealColumn) {
		this(selectionLayer, new LRangeList(columnPosition),
				(revealColumn) ? columnPosition : NO_SELECTION );
	}
	
	public ColumnSelectionEvent(final SelectionLayer selectionLayer,
			final Collection<LRange> columnPositions, final long columnPositionToReveal) {
		super(selectionLayer, columnPositions);
		this.selectionLayer= selectionLayer;
		this.columnPositionToReveal= columnPositionToReveal;
	}
	
	protected ColumnSelectionEvent(final ColumnSelectionEvent event) {
		super(event);
		this.selectionLayer= event.selectionLayer;
		this.columnPositionToReveal= event.columnPositionToReveal;
	}
	
	@Override
	public ColumnSelectionEvent cloneEvent() {
		return new ColumnSelectionEvent(this);
	}
	
	
	@Override
	public SelectionLayer getSelectionLayer() {
		return this.selectionLayer;
	}
	
	public long getColumnPositionToReveal() {
		return this.columnPositionToReveal;
	}
	
	@Override
	public boolean convertToLocal(final ILayer localLayer) {
		if (this.columnPositionToReveal != NO_SELECTION) {
			this.columnPositionToReveal= localLayer.getDim(HORIZONTAL).underlyingToLocalPosition(
					getLayer().getDim(HORIZONTAL), this.columnPositionToReveal );
		}
		
		return super.convertToLocal(localLayer);
	}
	
}
