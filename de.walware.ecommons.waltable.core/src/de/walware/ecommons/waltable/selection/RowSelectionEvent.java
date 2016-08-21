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

import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;
import static de.walware.ecommons.waltable.selection.SelectionLayer.NO_SELECTION;

import java.util.Collection;

import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.LRangeList;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.event.RowVisualChangeEvent;


public class RowSelectionEvent extends RowVisualChangeEvent implements ISelectionEvent {
	
	
	private final SelectionLayer selectionLayer;
	
	private long rowPositionToReveal;
	
	
	public RowSelectionEvent(final SelectionLayer selectionLayer,
			final long rowPosition, final boolean revealRow) {
		this(selectionLayer, new LRangeList(rowPosition),
				(revealRow) ? rowPosition : NO_SELECTION );
	}
	
	public RowSelectionEvent(final SelectionLayer selectionLayer,
			final Collection<LRange> rowPositions, final long rowPositionToReveal) {
		super(selectionLayer, rowPositions);
		this.selectionLayer= selectionLayer;
		this.rowPositionToReveal= rowPositionToReveal;
	}
	
	protected RowSelectionEvent(final RowSelectionEvent event) {
		super(event);
		this.selectionLayer= event.selectionLayer;
		this.rowPositionToReveal= event.rowPositionToReveal;
	}
	
	@Override
	public RowSelectionEvent cloneEvent() {
		return new RowSelectionEvent(this);
	}
	
	
	@Override
	public SelectionLayer getSelectionLayer() {
		return this.selectionLayer;
	}
	
	public long getRowPositionToReveal() {
		return this.rowPositionToReveal;
	}
	
	@Override
	public boolean convertToLocal(final ILayer localLayer) {
		if (this.rowPositionToReveal != NO_SELECTION) {
			this.rowPositionToReveal= localLayer.getDim(VERTICAL).underlyingToLocalPosition(
					getLayer().getDim(VERTICAL), this.rowPositionToReveal );
		}
		
		return super.convertToLocal(localLayer);
	}
	
}
