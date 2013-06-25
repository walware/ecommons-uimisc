/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Dirk Fauth
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.grid.data;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.coordinate.Point;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

public class DummyModifiableBodyDataProvider implements IDataProvider {

	private long columnCount;
	
	private long rowCount;

	private Map<Point, Object> values = new HashMap<Point, Object>();
	
	public DummyModifiableBodyDataProvider(long columnCount, long rowCount) {
		this.columnCount = columnCount;
		this.rowCount = rowCount;
	}
	
	public long getColumnCount() {
		return columnCount;
	}

	public long getRowCount() {
		return rowCount;
	}

	/**
	 * You have to fire a {@link org.eclipse.nebula.widgets.nattable.layer.event.StructuralRefreshEvent} after using this method for refreshing the NatTable.
	 * @param columnCount
	 */
	public void setColumnCount(long columnCount) {
		this.columnCount = columnCount;
	}

	/**
	 * You have to fire a {@link org.eclipse.nebula.widgets.nattable.layer.event.StructuralRefreshEvent} after using this method for refreshing the NatTable.
	 * @param rowCount
	 */
	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}

	public Object getDataValue(long columnIndex, long rowIndex) {
		Point point = new Point(columnIndex, rowIndex);
		if (values.containsKey(point)) {
			return values.get(point);
		} else {
			return "Col: " + (columnIndex + 1) + ", Row: " + (rowIndex + 1); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	public void setDataValue(long columnIndex, long rowIndex, Object newValue) {
		values.put(new Point(columnIndex, rowIndex), newValue);
	}

}
