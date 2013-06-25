/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// ~
package org.eclipse.nebula.widgets.nattable.selection.event;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.event.CellVisualChangeEvent;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;


public class CellSelectionEvent extends CellVisualChangeEvent implements ISelectionEvent {
	
	private final SelectionLayer selectionLayer;
	
	private boolean revealCell;
	
	
	public CellSelectionEvent(SelectionLayer selectionLayer, int columnPosition, int rowPosition,
			boolean revealCell) {
		super(selectionLayer, columnPosition, rowPosition);
		this.selectionLayer = selectionLayer;
		this.revealCell = revealCell;
	}
	
	protected CellSelectionEvent(CellSelectionEvent event) {
		super(event);
		this.selectionLayer = event.selectionLayer;
		this.revealCell = event.revealCell;
	}
	
	@Override
	public CellSelectionEvent cloneEvent() {
		return new CellSelectionEvent(this);
	}
	
	
	public SelectionLayer getSelectionLayer() {
		return selectionLayer;
	}
	
	public boolean getRevealCell() {
		return revealCell;
	}
	
	public boolean convertToLocal(ILayer localLayer) {
		if(columnPosition == SelectionLayer.NO_SELECTION || rowPosition == SelectionLayer.NO_SELECTION){
			return true;
		}
		return super.convertToLocal(localLayer);
	}
	
}
