/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.group.command;

import static org.eclipse.nebula.widgets.nattable.painter.cell.GraphicsUtils.safe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import org.eclipse.nebula.widgets.nattable.Messages;
import org.eclipse.nebula.widgets.nattable.columnRename.ColumnRenameDialog;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel.ColumnGroup;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupUtils;
import org.eclipse.nebula.widgets.nattable.group.event.GroupColumnsEvent;
import org.eclipse.nebula.widgets.nattable.group.event.UngroupColumnsEvent;
import org.eclipse.nebula.widgets.nattable.reorder.command.MultiColumnReorderCommand;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;


public class ColumnGroupsCommandHandler extends AbstractLayerCommandHandler<IColumnGroupCommand>  {
	
	private final ColumnGroupModel model;
	private final SelectionLayer selectionLayer;
	private final ColumnGroupHeaderLayer contextLayer;
	private Map<Long, Long> columnIndexesToPositionsMap;

	public ColumnGroupsCommandHandler(ColumnGroupModel model, SelectionLayer selectionLayer, ColumnGroupHeaderLayer contextLayer) {
		this.model = model;
		this.selectionLayer = selectionLayer;
		this.contextLayer = contextLayer;
	}

	public boolean doCommand(IColumnGroupCommand command) {
		if (command instanceof CreateColumnGroupCommand) {
			if (columnIndexesToPositionsMap.size() > 0) {
				handleGroupColumnsCommand(((CreateColumnGroupCommand)command).getColumnGroupName());
				columnIndexesToPositionsMap.clear();
				return true;
			}
		} else if (command instanceof OpenCreateColumnGroupDialog) {
			OpenCreateColumnGroupDialog openDialogCommand = (OpenCreateColumnGroupDialog) command;
			loadSelectedColumnsIndexesWithPositions();
			if (!selectionLayer.getFullySelectedColumnPositions().isEmpty() && columnIndexesToPositionsMap.size() > 0) {
				openDialogCommand.openDialog(contextLayer);
			} else {				
				openDialogCommand.openErrorBox(Messages.getString("ColumnGroups.selectNonGroupedColumns"));				 //$NON-NLS-1$
			}
			return true;
		} else if (command instanceof UngroupColumnCommand) {
			handleUngroupCommand();
			return true;
		} else if (command instanceof RemoveColumnGroupCommand) {
			RemoveColumnGroupCommand removeColumnGroupCommand = (RemoveColumnGroupCommand) command;
			long columnIndex = removeColumnGroupCommand.getColumnIndex();
			handleRemoveColumnGroupCommand(columnIndex);
			return true;
		} else if (command instanceof DisplayColumnGroupRenameDialogCommand) {
			return displayColumnGroupRenameDialog((DisplayColumnGroupRenameDialogCommand) command);
		}
		return false;
	}

	private boolean displayColumnGroupRenameDialog(DisplayColumnGroupRenameDialogCommand command) {
		long columnPosition = command.getColumnPosition();

		ColumnRenameDialog dialog = new ColumnRenameDialog(Display.getDefault().getActiveShell(), null, null);
		Rectangle colHeaderBounds = contextLayer.getBoundsByPosition(columnPosition, 0);
		Point point = new Point(safe(colHeaderBounds.x), safe(colHeaderBounds.y + colHeaderBounds.height));
        dialog.setLocation(command.toDisplayCoordinates(point));
		dialog.open();

		if (!dialog.isCancelPressed()) {
			long columnIndex = contextLayer.getColumnIndexByPosition(columnPosition);
			ColumnGroup columnGroup = model.getColumnGroupByIndex(columnIndex);
			columnGroup.setName(dialog.getNewColumnLabel());
		}
		
		return true;
	}

	public Class<IColumnGroupCommand> getCommandClass() {
		return IColumnGroupCommand.class;
	}
	
