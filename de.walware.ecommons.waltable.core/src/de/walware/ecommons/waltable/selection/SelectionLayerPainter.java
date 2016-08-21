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
package de.walware.ecommons.waltable.selection;

import static de.walware.ecommons.waltable.painter.cell.GraphicsUtils.safe;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import de.walware.ecommons.waltable.config.CellConfigAttributes;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LPoint;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.painter.layer.GridLineCellLayerPainter;
import de.walware.ecommons.waltable.style.BorderStyle;
import de.walware.ecommons.waltable.style.CellStyleAttributes;
import de.walware.ecommons.waltable.style.DisplayMode;
import de.walware.ecommons.waltable.style.IStyle;
import de.walware.ecommons.waltable.style.SelectionStyleLabels;
import de.walware.ecommons.waltable.swt.SWTUtil;
import de.walware.ecommons.waltable.util.GUIHelper;


public class SelectionLayerPainter extends GridLineCellLayerPainter {
	
	
	private long columnPositionOffset;
	
	private long rowPositionOffset;
	
	private Map<LPoint, ILayerCell> cells;
	
	
	@Override
	public void paintLayer(final ILayer natLayer, final GC gc,
			final int xOffset, final int yOffset, final Rectangle pixelRectangle,
			final IConfigRegistry configRegistry) {
		final LRectangle positionRectangle= getPositionRectangleFromPixelRectangle(natLayer, pixelRectangle);
		this.columnPositionOffset= positionRectangle.x;
		this.rowPositionOffset= positionRectangle.y;
		this.cells= new HashMap<>();
		
		super.paintLayer(natLayer, gc, xOffset, yOffset, pixelRectangle, configRegistry);
		
		// Save gc settings
		final int originalLineStyle= gc.getLineStyle();
		final Color originalForeground= gc.getForeground();
		
		// Apply border settings
		//applyBorderStyle(gc, configRegistry);
		gc.setLineStyle(SWT.LINE_CUSTOM);
		gc.setLineDash(new int[] { 1, 1 });
		gc.setForeground(GUIHelper.COLOR_BLACK);
		
		// Draw horizontal borders
		boolean selectedMode= false;
		for (long columnPosition= this.columnPositionOffset; columnPosition < this.columnPositionOffset + positionRectangle.width; columnPosition++) {
			ILayerCell previousCell= null;
			ILayerCell currentCell= null;
			for (long rowPosition= this.rowPositionOffset; rowPosition < this.rowPositionOffset + positionRectangle.height; rowPosition++) {
				currentCell= this.cells.get(new LPoint(columnPosition, rowPosition));
				if (currentCell != null) {
					if (selectedMode != isSelected(currentCell)) {
						selectedMode= !selectedMode;
						
						// Draw minimal shared border between previous and current cell
						final LRectangle currentCellBounds= currentCell.getBounds();
						
						long x0= safe(currentCellBounds.x - 1);
						long x1= safe(currentCellBounds.x + currentCellBounds.width - 1);
						if (previousCell != null) {
							final LRectangle previousCellBounds= previousCell.getBounds();
							x0= Math.max(x0, previousCellBounds.x - 1);
							x1= Math.min(x1, previousCellBounds.x + previousCellBounds.width - 1);
						}
						final int y= safe(currentCellBounds.y - 1);
						
						gc.drawLine(safe(x0), y, safe(x1), y);
					}
				}
				previousCell= currentCell;
			}
			if (selectedMode && previousCell != null) {
				// If last cell is selected, draw its bottom edge
				final LRectangle cellBounds= previousCell.getBounds();
				gc.drawLine(
						safe(cellBounds.x - 1),
						safe(cellBounds.y + cellBounds.height - 1),
						safe(cellBounds.x + cellBounds.width - 1),
						safe(cellBounds.y + cellBounds.height - 1) );
			}
			selectedMode= false;
		}
		
		// Draw vertical borders
		for (long rowPosition= this.rowPositionOffset; rowPosition < this.rowPositionOffset + positionRectangle.height; rowPosition++) {
			ILayerCell previousCell= null;
			ILayerCell currentCell= null;
			for (long columnPosition= this.columnPositionOffset; columnPosition < this.columnPositionOffset + positionRectangle.width; columnPosition++) {
				currentCell= this.cells.get(new LPoint(columnPosition, rowPosition));
				if (currentCell != null) {
					if (selectedMode != isSelected(currentCell)) {
						selectedMode= !selectedMode;
						
						// Draw minimal shared border between previous and current cell
						final LRectangle currentCellBounds= currentCell.getBounds();
						
						final int x= safe(currentCellBounds.x - 1);
						
						long y0= currentCellBounds.y - 1;
						long y1= currentCellBounds.y + currentCellBounds.height - 1;
						if (previousCell != null) {
							final LRectangle previousCellBounds= previousCell.getBounds();
							y0= Math.max(y0, previousCellBounds.y - 1);
							y1= Math.min(y1, previousCellBounds.y + previousCellBounds.height - 1);
						}
						
						gc.drawLine(x, safe(y0), x, safe(y1));
					}
				}
				previousCell= currentCell;
			}
			if (selectedMode && previousCell != null) {
				// If last cell is selected, draw its right edge
				final LRectangle cellBounds= previousCell.getBounds();
				gc.drawLine(
						safe(cellBounds.x + cellBounds.width - 1),
						safe(cellBounds.y - 1),
						safe(cellBounds.x + cellBounds.width - 1),
						safe(cellBounds.y + cellBounds.height - 1) );
			}
			selectedMode= false;
		}
		
		// Restore original gc settings
		gc.setLineStyle(originalLineStyle);
		gc.setForeground(originalForeground);
	}
	
