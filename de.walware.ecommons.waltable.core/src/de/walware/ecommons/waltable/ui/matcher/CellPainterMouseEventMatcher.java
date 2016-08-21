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
package de.walware.ecommons.waltable.ui.matcher;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.config.CellConfigAttributes;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.painter.cell.ICellPainter;


/**
 * Matches a mouse click on a given cell painter within a cell.
 */
public class CellPainterMouseEventMatcher extends MouseEventMatcher {
	
	
	private ICellPainter targetCellPainter;
	private Class<? extends ICellPainter> targetCellPainterClass;
	
	
	public CellPainterMouseEventMatcher(final String regionName, final int button, final ICellPainter targetCellPainter) {
		super(regionName, button);
		this.targetCellPainter= targetCellPainter;
	}
	
	public CellPainterMouseEventMatcher(final String regionName, final int button, final Class<? extends ICellPainter> targetCellPainterClass) {
		super(regionName, button);
		this.targetCellPainterClass= targetCellPainterClass;
	}
	
	
	@Override
	public boolean matches(final NatTable natTable, final MouseEvent event, final LabelStack regionLabels) {
		if (super.matches(natTable, event, regionLabels)) {
			final long columnPosition= natTable.getColumnPositionByX(event.x);
			final long rowPosition= natTable.getRowPositionByY(event.y);
			
			final ILayerCell cell= natTable.getCellByPosition(columnPosition, rowPosition);
			
			//Bug 407598: only perform a check if the click in the body region was performed on a cell
			//cell == null can happen if the viewport is quite large and contains not enough cells to fill it.
			if (cell != null) {
				final IConfigRegistry configRegistry= natTable.getConfigRegistry();
				final ICellPainter cellPainter= configRegistry.getConfigAttribute(CellConfigAttributes.CELL_PAINTER,
						cell.getDisplayMode(), cell.getConfigLabels().getLabels() );
				
				final GC gc= new GC(natTable.getDisplay());
				try {
					final LRectangle adjustedCellBounds= natTable.getLayerPainter().adjustCellBounds(columnPosition, rowPosition, cell.getBounds());
					
					final ICellPainter clickedCellPainter= cellPainter.getCellPainterAt(event.x, event.y, cell, gc, adjustedCellBounds, configRegistry);
					if (clickedCellPainter != null) {
						if (	(this.targetCellPainter != null && this.targetCellPainter == clickedCellPainter) ||
								(this.targetCellPainterClass != null && this.targetCellPainterClass.isInstance(clickedCellPainter))) {
							return true;
						}
					}
				} finally {
					gc.dispose();
				}
			}
		}
		return false;
	}
	
}
