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
package org.eclipse.nebula.widgets.nattable.reorder.action;

import static org.eclipse.nebula.widgets.nattable.painter.cell.GraphicsUtils.safe;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.coordinate.Point;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.IOverlayPainter;
import org.eclipse.nebula.widgets.nattable.reorder.command.ColumnReorderEndCommand;
import org.eclipse.nebula.widgets.nattable.reorder.command.ColumnReorderStartCommand;
import org.eclipse.nebula.widgets.nattable.selection.command.ClearAllSelectionsCommand;
import org.eclipse.nebula.widgets.nattable.ui.action.IDragMode;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeDetectUtil;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.command.ViewportDragCommand;

/**
 * Default {@link IDragMode} invoked for 'left click + drag' on the column header.
 * It does the following when invoked:
 * <ol>
 *    <li>Fires a column reorder command, to move columns</li>
 *    <li>Overlays a black line indicating the new column position</li>
 * </ol>
 */
public class ColumnReorderDragMode implements IDragMode {
	
	protected NatTable natTable;
	protected MouseEvent initialEvent;
	protected MouseEvent currentEvent;
	protected long dragFromGridColumnPosition;
	
	protected ColumnReorderOverlayPainter targetOverlayPainter = new ColumnReorderOverlayPainter();
	
	public void mouseDown(NatTable natTable, MouseEvent event) {
		this.natTable = natTable;
		initialEvent = event;
		currentEvent = initialEvent;
		dragFromGridColumnPosition = getDragFromGridColumnPosition();
		
        natTable.addOverlayPainter(targetOverlayPainter);
        
        natTable.doCommand(new ClearAllSelectionsCommand());
        
        fireMoveStartCommand(natTable, dragFromGridColumnPosition);
	}
	
	public void mouseMove(NatTable natTable, MouseEvent event) {
		currentEvent = event;
		
		natTable.doCommand(new ViewportDragCommand(event.x, -1));
		
		natTable.redraw();
	}

	public void mouseUp(NatTable natTable, MouseEvent event) {
		natTable.removeOverlayPainter(targetOverlayPainter);
		
		long dragToGridColumnPosition = getDragToGridColumnPosition(getMoveDirection(event.x), natTable.getColumnPositionByX(event.x));
		
		if (!isValidTargetColumnPosition(natTable, dragFromGridColumnPosition, dragToGridColumnPosition)) {
			dragToGridColumnPosition = -1;
		}
		
		fireMoveEndCommand(natTable, dragToGridColumnPosition);
		
		natTable.doCommand(new ViewportDragCommand(-1, -1));  // Cancel any active viewport drag
		
		natTable.redraw();
	}
	
	private long getDragFromGridColumnPosition() {
		return natTable.getColumnPositionByX(initialEvent.x);
	}
	
	private long getDragToGridColumnPosition(CellEdgeEnum moveDirection, long gridColumnPosition) {
		long dragToGridColumnPosition = -1;
		
		if (moveDirection != null) {
			switch (moveDirection) {
			case LEFT:
				dragToGridColumnPosition = gridColumnPosition;
				break;
			case RIGHT:
				dragToGridColumnPosition = gridColumnPosition + 1;
				break;
			}
		}
		
		return dragToGridColumnPosition;
	}
	
	private CellEdgeEnum getMoveDirection(long x) {
	    ILayerCell cell = getColumnCell(x);
	    if (cell != null) {
			Rectangle selectedColumnHeaderRect = cell.getBounds();
			return CellEdgeDetectUtil.getHorizontalCellEdge(selectedColumnHeaderRect, new Point(x, initialEvent.y));
	    }
		
		return null;
	}
	
	private ILayerCell getColumnCell(long x) {
	    long gridColumnPosition = natTable.getColumnPositionByX(x);
	    long gridRowPosition = natTable.getRowPositionByY(initialEvent.y);
	    return natTable.getCellByPosition(gridColumnPosition, gridRowPosition);
	}

	protected boolean isValidTargetColumnPosition(ILayer natLayer, long dragFromGridColumnPosition, long dragToGridColumnPosition) {
		return dragFromGridColumnPosition >= 0 && dragToGridColumnPosition >= 0;
	}
	
	protected void fireMoveStartCommand(NatTable natTable, long dragFromGridColumnPosition) {
		natTable.doCommand(new ColumnReorderStartCommand(natTable, dragFromGridColumnPosition));
	}
	
	protected void fireMoveEndCommand(NatTable natTable, long dragToGridColumnPosition) {
		natTable.doCommand(new ColumnReorderEndCommand(natTable, dragToGridColumnPosition));
	}

	private class ColumnReorderOverlayPainter implements IOverlayPainter {

		public void paintOverlay(GC gc, ILayer layer) {
			long dragFromGridColumnPosition = getDragFromGridColumnPosition();
			
			if (currentEvent.x > natTable.getWidth()) {
				return;
			}
			
			CellEdgeEnum moveDirection = getMoveDirection(currentEvent.x);
			long dragToGridColumnPosition = getDragToGridColumnPosition(moveDirection, natTable.getColumnPositionByX(currentEvent.x));
			
			if (isValidTargetColumnPosition(natTable, dragFromGridColumnPosition, dragToGridColumnPosition)) {
				long dragToColumnHandleX = -1;
				
				if (moveDirection != null) {
					Rectangle selectedColumnHeaderRect = getColumnCell(currentEvent.x).getBounds();
					
					switch (moveDirection) {
					case LEFT:
						dragToColumnHandleX = selectedColumnHeaderRect.x;
						break;
					case RIGHT:
						dragToColumnHandleX = selectedColumnHeaderRect.x + selectedColumnHeaderRect.width;
						break;
					}
				}
				
				if (dragToColumnHandleX > 0 && dragToColumnHandleX < Integer.MAX_VALUE) {
					Color orgBgColor = gc.getBackground();
					gc.setBackground(GUIHelper.COLOR_DARK_GRAY);
					
					gc.fillRectangle(safe(dragToColumnHandleX - 1, 0, 2, layer.getHeight()));
					
					gc.setBackground(orgBgColor);
				}
			}
		}

	}
	
}
