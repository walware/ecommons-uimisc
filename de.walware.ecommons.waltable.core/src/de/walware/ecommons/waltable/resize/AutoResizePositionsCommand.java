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
// -GC
package de.walware.ecommons.waltable.resize;

import de.walware.ecommons.waltable.command.AbstractDimPositionsCommand;
import de.walware.ecommons.waltable.command.ILayerCommand;
import de.walware.ecommons.waltable.coordinate.LRangeList;
import de.walware.ecommons.waltable.layer.ILayerDim;


/**
 * Command indicating that all selected columns have to be auto resized i.e made
 * wide enough to just fit the widest cell. This should also take the column
 * header into account
 * 
 * Note: The {@link InitializeAutoResizeCommand} has to be fired first
 * when autoresizing columns.
 */
public class AutoResizePositionsCommand extends AbstractDimPositionsCommand {
	
	
	public AutoResizePositionsCommand(final ILayerDim layerDim, final LRangeList columnPositions) {
		super(layerDim, columnPositions);
	}
	
	protected AutoResizePositionsCommand(final AutoResizePositionsCommand command) {
		super(command);
	}
	
	@Override
	public ILayerCommand cloneCommand() {
		return new AutoResizePositionsCommand(this);
	}
	
}
