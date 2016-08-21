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

import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.event.CellVisualChangeEvent;


public class CellSelectionEvent extends CellVisualChangeEvent implements ISelectionEvent {
	
	private final SelectionLayer selectionLayer;
	
	private final boolean revealCell;
	
	
	public CellSelectionEvent(final SelectionLayer selectionLayer, final long columnPosition, final long rowPosition,
			final boolean revealCell) {
		super(selectionLayer, columnPosition, rowPosition);
		this.selectionLayer= selectionLayer;
		this.revealCell= revealCell;
	}
	
	protected CellSelectionEvent(final CellSelectionEvent event) {
		super(event);
		this.selectionLayer= event.selectionLayer;
		this.revealCell= event.revealCell;
	}
	
	@Override
	public CellSelectionEvent cloneEvent() {
		return new CellSelectionEvent(this);
	}
	
	
	@Override
	public SelectionLayer getSelectionLayer() {
		return this.selectionLayer;
	}
	
	public boolean getRevealCell() {
		return this.revealCell;
	}
	
	@Override
	public boolean convertToLocal(final ILayer localLayer) {
		if(this.columnPosition == SelectionLayer.NO_SELECTION || this.rowPosition == SelectionLayer.NO_SELECTION){
			return true;
		}
		return super.convertToLocal(localLayer);
	}
	
}
