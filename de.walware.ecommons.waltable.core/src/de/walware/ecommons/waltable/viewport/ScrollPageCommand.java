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
package de.walware.ecommons.waltable.viewport;

import de.walware.ecommons.waltable.command.AbstractRelativeCommand;
import de.walware.ecommons.waltable.coordinate.Direction;


public class ScrollPageCommand extends AbstractRelativeCommand {
	
	
	public ScrollPageCommand(final Direction direction) {
		super(direction, 1);
	}
	
	protected ScrollPageCommand(final ScrollPageCommand command) {
		super(command);
	}
	
	@Override
	public ScrollPageCommand cloneCommand() {
		return new ScrollPageCommand(this);
	}
	
	
}
