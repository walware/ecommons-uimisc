/*******************************************************************************
 * Copyright (c) 2010-2016 Stephan Wahlbrink (WalWare.de) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/
// +
package de.walware.ecommons.waltable.selection;

import de.walware.ecommons.waltable.command.AbstractRelativeCommand;
import de.walware.ecommons.waltable.coordinate.Direction;


public abstract class AbstractSelectRelativeCommand extends AbstractRelativeCommand {


	private final int selectionFlags;


	protected AbstractSelectRelativeCommand(final Direction direction, final long stepCount, final int selectionFlags) {
		super(direction, stepCount);
		
		this.selectionFlags= selectionFlags;
	}

	protected AbstractSelectRelativeCommand(final AbstractSelectRelativeCommand command) {
		super(command);
		
		this.selectionFlags= command.selectionFlags;
	}

	@Override
	public abstract AbstractSelectRelativeCommand cloneCommand();


	public int getSelectionFlags() {
		return this.selectionFlags;
	}

}
