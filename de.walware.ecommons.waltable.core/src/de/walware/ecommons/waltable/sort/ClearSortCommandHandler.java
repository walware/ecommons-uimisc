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

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;

import java.util.List;

import org.eclipse.swt.custom.BusyIndicator;

import de.walware.ecommons.waltable.command.ILayerCommandHandler;
import de.walware.ecommons.waltable.coordinate.LRangeList;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.ILayerDim;


public class ClearSortCommandHandler implements ILayerCommandHandler<ClearSortCommand> {
	
	
	private final ISortModel sortModel;
	
	
	public ClearSortCommandHandler(final ISortModel sortModel) {
		this.sortModel= sortModel;
	}
	
	
	@Override
	public Class<ClearSortCommand> getCommandClass() {
		return ClearSortCommand.class;
	}
	
	
	@Override
	public boolean doCommand(final ILayer targetLayer, final ClearSortCommand command) {
		// with busy indicator
		final Runnable sortRunner= new Runnable() {
			@Override
			public void run() {
				final ILayerDim dim= targetLayer.getDim(HORIZONTAL);
				
				final List<Long> sortedIds= ClearSortCommandHandler.this.sortModel.getSortedColumnIds();
				final LRangeList sortedPositions= new LRangeList();
				for (final Long id : sortedIds) {
					final long position= dim.getPositionById(id);
					if (position != ILayerDim.POSITION_NA) {
						sortedPositions.values().add(position);
					}
				}
				
				ClearSortCommandHandler.this.sortModel.clear();
				
				// Fire event
				final SortColumnEvent sortEvent= new SortColumnEvent(dim,
						sortedPositions );
				dim.getLayer().fireLayerEvent(sortEvent);
			}
		};
		BusyIndicator.showWhile(null, sortRunner);
		
		return true;
	}
	
}
