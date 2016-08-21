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
package de.walware.ecommons.waltable.resize;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.painter.cell.GraphicsUtils.check;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.NatTableDim;
import de.walware.ecommons.waltable.coordinate.LPoint;
import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.LRangeList;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.painter.IOverlayPainter;
import de.walware.ecommons.waltable.swt.SWTUtil;
import de.walware.ecommons.waltable.ui.action.IDragMode;
import de.walware.ecommons.waltable.ui.util.CellEdgeDetectUtil;
import de.walware.ecommons.waltable.util.GUIHelper;


/**
 * Drag mode that will implement the column/row resizing process.
 */
public class PositionResizeDragMode implements IDragMode {
	
	
	private static final int DEFAULT_WIDTH_MINIMUM= 25;
	
	private static final int RESIZE_OVERLAY_SIZE= 2;
	
	
	private static void addPositions(final long pixel, final ILayerDim dim, final LRangeList positions) {
		final long startPixel= Math.max(pixel - RESIZE_OVERLAY_SIZE / 2, 0);
		final long endPixel= Math.min(pixel + RESIZE_OVERLAY_SIZE / 2, dim.getSize() - 1);
		if (startPixel < endPixel) {
			positions.add(new LRange(dim.getPositionByPixel(startPixel), dim.getPositionByPixel(endPixel) + 1));
		}
	}
	
	
	private class ColumnResizeOverlayPainter implements IOverlayPainter {
		
		@Override
		public void paintOverlay(final GC gc, final ILayer layer) {
			final Color originalBackgroundColor= gc.getBackground();
			gc.setBackground(GUIHelper.COLOR_DARK_GRAY);
			gc.fillRectangle(PositionResizeDragMode.this.currentPixel - (RESIZE_OVERLAY_SIZE / 2), 0, RESIZE_OVERLAY_SIZE, check(layer.getHeight()));
			gc.setBackground(originalBackgroundColor);
		}
		
	}
	
	private class RowResizeOverlayPainter implements IOverlayPainter {
		
		@Override
		public void paintOverlay(final GC gc, final ILayer layer) {
			final Color originalBackgroundColor= gc.getBackground();
			gc.setBackground(GUIHelper.COLOR_DARK_GRAY);
			gc.fillRectangle(0, PositionResizeDragMode.this.currentPixel - (RESIZE_OVERLAY_SIZE / 2), check(layer.getWidth()), RESIZE_OVERLAY_SIZE);
			gc.setBackground(originalBackgroundColor);
		}
	}
	
	
	private final Orientation orientation;
	
	private long positionToResize;
	
	private int positionStart;
	private int positionSize;
	
	private int startPixel;
	private int currentPixel;
	private int lastPixel= -1;
	
	private final IOverlayPainter overlayPainter;
	
	
	public PositionResizeDragMode(final Orientation orientation) {
		this.orientation= orientation;
		
		this.overlayPainter= (orientation == HORIZONTAL) ?
				new ColumnResizeOverlayPainter() :
				new RowResizeOverlayPainter();
	}
	
	
	// XXX: This method must ask the layer what it's minimum width is!
	private int getMinPositionSize() {
		return DEFAULT_WIDTH_MINIMUM;
	}
	
	
	@Override
	public void mouseDown(final NatTable natTable, final MouseEvent event) {
		natTable.forceFocus();
		this.positionToResize= CellEdgeDetectUtil.getPositionToResize(natTable,
				new LPoint(event.x, event.y), this.orientation );
		if (this.positionToResize >= 0) {
			final ILayerDim dim= natTable.getDim(this.orientation);
			
			this.positionStart= check(dim.getPositionStart(this.positionToResize));
			this.positionSize= dim.getPositionSize(this.positionToResize);
			this.startPixel= SWTUtil.get(event, this.orientation);
			
			natTable.addOverlayPainter(this.overlayPainter);
		}
	}
	
	@Override
	public void mouseMove(final NatTable natTable, final MouseEvent event) {
		final NatTableDim dim= natTable.getDim(this.orientation);
		
		final int pixel= SWTUtil.get(event, this.orientation);
		if (pixel > dim.getSize()) {
			return;
		}
		
		if (pixel < this.positionStart + getMinPositionSize()) {
			this.currentPixel= this.positionStart + getMinPositionSize();
		}
		else {
			this.currentPixel= pixel;
			
			final LRangeList positionsToRepaint= new LRangeList();
			
			addPositions(this.currentPixel, dim, positionsToRepaint);
			if (this.lastPixel >= 0) {
				addPositions(this.lastPixel, dim, positionsToRepaint);
			}
			for (final LRange positions : positionsToRepaint) {
				dim.repaintPositions(positions);
			}
			
			this.lastPixel= this.currentPixel;
		}
	}
	
	@Override
	public void mouseUp(final NatTable natTable, final MouseEvent event) {
		final NatTableDim dim= natTable.getDim(this.orientation);
		
		natTable.removeOverlayPainter(this.overlayPainter);
		
		updatePositionSize(dim, SWTUtil.get(event, this.orientation));
	}
	
	private void updatePositionSize(final ILayerDim dim, final int pixel) {
		final int dragSize= pixel - this.startPixel;
		int newSize= this.positionSize + dragSize;
		if (newSize < getMinPositionSize()) {
			newSize= getMinPositionSize();
		}
		
		dim.getLayer().doCommand(new DimPositionResizeCommand(dim, this.positionToResize, newSize));
	}
	
}
