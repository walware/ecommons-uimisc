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

package de.walware.ecommons.waltable.layer;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import de.walware.ecommons.waltable.data.ISpanningDataProvider;
import de.walware.ecommons.waltable.layer.cell.DataCell;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.layer.cell.LayerCellDim;


public class SpanningDataLayer extends DataLayer {
	
	
	public SpanningDataLayer(final ISpanningDataProvider dataProvider,
			final long columnIdCat, final int defaultColumnWidth,
			final long rowIdCat, final int defaultRowHeight) {
		super(dataProvider,
				columnIdCat, defaultColumnWidth,
				rowIdCat, defaultRowHeight );
	}
	
	
	@Override
	public ISpanningDataProvider getDataProvider() {
		return (ISpanningDataProvider) super.getDataProvider();
	}
	
	@Override
	public ILayerCell getCellByPosition(final long columnPosition, final long rowPosition) {
		final ILayerDim hDim= getDim(HORIZONTAL);
		final ILayerDim vDim= getDim(VERTICAL);
		final long columnId= hDim.getPositionId(columnPosition, columnPosition);
		final long rowId= vDim.getPositionId(rowPosition, rowPosition);
		
		final DataCell dataCell= getDataProvider().getCellByPosition(columnPosition, rowPosition);
		
		return new DataLayerCell(
				new LayerCellDim(HORIZONTAL, columnId, columnPosition,
						dataCell.getColumnPosition(), dataCell.getColumnSpan() ),
				new LayerCellDim(VERTICAL, rowId, rowPosition,
						dataCell.getRowPosition(), dataCell.getRowSpan() ));
	}
	
}
