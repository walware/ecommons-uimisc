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
// ~
package org.eclipse.nebula.widgets.nattable.viewport.command;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectColumnsCommand;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectRowsCommand;


public class ViewportSelectDimPositionsCommandHandler extends AbstractLayerCommandHandler<ViewportSelectDimPositionsCommand> {
	
	
	private final AbstractLayer viewportLayer;
	
	private final Orientation orientation;
	
	
	public ViewportSelectDimPositionsCommandHandler(final AbstractLayer viewportLayer) {
		this(viewportLayer, null);
	}
	
	public ViewportSelectDimPositionsCommandHandler(final AbstractLayer viewportLayer,
			final Orientation orientation) {
		this.viewportLayer = viewportLayer;
		this.orientation = orientation;
	}
	
	
	@Override
	public Class<ViewportSelectDimPositionsCommand> getCommandClass() {
		return ViewportSelectDimPositionsCommand.class;
	}
	
	
	@Override
	protected boolean doCommand(final ViewportSelectDimPositionsCommand command) {
		if (this.orientation != null && command.getOrientation() != this.orientation) {
			return false;
		}
		
		switch (command.getOrientation()) {
		case HORIZONTAL:
			this.viewportLayer.doCommand(new SelectColumnsCommand(this.viewportLayer,
					command.getPositions(), 0,
					command.getSelectionFlags(), command.getPositionToReveal() ));
			return true;
		case VERTICAL:
			this.viewportLayer.doCommand(new SelectRowsCommand(this.viewportLayer,
					0, command.getPositions(),
					command.getSelectionFlags(), command.getPositionToReveal() ));
			return true;
		default:
			throw new IllegalStateException();
		}
	}
	
}
