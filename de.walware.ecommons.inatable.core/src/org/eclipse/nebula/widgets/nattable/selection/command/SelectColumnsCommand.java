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

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;
import static org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.NO_SELECTION;

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.command.AbstractDimPositionsCommand;
import org.eclipse.nebula.widgets.nattable.command.LayerCommandUtil;
import org.eclipse.nebula.widgets.nattable.coordinate.ColumnPositionCoordinate;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.coordinate.RangeList;
import org.eclipse.nebula.widgets.nattable.coordinate.RowPositionCoordinate;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;


public class SelectColumnsCommand extends AbstractDimPositionsCommand {


	private final int selectionFlags;

	private RowPositionCoordinate rowPositionCoordinate;

	private ColumnPositionCoordinate columnPositionToReveal;


	public SelectColumnsCommand(final ILayer layer,
			final long columnPosition, final long rowPosition,
			final int selectionFlags) {
		this(layer, new RangeList(columnPosition), rowPosition, selectionFlags, columnPosition);
	}

	public SelectColumnsCommand(final ILayer layer,
			final Collection<Range> columnPositions, final long rowPosition,
			final int selectionFlags, final long columnPositionToReveal) {
		super(layer.getDim(HORIZONTAL), columnPositions);
		
		this.selectionFlags = selectionFlags;
		this.rowPositionCoordinate = new RowPositionCoordinate(layer, rowPosition);
		if (columnPositionToReveal != NO_SELECTION) {
			this.columnPositionToReveal = new ColumnPositionCoordinate(layer, columnPositionToReveal);
		}
	}

	protected SelectColumnsCommand(SelectColumnsCommand command) {
		super(command);
		
		this.selectionFlags = command.selectionFlags;
		this.rowPositionCoordinate = command.rowPositionCoordinate;
		this.columnPositionToReveal = command.columnPositionToReveal;
	}
	
	@Override
	public SelectColumnsCommand cloneCommand() {
		return new SelectColumnsCommand(this);
	}
	
	
	@Override
	public boolean convertToTargetLayer(ILayer targetLayer) {
		RowPositionCoordinate targetRowPositionCoordinate = LayerCommandUtil.convertRowPositionToTargetContext(
				rowPositionCoordinate, targetLayer );
		if (targetRowPositionCoordinate != null && targetRowPositionCoordinate.getRowPosition() >= 0
				&& super.convertToTargetLayer(targetLayer) ) {
			this.rowPositionCoordinate = targetRowPositionCoordinate;
			this.columnPositionToReveal = LayerCommandUtil.convertColumnPositionToTargetContext(
					this.columnPositionToReveal, targetLayer);
			return true;
		}
		return false;
	}

	public long getRowPosition() {
		return rowPositionCoordinate.rowPosition;
	}

	public int getSelectionFlags() {
		return selectionFlags;
	}

	public long getColumnPositionToReveal() {
		if (columnPositionToReveal != null) {
			return columnPositionToReveal.columnPosition;
		} else {
			return NO_SELECTION;
		}
	}

}
