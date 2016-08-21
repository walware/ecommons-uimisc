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
// ~
package de.walware.ecommons.waltable.viewport;

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.layer.AbstractLayer;
import de.walware.ecommons.waltable.selection.SelectDimPositionsCommand;


public class ViewportSelectDimPositionsCommandHandler extends AbstractLayerCommandHandler<ViewportSelectDimPositionsCommand> {
	
	
	private final AbstractLayer viewportLayer;
	
	private final Orientation orientation;
	
	
	public ViewportSelectDimPositionsCommandHandler(final AbstractLayer viewportLayer) {
		this(viewportLayer, null);
	}
	
	public ViewportSelectDimPositionsCommandHandler(final AbstractLayer viewportLayer,
			final Orientation orientation) {
		this.viewportLayer= viewportLayer;
		this.orientation= orientation;
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
		
		this.viewportLayer.doCommand(new SelectDimPositionsCommand(
				this.viewportLayer.getDim(command.getOrientation()),
				command.getRefPosition(), command.getPositions(), 0,
				command.getSelectionFlags(), command.getPositionToReveal() ));
		return true;
	}
	
}
