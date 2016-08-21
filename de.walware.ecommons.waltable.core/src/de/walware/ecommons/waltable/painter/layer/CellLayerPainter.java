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

package de.walware.ecommons.waltable.painter.layer;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;
import static de.walware.ecommons.waltable.painter.cell.GraphicsUtils.safe;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import de.walware.ecommons.waltable.config.CellConfigAttributes;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.painter.cell.ICellPainter;


public class CellLayerPainter implements ILayerPainter {
	
	
	protected static final long getEndPosition(final ILayerDim dim, final int pixel) {
		final long positionByPixel= dim.getPositionByPixel(pixel);
		return ((positionByPixel > 0) ?
						Math.min(dim.getPositionCount(), positionByPixel) :
						dim.getPositionCount() );
	}
	
	private static Map<Long, Long> calculateDimensionInfo(final ILayerDim dim,
			final long startPosition, final long endPosition) {
		final Map<Long, Long> positionToPixelMap= new HashMap<>();
		long start2= (startPosition > 0) ?
				dim.getPositionStart(startPosition - 1)
						+ dim.getPositionSize(startPosition - 1) :
				Long.MIN_VALUE;
		for (long position= startPosition; position < endPosition; position++) {
			final long start1= dim.getPositionStart(position);
			positionToPixelMap.put(position, Math.max(start1, start2));
			start2= start1 + dim.getPositionSize(position);
		}
		if (endPosition < dim.getPositionCount()) {
			final long start1= dim.getPositionStart(endPosition);
			positionToPixelMap.put(endPosition, Math.max(start1, start2));
		}
		return positionToPixelMap;
	}
	
	private static long getPositionStart(final ILayerDim dim, final long position,
			final Map<Long, Long> positionToPixelMap) {
		if (position < dim.getPositionCount()) {
			Long start= positionToPixelMap.get(position);
			if (start == null) {
				start= Long.valueOf(dim.getPositionStart(position));
				if (position > 0) {
					final long start2= dim.getPositionStart(position - 1)
							+ dim.getPositionStart(position - 1);
					if (start2 > start.longValue()) {
						start= Long.valueOf(start2);
					}
				}
				positionToPixelMap.put(position, start);
			}
			return start.longValue();
		}
		else {
			return dim.getSize();
		}
	}
	
	
	private ILayer natLayer;
	
	private Map<Long, Long> horizontalPositionToPixelMap;
	private Map<Long, Long> verticalPositionToPixelMap;
	
	
	@Override
	public void paintLayer(final ILayer natLayer, final GC gc, final int xOffset, final int yOffset,
			final Rectangle pixelRectangle, final IConfigRegistry configRegistry) {
		if (pixelRectangle.isEmpty()) {
			return;
		}
		
		this.natLayer= natLayer;
		final LRectangle positionRectangle= getPositionRectangleFromPixelRectangle(natLayer, pixelRectangle);
		
		calculateDimensionInfo(positionRectangle);
		
		final Collection<ILayerCell> spannedCells= new HashSet<>();
		
		for (long columnPosition= positionRectangle.x; columnPosition < positionRectangle.x + positionRectangle.width; columnPosition++) {
			for (long rowPosition= positionRectangle.y; rowPosition < positionRectangle.y + positionRectangle.height; rowPosition++) {
				final ILayerCell cell= natLayer.getCellByPosition(columnPosition, rowPosition);
				if (cell != null) {
					if (cell.isSpannedCell()) {
						spannedCells.add(cell);
					}
					else {
						paintCell(cell, gc, configRegistry);
					}
				}
			}
		}
		
		for (final ILayerCell cell : spannedCells) {
			paintCell(cell, gc, configRegistry);
		}
	}
	
	private void calculateDimensionInfo(final LRectangle positionRectangle) {
		this.horizontalPositionToPixelMap= calculateDimensionInfo(
				this.natLayer.getDim(HORIZONTAL),
				positionRectangle.x, positionRectangle.x + positionRectangle.width);
		this.verticalPositionToPixelMap= calculateDimensionInfo(
				this.natLayer.getDim(VERTICAL),
				positionRectangle.y, positionRectangle.y + positionRectangle.height);
	}
	
	@Override
	public LRectangle adjustCellBounds(final long columnPosition, final long rowPosition, final LRectangle cellBounds) {
		return cellBounds;
	}
	
	protected LRectangle getPositionRectangleFromPixelRectangle(final ILayer natLayer, final Rectangle pixelRectangle) {
		final long columnPositionOffset= natLayer.getColumnPositionByX(pixelRectangle.x);
		final long rowPositionOffset= natLayer.getRowPositionByY(pixelRectangle.y);
		final long numColumns= natLayer.getColumnPositionByX(Math.min(natLayer.getWidth(), pixelRectangle.x + pixelRectangle.width) - 1) - columnPositionOffset + 1;
		final long numRows= natLayer.getRowPositionByY(Math.min(natLayer.getHeight(), pixelRectangle.y + pixelRectangle.height) - 1) - rowPositionOffset + 1;
		
		if (columnPositionOffset < 0 || rowPositionOffset < 0 || numColumns < 0 || numRows < 0) {
//			getPositionRectangleFromPixelRectangle(natLayer, pixelRectangle);
			throw new RuntimeException();
		}
		
		return new LRectangle(columnPositionOffset, rowPositionOffset, numColumns, numRows);
	}
	
	protected void paintCell(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
		final ILayer layer= cell.getLayer();
		final long columnPosition= cell.getColumnPosition();
		final long rowPosition= cell.getRowPosition();
		
		final ICellPainter cellPainter= configRegistry.getConfigAttribute(CellConfigAttributes.CELL_PAINTER,
				cell.getDisplayMode(), cell.getConfigLabels().getLabels() );
		final LRectangle adjustedCellBounds= layer.getLayerPainter().adjustCellBounds(columnPosition, rowPosition, cell.getBounds());
		if (cellPainter != null) {
			final Rectangle originalClipping= gc.getClipping();
			
			final long startX= getColumnPositionStart(columnPosition);
			final long startY= getRowPositionStart(rowPosition);
			
			final long endX= getColumnPositionStart(cell.getOriginColumnPosition() + cell.getColumnSpan());
			final long endY= getRowPositionStart(cell.getOriginRowPosition() + cell.getRowSpan());
			
			final LRectangle clipBounds= new LRectangle(startX, startY, endX - startX, endY - startY);
			final LRectangle adjustedClipBounds= clipBounds.intersection(adjustedCellBounds);
//			LRectangle adjustedClipBounds= layer.getLayerPainter().adjustCellBounds(columnPosition, rowPosition, clipBounds);
			gc.setClipping(safe(adjustedClipBounds));
			
			cellPainter.paintCell(cell, gc, adjustedCellBounds, configRegistry);
			
			gc.setClipping(originalClipping);
		}
	}
	
	private long getColumnPositionStart(final long columnPosition) {
		return getPositionStart(this.natLayer.getDim(HORIZONTAL), columnPosition,
				this.horizontalPositionToPixelMap );
	}
	
	private long getRowPositionStart(final long rowPosition) {
		return getPositionStart(this.natLayer.getDim(VERTICAL), rowPosition,
				this.verticalPositionToPixelMap );
	}
	
}
