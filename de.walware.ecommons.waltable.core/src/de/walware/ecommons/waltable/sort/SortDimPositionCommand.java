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

package de.walware.ecommons.waltable.sort;

import de.walware.ecommons.waltable.command.AbstractDimPositionCommand;
import de.walware.ecommons.waltable.layer.ILayerDim;


public class SortDimPositionCommand extends AbstractDimPositionCommand {
	
	
	private final boolean accumulate;
	
	private final SortDirection direction;
	
	
	public SortDimPositionCommand(final ILayerDim layerDim, final long columnPosition,
			final boolean accumulate) {
		this(layerDim, columnPosition, null, accumulate);
	}
	
	public SortDimPositionCommand(final ILayerDim layerDim, final long columnPosition,
			final SortDirection direction, final boolean accumulate) {
		super(layerDim, columnPosition);
		
		this.direction= direction;
		this.accumulate= accumulate;
	}
	
	protected SortDimPositionCommand(final SortDimPositionCommand command) {
		super(command);
		
		this.accumulate= command.accumulate;
		this.direction= command.direction;
	}
	
	@Override
	public SortDimPositionCommand cloneCommand() {
		return new SortDimPositionCommand(this);
	}
	
	
	public boolean isAccumulate() {
		return this.accumulate;
	}
	
	/**
	 * The sort direction, if specified.
	 * 
	 * @return the sort direction or <code>null</code> for automatic iteration
	 */
	public SortDirection getDirection() {
		return this.direction;
	}
	
}
