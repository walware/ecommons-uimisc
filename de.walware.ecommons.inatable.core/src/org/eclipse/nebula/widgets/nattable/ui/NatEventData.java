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
package org.eclipse.nebula.widgets.nattable.ui;

import org.eclipse.swt.events.MouseEvent;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;


public class NatEventData {
	
	private Object originalEvent;
	private final NatTable natTable;
	private final LabelStack regionLabels;
	long columnPosition;
	long rowPosition;

	public static NatEventData createInstanceFromEvent(MouseEvent event) {
		NatTable natTable = (NatTable) event.widget;
		
		long columnPosition = natTable.getColumnPositionByX(event.x);
		long rowPosition = natTable.getRowPositionByY(event.y);
        
        return new NatEventData(
				natTable,
				natTable.getRegionLabelsByXY(event.x, event.y),
				columnPosition,
				rowPosition,
				event
		);
	}
	
	public NatEventData(NatTable natTable, LabelStack regionLabels, long columnPosition, long rowPosition, Object originalEvent) {
		this.natTable = natTable;
		this.regionLabels = regionLabels;
		this.columnPosition = columnPosition;
		this.rowPosition = rowPosition;
		this.originalEvent = originalEvent;
	}

	public NatTable getNatTable() {
		return natTable;
	}

	public LabelStack getRegionLabels() {
		return regionLabels;
	}

	public long getColumnPosition() {
		return columnPosition;
	}

	public long getRowPosition() {
		return rowPosition;
	}
	
	public Object getOriginalEvent() {
		return originalEvent;
	}

}
