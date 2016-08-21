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
package de.walware.ecommons.waltable.export;


import org.eclipse.swt.widgets.Shell;

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.layer.ILayer;

public class ExportCommandHandler extends AbstractLayerCommandHandler<ExportCommand> {

	private final ILayer layer;

	public ExportCommandHandler(final ILayer layer) {
		this.layer= layer;
	}

	@Override
	public boolean doCommand(final ExportCommand command) {
		final Shell shell= command.getShell();
		final IConfigRegistry configRegistry= command.getConfigRegistry();
		
		new NatExporter(shell).exportSingleLayer(this.layer, configRegistry);
		
		return true;
	}

	@Override
	public Class<ExportCommand> getCommandClass() {
		return ExportCommand.class;
	}

}
