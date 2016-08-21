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

import de.walware.ecommons.waltable.command.AbstractDimPositionCommand;
import de.walware.ecommons.waltable.layer.ILayerDim;


public class ShowDimPositionInViewportCommand extends AbstractDimPositionCommand {
	
	
	public ShowDimPositionInViewportCommand(final ILayerDim layerDim, final long position) {
		super(layerDim, position);
	}
	
	protected ShowDimPositionInViewportCommand(final ShowDimPositionInViewportCommand command) {
		super(command);
	}
	
	@Override
	public ShowDimPositionInViewportCommand cloneCommand() {
		return new ShowDimPositionInViewportCommand(this);
	}
	
	
}
