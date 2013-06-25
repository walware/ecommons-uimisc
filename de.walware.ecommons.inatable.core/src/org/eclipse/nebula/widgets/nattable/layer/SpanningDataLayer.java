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

package org.eclipse.nebula.widgets.nattable.layer;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;
import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;

import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.data.ISpanningDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.cell.DataCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCellDim;


public class SpanningDataLayer extends DataLayer {
	
	public SpanningDataLayer(ISpanningDataProvider dataProvider) {
		super(dataProvider);
	}
	
	public SpanningDataLayer(ISpanningDataProvider dataProvider, int defaultColumnWidth, int defaultRowHeight) {
		super(dataProvider, defaultColumnWidth, defaultRowHeight);
	}
	
	public SpanningDataLayer(ISpanningDataProvider dataProvider,
			int defaultColumnWidth, int defaultRowHeight,
			boolean enableColumnIndex, boolean enableRowIndex) {
		super(dataProvider,
				defaultColumnWidth, defaultRowHeight,
				enableColumnIndex, enableRowIndex );
	}
	
	
	@Override
	public ISpanningDataProvider getDataProvider() {
		return (ISpanningDataProvider) super.getDataProvider();
	}
	
	@Override
	public ILayerCell getCellByPosition(long columnPosition, long rowPosition) {
		if (columnPosition < 0 || columnPosition >= getColumnCount()
				|| rowPosition < 0 || rowPosition >= getRowCount()) {
			return null;
		}
		
		DataCell dataCell = getDataProvider().getCellByPosition(columnPosition, rowPosition);
		
		return new LayerCell(this,
				new LayerCellDim(HORIZONTAL, getColumnIndexByPosition(columnPosition),
						columnPosition,
						dataCell.getColumnPosition(), dataCell.getColumnSpan() ),
				new LayerCellDim(VERTICAL, getRowIndexByPosition(rowPosition),
						rowPosition,
						dataCell.getRowPosition(), dataCell.getRowSpan() ));
	}
	
	@Override
	public Rectangle getBoundsByPosition(long columnPosition, long rowPosition) {
		ILayerCell cell = getCellByPosition(columnPosition, rowPosition);
		return super.getBoundsByPosition(cell.getOriginColumnPosition(), cell.getOriginRowPosition());
	}
	
}
