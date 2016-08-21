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

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.coordinate.PositionCoordinate;
import de.walware.ecommons.waltable.selection.SelectionLayer;
import de.walware.ecommons.waltable.viewport.IViewportDim;
import de.walware.ecommons.waltable.viewport.ViewportLayer;

public class FreezeSelectionStrategy implements IFreezeCoordinatesProvider {

	private final FreezeLayer freezeLayer;

	private final ViewportLayer viewportLayer;
	
	private final SelectionLayer selectionLayer;

	public FreezeSelectionStrategy(final FreezeLayer freezeLayer, final ViewportLayer viewportLayer, final SelectionLayer selectionLayer) {
		this.freezeLayer= freezeLayer;
		this.viewportLayer= viewportLayer;
		this.selectionLayer= selectionLayer;
	}

	@Override
	public PositionCoordinate getTopLeftPosition() {
		final PositionCoordinate selectionAnchor= this.selectionLayer.getSelectionAnchor();
		if (selectionAnchor == null) {
			return null;
		}
		
		return new PositionCoordinate(this.freezeLayer,
				checkPosition(HORIZONTAL, selectionAnchor.columnPosition),
				checkPosition(VERTICAL, selectionAnchor.rowPosition) );
	}
	
	private long checkPosition(final Orientation orientation, final long maxPosition) {
		final IViewportDim dim= this.viewportLayer.getDim(orientation);
		if (dim.getSize() > 0) {
			final long position= dim.getOriginPosition();
			if (position < maxPosition) {
				return position;
			}
		}
		return -1;
	}
	
	@Override
	public PositionCoordinate getBottomRightPosition() {
		final PositionCoordinate selectionAnchor= this.selectionLayer.getSelectionAnchor();
		if (selectionAnchor == null) {
			return null;
		}
		return new PositionCoordinate(this.freezeLayer, selectionAnchor.columnPosition - 1, selectionAnchor.rowPosition - 1);
	}

}
