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

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;
import de.walware.ecommons.waltable.layer.DataLayer;


public class PositionResizeCommandHandler extends AbstractLayerCommandHandler<DimPositionResizeCommand> {
	
	
	private final DataLayer dataLayer;
	
	
	public PositionResizeCommandHandler(final DataLayer dataLayer) {
		this.dataLayer= dataLayer;
	}
	
	
	@Override
	public Class<DimPositionResizeCommand> getCommandClass() {
		return DimPositionResizeCommand.class;
	}
	
	@Override
	protected boolean doCommand(final DimPositionResizeCommand command) {
		if (command.getOrientation() == HORIZONTAL) {
			this.dataLayer.setColumnWidthByPosition(command.getPosition(), command.getNewSize());
		}
		else {
			this.dataLayer.setRowHeightByPosition(command.getPosition(), command.getNewSize());
		}
		return true;
	}
	
}
