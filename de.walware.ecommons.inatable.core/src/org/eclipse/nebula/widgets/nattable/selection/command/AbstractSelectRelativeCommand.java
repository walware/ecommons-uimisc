/*******************************************************************************
 * Copyright (c) 2010-2014 Stephan Wahlbrink (WalWare.de) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/
// +
package org.eclipse.nebula.widgets.nattable.selection.command;

import org.eclipse.nebula.widgets.nattable.command.AbstractRelativeCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.Direction;


public abstract class AbstractSelectRelativeCommand extends AbstractRelativeCommand {


	private final int selectionFlags;


	protected AbstractSelectRelativeCommand(final Direction direction, long stepCount, final int selectionFlags) {
		super(direction, stepCount);
		
		this.selectionFlags = selectionFlags;
	}

	protected AbstractSelectRelativeCommand(AbstractSelectRelativeCommand command) {
		super(command);
		
		this.selectionFlags = command.selectionFlags;
	}

	@Override
	public abstract AbstractSelectRelativeCommand cloneCommand();


	public int getSelectionFlags() {
		return selectionFlags;
	}

}
