/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
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

import static org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.NO_SELECTION;

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.coordinate.PositionUtil;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.event.RowVisualChangeEvent;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;


public class RowSelectionEvent extends RowVisualChangeEvent implements ISelectionEvent {


	private final SelectionLayer selectionLayer;

	private int rowPositionToReveal;


	public RowSelectionEvent(SelectionLayer selectionLayer, int rowPosition, boolean revealRow) {
		super(selectionLayer, new Range(rowPosition, rowPosition+1));
		this.selectionLayer = selectionLayer;
		this.rowPositionToReveal = (revealRow) ? rowPosition : NO_SELECTION;
	}

	public RowSelectionEvent(SelectionLayer selectionLayer, Collection<Integer> rowPositions, int rowPositionToReveal) {
		super(selectionLayer, PositionUtil.getRanges(rowPositions));
		this.selectionLayer = selectionLayer;
		this.rowPositionToReveal = rowPositionToReveal;
	}

	protected RowSelectionEvent(RowSelectionEvent event) {
		super(event);
		this.selectionLayer = event.selectionLayer;
		this.rowPositionToReveal = event.rowPositionToReveal;
	}

	public RowSelectionEvent cloneEvent() {
		return new RowSelectionEvent(this);
	}


	public SelectionLayer getSelectionLayer() {
		return selectionLayer;
	}

	public int getRowPositionToReveal() {
		return rowPositionToReveal;
	}

	@Override
	public boolean convertToLocal(ILayer localLayer) {
		if (rowPositionToReveal != NO_SELECTION) {
			rowPositionToReveal = localLayer.underlyingToLocalRowPosition(getLayer(), rowPositionToReveal);
		}
		
		return super.convertToLocal(localLayer);
	}

}
