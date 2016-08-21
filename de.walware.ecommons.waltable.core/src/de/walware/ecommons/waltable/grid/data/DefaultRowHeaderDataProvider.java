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

import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.ecommons.waltable.data.IDataProvider;


public class DefaultRowHeaderDataProvider implements IDataProvider {
	
	
	protected final IDataProvider bodyDataProvider;
	
	
	public DefaultRowHeaderDataProvider(final IDataProvider bodyDataProvider) {
		this.bodyDataProvider= bodyDataProvider;
	}
	
	
	@Override
	public long getColumnCount() {
		return 1;
	}
	
	@Override
	public long getRowCount() {
		return this.bodyDataProvider.getRowCount();
	}
	
	@Override
	public Object getDataValue(final long columnIndex, final long rowIndex, final int flags, final IProgressMonitor monitor) {
		return Long.valueOf(rowIndex + 1);
	}
	
	@Override
	public void setDataValue(final long columnIndex, final long rowIndex, final Object newValue) {
		throw new UnsupportedOperationException();
	}
	
}