	protected void loadSelectedColumnsIndexesWithPositions() {
		columnIndexesToPositionsMap = new LinkedHashMap<Long, Long>();
		List<Range> fullySelectedColumns = selectionLayer.getFullySelectedColumnPositions();
		
		if (!fullySelectedColumns.isEmpty()) {
			for (final Range range : fullySelectedColumns) {
				for (long position = range.start; position < range.end; position++) {
					long columnIndex = selectionLayer.getColumnIndexByPosition(position);
					if (columnIndex >= 0 && model.isPartOfAGroup(columnIndex)){
						columnIndexesToPositionsMap.clear();
						break;
					}
					columnIndexesToPositionsMap.put(Long.valueOf(columnIndex), Long.valueOf(position));
				}
			}
		}
	}

	public void handleGroupColumnsCommand(String columnGroupName) {
			
		try {
			List<Long> selectedPositions = new ArrayList<Long>();
			long[] fullySelectedColumns = new long[columnIndexesToPositionsMap.size()];
			int count = 0;
			for (Long columnIndex : columnIndexesToPositionsMap.keySet()) {
				fullySelectedColumns[count++] = columnIndex.longValue();
				selectedPositions.add(columnIndexesToPositionsMap.get(columnIndex));
			}
			model.addColumnsIndexesToGroup(columnGroupName, fullySelectedColumns);
			selectionLayer.doCommand(new MultiColumnReorderCommand(selectionLayer, selectedPositions, selectedPositions.get(0).longValue()));
			selectionLayer.clear();
		} catch (Throwable t) {
		}
		contextLayer.fireLayerEvent(new GroupColumnsEvent(contextLayer));
	}

	public void handleUngroupCommand() {
		// Grab fully selected column positions
		List<Range> fullySelectedColumns = selectionLayer.getFullySelectedColumnPositions();
		Map<String, Long> toColumnPositions = new HashMap<String, Long>();
		if (!fullySelectedColumns.isEmpty()) {
		
		// Pick the ones which belong to a group and remove them from the group
			for (final Range range : fullySelectedColumns) {
				for (long position = range.start; position < range.end; position++) {
					long columnIndex = selectionLayer.getColumnIndexByPosition(position);
					if (columnIndex >= 0 && model.isPartOfAGroup(columnIndex) && !model.isPartOfAnUnbreakableGroup(columnIndex)){
						handleRemovalFromGroup(toColumnPositions, columnIndex);
					}
				}
			}
		// The groups which were affected should be reordered to the start position, this should group all columns together
			Collection<Long> values = toColumnPositions.values();
			final Iterator<Long> toColumnPositionsIterator = values.iterator();
			while(toColumnPositionsIterator.hasNext()) {
				Long toColumnPosition = toColumnPositionsIterator.next();
				selectionLayer.doCommand(new ReorderColumnGroupCommand(selectionLayer, toColumnPosition.longValue(), toColumnPosition.longValue()));
			}
			selectionLayer.clear();
		} 
		
		contextLayer.fireLayerEvent(new UngroupColumnsEvent(contextLayer));
	}

	private void handleRemovalFromGroup(Map<String, Long> toColumnPositions, long columnIndex) {
		ColumnGroup columnGroup = model.getColumnGroupByIndex(columnIndex);
		
		final String columnGroupName = columnGroup.getName();
		final List<Long> columnIndexesInGroup = columnGroup.getMembers();
		final long columnGroupSize = columnIndexesInGroup.size();
		if (!toColumnPositions.containsKey(columnGroupName)) {
			for (long colGroupIndex : columnIndexesInGroup) {
				if (ColumnGroupUtils.isFirstVisibleColumnIndexInGroup(colGroupIndex, contextLayer, selectionLayer, model)) {
					long toPosition = selectionLayer.getColumnPositionByIndex(colGroupIndex);
					if (colGroupIndex == columnIndex) {
						if (columnGroupSize == 1) {
							break;
						} else {
							toPosition++;
						}
					}
					toColumnPositions.put(columnGroupName, Long.valueOf(toPosition));
					break;
				}
			}
		} else {
			if (columnGroupSize - 1 <= 0) {
				toColumnPositions.remove(columnGroupName);
			}
		}
		columnGroup.removeColumn(columnIndex);
	}	
	
	private void handleRemoveColumnGroupCommand(long columnIndex) {
		ColumnGroup columnGroup = model.getColumnGroupByIndex(columnIndex);
		model.removeColumnGroup(columnGroup);
	}

}
