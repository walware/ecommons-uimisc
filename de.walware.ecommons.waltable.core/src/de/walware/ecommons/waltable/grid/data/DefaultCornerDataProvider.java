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

package de.walware.ecommons.waltable.grid.data;

import de.walware.ecommons.waltable.data.IDataProvider;


public class DefaultCornerDataProvider implements IDataProvider {
	
	
	private final IDataProvider columnHeaderDataProvider;
	private final IDataProvider rowHeaderDataProvider;
	
	
	public DefaultCornerDataProvider(final IDataProvider columnHeaderDataProvider, final IDataProvider rowHeaderDataProvider) {
		this.columnHeaderDataProvider= columnHeaderDataProvider;
		this.rowHeaderDataProvider= rowHeaderDataProvider;
	}
	
	@Override
	public long getColumnCount() {
		return this.rowHeaderDataProvider.getColumnCount();
	}
	
	@Override
	public long getRowCount() {
		return this.columnHeaderDataProvider.getRowCount();
	}
	
	@Override
	public Object getDataValue(final long columnIndex, final long rowIndex, final int flags) {
		return ""; //$NON-NLS-1$
	}
	
	@Override
	public void setDataValue(final long columnIndex, final long rowIndex, final Object newValue) {
		throw new UnsupportedOperationException();
	}
	
}
