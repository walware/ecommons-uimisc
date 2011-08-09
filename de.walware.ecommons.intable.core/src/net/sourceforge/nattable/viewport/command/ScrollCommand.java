/*******************************************************************************
 * Copyright (c) 2010 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package net.sourceforge.nattable.viewport.command;

import net.sourceforge.nattable.command.AbstractNavigationCommand;
import net.sourceforge.nattable.coordinate.IRelative.Direction;
import net.sourceforge.nattable.coordinate.IRelative.Scale;



public class ScrollCommand extends AbstractNavigationCommand {
	
	
	public ScrollCommand(Direction direction, Scale stepSize) {
		super(direction, stepSize);
	}
	
	@Override
	public ScrollCommand cloneCommand() {
		return new ScrollCommand(getDirection(), getScale());
	}
	
	
}
