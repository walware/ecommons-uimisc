/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package de.walware.ecommons.waltable.viewport;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;


public class ShowCellInViewportCommandHandler extends AbstractLayerCommandHandler<ShowCellInViewportCommand> {
	
	
	private final ViewportLayer viewportLayer;
	
	
	public ShowCellInViewportCommandHandler(final ViewportLayer viewportLayer) {
		this.viewportLayer= viewportLayer;
	}
	
	@Override
	public Class<ShowCellInViewportCommand> getCommandClass() {
		return ShowCellInViewportCommand.class;
	}
	
	
	@Override
	protected boolean doCommand(final ShowCellInViewportCommand command) {
		this.viewportLayer.getDim(HORIZONTAL).movePositionIntoViewport(command.getColumnPosition());
		this.viewportLayer.getDim(VERTICAL).movePositionIntoViewport(command.getRowPosition());
		return true;
	}
	
}
