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

import org.eclipse.swt.custom.BusyIndicator;

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;
import de.walware.ecommons.waltable.layer.ILayerDim;


/**
 * Handle sort commands
 */
public class SortPositionCommandHandler extends AbstractLayerCommandHandler<SortDimPositionCommand> {
	
	
	private final ISortModel sortModel;
	
	
	public SortPositionCommandHandler(final ISortModel sortModel) {
		this.sortModel= sortModel;
	}
	
	
	@Override
	public Class<SortDimPositionCommand> getCommandClass() {
		return SortDimPositionCommand.class;
	}
	
	@Override
	protected boolean doCommand(final SortDimPositionCommand command) {
		final long columnId= command.getDim()
				.getPositionId(command.getPosition(), command.getPosition());
		
		// with busy indicator
		final Runnable sortRunner= new Runnable() {
			@Override
			public void run() {
				final ILayerDim dim= command.getDim();
				
				final SortDirection newSortDirection= (command.getDirection() != null) ?
						command.getDirection() :
							SortPositionCommandHandler.this.sortModel.getSortDirection(columnId).getNextSortDirection();
				SortPositionCommandHandler.this.sortModel.sort(columnId,
						newSortDirection, command.isAccumulate() );
				
				// Fire event
				final SortColumnEvent sortEvent= new SortColumnEvent(dim,
						command.getPosition() );
				dim.getLayer().fireLayerEvent(sortEvent);
			}
		};
		BusyIndicator.showWhile(null, sortRunner);
		
		return true;
	}
	
}
