/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.group;

import org.eclipse.nebula.widgets.nattable.grid.layer.DimensionallyDependentIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.layer.ILayerPainter;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.SelectionStyleLabels;


public class NRowGroupHeaderLayer extends DimensionallyDependentIndexLayer {


	private final SelectionLayer selectionLayer;


	public NRowGroupHeaderLayer(IUniqueIndexLayer rowHeaderDataLayer, IUniqueIndexLayer verticalLayerDependency,
			SelectionLayer selectionLayer, boolean useDefaultConfiguration, ILayerPainter layerPainter) {
		super(rowHeaderDataLayer, rowHeaderDataLayer, verticalLayerDependency);
		if (selectionLayer == null) {
			throw new NullPointerException("selectionLayer"); //$NON-NLS-1$
		}

		this.selectionLayer = selectionLayer;
		this.layerPainter = layerPainter;
		
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
		if (selectionLayer.isRowPositionSelected(
				LayerUtil.convertRowPosition(this, rowPosition, selectionLayer) )) {
			return true;
		}
		final ILayerCell cell = getCellByPosition(columnPosition, rowPosition);
		if (cell.isSpannedCell()) {
			int position = cell.getOriginRowPosition();
			int span = cell.getRowSpan();
			for (int i = 0; i < span; i++, position++) {
				if (position != rowPosition && selectionLayer.isRowPositionSelected(
						LayerUtil.convertRowPosition(this, position, selectionLayer) )) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean isFullySelected(int columnPosition, int rowPosition) {
		if (!selectionLayer.isRowPositionFullySelected(
				LayerUtil.convertRowPosition(this, rowPosition, selectionLayer) )) {
			return false;
		}
		final ILayerCell cell = getCellByPosition(columnPosition, rowPosition);
		if (cell.isSpannedCell()) {
			int position = cell.getOriginRowPosition();
			int span = cell.getRowSpan();
			for (int i = 0; i < span; i++, position++) {
				if (position != rowPosition && !selectionLayer.isRowPositionFullySelected(
						LayerUtil.convertRowPosition(this, position, selectionLayer) )) {
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
