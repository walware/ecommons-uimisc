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

import de.walware.ecommons.waltable.command.AbstractPositionCommand;
import de.walware.ecommons.waltable.layer.ILayer;

public class ShowCellInViewportCommand extends AbstractPositionCommand {
	
	public ShowCellInViewportCommand(final ILayer layer, final long columnPosition, final long rowPosition) {
		super(layer, columnPosition, rowPosition);
	}
	
	protected ShowCellInViewportCommand(final ShowCellInViewportCommand command) {
		super(command);
	}
	
	@Override
	public ShowCellInViewportCommand cloneCommand() {
		return new ShowCellInViewportCommand(this);
	}

}
