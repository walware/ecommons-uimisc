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
package de.walware.ecommons.waltable.command;

import de.walware.ecommons.waltable.coordinate.Direction;


public abstract class AbstractRelativeCommand extends AbstractContextFreeCommand {
	
	
	private final Direction direction;
	
	private final long stepCount;
	
	
	public AbstractRelativeCommand(final Direction direction, final long stepCount) {
		if (direction == null) {
			throw new NullPointerException("direction"); //$NON-NLS-1$
		}
		this.direction= direction;
		this.stepCount= stepCount;
	}
	
	protected AbstractRelativeCommand(final AbstractRelativeCommand command) {
		this.direction= command.direction;
		this.stepCount= command.stepCount;
	}
	
	@Override
	public abstract AbstractRelativeCommand cloneCommand();
	
	
	public Direction getDirection() {
		return this.direction;
	}
	
	public long getStepCount() {
		return this.stepCount;
	}
	
}
