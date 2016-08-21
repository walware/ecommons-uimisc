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
// -depend
package de.walware.ecommons.waltable.layer.cell;


public class DataCell {

	protected long columnPosition;
	
	protected long rowPosition;
	
	protected long columnSpan;
	
	protected long rowSpan;
	
	public DataCell(final long columnPosition, final long rowPosition) {
		this(columnPosition, rowPosition, 1, 1);
	}	
	
	public DataCell(final long columnPosition, final long rowPosition, final long columnSpan, final long rowSpan) {
		this.columnPosition= columnPosition;
		this.rowPosition= rowPosition;
		this.columnSpan= columnSpan;
		this.rowSpan= rowSpan;
	}
	
	public long getColumnPosition() {
		return this.columnPosition;
	}
	
	public long getRowPosition() {
		return this.rowPosition;
	}
	
	public long getColumnSpan() {
		return this.columnSpan;
	}
	
	public long getRowSpan() {
		return this.rowSpan;
	}
	
	public boolean isSpannedCell() {
		return this.columnSpan > 1 || this.rowSpan > 1;
	}
	
	
	@Override
	public int hashCode() {
		int h= (int) (this.columnPosition ^ (this.columnPosition >>> 32));
		h= Integer.rotateRight(h, 15);
		h ^= (int) (this.rowPosition ^ (this.rowPosition >>> 32));
		h ^= (h >>> 7);
		h ^= 3 * (int) (this.columnSpan ^ (this.columnSpan >>> 32));
		h ^= 31 * (int) (this.rowSpan ^ (this.rowSpan >>> 32));
		return h;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DataCell)) {
			return false;
		}
		final DataCell other= (DataCell) obj;
		return (this.columnPosition == other.columnPosition
				&& this.rowPosition == other.rowPosition
				&& this.columnSpan == other.columnSpan
				&& this.rowSpan == other.rowSpan);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " (" //$NON-NLS-1$
			+ "\n\tcolumnPosition= " + this.columnPosition //$NON-NLS-1$
			+ "\n\trowPosition= " + this.rowPosition //$NON-NLS-1$
			+ "\n\tcolumnSpan= " + this.columnSpan //$NON-NLS-1$
			+ "\n\trowSpan= " + this.rowSpan //$NON-NLS-1$
			+ "\n)"; //$NON-NLS-1$
	}
	
}
