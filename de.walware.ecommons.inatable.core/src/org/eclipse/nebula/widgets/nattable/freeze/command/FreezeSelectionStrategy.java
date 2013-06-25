/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.freeze.command;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;
import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;

import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.freeze.FreezeLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.IViewportDim;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;

public class FreezeSelectionStrategy implements IFreezeCoordinatesProvider {

	private final FreezeLayer freezeLayer;

	private final ViewportLayer viewportLayer;
	
	private final SelectionLayer selectionLayer;

	public FreezeSelectionStrategy(FreezeLayer freezeLayer, ViewportLayer viewportLayer, SelectionLayer selectionLayer) {
		this.freezeLayer = freezeLayer;
		this.viewportLayer = viewportLayer;
		this.selectionLayer = selectionLayer;
	}

	public PositionCoordinate getTopLeftPosition() {
		PositionCoordinate selectionAnchor = selectionLayer.getSelectionAnchor();
		if (selectionAnchor == null) {
			return null;
		}
		
		return new PositionCoordinate(freezeLayer,
				checkPosition(HORIZONTAL, selectionAnchor.columnPosition),
				checkPosition(VERTICAL, selectionAnchor.rowPosition) );
	}
	
	private long checkPosition(final Orientation orientation, final long maxPosition) {
		final IViewportDim dim = viewportLayer.getDim(orientation);
		if (dim.getSize() > 0) {
			final long position = dim.getOriginPosition();
			if (position < maxPosition) {
				return position;
			}
		}
		return -1;
	}
	
	public PositionCoordinate getBottomRightPosition() {
		PositionCoordinate selectionAnchor = selectionLayer.getSelectionAnchor();
		if (selectionAnchor == null) {
			return null;
		}
		return new PositionCoordinate(freezeLayer, selectionAnchor.columnPosition - 1, selectionAnchor.rowPosition - 1);
	}

}
