/*******************************************************************************
 * Copyright (c) 2012-2015 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.resize.mode;

import static org.eclipse.nebula.widgets.nattable.painter.cell.GraphicsUtils.check;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.coordinate.Point;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.painter.IOverlayPainter;
import org.eclipse.nebula.widgets.nattable.resize.command.ColumnResizeCommand;
import org.eclipse.nebula.widgets.nattable.ui.action.IDragMode;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeDetectUtil;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;


/**
 * Drag mode that will implement the column resizing process.
 */
public class ColumnResizeDragMode implements IDragMode {

	private static final int DEFAULT_COLUMN_WIDTH_MINIMUM = 25;

	private long columnPositionToResize;
	private int originalColumnWidth;
	private int startX;
	private int currentX;
	private int lastX = -1;
	private int gridColumnStartX;

	private final IOverlayPainter overlayPainter = new ColumnResizeOverlayPainter();

	public void mouseDown(NatTable natTable, MouseEvent event) {
		natTable.forceFocus();
		columnPositionToResize =
		    CellEdgeDetectUtil.getColumnPositionToResize(natTable, new Point(event.x, event.y));
		if (columnPositionToResize >= 0) {
		    gridColumnStartX = check(natTable.getStartXOfColumnPosition(columnPositionToResize));
		    originalColumnWidth = natTable.getColumnWidthByPosition(columnPositionToResize);
		    startX = event.x;
		    natTable.addOverlayPainter(overlayPainter);
		}
	}

	public void mouseMove(NatTable natTable, MouseEvent event) {
		if (event.x > natTable.getWidth()) {
			return;
		}
	    this.currentX = event.x;
	    if (currentX < gridColumnStartX + getColumnWidthMinimum()) {
	        currentX = gridColumnStartX + getColumnWidthMinimum();
	    } else {
	    	long overlayExtent = ColumnResizeOverlayPainter.COLUMN_RESIZE_OVERLAY_WIDTH / 2;

	    	Set<Long> columnsToRepaint = new HashSet<Long>();

	    	columnsToRepaint.add(Long.valueOf(natTable.getColumnPositionByX(currentX - overlayExtent)));
	    	columnsToRepaint.add(Long.valueOf(natTable.getColumnPositionByX(currentX + overlayExtent)));

	    	if (lastX >= 0) {
	    		columnsToRepaint.add(Long.valueOf(natTable.getColumnPositionByX(lastX - overlayExtent)));
	    		columnsToRepaint.add(Long.valueOf(natTable.getColumnPositionByX(lastX + overlayExtent)));
	    	}

	    	for (Long columnToRepaint : columnsToRepaint) {
	    		natTable.repaintColumn(columnToRepaint.longValue());
	    	}

	        lastX = currentX;
	    }
	}

	public void mouseUp(NatTable natTable, MouseEvent event) {
	    natTable.removeOverlayPainter(overlayPainter);
		updateColumnWidth(natTable, event);
	}

	private void updateColumnWidth(ILayer natLayer, MouseEvent e) {
	    int dragWidth = e.x - startX;
        int newColumnWidth = originalColumnWidth + dragWidth;
        if (newColumnWidth < getColumnWidthMinimum()) newColumnWidth = getColumnWidthMinimum();
		natLayer.doCommand(new ColumnResizeCommand(natLayer, columnPositionToResize, newColumnWidth));
	}

	// XXX: This method must ask the layer what it's minimum width is!
	private int getColumnWidthMinimum() {
	    return DEFAULT_COLUMN_WIDTH_MINIMUM;
	}

	private class ColumnResizeOverlayPainter implements IOverlayPainter {

		static final int COLUMN_RESIZE_OVERLAY_WIDTH = 2;

	    public void paintOverlay(GC gc, ILayer layer) {
	        Color originalBackgroundColor = gc.getBackground();
	        gc.setBackground(GUIHelper.COLOR_DARK_GRAY);
	        gc.fillRectangle(currentX - (COLUMN_RESIZE_OVERLAY_WIDTH / 2), 0, COLUMN_RESIZE_OVERLAY_WIDTH, check(layer.getHeight()));
	        gc.setBackground(originalBackgroundColor);
	    }
	}
}
