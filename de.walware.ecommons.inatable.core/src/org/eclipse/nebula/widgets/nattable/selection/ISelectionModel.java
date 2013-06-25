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
// ~Selection
package org.eclipse.nebula.widgets.nattable.selection;

import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

/**
 * Tracks the selections made in the table.
 */
public interface ISelectionModel {
	
	public boolean isMultipleSelectionAllowed();
	

	public void addSelection(int columnPosition, int rowPosition);

	public void addSelection(final Rectangle range);

	public void clearSelection();

	public void clearSelection(int columnPosition, int rowPosition);

	public void clearSelection(Rectangle removedSelection);

	public boolean isEmpty();

	public List<Rectangle> getSelections();
	
	// Cell features

	public boolean isCellPositionSelected(ILayerCell cell);
	
	// Column features

	public int[] getSelectedColumnPositions();

	public boolean isColumnPositionSelected(int columnPosition);

	/**
	 * @param columnHeight the number of rows in a fully selected column
	 */
	public int[] getFullySelectedColumnPositions();

	/**
	 * @param columnHeight the number of rows in a fully selected column
	 */
	public boolean isColumnPositionFullySelected(int columnPosition);

	// Row features

	public int getSelectedRowCount();
	
	public Set<Range> getSelectedRowPositions();
	
	public boolean isRowPositionSelected(int rowPosition);

	public int[] getFullySelectedRowPositions();

	public boolean isRowPositionFullySelected(int rowPosition);
	
}
