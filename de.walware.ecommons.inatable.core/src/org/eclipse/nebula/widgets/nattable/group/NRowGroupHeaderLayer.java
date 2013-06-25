/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.group;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;

import org.eclipse.nebula.widgets.nattable.grid.layer.DimensionallyDependentIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerDim;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCellDim;
import org.eclipse.nebula.widgets.nattable.painter.layer.ILayerPainter;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.SelectionStyleLabels;


public class NRowGroupHeaderLayer extends DimensionallyDependentIndexLayer {
	
	
	private final SelectionLayer selectionLayer;
	
	
	public NRowGroupHeaderLayer(final IUniqueIndexLayer rowHeaderDataLayer, final ILayer verticalLayerDependency,
			final SelectionLayer selectionLayer, final boolean useDefaultConfiguration, final ILayerPainter layerPainter) {
		super(rowHeaderDataLayer, rowHeaderDataLayer, verticalLayerDependency);
		if (selectionLayer == null) {
			throw new NullPointerException("selectionLayer"); //$NON-NLS-1$
		}
		
		this.selectionLayer = selectionLayer;
		this.layerPainter = layerPainter;
		
		registerCommandHandlers();
	}
	
	
	@Override
	protected String getDisplayMode(final LayerCellDim hDim, final LayerCellDim vDim,
			final String displayMode) {
		return (isSelected(hDim, vDim)) ? DisplayMode.SELECT : displayMode;
	}
	
	@Override
	public LabelStack getConfigLabelsByPosition(final int columnPosition, final int rowPosition) {
		final LabelStack labelStack = super.getConfigLabelsByPosition(columnPosition, rowPosition);
		
		if (isFullySelected(columnPosition, rowPosition)) {
			labelStack.addLabel(SelectionStyleLabels.ROW_FULLY_SELECTED_STYLE);
		}
		
		return labelStack;
	}
	
	protected boolean isSelected(final LayerCellDim hDim, final LayerCellDim vDim) {
		final ILayerDim dim = getDim(VERTICAL);
		final int rowPosition = vDim.getPosition();
		if (this.selectionLayer.isRowPositionSelected(
				LayerUtil.convertPosition(dim, rowPosition, rowPosition, this.selectionLayer) )) {
			return true;
		}
		if (vDim.getPositionSpan() > 1) {
			int position = vDim.getOriginPosition();
			final int span = vDim.getPositionSpan();
			for (int i = 0; i < span; i++, position++) {
				if (position != rowPosition && this.selectionLayer.isRowPositionSelected(
						LayerUtil.convertPosition(dim, rowPosition, position, this.selectionLayer) )) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected boolean isFullySelected(final int columnPosition, final int rowPosition) {
		final ILayerDim dim = getDim(VERTICAL);
		if (!this.selectionLayer.isRowPositionFullySelected(
				LayerUtil.convertPosition(dim, rowPosition, rowPosition, this.selectionLayer) )) {
			return false;
		}
		final ILayerCell cell = getCellByPosition(columnPosition, rowPosition);
		if (cell.isSpannedCell()) {
			int position = cell.getOriginRowPosition();
			final int span = cell.getRowSpan();
			for (int i = 0; i < span; i++, position++) {
				if (position != rowPosition && !this.selectionLayer.isRowPositionFullySelected(
						LayerUtil.convertPosition(dim, rowPosition, position, this.selectionLayer) )) {
					return false;
				}
			}
		}
		return true;
	}
	
	public SelectionLayer getSelectionLayer() {
		return this.selectionLayer;
	}
	
}
