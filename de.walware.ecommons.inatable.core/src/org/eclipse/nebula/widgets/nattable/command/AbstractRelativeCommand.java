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
// +
package org.eclipse.nebula.widgets.nattable.command;

import org.eclipse.nebula.widgets.nattable.coordinate.Direction;


public abstract class AbstractRelativeCommand extends AbstractContextFreeCommand {


	private final Direction direction;

	private int stepCount;


	public AbstractRelativeCommand(final Direction direction, final int stepCount) {
		if (direction == null) {
			throw new NullPointerException("direction");
		}
		this.direction = direction;
		this.stepCount = stepCount;
	}

	protected AbstractRelativeCommand(final AbstractRelativeCommand command) {
		this.direction = command.direction;
		this.stepCount = command.stepCount;
	}

	@Override
	public abstract AbstractRelativeCommand cloneCommand();


	public Direction getDirection() {
		return direction;
	}

	public int getStepCount() {
		return stepCount;
	}


}