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
package org.eclipse.nebula.widgets.nattable.group;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;
import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;
import static org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell.NO_INDEX;

import java.util.List;
import java.util.Properties;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel.ColumnGroup;
import org.eclipse.nebula.widgets.nattable.group.config.DefaultColumnGroupHeaderLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.SizeConfig;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCellDim;
import org.eclipse.nebula.widgets.nattable.painter.layer.CellLayerPainter;
import org.eclipse.nebula.widgets.nattable.painter.layer.ILayerPainter;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;

/**
 * Adds the Column grouping functionality to the column headers.
 * Also persists the state of the column groups when {@link NatTable#saveState(String, Properties)} is invoked.
 * 
 * Internally uses the {@link ColumnGroupModel} to track the column groups.
 * <p>
 * See ColumnGroupGridExample
 */
public class ColumnGroupGroupHeaderLayer extends AbstractLayerTransform {

	private final SizeConfig rowHeightConfig = new SizeConfig(DataLayer.DEFAULT_ROW_HEIGHT);
	private final ColumnGroupModel model;
	private final ColumnGroupHeaderLayer columnGroupHeaderLayer;
	
	private final ILayerPainter layerPainter = new CellLayerPainter();

	public ColumnGroupGroupHeaderLayer(ColumnGroupHeaderLayer columnGroupHeaderLayer, SelectionLayer selectionLayer, ColumnGroupModel columnGroupModel) {
		this(columnGroupHeaderLayer, selectionLayer, columnGroupModel, true);
	}

	public ColumnGroupGroupHeaderLayer(ColumnGroupHeaderLayer columnGroupHeaderLayer, SelectionLayer selectionLayer, ColumnGroupModel columnGroupModel, boolean useDefaultConfiguration) {
		super(columnGroupHeaderLayer);
		this.columnGroupHeaderLayer = columnGroupHeaderLayer;
		this.model = columnGroupModel;

		if (useDefaultConfiguration) {
			addConfiguration(new DefaultColumnGroupHeaderLayerConfiguration(columnGroupModel));
		}
	}

	// Configuration

	@Override
	public ILayerPainter getLayerPainter() {
		return layerPainter;
	}

	// Vertical features

	// Rows

	@Override
	public long getRowCount() {
		return columnGroupHeaderLayer.getRowCount() + 1;
	}

	@Override
	public long getPreferredRowCount() {
		return columnGroupHeaderLayer.getPreferredRowCount() + 1;
	}

	@Override
	public long getRowIndexByPosition(long rowPosition) {
		if (rowPosition == 0) {
			return rowPosition;
		} else {
			return columnGroupHeaderLayer.getRowIndexByPosition(rowPosition - 1);
		}
	}

	// Height

	@Override
	public long getHeight() {
		return rowHeightConfig.getAggregateSize(1) + columnGroupHeaderLayer.getHeight();
	}

	@Override
	public long getPreferredHeight() {
		return rowHeightConfig.getAggregateSize(1) + columnGroupHeaderLayer.getPreferredHeight();
	}

	@Override
	public int getRowHeightByPosition(long rowPosition) {
		if (rowPosition == 0) {
			return rowHeightConfig.getSize(rowPosition);
		} else {
			return columnGroupHeaderLayer.getRowHeightByPosition(rowPosition - 1);
		}
	}

	public void setRowHeight(int rowHeight) {
		this.rowHeightConfig.setSize(0, rowHeight);
	}

	// Row resize

	@Override
	public boolean isRowPositionResizable(long rowPosition) {
		if (rowPosition == 0) {
			return rowHeightConfig.isPositionResizable(rowPosition);
		} else {
			return columnGroupHeaderLayer.isRowPositionResizable(rowPosition - 1);
		}
	}

	// Y

	@Override
	public long getRowPositionByY(long y) {
		long row0Height = getRowHeightByPosition(0);
		if (y < row0Height) {
			return 0;
		} else {
			return 1 + columnGroupHeaderLayer.getRowPositionByY(y - row0Height);
		}
	}

