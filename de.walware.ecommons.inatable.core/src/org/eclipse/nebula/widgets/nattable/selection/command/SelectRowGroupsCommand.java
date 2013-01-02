/*******************************************************************************
 * Copyright (c) 2012-2013 Original authors and others.
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

import org.eclipse.nebula.widgets.nattable.command.AbstractRowCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.ColumnPositionCoordinate;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;


public class SelectRowGroupsCommand extends AbstractRowCommand {


	private final int selectionFlags;

	private ColumnPositionCoordinate columnPositionCoordinate;

	private boolean moveAnchorToTopOfGroup = false;
	private int rowPositionToReveal; 


	public SelectRowGroupsCommand(ILayer layer, int columnPosition, int rowPosition, int selectionFlags, boolean moveAnchortoTopOfGroup) {
		this(layer, columnPosition, rowPosition, selectionFlags, moveAnchortoTopOfGroup, -1);
	}

	public SelectRowGroupsCommand(ILayer layer, int columnPosition, int rowPosition, int selectionFlags) {
		this(layer, columnPosition, rowPosition, selectionFlags, false, -1);
	}

	public SelectRowGroupsCommand(ILayer layer, int columnPosition, int rowPosition, int selectionFlags,
			boolean moveAnchortoTopOfGroup, int rowPositionToReveal) {
		super(layer, rowPosition);
		this.selectionFlags = selectionFlags;
		this.columnPositionCoordinate = new ColumnPositionCoordinate(layer, columnPosition);
		
		this.moveAnchorToTopOfGroup = moveAnchortoTopOfGroup;
		this.rowPositionToReveal = rowPositionToReveal;
	}

	protected SelectRowGroupsCommand(SelectRowGroupsCommand command) {
		super(command);
		
		this.selectionFlags = command.selectionFlags;
		this.columnPositionCoordinate = command.columnPositionCoordinate;
		this.moveAnchorToTopOfGroup = command.moveAnchorToTopOfGroup;
		this.rowPositionToReveal = command.rowPositionToReveal;
	}

	public SelectRowGroupsCommand cloneCommand() {
		return new SelectRowGroupsCommand(this);
	}


	public int getSelectionFlags() {
		return selectionFlags;
	}

	public int getColumnPosition() {
		return columnPositionCoordinate.columnPosition;
	}

	public boolean isMoveAnchorToTopOfGroup() {
		return moveAnchorToTopOfGroup;
	}

	public int getRowPositionToReveal() {
		return rowPositionToReveal;
	}

}
