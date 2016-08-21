/*******************************************************************************
 * Copyright (c) 2012-2016 Stephan Wahlbrink (WalWare.de) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.waltable.ui.action;

import de.walware.ecommons.waltable.coordinate.Direction;


public abstract class AbstractNavigationAction implements IKeyAction {


	private final Direction direction;


	public AbstractNavigationAction(final Direction direction) {
		this.direction= direction;
	}


	public Direction getDirection() {
		return this.direction;
	}

}
