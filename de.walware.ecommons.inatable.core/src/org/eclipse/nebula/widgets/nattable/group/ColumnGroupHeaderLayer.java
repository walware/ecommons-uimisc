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
import org.eclipse.nebula.widgets.nattable.group.command.ColumnGroupsCommandHandler;
import org.eclipse.nebula.widgets.nattable.group.config.DefaultColumnGroupHeaderLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.SizeConfig;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCellDim;
import org.eclipse.nebula.widgets.nattable.layer.event.ColumnStructuralRefreshEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.RowStructuralRefreshEvent;
import org.eclipse.nebula.widgets.nattable.painter.layer.CellLayerPainter;
import org.eclipse.nebula.widgets.nattable.painter.layer.ILayerPainter;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;


/**
 * Adds the Column grouping functionality to the column headers.
 * Also persists the state of the column groups when {@link NatTable#saveState(String, Properties)} is invoked.
 * 
 * Internally uses the {@link ColumnGroupModel} to track the column groups.
 * See ColumnGroupGridExample
 */
public class ColumnGroupHeaderLayer extends AbstractLayerTransform {

	private final SizeConfig rowHeightConfig = new SizeConfig(DataLayer.DEFAULT_ROW_HEIGHT);
	private final ColumnGroupModel model;
	private final ILayer columnHeaderLayer;
	private final ILayerPainter layerPainter = new CellLayerPainter();
	
	/**
	 * Flag which is used to tell the ColumnGroupHeaderLayer whether to calculate the height of the layer
	 * dependent on column group configuration or not. If it is set to <code>true</code> the column header
	 * will check if column groups are configured and if not, the height of the column header will not
	 * show the double height for showing column groups.
	 */
	private boolean calculateHeight = false;
	
	/**
	 * Listener that will fire a RowStructuralRefreshEvent in case the ColumnGroupModel changes.
	 * Is only needed in case the dynamic height calculation is enabled.
	 */
	private IColumnGroupModelListener modelChangeListener;

	public ColumnGroupHeaderLayer(ILayer columnHeaderLayer, SelectionLayer selectionLayer, ColumnGroupModel columnGroupModel) {
		this(columnHeaderLayer, selectionLayer, columnGroupModel, true);
	}

	public ColumnGroupHeaderLayer(final ILayer columnHeaderLayer, SelectionLayer selectionLayer, ColumnGroupModel columnGroupModel, boolean useDefaultConfiguration) {
		super(columnHeaderLayer);
		
		this.columnHeaderLayer = columnHeaderLayer;
		this.model = columnGroupModel;
		
		registerCommandHandler(new ColumnGroupsCommandHandler(model, selectionLayer, this));

		if (useDefaultConfiguration) {
			addConfiguration(new DefaultColumnGroupHeaderLayerConfiguration(columnGroupModel));
		}

		modelChangeListener = new IColumnGroupModelListener() { 
			public void columnGroupModelChanged() { 
				fireLayerEvent(new RowStructuralRefreshEvent(columnHeaderLayer)); 
			} 
		};
		
		this.model.registerColumnGroupModelListener(modelChangeListener);
	}

	// Persistence

	@Override
	public void saveState(String prefix, Properties properties) {
		super.saveState(prefix, properties);
		model.saveState(prefix, properties);
	}

	@Override
	public void loadState(String prefix, Properties properties) {
		super.loadState(prefix, properties);
		model.loadState(prefix, properties);
		fireLayerEvent(new ColumnStructuralRefreshEvent(this));
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
		if (!calculateHeight 
				|| (this.model.getAllIndexesInGroups() != null 
						&& this.model.getAllIndexesInGroups().size() > 0)) { 
			return columnHeaderLayer.getRowCount() + 1;
		}
		return columnHeaderLayer.getRowCount();
	}

	@Override
	public long getPreferredRowCount() {
		return columnHeaderLayer.getPreferredRowCount() + 1;
	}

	@Override
	public long getRowIndexByPosition(long rowPosition) {
		if (rowPosition == 0) {
			return rowPosition;
		} else {
			return columnHeaderLayer.getRowIndexByPosition(rowPosition - 1);
		}
	}

	// Height

	@Override 
	public long getHeight() { 
		if (!calculateHeight 
				|| (this.model.getAllIndexesInGroups() != null 
						&& this.model.getAllIndexesInGroups().size() > 0)) { 
			return rowHeightConfig.getAggregateSize(1) + columnHeaderLayer.getHeight();
		} 
		return columnHeaderLayer.getHeight(); 
	} 
	

	
	@Override
	public long getPreferredHeight() {
		return rowHeightConfig.getAggregateSize(1) + columnHeaderLayer.getPreferredHeight();
	}

	@Override
	public int getRowHeightByPosition(long rowPosition) {
		if (rowPosition == 0) {
			return rowHeightConfig.getSize(rowPosition);
		} else {
			return columnHeaderLayer.getRowHeightByPosition(rowPosition - 1);
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
			return columnHeaderLayer.isRowPositionResizable(rowPosition - 1);
		}
	}

	// Y

	@Override
	public long getRowPositionByY(long y) {
		long row0Height = getRowHeightByPosition(0);
		if (y < row0Height) {
			return 0;
		} else {
			return 1 + columnHeaderLayer.getRowPositionByY(y - row0Height);
		}
	}

