/*******************************************************************************
 * Copyright (c) 2010, 2013 WalWare/StatET-Project (www.walware.de/goto/statet).
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


public class ScrollStepCommandHandler extends AbstractLayerCommandHandler<ScrollStepCommand> {
	
	
	private final ViewportLayer viewportLayer;
	
	
	public ScrollStepCommandHandler(final ViewportLayer viewportLayer) {
		this.viewportLayer = viewportLayer;
	}
	
	public Class<ScrollStepCommand> getCommandClass() {
		return ScrollStepCommand.class;
	}
	
	
	protected boolean doCommand(ScrollStepCommand command) {
		final Direction direction = command.getDirection();
		final IViewportDim dim = this.viewportLayer.getDim(direction.getOrientation());
		if (direction.isBackward()) {
			dim.scrollBackwardByStep();
		}
		else /*if (direction.isForward())*/ {
			dim.scrollForwardByStep();
		}
		return true;
	}
	
}
