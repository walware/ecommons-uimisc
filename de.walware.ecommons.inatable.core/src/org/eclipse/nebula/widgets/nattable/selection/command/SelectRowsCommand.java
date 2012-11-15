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
// ~Selection
package org.eclipse.nebula.widgets.nattable.selection.command;

import static org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.NO_SELECTION;

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.command.AbstractMultiRowCommand;
import org.eclipse.nebula.widgets.nattable.command.LayerCommandUtil;
import org.eclipse.nebula.widgets.nattable.coordinate.ColumnPositionCoordinate;
import org.eclipse.nebula.widgets.nattable.coordinate.RowPositionCoordinate;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;


public class SelectRowsCommand extends AbstractMultiRowCommand {


	private final int selectionFlags;

	private ColumnPositionCoordinate columnPositionCoordinate;

	private RowPositionCoordinate rowPositionToReveal;


	public SelectRowsCommand(ILayer layer, int columnPosition, int rowPosition,
			final int selectionFlags) {
		super(layer, columnPosition);
		
		this.selectionFlags = selectionFlags;
		init(layer, columnPosition, rowPosition);
	}

	public SelectRowsCommand(final ILayer layer, final int columnPosition, final int[] rowPositions,
			final int rowPositionToReveal, final int selectionFlags) {
		super(layer, rowPositions);
		
		this.selectionFlags = selectionFlags;
		init(layer, columnPosition, rowPositionToReveal);
	}

	public SelectRowsCommand(final ILayer layer, final int columnPosition, final Collection<Integer> rowPositions,
			final int rowPositionToReveal, final int selectionFlags) {
		super(layer, rowPositions);
		
		this.selectionFlags = selectionFlags;
		init(layer, columnPosition, rowPositionToReveal);
	}

	private void init(final ILayer layer, final int columnPosition, final int rowPositionToReveal) {
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

	public SelectRowsCommand cloneCommand() {
		return new SelectRowsCommand(this);
	}


	@Override
	public boolean convertToTargetLayer(ILayer targetLayer) {
		if (super.convertToTargetLayer(targetLayer)) {
			
			columnPositionCoordinate = LayerCommandUtil.convertColumnPositionToTargetContext(
					columnPositionCoordinate, targetLayer );
			rowPositionToReveal = LayerCommandUtil.convertRowPositionToTargetContext(
					rowPositionToReveal, targetLayer );
			
			return (columnPositionCoordinate != null);
		}
		return false;
	}

	public int getColumnPosition() {
		return columnPositionCoordinate.columnPosition;
	}

	public int getSelectionFlags() {
		return selectionFlags;
	}

	public int getRowPositionToReveal() {
		if (rowPositionToReveal != null) {
			return rowPositionToReveal.rowPosition;
		} else {
			return NO_SELECTION;
		}
	}

}
