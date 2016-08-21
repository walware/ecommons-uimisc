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
// ~
package de.walware.ecommons.waltable.painter.cell.decorator;

import static de.walware.ecommons.waltable.painter.cell.GraphicsUtils.safe;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.painter.cell.CellPainterWrapper;
import de.walware.ecommons.waltable.painter.cell.ICellPainter;
import de.walware.ecommons.waltable.style.CellStyleAttributes;
import de.walware.ecommons.waltable.style.CellStyleUtil;
import de.walware.ecommons.waltable.style.HorizontalAlignment;
import de.walware.ecommons.waltable.style.IStyle;
import de.walware.ecommons.waltable.style.VerticalAlignmentEnum;


public class PaddingDecorator extends CellPainterWrapper {
	
	
	private final long topPadding;
	private final long rightPadding;
	private final long bottomPadding;
	private final long leftPadding;
	
	
	public PaddingDecorator(final ICellPainter interiorPainter) {
		this(interiorPainter, 2);
	}
	
	public PaddingDecorator(final ICellPainter interiorPainter, final long padding) {
		this(interiorPainter, padding, padding, padding, padding);
	}
	
	public PaddingDecorator(final ICellPainter interiorPainter, final long topPadding, final long rightPadding, final long bottomPadding, final long leftPadding) {
		super(interiorPainter);
		this.topPadding= topPadding;
		this.rightPadding= rightPadding;
		this.bottomPadding= bottomPadding;
		this.leftPadding= leftPadding;
	}
	
	
	@Override
	public long getPreferredWidth(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
		return this.leftPadding + super.getPreferredWidth(cell, gc, configRegistry) + this.rightPadding;
	}
	
	@Override
	public long getPreferredHeight(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
		return this.topPadding + super.getPreferredHeight(cell, gc, configRegistry) + this.bottomPadding;
	}
	
	@Override
	public void paintCell(final ILayerCell cell, final GC gc, final LRectangle adjustedCellBounds, final IConfigRegistry configRegistry) {
		final Color originalBg= gc.getBackground();
		final Color cellStyleBackground= getBackgroundColor(cell, configRegistry);
		if (cellStyleBackground != null) {
			gc.setBackground(cellStyleBackground);
			gc.fillRectangle(safe(adjustedCellBounds));
			gc.setBackground(originalBg);
		}
		else {
			gc.fillRectangle(safe(adjustedCellBounds));
		}
		
		final LRectangle interiorBounds= getInteriorBounds(adjustedCellBounds);
		if (interiorBounds.width > 0 && interiorBounds.height > 0) {
			super.paintCell(cell, gc, interiorBounds, configRegistry);
		}
	}
	
	protected LRectangle getInteriorBounds(final LRectangle adjustedCellBounds) {
		return new LRectangle(
				adjustedCellBounds.x + this.leftPadding,
				adjustedCellBounds.y + this.topPadding,
				adjustedCellBounds.width - this.leftPadding - this.rightPadding,
				adjustedCellBounds.height - this.topPadding - this.bottomPadding
		);
	}
	
	protected Color getBackgroundColor(final ILayerCell cell, final IConfigRegistry configRegistry) {
		return CellStyleUtil.getCellStyle(cell, configRegistry).getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR);		
	}
	
	@Override
	public ICellPainter getCellPainterAt(final long x, final long y, final ILayerCell cell, final GC gc, final LRectangle adjustedCellBounds, final IConfigRegistry configRegistry) {
		//need to take the alignment into account
		final IStyle cellStyle= CellStyleUtil.getCellStyle(cell, configRegistry);
		
		final HorizontalAlignment horizontalAlignment= cellStyle.getAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT);
		long horizontalAlignmentPadding= 0;
		switch (horizontalAlignment) {
			case LEFT: horizontalAlignmentPadding= this.leftPadding;
						break;
			case CENTER: horizontalAlignmentPadding= this.leftPadding/2;
						break;
		}
		
		final VerticalAlignmentEnum verticalAlignment= cellStyle.getAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT);
		long verticalAlignmentPadding= 0;
		switch (verticalAlignment) {
			case TOP: verticalAlignmentPadding= this.topPadding;
						break;
			case MIDDLE: verticalAlignmentPadding= this.topPadding/2;
						break;
		}
		
		return super.getCellPainterAt(x - horizontalAlignmentPadding, y - verticalAlignmentPadding, cell, gc, adjustedCellBounds,
				configRegistry);
	}
	
}