	@Override
	public long getStartYOfRowPosition(long rowPosition) {
		if (rowPosition == 0) {
			return rowHeightConfig.getAggregateSize(rowPosition);
		} else {
			return getRowHeightByPosition(0) + columnGroupHeaderLayer.getStartYOfRowPosition(rowPosition - 1);
		}
	}

	// Cell features

	/**
	 * If a cell belongs to a column group:
	 * 	 column position - set to the start position of the group
	 * 	 span - set to the width/size of the column group
	 *
	 * NOTE: gc.setClip() is used in the CompositeLayerPainter to ensure that partially visible
	 * Column group header cells are rendered properly.
	 */
	@Override
	public ILayerCell getCellByPosition(long columnPosition, long rowPosition) {
		long columnIndex = getColumnIndexByPosition(columnPosition);
		String displayMode = getDisplayModeByPosition(columnPosition, rowPosition, columnIndex);

		if (rowPosition == 0) {
			if (model.isPartOfAGroup(columnIndex)) {
				return new LayerCell(this,
						new LayerCellDim(HORIZONTAL, columnIndex,
								columnPosition, getStartPositionOfGroup(columnPosition), getColumnSpan(columnPosition) ),
						new LayerCellDim(VERTICAL, NO_INDEX, rowPosition),
						displayMode );
			} else {
				ILayerCell underlyingCell = columnGroupHeaderLayer.getCellByPosition(columnPosition, rowPosition);
				return new LayerCell(this,
						new LayerCellDim(HORIZONTAL, columnIndex,
								columnPosition, underlyingCell.getOriginColumnPosition(), underlyingCell.getColumnSpan()),
						new LayerCellDim(VERTICAL, NO_INDEX,
								rowPosition, underlyingCell.getOriginRowPosition(), underlyingCell.getRowSpan() + 1),
						displayMode );
			}
		} else if (rowPosition == 1) {
			ILayerCell underlyingCell = columnGroupHeaderLayer.getCellByPosition(columnPosition, rowPosition - 1);
			boolean partOfAGroup = model.isPartOfAGroup(columnIndex);
			return new LayerCell(this,
					new LayerCellDim(HORIZONTAL, columnIndex,
							columnPosition, underlyingCell.getOriginColumnPosition(), underlyingCell.getColumnSpan() ),
					new LayerCellDim(VERTICAL, NO_INDEX,
							rowPosition, underlyingCell.getOriginRowPosition() + (partOfAGroup ? 1 : 0),
									underlyingCell.getRowSpan() + (partOfAGroup ? 0 : 1 )),
					displayMode );
		} else if (rowPosition == 2) {
			ILayerCell underlyingCell = columnGroupHeaderLayer.getCellByPosition(columnPosition, rowPosition - 1);
			boolean partOfAGroup = model.isPartOfAGroup(columnIndex) || columnGroupHeaderLayer.isColumnInGroup(columnIndex);
			return new LayerCell(this,
					new LayerCellDim(HORIZONTAL, columnIndex,
							columnPosition, underlyingCell.getOriginColumnPosition(), underlyingCell.getColumnSpan() ),
					new LayerCellDim(VERTICAL, NO_INDEX,
							rowPosition, underlyingCell.getOriginRowPosition() + (partOfAGroup ? 1 : 0),
									underlyingCell.getRowSpan() + (partOfAGroup ? 0 : 1 )),
					displayMode );
		}
		return null;
	}

