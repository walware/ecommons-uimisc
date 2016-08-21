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
// ~Selection
package de.walware.ecommons.waltable.viewport;

import java.util.Collection;

import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.selection.AbstractSelectDimPositionsCommand;


/**
 * Command to select column(s)/row(s) in the viewport.
 */
public class ViewportSelectDimPositionsCommand extends AbstractSelectDimPositionsCommand {
	
	
	public ViewportSelectDimPositionsCommand(final ILayerDim dim,
			final long position, final int selectionFlags) {
		super(dim, position, selectionFlags);
	}
	
	public ViewportSelectDimPositionsCommand(final ILayerDim dim,
			final long refPosition, final Collection<LRange> positions,
			final long positionToReveal, final int selectionFlags) {
		super(dim, refPosition, positions, positionToReveal, selectionFlags);
	}
	
	protected ViewportSelectDimPositionsCommand(final ViewportSelectDimPositionsCommand command) {
		super(command);
	}
	
	@Override
	public ViewportSelectDimPositionsCommand cloneCommand() {
		return new ViewportSelectDimPositionsCommand(this);
	}
	
}
