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
package org.eclipse.nebula.widgets.nattable.viewport.command;

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.selection.command.AbstractSelectDimPositionsCommand;


/**
 * Command to select column(s)/row(s) in the viewport.
 */
public class ViewportSelectDimPositionsCommand extends AbstractSelectDimPositionsCommand {
	
	
	public ViewportSelectDimPositionsCommand(final Orientation orienation,
			final ILayer layer, final int position, final int selectionFlags) {
		super(orienation, layer, position, selectionFlags);
	}
	
	public ViewportSelectDimPositionsCommand(final Orientation orientation,
			final ILayer layer, final int refPosition, final Collection<Range> positions,
			final int positionToReveal, final int selectionFlags) {
		super(orientation, layer, refPosition, positions, positionToReveal, selectionFlags);
	}
	
	protected ViewportSelectDimPositionsCommand(final ViewportSelectDimPositionsCommand command) {
		super(command);
	}
	
	public ViewportSelectDimPositionsCommand cloneCommand() {
		return new ViewportSelectDimPositionsCommand(this);
	}
	
	
}
