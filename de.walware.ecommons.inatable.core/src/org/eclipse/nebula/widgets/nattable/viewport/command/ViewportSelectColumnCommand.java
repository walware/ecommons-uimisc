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
package org.eclipse.nebula.widgets.nattable.viewport.command;

import static org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.NO_SELECTION;

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.command.AbstractMultiColumnCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.ColumnPositionCoordinate;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;


/**
 * Command to select column(s).
 * Note: The column positions are in top level composite Layer (NatTable) coordinates
 */
public class ViewportSelectColumnCommand extends AbstractMultiColumnCommand {


	private final int selectionFlags;

	private ColumnPositionCoordinate columnPositionToReveal;


	public ViewportSelectColumnCommand(final ILayer layer, int columnPosition,
			int selectionFlags) {
		super(layer, columnPosition);
		
		this.selectionFlags = selectionFlags;
		this.columnPositionToReveal = new ColumnPositionCoordinate(layer, columnPosition);
	}

	public ViewportSelectColumnCommand(final ILayer layer, final Collection<Integer> columnPositions,
			int selectionFlags, int columnPositionToReveal) {
		super(layer, columnPositions);
		
		this.selectionFlags = selectionFlags;
		if (columnPositionToReveal != NO_SELECTION) {
			this.columnPositionToReveal = new ColumnPositionCoordinate(layer, columnPositionToReveal);
		}
	}

	protected ViewportSelectColumnCommand(ViewportSelectColumnCommand command) {
		super(command);
		
		this.selectionFlags = command.selectionFlags;
		this.columnPositionToReveal = command.columnPositionToReveal;
	}

	public ViewportSelectColumnCommand cloneCommand() {
		return new ViewportSelectColumnCommand(this);
	}


	public int getSelectionFlags() {
		return selectionFlags;
	}

	public int getColumnPositionToReveal() {
		if (columnPositionToReveal != null) {
			return columnPositionToReveal.columnPosition;
		} else {
			return NO_SELECTION;
		}
	}

}
