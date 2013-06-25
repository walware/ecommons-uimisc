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
package org.eclipse.nebula.widgets.nattable.data;

import java.util.List;

/**
 * Enables the use of a {@link List} containing POJO(s) as a backing data source.
 * 
 * By default a bean at position 'X' in the list is displayed in 
 * row 'X' in the table. The properties of the bean are used to 
 * populate the columns. A {@link IColumnPropertyResolver} is used to
 * retrieve column data from the bean properties. 
 *
 * @param <T> type of the Objects in the backing list.
 * @see IColumnPropertyResolver
 */
public class ListDataProvider<T> implements IRowDataProvider<T> {

	protected List<T> list;
	protected IColumnAccessor<T> columnAccessor;
	
	public ListDataProvider(List<T> list, IColumnAccessor<T> columnAccessor) {
		this.list = list;
		this.columnAccessor = columnAccessor;
	}
	
	public long getColumnCount() {
		return columnAccessor.getColumnCount();
	}
	
	public long getRowCount() {
		return list.size();
	}

	public Object getDataValue(long columnIndex, long rowIndex) {
		if (rowIndex >= Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		T rowObj = list.get((int) rowIndex);
		return columnAccessor.getDataValue(rowObj, columnIndex);
	}
	
	public void setDataValue(long columnIndex, long rowIndex, Object newValue) {
		if (rowIndex >= Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		T rowObj = list.get((int) rowIndex);
		columnAccessor.setDataValue(rowObj, columnIndex, newValue);
	}

	public T getRowObject(long rowIndex) {
		if (rowIndex >= Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		return list.get((int) rowIndex);
	}
	
	public long indexOfRowObject(T rowObject) {
		return list.indexOf(rowObject);
	}
	
	public List<T> getList() {
		return list;
	}
	
}
