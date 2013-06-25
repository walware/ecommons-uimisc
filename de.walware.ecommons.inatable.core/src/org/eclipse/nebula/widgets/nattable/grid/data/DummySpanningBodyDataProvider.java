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
package org.eclipse.nebula.widgets.nattable.grid.data;

import org.eclipse.nebula.widgets.nattable.data.ISpanningDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.cell.DataCell;

public class DummySpanningBodyDataProvider extends DummyBodyDataProvider implements ISpanningDataProvider {
	
	private static final long BLOCK_SIZE = 4;

	private static final long CELL_SPAN = 2;
	
	public DummySpanningBodyDataProvider(long columnCount, long rowCount) {
		super(columnCount, rowCount);
	}
	
	public DataCell getCellByPosition(long columnPosition, long rowPosition) {
		long columnBlock = columnPosition / BLOCK_SIZE;
		long rowBlock = rowPosition / BLOCK_SIZE;
		
		boolean isSpanned = isEven(columnBlock + rowBlock) && (columnPosition % BLOCK_SIZE) < CELL_SPAN && (rowPosition % BLOCK_SIZE) < CELL_SPAN;
		long columnSpan = isSpanned ? CELL_SPAN : 1;
		long rowSpan = isSpanned ? CELL_SPAN : 1;
		
		long cellColumnPosition = columnPosition;
		long cellRowPosition = rowPosition;
		
		if (isSpanned) {
			cellColumnPosition -= columnPosition % BLOCK_SIZE;
			cellRowPosition -= rowPosition % BLOCK_SIZE;
		}
		
		return new DataCell(cellColumnPosition, cellRowPosition, columnSpan, rowSpan);
	}
	
	private boolean isEven(long i) {
		return i % 2 == 0;
	}
	
}
