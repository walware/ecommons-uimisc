/*******************************************************************************
 * Copyright (c) 2012, 2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.nattable.ui.action;

import org.eclipse.nebula.widgets.nattable.coordinate.Direction;


public abstract class AbstractNavigationAction implements IKeyAction {


	private final Direction direction;


	public AbstractNavigationAction(final Direction direction) {
		this.direction = direction;
	}


	public Direction getDirection() {
		return direction;
	}

}
