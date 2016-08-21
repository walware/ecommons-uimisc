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

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.ecommons.waltable.data.IDataProvider;


public class DefaultColumnHeaderDataProvider implements IDataProvider {
	
	
	private final String[] propertyNames;
	
	private Map<String, String> propertyToLabelMap;
	
	
	public DefaultColumnHeaderDataProvider(final String[] columnLabels) {
		this.propertyNames= columnLabels;
	}
	
	public DefaultColumnHeaderDataProvider(final String[] propertyNames, final Map<String, String> propertyToLabelMap) {
		this.propertyNames= propertyNames;
		this.propertyToLabelMap= propertyToLabelMap;
	}
	
	
	public String getColumnHeaderLabel(final long columnIndex) {
		if (columnIndex >= Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		final String propertyName= this.propertyNames[(int) columnIndex];
		if (this.propertyToLabelMap != null) {
			final String label= this.propertyToLabelMap.get(propertyName);
			if (label != null) {
				return label;
			}
		}
		return propertyName;
	}
	
	@Override
	public long getColumnCount() {
		return this.propertyNames.length;
	}
	
	@Override
	public long getRowCount() {
		return 1;
	}
	
	/**
	 * This class does not support multiple rows in the column header layer.
	 */
	@Override
	public Object getDataValue(final long columnIndex, final long rowIndex, final int flags, final IProgressMonitor monitor) {
		return getColumnHeaderLabel(columnIndex);
	}
	
	@Override
	public void setDataValue(final long columnIndex, final long rowIndex, final Object newValue) {
		throw new UnsupportedOperationException();
	}
	
}
