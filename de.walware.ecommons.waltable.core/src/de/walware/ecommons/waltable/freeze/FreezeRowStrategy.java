/*******************************************************************************
 * Copyright (c) 2013-2016 Dirk Fauth and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Dirk Fauth <dirk.fauth@gmail.com> - initial API and implementation
 *******************************************************************************/
package de.walware.ecommons.waltable.freeze;

import de.walware.ecommons.waltable.coordinate.PositionCoordinate;


public class FreezeRowStrategy implements IFreezeCoordinatesProvider {

	private final FreezeLayer freezeLayer;
	
	private final long rowPosition;

	public FreezeRowStrategy(final FreezeLayer freezeLayer, final long rowPosition) {
		this.freezeLayer= freezeLayer;
		this.rowPosition= rowPosition;
	}

	@Override
	public PositionCoordinate getTopLeftPosition() {
		return new PositionCoordinate(this.freezeLayer, -1, 0);
	}

	@Override
	public PositionCoordinate getBottomRightPosition() {
		return new PositionCoordinate(this.freezeLayer, -1, this.rowPosition);
	}

}
