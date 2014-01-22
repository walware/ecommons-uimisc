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
package org.eclipse.nebula.widgets.nattable.selection;

import static org.eclipse.nebula.widgets.nattable.painter.cell.GraphicsUtils.safe;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.coordinate.Point;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.layer.GridLineCellLayerPainter;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.SelectionStyleLabels;
import org.eclipse.nebula.widgets.nattable.swt.SWTUtil;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;


public class SelectionLayerPainter extends GridLineCellLayerPainter {

	private long columnPositionOffset;
	
	private long rowPositionOffset;
	
	private Map<Point, ILayerCell> cells;
	
	@Override
	public void paintLayer(ILayer natLayer, GC gc, int xOffset, int yOffset, org.eclipse.swt.graphics.Rectangle pixelRectangle, IConfigRegistry configRegistry) {
		if (pixelRectangle.width <= 0 || pixelRectangle.height <= 0) {
			return;
		}
		
		Rectangle positionRectangle = getPositionRectangleFromPixelRectangle(natLayer, pixelRectangle);
		columnPositionOffset = positionRectangle.x;
		rowPositionOffset = positionRectangle.y;
		cells = new HashMap<Point, ILayerCell>();
		
		super.paintLayer(natLayer, gc, xOffset, yOffset, pixelRectangle, configRegistry);
		
		// Save gc settings
		int originalLineStyle = gc.getLineStyle();
		Color originalForeground = gc.getForeground();
		
		// Apply border settings
		//applyBorderStyle(gc, configRegistry);
		gc.setLineStyle(SWT.LINE_CUSTOM);
		gc.setLineDash(new int[] { 1, 1 });
		gc.setForeground(GUIHelper.COLOR_BLACK);
		
		// Draw horizontal borders
		boolean selectedMode = false;
		for (long columnPosition = columnPositionOffset; columnPosition < columnPositionOffset + positionRectangle.width; columnPosition++) {
			ILayerCell previousCell = null;
			ILayerCell currentCell = null;
			for (long rowPosition = rowPositionOffset; rowPosition < rowPositionOffset + positionRectangle.height; rowPosition++) {
				currentCell = cells.get(new Point(columnPosition, rowPosition));
				if (currentCell != null) {
					if (selectedMode != isSelected(currentCell)) {
						selectedMode = !selectedMode;
						
						// Draw minimal shared border between previous and current cell
						Rectangle currentCellBounds = currentCell.getBounds();
						
						long x0 = safe(xOffset + currentCellBounds.x - 1);
						long x1 = safe(xOffset + currentCellBounds.x + currentCellBounds.width - 1);
						if (previousCell != null) {
							Rectangle previousCellBounds = previousCell.getBounds();
							x0 = Math.max(x0, xOffset + previousCellBounds.x - 1);
							x1 = Math.min(x1, xOffset + previousCellBounds.x + previousCellBounds.width - 1);
						}
						int y = safe(yOffset + currentCellBounds.y - 1);
						
						gc.drawLine(safe(x0), y, safe(x1), y);
					}
				}
				previousCell = currentCell;
			}
			if (selectedMode && currentCell != null) {
				// If last cell is selected, draw its bottom edge
				Rectangle cellBounds = currentCell.getBounds();
				gc.drawLine(
						safe(xOffset + cellBounds.x - 1),
						safe(yOffset + cellBounds.y + cellBounds.height - 1),
						safe(xOffset + cellBounds.x + cellBounds.width - 1),
						safe(yOffset + cellBounds.y + cellBounds.height - 1) );
			}
			selectedMode = false;
		}
		
		// Draw vertical borders
		for (long rowPosition = rowPositionOffset; rowPosition < rowPositionOffset + positionRectangle.height; rowPosition++) {
			ILayerCell previousCell = null;
			ILayerCell currentCell = null;
			for (long columnPosition = columnPositionOffset; columnPosition < columnPositionOffset + positionRectangle.width; columnPosition++) {
				currentCell = cells.get(new Point(columnPosition, rowPosition));
				if (currentCell != null) {
					if (selectedMode != isSelected(currentCell)) {
						selectedMode = !selectedMode;
						
						// Draw minimal shared border between previous and current cell
						Rectangle currentCellBounds = currentCell.getBounds();
						
						int x = safe(xOffset + currentCellBounds.x - 1);
						
						long y0 = yOffset + currentCellBounds.y - 1;
						long y1 = yOffset + currentCellBounds.y + currentCellBounds.height - 1;
						if (previousCell != null) {
							Rectangle previousCellBounds = previousCell.getBounds();
							y0 = Math.max(y0, yOffset + previousCellBounds.y - 1);
							y1 = Math.min(y1, yOffset + previousCellBounds.y + previousCellBounds.height - 1);
						}
						
						gc.drawLine(x, safe(y0), x, safe(y1));
					}
				}
				previousCell = currentCell;
			}
			if (selectedMode && currentCell != null) {
				// If last cell is selected, draw its right edge
				Rectangle cellBounds = currentCell.getBounds();
				gc.drawLine(
						safe(xOffset + cellBounds.x + cellBounds.width - 1),
						safe(yOffset + cellBounds.y - 1),
						safe(xOffset + cellBounds.x + cellBounds.width - 1),
						safe(yOffset + cellBounds.y + cellBounds.height - 1) );
			}
			selectedMode = false;
		}
		
		// Restore original gc settings
		gc.setLineStyle(originalLineStyle);
		gc.setForeground(originalForeground);
	}
	
	@Override
	protected void paintCell(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
		for (long columnPosition = cell.getOriginColumnPosition(); columnPosition < cell.getOriginColumnPosition() + cell.getColumnSpan(); columnPosition++) {
			for (long rowPosition = cell.getOriginRowPosition(); rowPosition < cell.getOriginRowPosition() + cell.getRowSpan(); rowPosition++) {
				cells.put(new Point(columnPosition, rowPosition), cell);
			}
		}
		
		super.paintCell(cell, gc, configRegistry);
	}
	
	private boolean isSelected(ILayerCell cell) {
		return cell.getDisplayMode() == DisplayMode.SELECT;
	}
	
	private void applyBorderStyle(GC gc, IConfigRegistry configRegistry) {
		//Note: If there is no style configured for the SelectionStyleLabels.SELECTION_ANCHOR_GRID_LINE_STYLE
		//		label, the style configured for DisplayMode.SELECT will be retrieved by this call.
		//		Ensure that the selection style configuration does not contain a border style configuration
		//		to avoid strange rendering behaviour. By default there is no border configuration added,
		//		so there shouldn't be issues with backwards compatibility. And if there are some, they can
		//		be solved easily by adding the necessary border style configuration.
		IStyle cellStyle = configRegistry.getConfigAttribute(
				CellConfigAttributes.CELL_STYLE, 
				DisplayMode.SELECT, 
				SelectionStyleLabels.SELECTION_ANCHOR_GRID_LINE_STYLE);
		BorderStyle borderStyle = cellStyle != null ? cellStyle.getAttributeValue(CellStyleAttributes.BORDER_STYLE) : null;
		
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
