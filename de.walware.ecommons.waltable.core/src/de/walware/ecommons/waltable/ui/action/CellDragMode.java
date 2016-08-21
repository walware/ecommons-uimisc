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
package de.walware.ecommons.waltable.ui.action;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.config.CellConfigAttributes;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.painter.IOverlayPainter;
import de.walware.ecommons.waltable.painter.cell.ICellPainter;

public class CellDragMode implements IDragMode {
	
	private MouseEvent initialEvent;
	private MouseEvent currentEvent;
	
	private int xOffset;
	private int yOffset;
	private Image cellImage;
	protected CellImageOverlayPainter cellImageOverlayPainter= new CellImageOverlayPainter();
	
	@Override
	public void mouseDown(final NatTable natTable, final MouseEvent event) {
		this.initialEvent= event;
		this.currentEvent= this.initialEvent;
		
		setCellImage(natTable);
		
		natTable.forceFocus();

		natTable.addOverlayPainter(this.cellImageOverlayPainter);
	}

	@Override
	public void mouseMove(final NatTable natTable, final MouseEvent event) {
		this.currentEvent= event;
		
		natTable.redraw();
	}

	@Override
	public void mouseUp(final NatTable natTable, final MouseEvent event) {
		natTable.removeOverlayPainter(this.cellImageOverlayPainter);
		this.cellImage.dispose();
		
		natTable.redraw();
	}
	
	protected MouseEvent getInitialEvent() {
		return this.initialEvent;
	}
	
	protected MouseEvent getCurrentEvent() {
		return this.currentEvent;
	}
	
	private void setCellImage(final NatTable natTable) {
		final long columnPosition= natTable.getColumnPositionByX(this.currentEvent.x);
		final long rowPosition= natTable.getRowPositionByY(this.currentEvent.y);
		final ILayerCell cell= natTable.getCellByPosition(columnPosition, rowPosition);
		
		final LRectangle cellBounds= cell.getBounds();
		final int width= (int) Math.min(cellBounds.width, 0x1fff);
		final int height= (int) Math.min(cellBounds.height, 0x1fff);
		this.xOffset= (int) Math.max(this.currentEvent.x - cellBounds.x, 0);
		this.yOffset= (int) Math.max(this.currentEvent.y - cellBounds.y, 0);
		final Image image= new Image(natTable.getDisplay(), width, height);
		
		final GC gc= new GC(image);
		final IConfigRegistry configRegistry= natTable.getConfigRegistry();
		final ICellPainter cellPainter= configRegistry.getConfigAttribute(CellConfigAttributes.CELL_PAINTER,
				cell.getDisplayMode(), cell.getConfigLabels().getLabels() );
		if (cellPainter != null) {
			cellPainter.paintCell(cell, gc, new LRectangle(0, 0, width, height), configRegistry);
		}
		gc.dispose();
		
		final ImageData imageData= image.getImageData();
		image.dispose();
		imageData.alpha= 150;
		
		this.cellImage= new Image(natTable.getDisplay(), imageData);
	}

	private class CellImageOverlayPainter implements IOverlayPainter {

		@Override
		public void paintOverlay(final GC gc, final ILayer layer) {
			if (CellDragMode.this.cellImage != null & !CellDragMode.this.cellImage.isDisposed()) {
				gc.drawImage(CellDragMode.this.cellImage, CellDragMode.this.currentEvent.x - CellDragMode.this.xOffset, CellDragMode.this.currentEvent.y - CellDragMode.this.yOffset);
			}
		}

	}

}
