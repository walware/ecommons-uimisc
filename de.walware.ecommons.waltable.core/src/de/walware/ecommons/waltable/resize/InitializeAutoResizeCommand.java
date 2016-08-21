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

package de.walware.ecommons.waltable.resize;

import de.walware.ecommons.waltable.command.AbstractDimPositionCommand;
import de.walware.ecommons.waltable.command.ILayerCommand;
import de.walware.ecommons.waltable.grid.layer.GridLayer;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.selection.SelectionLayer;


/**
 * This command triggers the AutoResizeColumms command. It collects the selected
 * columns from the {@link SelectionLayer} and fires the
 * {@link AutoResizePositionsCommand} on the {@link GridLayer}
 */
public class InitializeAutoResizeCommand extends AbstractDimPositionCommand {
	
	
	public InitializeAutoResizeCommand(final ILayerDim layer, final long position) {
		super(layer, position);
	}
	
	protected InitializeAutoResizeCommand(final InitializeAutoResizeCommand command) {
		super(command);
	}
	
	@Override
	public ILayerCommand cloneCommand() {
		return new InitializeAutoResizeCommand(this);
	}
	
	
}
