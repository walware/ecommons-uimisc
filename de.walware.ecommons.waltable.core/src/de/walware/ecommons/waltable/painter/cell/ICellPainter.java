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

/**
 * Implementations are responsible for painting a cell.
 * 
 * Custom {@link ICellPainter} can be registered in the {@link IConfigRegistry}.
 * This is a mechanism for plugging in custom cell painting.
 * 
 * @see PercentageBarCellPainter
 */
public interface ICellPainter {
	
	
	void paintCell(ILayerCell cell, GC gc, LRectangle bounds, IConfigRegistry configRegistry);
	
	/**
	 * Get the preferred width of the cell when rendered by this painter. Used for auto-resize.
	 * @param cell
	 * @param gc
	 * @param configRegistry
	 * @return
	 */
	long getPreferredWidth(ILayerCell cell, GC gc, IConfigRegistry configRegistry);
	
	/**
	 * Get the preferred height of the cell when rendered by this painter. Used for auto-resize.
	 * @param cell
	 * @param gc
	 * @param configRegistry
	 * @return
	 */
	long getPreferredHeight(ILayerCell cell, GC gc, IConfigRegistry configRegistry);
	
	ICellPainter getCellPainterAt(long x, long y, ILayerCell cell, GC gc,
			LRectangle adjustedCellBounds, IConfigRegistry configRegistrys);
	
}
