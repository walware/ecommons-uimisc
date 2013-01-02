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

import org.eclipse.nebula.widgets.nattable.command.AbstractPositionCommand;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;


/**
 * Event indicating that the user has selected a specific cell in the data grid. This command should be used for 
 * implementing all selection handling by layers. 
 * 
 * <strong>Note that this command takes a Grid PositionCoordinate describing a cell on the screen on which the user has 
 * clicked. Do not pass it anything else or you will introduce very subtle and very difficult to debug bugs into the 
 * code and then we will have to pay you a visit on one random Sunday morning when you least expect it.<strong>
 */
public class SelectCellCommand extends AbstractPositionCommand {


	private final int selectionFlags;

	private final boolean revealCell;


	public SelectCellCommand(final ILayer layer, final int columnPosition, final int rowPosition,
			final int selectionFlags) {
		this(layer, columnPosition, rowPosition, selectionFlags, true);
	}

	public SelectCellCommand(final ILayer layer, final int columnPosition, final int rowPosition,
			final int selectionFlags, boolean revealCell) {
		super(layer, columnPosition, rowPosition);
		
		this.selectionFlags = selectionFlags;
		this.revealCell = revealCell;
	}

	protected SelectCellCommand(SelectCellCommand command) {
		super(command);
		
		this.selectionFlags = command.selectionFlags;
		this.revealCell = command.revealCell;
	}

	public SelectCellCommand cloneCommand() {
		return new SelectCellCommand(this);
	}


	public int getSelectionFlags() {
		return selectionFlags;
	}

	public boolean getRevealCell() {
		return revealCell;
	}

}
