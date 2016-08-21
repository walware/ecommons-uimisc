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

package de.walware.ecommons.waltable.data;

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
	
	
	public ListDataProvider(final List<T> list, final IColumnAccessor<T> columnAccessor) {
		this.list= list;
		this.columnAccessor= columnAccessor;
	}
	
	@Override
	public long getColumnCount() {
		return this.columnAccessor.getColumnCount();
	}
	
	@Override
	public long getRowCount() {
		return this.list.size();
	}
	
	@Override
	public Object getDataValue(final long columnIndex, final long rowIndex, final int flags) {
		if (rowIndex >= Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		final T rowObj= this.list.get((int) rowIndex);
		return this.columnAccessor.getDataValue(rowObj, columnIndex);
	}
	
	@Override
	public void setDataValue(final long columnIndex, final long rowIndex, final Object newValue) {
		if (rowIndex >= Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		final T rowObj= this.list.get((int) rowIndex);
		this.columnAccessor.setDataValue(rowObj, columnIndex, newValue);
	}
	
	@Override
	public T getRowObject(final long rowIndex) {
		if (rowIndex >= Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		return this.list.get((int) rowIndex);
	}
	
	@Override
	public long indexOfRowObject(final T rowObject) {
		return this.list.indexOf(rowObject);
	}
	
	public List<T> getList() {
		return this.list;
	}
	
}
