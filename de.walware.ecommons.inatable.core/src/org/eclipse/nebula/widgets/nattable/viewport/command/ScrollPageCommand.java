/*******************************************************************************
 * Copyright (c) 2010-2012 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.nattable.viewport.command;

import org.eclipse.nebula.widgets.nattable.command.AbstractRelativeCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.Direction;


public class ScrollPageCommand extends AbstractRelativeCommand {


	public ScrollPageCommand(Direction direction) {
		super(direction, 1);
	}

	protected ScrollPageCommand(ScrollPageCommand command) {
		super(command);
	}

	@Override
	public ScrollPageCommand cloneCommand() {
		return new ScrollPageCommand(this);
	}


}
