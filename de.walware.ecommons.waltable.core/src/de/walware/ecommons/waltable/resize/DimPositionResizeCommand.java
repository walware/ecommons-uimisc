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
import de.walware.ecommons.waltable.layer.ILayerDim;


/**
 * Event indicating that a column has been resized.
 */
public class DimPositionResizeCommand extends AbstractDimPositionCommand {
	
	
	private final int newSize;
	
	
	public DimPositionResizeCommand(final ILayerDim layerDim, final long position, final int newSize) {
		super(layerDim, position);
		
		this.newSize= newSize;
	}
	
	protected DimPositionResizeCommand(final DimPositionResizeCommand command) {
		super(command);
		
		this.newSize= command.newSize;
	}
	
	
	public int getNewSize() {
		return this.newSize;
	}
	
	@Override
	public DimPositionResizeCommand cloneCommand() {
		return new DimPositionResizeCommand(this);
	}
	
}
