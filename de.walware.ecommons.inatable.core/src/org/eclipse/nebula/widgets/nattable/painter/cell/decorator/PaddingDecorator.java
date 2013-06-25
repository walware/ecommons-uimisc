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
// ~
package org.eclipse.nebula.widgets.nattable.painter.cell.decorator;

import static org.eclipse.nebula.widgets.nattable.painter.cell.GraphicsUtils.safe;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.CellPainterWrapper;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.CellStyleUtil;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignment;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;


public class PaddingDecorator extends CellPainterWrapper {
	
	
	private final long topPadding;
	private final long rightPadding;
	private final long bottomPadding;
	private final long leftPadding;
	
	
	public PaddingDecorator(ICellPainter interiorPainter) {
		this(interiorPainter, 2);
	}
	
	public PaddingDecorator(ICellPainter interiorPainter, long padding) {
		this(interiorPainter, padding, padding, padding, padding);
	}
	
	public PaddingDecorator(ICellPainter interiorPainter, long topPadding, long rightPadding, long bottomPadding, long leftPadding) {
		super(interiorPainter);
		this.topPadding = topPadding;
		this.rightPadding = rightPadding;
		this.bottomPadding = bottomPadding;
		this.leftPadding = leftPadding;
	}
	
	
	@Override
	public long getPreferredWidth(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
		return leftPadding + super.getPreferredWidth(cell, gc, configRegistry) + rightPadding;
	}
	
	@Override
	public long getPreferredHeight(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
		return topPadding + super.getPreferredHeight(cell, gc, configRegistry) + bottomPadding;
	}
	
	@Override
	public void paintCell(ILayerCell cell, GC gc, Rectangle adjustedCellBounds, IConfigRegistry configRegistry) {
		Color originalBg = gc.getBackground();
		Color cellStyleBackground = getBackgroundColor(cell, configRegistry);
		if (cellStyleBackground != null) {
			gc.setBackground(cellStyleBackground);
			gc.fillRectangle(safe(adjustedCellBounds));
			gc.setBackground(originalBg);
		}
		else {
			gc.fillRectangle(safe(adjustedCellBounds));
		}
		
		Rectangle interiorBounds = getInteriorBounds(adjustedCellBounds);
		if (interiorBounds.width > 0 && interiorBounds.height > 0) {
			super.paintCell(cell, gc, interiorBounds, configRegistry);
		}
	}
	
	protected Rectangle getInteriorBounds(Rectangle adjustedCellBounds) {
		return new Rectangle(
				adjustedCellBounds.x + leftPadding,
				adjustedCellBounds.y + topPadding,
				adjustedCellBounds.width - leftPadding - rightPadding,
				adjustedCellBounds.height - topPadding - bottomPadding
		);
	}
	
	protected Color getBackgroundColor(ILayerCell cell, IConfigRegistry configRegistry) {
		return CellStyleUtil.getCellStyle(cell, configRegistry).getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR);		
	}
	
	@Override
	public ICellPainter getCellPainterAt(long x, long y, ILayerCell cell, GC gc, Rectangle adjustedCellBounds, IConfigRegistry configRegistry) {
		//need to take the alignment into account
		IStyle cellStyle = CellStyleUtil.getCellStyle(cell, configRegistry);
		
		HorizontalAlignment horizontalAlignment = cellStyle.getAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT);
		long horizontalAlignmentPadding = 0;
		switch (horizontalAlignment) {
			case LEFT: horizontalAlignmentPadding = leftPadding;
						break;
			case CENTER: horizontalAlignmentPadding = leftPadding/2;
						break;
		}
		
		VerticalAlignmentEnum verticalAlignment = cellStyle.getAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT);
		long verticalAlignmentPadding = 0;
		switch (verticalAlignment) {
			case TOP: verticalAlignmentPadding = topPadding;
						break;
			case MIDDLE: verticalAlignmentPadding = topPadding/2;
						break;
		}
		
		return super.getCellPainterAt(x - horizontalAlignmentPadding, y - verticalAlignmentPadding, cell, gc, adjustedCellBounds,
				configRegistry);
	}
	
}
