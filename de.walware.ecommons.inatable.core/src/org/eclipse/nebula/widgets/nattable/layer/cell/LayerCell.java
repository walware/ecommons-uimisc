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

import org.eclipse.nebula.widgets.nattable.layer.ILayer;

public class LayerCell extends AbstractLayerCell {
	
	private ILayer layer;

	private int columnPosition;
	private int rowPosition;

	private int originColumnPosition;
	private int originRowPosition;
	
	private int columnSpan;
	private int rowSpan;

	public LayerCell(ILayer layer, int columnPosition, int rowPosition, DataCell cell) {
		this(layer, cell.columnPosition, cell.rowPosition, columnPosition, rowPosition, cell.columnSpan, cell.rowSpan);
	}

	public LayerCell(ILayer layer, int columnPosition, int rowPosition) {
		this(layer, columnPosition, rowPosition, columnPosition, rowPosition, 1, 1);
	}

	public LayerCell(ILayer layer, int originColumnPosition, int originRowPosition, int columnPosition, int rowPosition, int columnSpan, int rowSpan) {
		if (layer == null) {
			throw new NullPointerException();
		}
		this.layer = layer;
		
		this.originColumnPosition = originColumnPosition;
		this.originRowPosition = originRowPosition;
		
		this.columnPosition = columnPosition;
		this.rowPosition = rowPosition;
		
		this.columnSpan = columnSpan;
		this.rowSpan = rowSpan;
	}

	public int getOriginColumnPosition() {
		return originColumnPosition;
	}
	
	public int getOriginRowPosition() {
		return originRowPosition;
	}
	
	public ILayer getLayer() {
		return layer;
	}
	
	public int getColumnPosition() {
		return columnPosition;
	}
	
	public int getRowPosition() {
		return rowPosition;
	}
	
	public int getColumnIndex() {
		return getLayer().getColumnIndexByPosition(getColumnPosition());
	}
	
	public int getRowIndex() {
		return getLayer().getRowIndexByPosition(getRowPosition());
	}
	
	public int getColumnSpan() {
		return columnSpan;
	}
	
	public int getRowSpan() {
		return rowSpan;
	}


	@Override
	public int hashCode() {
		return ((layer.hashCode()
				* (originColumnPosition * 253 + (originRowPosition << 16) + originRowPosition)
				* columnSpan + 13)
				* rowSpan + 14);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof LayerCell)) {
			return false;
		}
		final LayerCell other = (LayerCell) obj;
		return (layer.equals(other.layer)
				&& originColumnPosition == other.originColumnPosition
				&& originRowPosition == other.originRowPosition
				&& columnSpan == other.columnSpan
				&& rowSpan == other.rowSpan);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " (" //$NON-NLS-1$
			+ "\n\tdata= " + getDataValue() //$NON-NLS-1$
			+ "\n\tlayer= " + getLayer().getClass().getSimpleName() //$NON-NLS-1$
			+ "\n\toriginColumnPosition= " + originColumnPosition //$NON-NLS-1$
			+ "\n\toriginRowPosition= " + originRowPosition //$NON-NLS-1$
			+ "\n\tcolumnSpan= " + columnSpan //$NON-NLS-1$
			+ "\n\trowSpan= " + rowSpan //$NON-NLS-1$
			+ "\n)"; //$NON-NLS-1$
	}
	
}
