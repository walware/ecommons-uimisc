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
package de.walware.ecommons.waltable.command;

import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.event.VisualRefreshEvent;

/**
 * Command handler for handling {@link VisualRefreshCommand}s.
 * Simply fires a {@link VisualRefreshEvent}.
 * 
 * Needed to be able to refresh all layers by simply calling a command on the NatTable
 * instance itself (Remember that events are fired bottom up the layer stack while commands
 * are propagated top down). 
 * 
 * To refresh all layers by calling a {@link VisualRefreshCommand} on the NatTable
 * instance, the {@link VisualRefreshCommandHandler} should be registered against
 * the DataLayer.
 */
public class VisualRefreshCommandHandler implements ILayerCommandHandler<VisualRefreshCommand> {

	@Override
	public Class<VisualRefreshCommand> getCommandClass() {
		return VisualRefreshCommand.class;
	}

	@Override
	public boolean doCommand(final ILayer targetLayer, final VisualRefreshCommand command) {
		targetLayer.fireLayerEvent(new VisualRefreshEvent(targetLayer));
		return true;
	}

}
