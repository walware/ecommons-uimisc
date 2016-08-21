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

import de.walware.ecommons.waltable.layer.DataLayer;

/**
 * Provide data to the table.
 *
 * @see DataLayer
 * @see ListDataProvider
 */
public interface IDataProvider {
	
	
	int FORCE_SYNC= 1 << 0;
	
	
	long getColumnCount();
	
	long getRowCount();
	
	
	/**
	 * Gets the value at the given column and row index.
	 *
	 * @param columnIndex
	 * @param rowIndex
	 * @param flags
	 * @return the data value associated with the specified cell
	 */
	Object getDataValue(long columnIndex, long rowIndex, final int flags);
	
	/**
	 * Sets the value at the given column and row index. Optional operation. Should throw UnsupportedOperationException
	 * if this operation is not supported.
	 *
	 * @param columnIndex
	 * @param rowIndex
	 * @param newValue
	 */
	void setDataValue(long columnIndex, long rowIndex, Object newValue);
	
}
