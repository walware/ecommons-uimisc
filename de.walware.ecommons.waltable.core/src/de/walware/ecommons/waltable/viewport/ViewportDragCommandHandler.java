/*******************************************************************************
 * Copyright (c) 2012-2016 Edwin Park and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Edwin Park - initial API and implementation
 ******************************************************************************/
package de.walware.ecommons.waltable.viewport;

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;

public class ViewportDragCommandHandler extends AbstractLayerCommandHandler<ViewportDragCommand> {

	private final ViewportLayer viewportLayer;

	public ViewportDragCommandHandler(final ViewportLayer viewportLayer) {
		this.viewportLayer= viewportLayer;
	}

	@Override
	public Class<ViewportDragCommand> getCommandClass() {
		return ViewportDragCommand.class;
	}

	@Override
	protected boolean doCommand(final ViewportDragCommand command) {
		this.viewportLayer.drag(command.getX(), command.getY());
		return true;
	}

}
