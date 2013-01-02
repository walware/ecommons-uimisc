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
package org.eclipse.nebula.widgets.nattable.viewport.command;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectColumnsCommand;


public class ViewportSelectColumnCommandHandler extends AbstractLayerCommandHandler<ViewportSelectColumnCommand> {


	private final AbstractLayer viewportLayer;


	public ViewportSelectColumnCommandHandler(AbstractLayer viewportLayer) {
		this.viewportLayer = viewportLayer;
	}


	public Class<ViewportSelectColumnCommand> getCommandClass() {
		return ViewportSelectColumnCommand.class;
	}


	@Override
	protected boolean doCommand(ViewportSelectColumnCommand command) {
		viewportLayer.doCommand(new SelectColumnsCommand(viewportLayer,
				command.getColumnPositions(), 0,
				command.getSelectionFlags(), command.getColumnPositionToReveal() ));
		return true;
	}

}
