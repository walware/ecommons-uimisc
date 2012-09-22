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

import org.eclipse.nebula.widgets.nattable.command.AbstractMultiRowCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.RowPositionCoordinate;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;


/**
 * Command to select row(s).
 * Note: The row positions are in top level composite Layer (NatTable) coordinates
 */
public class ViewportSelectRowCommand extends AbstractMultiRowCommand {


	private final int selectionFlags;

	private RowPositionCoordinate rowPositionToReveal;


	public ViewportSelectRowCommand(final ILayer layer, final int rowPosition,
			final int selectionFlags) {
		super(layer, rowPosition);
		
		this.selectionFlags = selectionFlags;
		this.rowPositionToReveal = new RowPositionCoordinate(layer, rowPosition);
	}

	public ViewportSelectRowCommand(final ILayer layer, final Collection<Integer> rowPositions,
			int selectionFlags, int rowPositionToReveal) {
		super(layer, rowPositions);
		
		this.selectionFlags = selectionFlags;
		if (rowPositionToReveal != NO_SELECTION) {
			this.rowPositionToReveal = new RowPositionCoordinate(layer, rowPositionToReveal);
		}
	}

	protected ViewportSelectRowCommand(ViewportSelectRowCommand command) {
		super(command);
		
		this.selectionFlags = command.selectionFlags;
	}

	public ViewportSelectRowCommand cloneCommand() {
		return new ViewportSelectRowCommand(this);
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
