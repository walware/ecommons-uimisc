/*******************************************************************************
 * Copyright (c) 2012-2013 Original authors and others.
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

	protected int columnPosition;
	
	protected int rowPosition;
	
	protected int columnSpan;
	
	protected int rowSpan;
	
	public DataCell(int columnPosition, int rowPosition) {
		this(columnPosition, rowPosition, 1, 1);
	}	
	
	public DataCell(int columnPosition, int rowPosition, int columnSpan, int rowSpan) {
		this.columnPosition = columnPosition;
		this.rowPosition = rowPosition;
		this.columnSpan = columnSpan;
		this.rowSpan = rowSpan;
	}
	
	public int getColumnPosition() {
		return columnPosition;
	}
	
	public int getRowPosition() {
		return rowPosition;
	}
	
	public int getColumnSpan() {
		return columnSpan;
	}
	
	public int getRowSpan() {
		return rowSpan;
	}
	
	public boolean isSpannedCell() {
		return columnSpan > 1 || rowSpan > 1;
	}
	
	
	@Override
	public int hashCode() {
		return (((columnPosition * 253 + (rowPosition << 16) + rowPosition)
				* 13 + columnSpan)
				* 14 + rowSpan);
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
