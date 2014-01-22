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
package org.eclipse.nebula.widgets.nattable.selection.command;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;
import static org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.NO_SELECTION;

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.command.AbstractDimPositionsCommand;
import org.eclipse.nebula.widgets.nattable.command.LayerCommandUtil;
import org.eclipse.nebula.widgets.nattable.coordinate.ColumnPositionCoordinate;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.coordinate.RangeList;
import org.eclipse.nebula.widgets.nattable.coordinate.RowPositionCoordinate;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;


public class SelectRowsCommand extends AbstractDimPositionsCommand {


	private final int selectionFlags;

	private ColumnPositionCoordinate columnPositionCoordinate;

	private RowPositionCoordinate rowPositionToReveal;


	public SelectRowsCommand(final ILayer layer,
			final long columnPosition, final long rowPosition,
			final int selectionFlags) {
		this(layer, columnPosition, new RangeList(rowPosition), selectionFlags, rowPosition);
	}

	public SelectRowsCommand(final ILayer layer,
			final long columnPosition, final Collection<Range> rowPositions,
			final int selectionFlags, final long rowPositionToReveal) {
		super(layer.getDim(VERTICAL), rowPositions);
		
		this.selectionFlags = selectionFlags;
		this.columnPositionCoordinate = new ColumnPositionCoordinate(layer, columnPosition);
		if (rowPositionToReveal != NO_SELECTION) {
			this.rowPositionToReveal = new RowPositionCoordinate(layer, rowPositionToReveal);
		}
	}

	protected SelectRowsCommand(SelectRowsCommand command) {
		super(command);
		
		this.selectionFlags = command.selectionFlags;
		this.columnPositionCoordinate = command.columnPositionCoordinate;
		this.rowPositionToReveal = command.rowPositionToReveal;
	}
	
	@Override
	public SelectRowsCommand cloneCommand() {
		return new SelectRowsCommand(this);
	}
	
	
	@Override
	public boolean convertToTargetLayer(ILayer targetLayer) {
		ColumnPositionCoordinate targetColumnPositionCoordinate = LayerCommandUtil.convertColumnPositionToTargetContext(
				columnPositionCoordinate, targetLayer );
		if (targetColumnPositionCoordinate != null && targetColumnPositionCoordinate.getColumnPosition() >= 0
				&& super.convertToTargetLayer(targetLayer) ) {
			this.columnPositionCoordinate = targetColumnPositionCoordinate;
			this.rowPositionToReveal = LayerCommandUtil.convertRowPositionToTargetContext(
					this.rowPositionToReveal, targetLayer );
			return true;
		}
		return false;
	}

	public long getColumnPosition() {
		return columnPositionCoordinate.columnPosition;
	}

	public int getSelectionFlags() {
		return selectionFlags;
	}

	public long getRowPositionToReveal() {
		if (rowPositionToReveal != null) {
			return rowPositionToReveal.rowPosition;
		} else {
			return NO_SELECTION;
		}
	}

}
