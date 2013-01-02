/*******************************************************************************
 * Copyright (c) 2012-2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.group;

import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderSelectionListener;
import org.eclipse.nebula.widgets.nattable.grid.layer.DimensionallyDependentIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.layer.ILayerPainter;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.SelectionStyleLabels;


public class NColumnGroupHeaderLayer extends DimensionallyDependentIndexLayer {


	private final SelectionLayer selectionLayer;


	public NColumnGroupHeaderLayer(IUniqueIndexLayer columnHeaderDataLayer, IUniqueIndexLayer horizontalLayerDependency,
			SelectionLayer selectionLayer, boolean useDefaultConfiguration, ILayerPainter layerPainter) {
		super(columnHeaderDataLayer, horizontalLayerDependency, columnHeaderDataLayer);
		if (selectionLayer == null) {
			throw new NullPointerException("selectionLayer"); //$NON-NLS-1$
		}

		this.selectionLayer = selectionLayer;
		this.layerPainter = layerPainter;
		
		selectionLayer.addLayerListener(new ColumnHeaderSelectionListener(this));
		registerCommandHandlers();
	}


	@Override
	public String getDisplayModeByPosition(int columnPosition, int rowPosition) {
		if (isSelected(columnPosition, rowPosition)) {
			return DisplayMode.SELECT;
		} else {
			return super.getDisplayModeByPosition(columnPosition, rowPosition);
		}
	}

	@Override
	public LabelStack getConfigLabelsByPosition(int columnPosition, int rowPosition) {
		LabelStack labelStack = super.getConfigLabelsByPosition(columnPosition, rowPosition);
		
		if (isFullySelected(columnPosition, rowPosition)) {
			labelStack.addLabel(SelectionStyleLabels.COLUMN_FULLY_SELECTED_STYLE);
		}
		
		return labelStack;
	}

	protected boolean isSelected(int columnPosition, int rowPosition) {
		if (selectionLayer.isColumnPositionSelected(
				LayerUtil.convertColumnPosition(this, columnPosition, selectionLayer) )) {
			return true;
		}
		final ILayerCell cell = getCellByPosition(columnPosition, rowPosition);
		if (cell.isSpannedCell()) {
			int position = cell.getOriginColumnPosition();
			int span = cell.getColumnSpan();
			for (int i = 0; i < span; i++, position++) {
				if (position != columnPosition && selectionLayer.isColumnPositionSelected(
						LayerUtil.convertColumnPosition(this, position, selectionLayer) )) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected boolean isFullySelected(int columnPosition, int rowPosition) {
		if (!selectionLayer.isColumnPositionFullySelected(
				LayerUtil.convertColumnPosition(this, columnPosition, selectionLayer) )) {
			return false;
		}
		final ILayerCell cell = getCellByPosition(columnPosition, rowPosition);
		if (cell.isSpannedCell()) {
			int position = cell.getOriginColumnPosition();
			int span = cell.getColumnSpan();
			for (int i = 0; i < span; i++, position++) {
				if (position != columnPosition && !selectionLayer.isColumnPositionFullySelected(
						LayerUtil.convertColumnPosition(this, position, selectionLayer) )) {
					return false;
				}
			}
		}
		return true;
	}

	public SelectionLayer getSelectionLayer() {
		return selectionLayer;
	}

}