	@Override
	protected void paintCell(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
		for (long columnPosition= cell.getOriginColumnPosition(); columnPosition < cell.getOriginColumnPosition() + cell.getColumnSpan(); columnPosition++) {
			for (long rowPosition= cell.getOriginRowPosition(); rowPosition < cell.getOriginRowPosition() + cell.getRowSpan(); rowPosition++) {
				this.cells.put(new LPoint(columnPosition, rowPosition), cell);
			}
		}
		
		super.paintCell(cell, gc, configRegistry);
	}
	
	private boolean isSelected(final ILayerCell cell) {
		return (cell.getDisplayMode() == DisplayMode.SELECT);
	}
	
	private void applyBorderStyle(final GC gc, final IConfigRegistry configRegistry) {
		//Note: If there is no style configured for the SelectionStyleLabels.SELECTION_ANCHOR_GRID_LINE_STYLE
		//		label, the style configured for DisplayMode.SELECT will be retrieved by this call.
		//		Ensure that the selection style configuration does not contain a border style configuration
		//		to avoid strange rendering behaviour. By default there is no border configuration added,
		//		so there shouldn't be issues with backwards compatibility. And if there are some, they can
		//		be solved easily by adding the necessary border style configuration.
		final IStyle cellStyle= configRegistry.getConfigAttribute(
				CellConfigAttributes.CELL_STYLE, 
				DisplayMode.SELECT, 
				SelectionStyleLabels.SELECTION_ANCHOR_GRID_LINE_STYLE);
		final BorderStyle borderStyle= cellStyle != null ? cellStyle.getAttributeValue(CellStyleAttributes.BORDER_STYLE) : null;
		
		//if there is no border style configured, use the default one for backwards compatibility
		if (borderStyle == null) {
			gc.setLineStyle(SWT.LINE_CUSTOM);
			gc.setLineDash(new int[] { 1, 1 });
			gc.setForeground(GUIHelper.COLOR_BLACK);
		}
		else {
			gc.setLineStyle(SWTUtil.toSWT(borderStyle.getLineStyle()));
			gc.setLineWidth(borderStyle.getThickness());
			gc.setForeground(borderStyle.getColor());
		}
	}

}
