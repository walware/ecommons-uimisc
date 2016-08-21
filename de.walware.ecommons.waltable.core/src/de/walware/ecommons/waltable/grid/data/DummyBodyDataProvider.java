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
package de.walware.ecommons.waltable.grid.data;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.ecommons.waltable.coordinate.LPoint;
import de.walware.ecommons.waltable.data.IDataProvider;


public class DummyBodyDataProvider implements IDataProvider {
	
	
	private final long columnCount;
	
	private final long rowCount;
	
	private final Map<LPoint, Object> values= new HashMap<>();
	
	
	public DummyBodyDataProvider(final long columnCount, final long rowCount) {
		this.columnCount= columnCount;
		this.rowCount= rowCount;
	}
	
	
	@Override
	public long getColumnCount() {
		return this.columnCount;
	}
	
	@Override
	public long getRowCount() {
		return this.rowCount;
	}
	
	@Override
	public Object getDataValue(final long columnIndex, final long rowIndex, final int flags, final IProgressMonitor monitor) {
		final LPoint lPoint= new LPoint(columnIndex, rowIndex);
		if (this.values.containsKey(lPoint)) {
			return this.values.get(lPoint);
		}
		else {
			return "Col: " + (columnIndex + 1) + ", Row: " + (rowIndex + 1); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	@Override
	public void setDataValue(final long columnIndex, final long rowIndex, final Object newValue) {
		this.values.put(new LPoint(columnIndex, rowIndex), newValue);
	}
	
}
