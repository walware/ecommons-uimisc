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

public abstract class AbstractCellPainter implements ICellPainter {

	@Override
	public ICellPainter getCellPainterAt(final long x, final long y, final ILayerCell cell, final GC gc,
			final LRectangle adjustedCellBounds, final IConfigRegistry configRegistry) {
		return this;
	}

}
