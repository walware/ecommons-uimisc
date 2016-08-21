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


public abstract class AbstractLayerCommandHandler<T extends ILayerCommand> implements ILayerCommandHandler<T> {
	
	
	@Override
	public final boolean doCommand(final ILayer targetLayer, final T command) {
		if (command.convertToTargetLayer(targetLayer)) {
			return doCommand(command);
		}
		return false;
	}
	
	protected abstract boolean doCommand(T command);
	
}
