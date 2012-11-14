/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.sort.command;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.custom.BusyIndicator;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.sort.ISortModel;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.sort.event.SortColumnEvent;


public class ClearSortCommandHandler extends AbstractLayerCommandHandler<ClearSortCommand> {


	private final ISortModel sortModel;
	private final SortHeaderLayer<?> sortHeaderLayer;


	public ClearSortCommandHandler(ISortModel sortModel, SortHeaderLayer<?> sortHeaderLayer) {
		this.sortModel = sortModel;
		this.sortHeaderLayer = sortHeaderLayer;
	}


	public Class<ClearSortCommand> getCommandClass() {
		return ClearSortCommand.class;
	}

	@Override
	protected boolean doCommand(ClearSortCommand command) {
		// with busy indicator
		Runnable sortRunner = new Runnable() {
			public void run() {
				// collect sorted columns for event
				final int columnCount = sortHeaderLayer.getColumnCount();
				Collection<Integer> sortedColumns = new ArrayList<Integer>();
				for (int i = 0; i < columnCount; i++) {
					if (sortModel.isColumnIndexSorted(i)) {
						sortedColumns.add(Integer.valueOf(i));
					}
				}
				
				sortModel.clear();
				
				// Fire event
				SortColumnEvent sortEvent = new SortColumnEvent(sortHeaderLayer, sortedColumns);
				sortHeaderLayer.fireLayerEvent(sortEvent);
			}
		};
		BusyIndicator.showWhile(null, sortRunner);
		
		return true;
	}

}