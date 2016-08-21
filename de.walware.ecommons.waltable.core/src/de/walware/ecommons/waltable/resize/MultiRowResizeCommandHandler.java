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

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;
import de.walware.ecommons.waltable.coordinate.ILValueIterator;
import de.walware.ecommons.waltable.coordinate.LRangeList.ValueIterator;
import de.walware.ecommons.waltable.layer.DataLayer;

public class MultiRowResizeCommandHandler extends AbstractLayerCommandHandler<MultiRowResizeCommand> {

	private final DataLayer dataLayer;

	public MultiRowResizeCommandHandler(final DataLayer dataLayer) {
		this.dataLayer= dataLayer;
	}
	
	@Override
	public Class<MultiRowResizeCommand> getCommandClass() {
		return MultiRowResizeCommand.class;
	}

	@Override
	protected boolean doCommand(final MultiRowResizeCommand command) {
		for (final ILValueIterator rowIter= new ValueIterator(command.getPositions()); rowIter.hasNext(); ) {
			final long rowPosition= rowIter.nextValue();
			this.dataLayer.setRowHeightByPosition(rowPosition, command.getRowHeight(rowPosition));
		}
		return true;
	}

}