	@Override
	public long getStartYOfRowPosition(long rowPosition) {
		if (rowPosition == 0) {
			return rowHeightConfig.getAggregateSize(rowPosition);
		} else {
			return getRowHeightByPosition(0) + columnHeaderLayer.getStartYOfRowPosition(rowPosition - 1);
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

		// Column group header cell
		if (model.isPartOfAGroup(columnIndex)) {
			if (rowPosition == 0) {
				return new LayerCell(this,
						new LayerCellDim(HORIZONTAL, columnIndex,
								columnPosition, getStartPositionOfGroup(columnPosition), getColumnSpan(columnPosition) ),
						new LayerCellDim(VERTICAL, NO_INDEX, rowPosition),
						displayMode );
			} else {
				return new LayerCell(this,
						new LayerCellDim(HORIZONTAL, columnIndex, columnPosition),
						new LayerCellDim(VERTICAL, NO_INDEX, rowPosition),
						displayMode );
			}
		} else {
			// render column header w/ rowspan = 2
			// as in this case we ask the column header layer for the cell position
			// and the column header layer asks his data provider for the row count
			// which should always return 1, we ask for row position 0 instead of 
			// using getGroupHeaderRowPosition(), if we would use getGroupHeaderRowPosition()
			// the ColumnGroupGroupHeaderLayer wouldn't work anymore
			ILayerCell cell = columnHeaderLayer.getCellByPosition(columnPosition, 0);
			if (cell != null) {
				final long rowSpan;
				
				if (calculateHeight && model.size() == 0) {
					rowSpan = 1;
				} else {
					rowSpan = 2;
				}
				
				LayerCellDim vDim = cell.getDim(VERTICAL);
				cell = new LayerCell(this, cell.getDim(HORIZONTAL),
						new LayerCellDim(VERTICAL, vDim.getIndex(),
								vDim.getPosition(), vDim.getOriginPosition(), rowSpan ),
						displayMode );
			}
			return cell;
		}
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
			long sizeOfStaticColumns = columnGroup.getStaticColumnIndexes().size();
			return sizeOfStaticColumns == 0 ? 1 : sizeOfStaticColumns;
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
			ILayerCell cell = columnHeaderLayer.getCellByPosition(columnPosition, rowPosition);
			return (cell != null) ? cell.getDisplayMode() : DisplayMode.NORMAL;
		}
	}

	@Override
	public LabelStack getConfigLabelsByPosition(long columnPosition, long rowPosition) {
		long columnIndex = getColumnIndexByPosition(columnPosition);
		if (rowPosition == 0 && model.isPartOfAGroup(columnIndex)) {
			return new LabelStack(GridRegion.COLUMN_GROUP_HEADER);
		} else {
			return columnHeaderLayer.getConfigLabelsByPosition(columnPosition, rowPosition);
		}
	}

	@Override
	public Object getDataValueByPosition(long columnPosition, long rowPosition) {
		long columnIndex = getColumnIndexByPosition(columnPosition);
		if (rowPosition == 0 && model.isPartOfAGroup(columnIndex)) {
			return model.getColumnGroupByIndex(columnIndex).getName();
		} else {
			return columnHeaderLayer.getDataValueByPosition(columnPosition, 0);
		}
	}

	@Override
	public LabelStack getRegionLabelsByXY(long x, long y) {
		long columnIndex = getColumnIndexByPosition(getColumnPositionByX(x));
		if (model.isPartOfAGroup(columnIndex) && y < getRowHeightByPosition(0)) {
			return new LabelStack(GridRegion.COLUMN_GROUP_HEADER);
		} else {
			return columnHeaderLayer.getRegionLabelsByXY(x, y - getRowHeightByPosition(0));
		}
	}

	// ColumnGroupModel delegates

	public void addColumnsIndexesToGroup(String colGroupName, long... colIndexes) {
		model.addColumnsIndexesToGroup(colGroupName, colIndexes);
	}

	public void clearAllGroups(){
		model.clear();
	}
	
	public void setStaticColumnIndexesByGroup(String colGroupName, long... staticColumnIndexes) {
		model.setStaticColumnIndexesByGroup(colGroupName, staticColumnIndexes);
	}

	public boolean isColumnInGroup(long bodyColumnIndex) {
		return model.isPartOfAGroup(bodyColumnIndex);
	}

	/**
	 * @see ColumnGroup#setUnbreakable(boolean)
	 */
	public void setGroupUnbreakable(long columnIndex){
		ColumnGroup columnGroup = model.getColumnGroupByIndex(columnIndex);
		columnGroup.setUnbreakable(true);
	}

	public void setGroupAsCollapsed(long columnIndex) {
		ColumnGroup columnGroup = model.getColumnGroupByIndex(columnIndex);
		columnGroup.setCollapsed(true);
	}
	
	public boolean isCalculateHeight() {
		return calculateHeight;
	}

	public void setCalculateHeight(boolean calculateHeight) {
		this.calculateHeight = calculateHeight;
		
		if (calculateHeight) {
			this.model.registerColumnGroupModelListener(modelChangeListener);
		} else {
			this.model.unregisterColumnGroupModelListener(modelChangeListener);
		}
	}

}
