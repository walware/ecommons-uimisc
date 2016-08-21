/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/

package de.walware.ecommons.waltable.freeze;

import de.walware.ecommons.waltable.coordinate.PositionCoordinate;

public class FreezeColumnStrategy implements IFreezeCoordinatesProvider {
	
	
	private final FreezeLayer freezeLayer;
	
	private final long columnPosition;
	
	
	public FreezeColumnStrategy(final FreezeLayer freezeLayer, final long columnPosition) {
		this.freezeLayer= freezeLayer;
		this.columnPosition= columnPosition;
	}
	
	
	@Override
	public PositionCoordinate getTopLeftPosition() {
		return new PositionCoordinate(this.freezeLayer, 0, -1);
	}
	
	@Override
	public PositionCoordinate getBottomRightPosition() {
		return new PositionCoordinate(this.freezeLayer, this.columnPosition, -1);
	}
	
}