	/**
	 * Calculates the span of a cell in a Column Group.
	 * Takes into account collapsing and hidden columns in the group.
	 *
	 * @param columnPosition position of any column belonging to the group
	 */
	protected long getColumnSpan(long columnPosition) {
		long columnIndex = getColumnIndexByPosition(columnPosition);
		ColumnGroup columnGroup = model.getColumnGroupByIndex(columnIndex);

		if (columnGroup.isCollapsed()) {
			return columnGroup.getStaticColumnIndexes().size();
		} else {
			long startPositionOfGroup = getStartPositionOfGroup(columnPosition);
			long sizeOfGroup = columnGroup.getSize();
			long endPositionOfGroup = startPositionOfGroup + sizeOfGroup;
			List<Long> columnIndexesInGroup = columnGroup.getMembers();

			for (long i = startPositionOfGroup; i < endPositionOfGroup; i++) {
				long index = getColumnIndexByPosition(i);
				if (!columnIndexesInGroup.contains(Long.valueOf(index))) {
					sizeOfGroup--;
				}
			}
			return sizeOfGroup;
		}
	}

	/**
	 * Figures out the start position of the group.
	 *
	 * @param selectionLayerColumnPosition of any column belonging to the group
	 * @return first position of the column group
	 */
	private long getStartPositionOfGroup(long columnPosition) {
		long bodyColumnIndex = getColumnIndexByPosition(columnPosition);
		ColumnGroup columnGroup = model.getColumnGroupByIndex(bodyColumnIndex);

		long leastPossibleStartPositionOfGroup = columnPosition - columnGroup.getSize();
		long i = 0;
		for (i = leastPossibleStartPositionOfGroup; i < columnPosition; i++) {
			if (ColumnGroupUtils.isInTheSameGroup(getColumnIndexByPosition(i), bodyColumnIndex, model)) {
				break;
			}
		}
		return i;
	}

	public String getDisplayModeByPosition(long columnPosition, long rowPosition, long columnIndex) {
		if (rowPosition == 0 && model.isPartOfAGroup(columnIndex)) {
			return DisplayMode.NORMAL;
		} else {
			return columnGroupHeaderLayer.getDisplayModeByPosition(columnPosition, rowPosition, columnIndex);
		}
	}

	@Override
	public LabelStack getConfigLabelsByPosition(long columnPosition, long rowPosition) {
		long columnIndex = getColumnIndexByPosition(columnPosition);
		if (rowPosition == 0 && model.isPartOfAGroup(columnIndex)) {
			return new LabelStack(GridRegion.COLUMN_GROUP_HEADER);
		} else {
			return columnGroupHeaderLayer.getConfigLabelsByPosition(columnPosition, rowPosition);
		}
	}

	@Override
	public Object getDataValueByPosition(long columnPosition, long rowPosition) {
		long columnIndex = getColumnIndexByPosition(columnPosition);
		ColumnGroup columnGroup = model.getColumnGroupByIndex(columnIndex);
		
		if (rowPosition == 0) {
			if (model.isPartOfAGroup(columnIndex)) {
				return columnGroup.getName();
			}
		} else {
			rowPosition--;
		}
		
		return columnGroupHeaderLayer.getDataValueByPosition(columnPosition, rowPosition);
	}

	@Override
	public LabelStack getRegionLabelsByXY(long x, long y) {
		long columnIndex = getColumnIndexByPosition(getColumnPositionByX(x));
		if (model.isPartOfAGroup(columnIndex) && y < getRowHeightByPosition(0)) {
			return new LabelStack(GridRegion.COLUMN_GROUP_HEADER);
		} else {
			return columnGroupHeaderLayer.getRegionLabelsByXY(x, y - getRowHeightByPosition(0));
		}
	}

	// ColumnGroupModel delegates

	public void addColumnsIndexesToGroup(String colGroupName, long... colIndexes) {
		model.addColumnsIndexesToGroup(colGroupName, colIndexes);
	}

	public void clearAllGroups(){
		model.clear();
	}

	/**
	 * @see ColumnGroup#setUnbreakable(boolean)
	 */
	public void setGroupUnbreakable(long columnIndex) {
		ColumnGroup columnGroup = model.getColumnGroupByIndex(columnIndex);
		columnGroup.setUnbreakable(true);
	}

	public void setGroupAsCollapsed(long columnIndex) {
		ColumnGroup columnGroup = model.getColumnGroupByIndex(columnIndex);
		columnGroup.setCollapsed(true);
	}
	
}
