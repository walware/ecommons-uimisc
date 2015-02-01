/*******************************************************************************
 * Copyright (c) 2012-2015 Edwin Park and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Edwin Park - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.layer.cell;

import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;


public class InvertedLayerCell implements ILayerCell {

	private final ILayerCell layerCell;

	public InvertedLayerCell(ILayerCell layerCell) {
		this.layerCell = layerCell;
	}

	public long getOriginColumnPosition() {
		return layerCell.getOriginRowPosition();
	}

	public long getOriginRowPosition() {
		return layerCell.getOriginColumnPosition();
	}

	public ILayer getLayer() {
		return layerCell.getLayer();
	}
	
	@Override
	public LayerCellDim getDim(Orientation orientation) {
		return layerCell.getDim(orientation.getOrthogonal());
	}

	public long getColumnPosition() {
		return layerCell.getRowPosition();
	}

	public long getRowPosition() {
		return layerCell.getColumnPosition();
	}

	public long getColumnIndex() {
		return layerCell.getRowIndex();
	}

	public long getRowIndex() {
		return layerCell.getColumnIndex();
	}

	public long getColumnSpan() {
		return layerCell.getRowSpan();
	}

	public long getRowSpan() {
		return layerCell.getColumnSpan();
	}

	public boolean isSpannedCell() {
		return layerCell.isSpannedCell();
	}

	public String getDisplayMode() {
		return layerCell.getDisplayMode();
	}

	public LabelStack getConfigLabels() {
		return layerCell.getConfigLabels();
	}

	public Object getDataValue() {
		return layerCell.getDataValue();
	}

	public Rectangle getBounds() {
		return layerCell.getBounds().switchOrientation();
	}
	
}
