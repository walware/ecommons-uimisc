/*******************************************************************************
 * Copyright (c) 2010-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
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

import org.eclipse.nebula.widgets.nattable.coordinate.Direction;


public class SelectRelativeCellCommand extends AbstractSelectRelativeCommand {


	public SelectRelativeCellCommand(final Direction direction) {
		this(direction, 1, 0);
	}
	
	public SelectRelativeCellCommand(final Direction direction, int stepCount, final int selectionFlags) {
		super(direction, stepCount, selectionFlags);
	}

	protected SelectRelativeCellCommand(SelectRelativeCellCommand command) {
		super(command);
	}

	@Override
	public SelectRelativeCellCommand cloneCommand() {
		return new SelectRelativeCellCommand(this);
	}


}
