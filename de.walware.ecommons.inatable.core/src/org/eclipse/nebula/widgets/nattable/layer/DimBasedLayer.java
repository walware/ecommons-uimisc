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

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.coordinate.Range;


public abstract class DimBasedLayer extends AbstractLayer {
	
	
	protected DimBasedLayer() {
	}
	
	
	@Override
	protected abstract void updateDims();
	
	
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
		return super.getDim(HORIZONTAL).getPreferredPositionCount();
	}
	
	@Override
	public final long localToUnderlyingColumnPosition(final long localColumnPosition) {
		return LayerUtil.localToUnderlyingPosition(super.getDim(HORIZONTAL), localColumnPosition);
	}
	
	@Override
	public final long underlyingToLocalColumnPosition(final ILayer sourceUnderlyingLayer,
			final long underlyingColumnPosition) {
		return super.getDim(HORIZONTAL).underlyingToLocalPosition(sourceUnderlyingLayer,
				underlyingColumnPosition );
	}
	
	@Override
	public final Collection<Range> underlyingToLocalColumnPositions(final ILayer sourceUnderlyingLayer,
			final Collection<Range> underlyingColumnPositionRanges) {
		return super.getDim(HORIZONTAL).underlyingToLocalPositions(sourceUnderlyingLayer,
				underlyingColumnPositionRanges );
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
		return super.getDim(HORIZONTAL).getUnderlyingLayersByPosition(columnPosition);
	}
	
	
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
		return super.getDim(VERTICAL).getPreferredPositionCount();
	}
	
	@Override
	public final long localToUnderlyingRowPosition(final long localRowPosition) {
		return LayerUtil.localToUnderlyingPosition(super.getDim(VERTICAL), localRowPosition);
	}
	
	@Override
	public final long underlyingToLocalRowPosition(final ILayer sourceUnderlyingLayer,
			final long underlyingRowPosition) {
		return super.getDim(VERTICAL).underlyingToLocalPosition(sourceUnderlyingLayer,
				underlyingRowPosition );
	}
	
	@Override
	public final Collection<Range> underlyingToLocalRowPositions(final ILayer sourceUnderlyingLayer,
			final Collection<Range> underlyingRowPositionRanges) {
		return super.getDim(VERTICAL).underlyingToLocalPositions(sourceUnderlyingLayer,
				underlyingRowPositionRanges );
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
		return super.getDim(VERTICAL).getUnderlyingLayersByPosition(rowPosition);
	}
	
}
