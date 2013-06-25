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

import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

/**
 * Tracks the selections made in the table.
 */
public interface ISelectionModel {
	
	public boolean isMultipleSelectionAllowed();
	

	public void addSelection(long columnPosition, long rowPosition);

	public void addSelection(final Rectangle range);

	public void clearSelection();

	public void clearSelection(long columnPosition, long rowPosition);

	public void clearSelection(Rectangle removedSelection);

	public boolean isEmpty();

	public List<Rectangle> getSelections();
	
	// Cell features

	public boolean isCellPositionSelected(ILayerCell cell);
	
	// Column features

	public List<Range> getSelectedColumnPositions();

	public boolean isColumnPositionSelected(long columnPosition);

	public List<Range> getFullySelectedColumnPositions();

	public boolean isColumnPositionFullySelected(long columnPosition);

	// Row features

	public long getSelectedRowCount();
	
	public List<Range> getSelectedRowPositions();
	
	public boolean isRowPositionSelected(long rowPosition);

	public List<Range> getFullySelectedRowPositions();

	public boolean isRowPositionFullySelected(long rowPosition);
	
}
