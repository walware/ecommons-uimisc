/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// -depend
package org.eclipse.nebula.widgets.nattable.layer.cell;


public class DataCell {

	protected long columnPosition;
	
	protected long rowPosition;
	
	protected long columnSpan;
	
	protected long rowSpan;
	
	public DataCell(long columnPosition, long rowPosition) {
		this(columnPosition, rowPosition, 1, 1);
	}	
	
	public DataCell(long columnPosition, long rowPosition, long columnSpan, long rowSpan) {
		this.columnPosition = columnPosition;
		this.rowPosition = rowPosition;
		this.columnSpan = columnSpan;
		this.rowSpan = rowSpan;
	}
	
	public long getColumnPosition() {
		return columnPosition;
	}
	
	public long getRowPosition() {
		return rowPosition;
	}
	
	public long getColumnSpan() {
		return columnSpan;
	}
	
	public long getRowSpan() {
		return rowSpan;
	}
	
	public boolean isSpannedCell() {
		return columnSpan > 1 || rowSpan > 1;
	}
	
	
	@Override
	public int hashCode() {
		int h = (int) (columnPosition ^ (columnPosition >>> 32));
		h = Integer.rotateRight(h, 15);
		h ^= (int) (rowPosition ^ (rowPosition >>> 32));
		h ^= (h >>> 7);
		h ^= 3 * (int) (columnSpan ^ (columnSpan >>> 32));
		h ^= 31 * (int) (rowSpan ^ (rowSpan >>> 32));
		return h;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DataCell)) {
			return false;
		}
		final DataCell other = (DataCell) obj;
		return (columnPosition == other.columnPosition
				&& rowPosition == other.rowPosition
				&& columnSpan == other.columnSpan
				&& rowSpan == other.rowSpan);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " (" //$NON-NLS-1$
			+ "\n\tcolumnPosition= " + columnPosition //$NON-NLS-1$
			+ "\n\trowPosition= " + rowPosition //$NON-NLS-1$
			+ "\n\tcolumnSpan= " + columnSpan //$NON-NLS-1$
			+ "\n\trowSpan= " + rowSpan //$NON-NLS-1$
			+ "\n)"; //$NON-NLS-1$
	}
	
}
