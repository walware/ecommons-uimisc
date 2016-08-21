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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.ILayerDim;
import de.walware.ecommons.waltable.util.GUIHelper;


public class NatGridLayerPainter extends NatLayerPainter {
	
	
	private final Color gridColor;
	
	
	public NatGridLayerPainter(final NatTable natTable) {
		this(natTable, GUIHelper.COLOR_GRAY);
	}
	
	public NatGridLayerPainter(final NatTable natTable, final Color gridColor) {
		super(natTable);
		this.gridColor= gridColor;
	}
	
	
	@Override
	protected void paintBackground(final ILayer natLayer, final GC gc, final long xOffset, final long yOffset, final org.eclipse.swt.graphics.Rectangle rectangle, final IConfigRegistry configRegistry) {
		super.paintBackground(natLayer, gc, xOffset, yOffset, rectangle, configRegistry);
		
		gc.setForeground(this.gridColor);
		drawHorizontalLines(natLayer, gc, rectangle);
		drawVerticalLines(natLayer, gc, rectangle);
	}
	
	private void drawHorizontalLines(final ILayer natLayer, final GC gc, final org.eclipse.swt.graphics.Rectangle rectangle) {
		final int startX= rectangle.x;
		final int endX= rectangle.x + rectangle.width;
		
		final ILayerDim dim= natLayer.getDim(VERTICAL);
		final long endPosition= CellLayerPainter.getEndPosition(dim, rectangle.y + rectangle.height);
		for (long position= dim.getPositionByPixel(rectangle.y); position < endPosition; position++) {
			final int size= dim.getPositionSize(position);
			if (size > 0) {
				final int y= safe(dim.getPositionStart(position) + size - 1);
				gc.drawLine(startX, y, endX, y);
			}
		}
	}
	
	private void drawVerticalLines(final ILayer natLayer, final GC gc, final org.eclipse.swt.graphics.Rectangle rectangle) {
		final int startY= rectangle.y;
		final int endY= rectangle.y + rectangle.height;
		
		final ILayerDim dim= natLayer.getDim(HORIZONTAL);
		final long endPosition= CellLayerPainter.getEndPosition(dim, rectangle.x + rectangle.width);
		for (long position= dim.getPositionByPixel(rectangle.x); position < endPosition; position++) {
			final int size= dim.getPositionSize(position);
			if (size > 0) {
				final int x= safe(dim.getPositionStart(position) + size - 1);
				gc.drawLine(x, startY, x, endY);
			}
		}
	}
	
}
