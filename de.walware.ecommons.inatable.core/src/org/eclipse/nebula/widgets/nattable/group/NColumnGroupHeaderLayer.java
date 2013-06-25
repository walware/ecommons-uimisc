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

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;

import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderSelectionListener;
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


public class NColumnGroupHeaderLayer extends DimensionallyDependentIndexLayer {
	
	
	private final SelectionLayer selectionLayer;
	
	
	public NColumnGroupHeaderLayer(final IUniqueIndexLayer columnHeaderDataLayer, final ILayer horizontalLayerDependency,
			final SelectionLayer selectionLayer, final boolean useDefaultConfiguration, final ILayerPainter layerPainter) {
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
	protected String getDisplayMode(final LayerCellDim hDim, final LayerCellDim vDim,
			final String displayMode) {
		return (isSelected(hDim, vDim)) ? DisplayMode.SELECT : displayMode;
	}
	
	@Override
	public LabelStack getConfigLabelsByPosition(final long columnPosition, final long rowPosition) {
		final LabelStack labelStack = super.getConfigLabelsByPosition(columnPosition, rowPosition);
		
		if (isFullySelected(columnPosition, rowPosition)) {
			labelStack.addLabel(SelectionStyleLabels.COLUMN_FULLY_SELECTED_STYLE);
		}
		
		return labelStack;
	}
	
	protected boolean isSelected(final LayerCellDim hDim, final LayerCellDim vDim) {
		final ILayerDim dim = getDim(HORIZONTAL);
		final long columnPosition = hDim.getPosition();
		if (this.selectionLayer.isColumnPositionSelected(
				LayerUtil.convertPosition(dim, columnPosition, columnPosition, this.selectionLayer) )) {
			return true;
		}
		if (hDim.getPositionSpan() > 1) {
			long position = hDim.getOriginPosition();
			final long span = hDim.getPositionSpan();
			for (long i = 0; i < span; i++, position++) {
				if (position != columnPosition && this.selectionLayer.isColumnPositionSelected(
						LayerUtil.convertPosition(dim, columnPosition, position, this.selectionLayer) )) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected boolean isFullySelected(final long columnPosition, final long rowPosition) {
		final ILayerDim dim = getDim(HORIZONTAL);
		if (!this.selectionLayer.isColumnPositionFullySelected(
				LayerUtil.convertPosition(dim, columnPosition, columnPosition, this.selectionLayer) )) {
			return false;
		}
		final ILayerCell cell = getCellByPosition(columnPosition, rowPosition);
		if (cell.isSpannedCell()) {
			long position = cell.getOriginColumnPosition();
			final long span = cell.getColumnSpan();
			for (long i = 0; i < span; i++, position++) {
				if (position != columnPosition && !this.selectionLayer.isColumnPositionFullySelected(
						LayerUtil.convertPosition(dim, columnPosition, position, this.selectionLayer) )) {
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
