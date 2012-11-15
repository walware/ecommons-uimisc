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

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.command.AbstractMultiRowCommand;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;


/**
 * Command to select row(s).
 * Note: The row positions are in top level composite Layer (NatTable) coordinates
 */
public class ViewportSelectRowCommand extends AbstractMultiRowCommand {


	private final int selectionFlags;


	public ViewportSelectRowCommand(final ILayer layer, final int rowPosition,
			final int selectionFlags) {
		super(layer, rowPosition);
		
		this.selectionFlags = selectionFlags;
	}

	public ViewportSelectRowCommand(final ILayer layer, final Collection<Integer> rowPositions,
			int selectionFlags) {
		super(layer, rowPositions);
		
		this.selectionFlags = selectionFlags;
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

}
