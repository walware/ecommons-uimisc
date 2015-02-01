/*******************************************************************************
 * Copyright (c) 2010-2015 Stephan Wahlbrink (WalWare.de) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.nattable.viewport.command;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.coordinate.Direction;
import org.eclipse.nebula.widgets.nattable.viewport.IViewportDim;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;


public class ScrollPageCommandHandler extends AbstractLayerCommandHandler<ScrollPageCommand> {
	
	
	private ViewportLayer viewportLayer;
	
	
	public ScrollPageCommandHandler(ViewportLayer viewportLayer) {
		this.viewportLayer = viewportLayer;
	}
	
	@Override
	public Class<ScrollPageCommand> getCommandClass() {
		return ScrollPageCommand.class;
	}
	
	
	@Override
	protected boolean doCommand(ScrollPageCommand command) {
		final Direction direction = command.getDirection();
		final IViewportDim dim = this.viewportLayer.getDim(direction.getOrientation());
		if (direction.isBackward()) {
			dim.scrollBackwardByPage();
		}
		else /*if (direction.isForward())*/ {
			dim.scrollForwardByPage();
		}
		return true;
	}
	
}
