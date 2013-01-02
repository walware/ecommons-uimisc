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
// ~
package org.eclipse.nebula.widgets.nattable.coordinate;


public final class IndexCoordinate {
	
	
	public final int columnIndex;
	public final int rowIndex;
	
	
	public IndexCoordinate(final int columnIndex, final int rowIndex) {
		this.columnIndex = columnIndex;
		this.rowIndex = rowIndex;
	}
	
	
	public int getColumnIndex() {
		return columnIndex;
	}
	
	public int getRowIndex() {
		return rowIndex;
	}
	
	
	@Override
	public int hashCode() {
		int h = Integer.rotateRight(columnIndex, 15) + rowIndex * 17 ^ rowIndex & 0xff000000;
		return h ^ (h >>> 7);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof IndexCoordinate)) {
			return false;
		}
		final IndexCoordinate other = (IndexCoordinate) obj;
		return (this.columnIndex == other.columnIndex
				&& this.rowIndex == other.rowIndex );
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + columnIndex + "," + rowIndex + "]";
	}
	
}
