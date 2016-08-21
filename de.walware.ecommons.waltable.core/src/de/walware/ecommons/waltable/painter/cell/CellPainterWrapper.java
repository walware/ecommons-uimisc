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
package de.walware.ecommons.waltable.painter.cell;


import org.eclipse.swt.graphics.GC;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;

public abstract class CellPainterWrapper extends AbstractCellPainter {

	private ICellPainter wrappedPainter;

	public CellPainterWrapper() {}

	public CellPainterWrapper(final ICellPainter painter) {
		this.wrappedPainter= painter;
	}

	public void setWrappedPainter(final ICellPainter painter) {
		this.wrappedPainter= painter;
	}

	public ICellPainter getWrappedPainter() {
		return this.wrappedPainter;
	}

	public LRectangle getWrappedPainterBounds(final ILayerCell cell, final GC gc, final LRectangle bounds, final IConfigRegistry configRegistry) {
		return bounds;
	}

	@Override
	public ICellPainter getCellPainterAt(final long x, final long y, final ILayerCell cell, final GC gc, final LRectangle adjustedCellBounds, final IConfigRegistry configRegistry) {
		final LRectangle wrappedPainterBounds= getWrappedPainterBounds(cell, gc, adjustedCellBounds, configRegistry);
		if (this.wrappedPainter != null && wrappedPainterBounds.contains(x, y)) {
			return this.wrappedPainter.getCellPainterAt(x, y, cell, gc, wrappedPainterBounds, configRegistry);
		} else {
			return super.getCellPainterAt(x, y, cell, gc, adjustedCellBounds, configRegistry);
		}
	}

	@Override
	public long getPreferredWidth(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
		return this.wrappedPainter != null ? this.wrappedPainter.getPreferredWidth(cell, gc, configRegistry) : 0;
	}

	@Override
	public long getPreferredHeight(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
		return this.wrappedPainter != null ? this.wrappedPainter.getPreferredHeight(cell, gc, configRegistry) : 0;
	}

	@Override
	public void paintCell(final ILayerCell cell, final GC gc, final LRectangle adjustedCellBounds, final IConfigRegistry configRegistry) {
		if (this.wrappedPainter != null) {
			this.wrappedPainter.paintCell(cell, gc, adjustedCellBounds, configRegistry);
		}
	}

}
