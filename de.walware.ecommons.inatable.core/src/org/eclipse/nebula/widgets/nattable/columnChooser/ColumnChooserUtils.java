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
package org.eclipse.nebula.widgets.nattable.columnChooser;

import static org.eclipse.nebula.widgets.nattable.util.ObjectUtils.asLongArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.coordinate.RangeList;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.command.MultiColumnHideCommand;
import org.eclipse.nebula.widgets.nattable.hideshow.command.MultiColumnShowCommand;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;


public class ColumnChooserUtils {

	public static final String RENAMED_COLUMN_INDICATOR = "*"; //$NON-NLS-1$

	public static void hideColumnEntries(List<ColumnEntry> removedItems, ColumnHideShowLayer hideShowLayer) {
		MultiColumnHideCommand hideCommand = new MultiColumnHideCommand(hideShowLayer,
				new RangeList(asLongArray(getColumnEntryPositions(removedItems))));
		hideShowLayer.doCommand(hideCommand);
	}

	public static void hideColumnPositions(List<Long> removedPositions, ColumnHideShowLayer hideShowLayer) {
		MultiColumnHideCommand hideCommand = new MultiColumnHideCommand(hideShowLayer,
				new RangeList(asLongArray(removedPositions)));
		hideShowLayer.doCommand(hideCommand);
	}
	
	
	public static void showColumnEntries(List<ColumnEntry> addedItems, ColumnHideShowLayer hideShowLayer) {
		hideShowLayer.doCommand(new MultiColumnShowCommand(getColumnEntryIndexes(addedItems)));
	}

	public static void showColumnIndexes(List<Long> addedColumnIndexes, ColumnHideShowLayer hideShowLayer) {
		hideShowLayer.doCommand(new MultiColumnShowCommand(addedColumnIndexes));
	}

	public static List<ColumnEntry> getHiddenColumnEntries(ColumnHideShowLayer columnHideShowLayer, ColumnHeaderLayer columnHeaderLayer, DataLayer columnHeaderDataLayer) {
		Collection<Long> hiddenColumnIndexes = columnHideShowLayer.getHiddenColumnIndexes();
		ArrayList<ColumnEntry> hiddenColumnEntries= new ArrayList<ColumnEntry>();

		for (Long hiddenColumnIndex : hiddenColumnIndexes) {
			String label = getColumnLabel(columnHeaderLayer, columnHeaderDataLayer, hiddenColumnIndex);
			ColumnEntry columnEntry = new ColumnEntry(label, hiddenColumnIndex, Long.valueOf(-1));
			hiddenColumnEntries.add(columnEntry);
		}

		return hiddenColumnEntries;
	}

	/**
	 * @param columnHeaderLayer
	 * @param columnHeaderDataLayer
	 * @param columnIndex
	 * @return The renamed column header name for the given column index (if the column has been renamed),
	 * 	the original column name otherwise.
	 */
	public static String getColumnLabel(ColumnHeaderLayer columnHeaderLayer, DataLayer columnHeaderDataLayer, Long columnIndex) {
		String label = ""; //$NON-NLS-1$
		if (columnHeaderLayer.isColumnRenamed(columnIndex)) {
			label = columnHeaderLayer.getRenamedColumnLabelByIndex(columnIndex) + RENAMED_COLUMN_INDICATOR;
		} else {
			long position = columnHeaderDataLayer.getColumnPositionByIndex(columnIndex.longValue());
			label = columnHeaderDataLayer.getDataValueByPosition(position, 0).toString();
		}
		return label;
	}

	/**
	 * Get all visible columns from the selection layer and the corresponding labels in the header
	 */
	public static List<ColumnEntry> getVisibleColumnsEntries(ColumnHideShowLayer columnHideShowLayer, ColumnHeaderLayer columnHeaderLayer, DataLayer columnHeaderDataLayer) {
		long visibleColumnCount = columnHideShowLayer.getColumnCount();
		ArrayList<ColumnEntry> visibleColumnEntries= new ArrayList<ColumnEntry>();

		for (long i = 0; i < visibleColumnCount; i++) {
			long index = columnHideShowLayer.getColumnIndexByPosition(i);
			String label = getColumnLabel(columnHeaderLayer, columnHeaderDataLayer, index);
			ColumnEntry columnEntry = new ColumnEntry(label, Long.valueOf(index), Long.valueOf(i));
			visibleColumnEntries.add(columnEntry);
		}
		return visibleColumnEntries;
	}

	/**
	 * Search the collection for the entry with the given index.
	 */
	public static ColumnEntry find(List<ColumnEntry> entries, long indexToFind) {
		for (ColumnEntry columnEntry : entries) {
			if(columnEntry.getIndex().equals(indexToFind)){
				return columnEntry;
			}
		}
		return null;
	}

	/**
	 * Get ColumnEntry positions for the ColumnEntry objects.
	 */
	public static List<Long> getColumnEntryPositions(List<ColumnEntry> columnEntries) {
		List<Long> columnEntryPositions = new ArrayList<Long>();
		for (ColumnEntry columnEntry : columnEntries) {
			columnEntryPositions.add(columnEntry.getPosition());
		}
		return columnEntryPositions;
	}

	/**
	 * Get ColumnEntry positions for the ColumnEntry objects.
	 */
	public static List<Long> getColumnEntryIndexes(List<ColumnEntry> columnEntries) {
		List<Long> columnEntryIndexes = new ArrayList<Long>();
		for (ColumnEntry columnEntry : columnEntries) {
			columnEntryIndexes.add(columnEntry.getIndex());
		}
		return columnEntryIndexes;
	}

	/**
	 * @return TRUE if the list contains an entry with the given index
	 */
	public static boolean containsIndex(List<ColumnEntry> entries, long indexToFind) {
		return find(entries, indexToFind) != null;
	}

}
