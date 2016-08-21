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

import de.walware.ecommons.waltable.data.ISpanningDataProvider;
import de.walware.ecommons.waltable.layer.cell.DataCell;

public class DummySpanningBodyDataProvider extends DummyBodyDataProvider implements ISpanningDataProvider {
	
	private static final long BLOCK_SIZE= 4;

	private static final long CELL_SPAN= 2;
	
	public DummySpanningBodyDataProvider(final long columnCount, final long rowCount) {
		super(columnCount, rowCount);
	}
	
	@Override
	public DataCell getCellByPosition(final long columnPosition, final long rowPosition) {
		final long columnBlock= columnPosition / BLOCK_SIZE;
		final long rowBlock= rowPosition / BLOCK_SIZE;
		
		final boolean isSpanned= isEven(columnBlock + rowBlock) && (columnPosition % BLOCK_SIZE) < CELL_SPAN && (rowPosition % BLOCK_SIZE) < CELL_SPAN;
		final long columnSpan= isSpanned ? CELL_SPAN : 1;
		final long rowSpan= isSpanned ? CELL_SPAN : 1;
		
		long cellColumnPosition= columnPosition;
		long cellRowPosition= rowPosition;
		
		if (isSpanned) {
			cellColumnPosition-= columnPosition % BLOCK_SIZE;
			cellRowPosition-= rowPosition % BLOCK_SIZE;
		}
		
		return new DataCell(cellColumnPosition, cellRowPosition, columnSpan, rowSpan);
	}
	
	private boolean isEven(final long i) {
		return i % 2 == 0;
	}
	
}
