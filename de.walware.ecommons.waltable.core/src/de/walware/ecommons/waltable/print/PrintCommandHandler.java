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
package de.walware.ecommons.waltable.print;

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;
import de.walware.ecommons.waltable.layer.ILayer;


/**
 * ILayerCommandHandler for handling the PrintCommand.
 * Simply delegates to the 
 */
public class PrintCommandHandler extends AbstractLayerCommandHandler<PrintCommand> {

	private final ILayer layer;

	/**
	 * @param layer The layer that should be printed. 
	 * 			Usually the top most layer to print, e.g. the GridLayer.
	 */
	public PrintCommandHandler(final ILayer layer) {
		this.layer= layer;
	}

	@Override
	public boolean doCommand(final PrintCommand command) {
		new LayerPrinter(this.layer, command.getConfigRegistry()).print(command.getShell());
		return true;
	}

	@Override
	public Class<PrintCommand> getCommandClass() {
		return PrintCommand.class;
	}

}
