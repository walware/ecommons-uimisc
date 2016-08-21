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

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;

public class RecalculateScrollBarsCommandHandler extends AbstractLayerCommandHandler<RecalculateScrollBarsCommand> {
	
	private final ViewportLayer viewportLayer;

	public RecalculateScrollBarsCommandHandler(final ViewportLayer viewportLayer) {
		this.viewportLayer= viewportLayer;
	}
	
	@Override
	public Class<RecalculateScrollBarsCommand> getCommandClass() {
		return RecalculateScrollBarsCommand.class;
	}

	@Override
	protected boolean doCommand(final RecalculateScrollBarsCommand command) {
		this.viewportLayer.recalculateScrollBars();
		return true;
	}

}
