/*******************************************************************************
 * Copyright (c) 2010-2015 Stephan Wahlbrink (WalWare.de) and others.
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


public class ScrollStepCommand extends AbstractRelativeCommand {
	
	
	public ScrollStepCommand(Direction direction) {
		super(direction, 1);
	}
	
	protected ScrollStepCommand(ScrollStepCommand command) {
		super(command);
	}
	
	@Override
	public ScrollStepCommand cloneCommand() {
		return new ScrollStepCommand(this);
	}
	
	
}
