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
package de.walware.ecommons.waltable.command;

import de.walware.ecommons.waltable.coordinate.PositionCoordinate;
import de.walware.ecommons.waltable.layer.ILayer;


public abstract class AbstractPositionCommand implements ILayerCommand {
	
	
	private PositionCoordinate positionCoordinate;
	
	
	protected AbstractPositionCommand(final ILayer layer, final long columnPosition, final long rowPosition) {
		this.positionCoordinate= new PositionCoordinate(layer, columnPosition, rowPosition);
	}
	
	protected AbstractPositionCommand(final AbstractPositionCommand command) {
		this.positionCoordinate= command.positionCoordinate;
	}
	
	
	public long getColumnPosition() {
		return this.positionCoordinate.getColumnPosition();
	}
	
	public long getRowPosition() {
		return this.positionCoordinate.getRowPosition();
	}
	
	@Override
	public boolean convertToTargetLayer(final ILayer targetLayer) {
		final PositionCoordinate targetPositionCoordinate= LayerCommandUtil.convertPositionToTargetContext(this.positionCoordinate, targetLayer);
		if (targetPositionCoordinate != null) {
			this.positionCoordinate= targetPositionCoordinate;
			return true;
		} else {
			return false;
		}
	}
	
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " columnPosition=" + this.positionCoordinate.getColumnPosition() + ", rowPosition=" + this.positionCoordinate.getRowPosition(); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
}
