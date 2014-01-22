/*******************************************************************************
 * Copyright (c) 2013 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.layer;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;
import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;
import static org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell.NO_INDEX;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.coordinate.Range;


/**
 * This abstract layer can be used if a layer implementation bases on implementation of layer
 * dimensions.
 */
public abstract class DimBasedLayer extends AbstractLayer {
	
	
	private static long computePreferredPositionCount(final ILayerDim dim) {
		final long preferredSize = dim.getPreferredSize();
		long position = 0;
		long size = 0;
		try {
			while (size < preferredSize) {
				size += dim.getPositionSize(position, position++);
			}
		}
		catch (final Exception e) {}
		return position;
	}
	
	private static List<ILayer> convertDim2LayerList(final Collection<ILayerDim> dims) {
		if (dims == null) {
			return null;
		}
		switch (dims.size()) {
		case 0:
			return Collections.emptyList();
		case 1:
			return Collections.singletonList(dims.iterator().next().getLayer());
		default:
			final List<ILayer> layers = new ArrayList<ILayer>(dims.size());
			for (final ILayerDim underlyingDim : dims) {
				layers.add(underlyingDim.getLayer());
			}
			return layers;
		}
	}
	
	
	protected DimBasedLayer() {
	}
	
	
	@Override
	protected abstract void updateDims();
	
	
	@Override
	public long getColumnIndexByPosition(long columnPosition) {
		if (columnPosition < 0 || columnPosition >= getColumnCount()) {
			return NO_INDEX;
		}
		return super.getDim(HORIZONTAL).getPositionIndex(columnPosition, columnPosition);
	}
	
	
	@Override
	public final long getColumnCount() {
		return super.getDim(HORIZONTAL).getPositionCount();
	}
	
	@Override
	public final long getPreferredColumnCount() {
		return computePreferredPositionCount(super.getDim(HORIZONTAL));
	}
	
	@Override
	public final long localToUnderlyingColumnPosition(final long localColumnPosition) {
		return LayerUtil.localToUnderlyingPosition(super.getDim(HORIZONTAL), localColumnPosition);
	}
	
	@Override
	public final long underlyingToLocalColumnPosition(final ILayer sourceUnderlyingLayer,
			final long underlyingColumnPosition) {
		return super.getDim(HORIZONTAL).underlyingToLocalPosition(
				sourceUnderlyingLayer.getDim(HORIZONTAL), underlyingColumnPosition );
	}
	
	@Override
	public final Collection<Range> underlyingToLocalColumnPositions(final ILayer sourceUnderlyingLayer,
			final Collection<Range> underlyingColumnPositions) {
		return super.getDim(HORIZONTAL).underlyingToLocalPositions(
				sourceUnderlyingLayer.getDim(HORIZONTAL), underlyingColumnPositions );
	}
	
	@Override
	public final long getWidth() {
		return super.getDim(HORIZONTAL).getSize();
	}
	
	@Override
	public final long getPreferredWidth() {
		return super.getDim(HORIZONTAL).getPreferredSize();
	}
	
	@Override
	public final int getColumnWidthByPosition(final long columnPosition) {
		return super.getDim(HORIZONTAL).getPositionSize(columnPosition, columnPosition);
	}
	
	@Override
	public final boolean isColumnPositionResizable(final long columnPosition) {
		return super.getDim(HORIZONTAL).isPositionResizable(columnPosition);
	}
	
	@Override
	public final long getColumnPositionByX(final long x) {
		return super.getDim(HORIZONTAL).getPositionByPixel(x);
	}
	
	@Override
	public final long getStartXOfColumnPosition(final long columnPosition) {
		return super.getDim(HORIZONTAL).getPositionStart(columnPosition, columnPosition);
	}
	
	@Override
	public final Collection<ILayer> getUnderlyingLayersByColumnPosition(
			final long columnPosition) {
		return convertDim2LayerList(super.getDim(HORIZONTAL).getUnderlyingDimsByPosition(columnPosition));
	}
	
	
	@Override
	public final long getRowIndexByPosition(long rowPosition) {
		if (rowPosition < 0 || rowPosition >= getRowCount()) {
			return NO_INDEX;
		}
		return super.getDim(VERTICAL).getPositionIndex(rowPosition, rowPosition);
	}
	
	
	@Override
	public final long getRowCount() {
		return super.getDim(VERTICAL).getPositionCount();
	}
	
	@Override
	public final long getPreferredRowCount() {
		return computePreferredPositionCount(super.getDim(VERTICAL));
	}
	
	@Override
	public final long localToUnderlyingRowPosition(final long localRowPosition) {
		return LayerUtil.localToUnderlyingPosition(super.getDim(VERTICAL), localRowPosition);
	}
	
	@Override
	public final long underlyingToLocalRowPosition(final ILayer sourceUnderlyingLayer,
			final long underlyingRowPosition) {
		return super.getDim(VERTICAL).underlyingToLocalPosition(
				sourceUnderlyingLayer.getDim(VERTICAL), underlyingRowPosition );
	}
	
	@Override
	public final List<Range> underlyingToLocalRowPositions(final ILayer sourceUnderlyingLayer,
			final Collection<Range> underlyingRowPositions) {
		return super.getDim(VERTICAL).underlyingToLocalPositions(
				sourceUnderlyingLayer.getDim(VERTICAL), underlyingRowPositions );
	}
	
	@Override
	public final long getHeight() {
		return super.getDim(VERTICAL).getSize();
	}
	
	@Override
	public final long getPreferredHeight() {
		return super.getDim(VERTICAL).getPreferredSize();
	}
	
	@Override
	public final int getRowHeightByPosition(final long rowPosition) {
		return super.getDim(VERTICAL).getPositionSize(rowPosition, rowPosition);
	}
	
	@Override
	public final boolean isRowPositionResizable(final long rowPosition) {
		return super.getDim(VERTICAL).isPositionResizable(rowPosition);
	}
	
	@Override
	public final long getRowPositionByY(final long y) {
		return super.getDim(VERTICAL).getPositionByPixel(y);
	}
	
	@Override
	public final long getStartYOfRowPosition(final long rowPosition) {
		return super.getDim(VERTICAL).getPositionStart(rowPosition, rowPosition);
	}
	
	@Override
	public final Collection<ILayer> getUnderlyingLayersByRowPosition(final long rowPosition) {
		return convertDim2LayerList(super.getDim(VERTICAL).getUnderlyingDimsByPosition(rowPosition));
	}
	
}
