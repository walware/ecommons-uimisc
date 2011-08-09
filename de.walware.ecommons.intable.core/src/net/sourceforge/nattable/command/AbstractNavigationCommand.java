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

package net.sourceforge.nattable.command;

import net.sourceforge.nattable.coordinate.IRelative.Direction;
import net.sourceforge.nattable.coordinate.IRelative.Scale;
import net.sourceforge.nattable.layer.ILayer;
import net.sourceforge.nattable.viewport.ViewportLayer;


public abstract class AbstractNavigationCommand extends AbstractContextFreeCommand {
	
	
	private final Direction fDirection;
	
	private Scale fScale;
	
	private int fStepCount;
	
	
	public AbstractNavigationCommand(final Direction direction,
			final Scale scale) {
		fDirection = direction;
		fScale = scale;
		fStepCount = 1;
	}
	
	public AbstractNavigationCommand(final Direction direction,
			final Scale scale, final int stepCount) {
		fDirection = direction;
		fScale = scale;
		fStepCount = stepCount;
	}
	
	protected AbstractNavigationCommand(final AbstractNavigationCommand command) {
		fDirection = command.fDirection;
		fScale = command.fScale;
		fStepCount = command.fStepCount;
	}
	
	@Override
	public abstract AbstractNavigationCommand cloneCommand();
	
	
	public Direction getDirection() {
		return fDirection;
	}
	
	public Scale getScale() {
		return fScale;
	}
	
	public int getStepCount() {
		return fStepCount;
	}
	
	
	@Override
	public boolean convertToTargetLayer(ILayer targetLayer) {
		if (targetLayer instanceof ViewportLayer) {
			if (fScale == Scale.PAGE) {
				switch (fDirection) {
				case UP:
				case DOWN:
					fScale = Scale.CELL;
					fStepCount = targetLayer.getRowCount();
					break;
				case LEFT:
				case RIGHT:
					fScale = Scale.CELL;
					fStepCount = targetLayer.getColumnCount();
					break;
				}
			}
		}
		return (fStepCount >= 0);
	}
	
}
