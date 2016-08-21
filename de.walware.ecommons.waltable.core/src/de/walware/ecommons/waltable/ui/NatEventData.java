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
package de.walware.ecommons.waltable.ui;

import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.layer.LabelStack;


public class NatEventData {
	
	private final Object originalEvent;
	private final NatTable natTable;
	private final LabelStack regionLabels;
	long columnPosition;
	long rowPosition;

	public static NatEventData createInstanceFromEvent(final MouseEvent event) {
		final NatTable natTable= (NatTable) event.widget;
		
		final long columnPosition= natTable.getColumnPositionByX(event.x);
		final long rowPosition= natTable.getRowPositionByY(event.y);
        
        return new NatEventData(
				natTable,
				natTable.getRegionLabelsByXY(event.x, event.y),
				columnPosition,
				rowPosition,
				event
		);
	}
	
	public NatEventData(final NatTable natTable, final LabelStack regionLabels, final long columnPosition, final long rowPosition, final Object originalEvent) {
		this.natTable= natTable;
		this.regionLabels= regionLabels;
		this.columnPosition= columnPosition;
		this.rowPosition= rowPosition;
		this.originalEvent= originalEvent;
	}

	public NatTable getNatTable() {
		return this.natTable;
	}

	public LabelStack getRegionLabels() {
		return this.regionLabels;
	}

	public long getColumnPosition() {
		return this.columnPosition;
	}

	public long getRowPosition() {
		return this.rowPosition;
	}
	
	public Object getOriginalEvent() {
		return this.originalEvent;
	}

}
