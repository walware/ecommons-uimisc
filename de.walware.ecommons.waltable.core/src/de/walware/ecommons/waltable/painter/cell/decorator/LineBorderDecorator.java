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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.painter.cell.CellPainterWrapper;
import de.walware.ecommons.waltable.painter.cell.GraphicsUtils;
import de.walware.ecommons.waltable.painter.cell.ICellPainter;
import de.walware.ecommons.waltable.style.BorderStyle;
import de.walware.ecommons.waltable.style.CellStyleAttributes;
import de.walware.ecommons.waltable.style.CellStyleUtil;
import de.walware.ecommons.waltable.style.IStyle;
import de.walware.ecommons.waltable.swt.SWTUtil;


public class LineBorderDecorator extends CellPainterWrapper {

	private final BorderStyle defaultBorderStyle;

	public LineBorderDecorator(final ICellPainter interiorPainter) {
		this(interiorPainter, null);
	}
	
	public LineBorderDecorator(final ICellPainter interiorPainter, final BorderStyle defaultBorderStyle) {
		super(interiorPainter);
		this.defaultBorderStyle= defaultBorderStyle;
	}


	@Override
	public long getPreferredWidth(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
		final BorderStyle borderStyle= getBorderStyle(cell, configRegistry);
		final long padding= borderStyle != null ? Math.max(borderStyle.getOffset() + borderStyle.getThickness(), 0) : 0;
		
		return super.getPreferredWidth(cell, gc, configRegistry) + (padding * 2);
	}
	
	@Override
	public long getPreferredHeight(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
		final BorderStyle borderStyle= getBorderStyle(cell, configRegistry);
		final long padding= borderStyle != null ? Math.max(borderStyle.getOffset() + borderStyle.getThickness(), 0) : 0;
		
		return super.getPreferredHeight(cell, gc, configRegistry) + (padding * 2);
	}

	private BorderStyle getBorderStyle(final ILayerCell cell, final IConfigRegistry configRegistry) {
		final IStyle cellStyle= CellStyleUtil.getCellStyle(cell, configRegistry);
		BorderStyle borderStyle= cellStyle.getAttributeValue(CellStyleAttributes.BORDER_STYLE);
		if (borderStyle == null) {
			borderStyle= this.defaultBorderStyle;
		}
		return borderStyle;
	}

	@Override
	public void paintCell(final ILayerCell cell, final GC gc, final LRectangle lRectangle, final IConfigRegistry configRegistry) {
		final BorderStyle borderStyle= getBorderStyle(cell, configRegistry);
		
		final long padding= borderStyle != null ? Math.max(borderStyle.getOffset() + borderStyle.getThickness(), 0) : 0;
		final LRectangle interiorBounds =
			new LRectangle(
					lRectangle.x + padding,
					lRectangle.y + padding,
					lRectangle.width - (padding * 2),
					lRectangle.height - (padding * 2)
			);
		super.paintCell(cell, gc, interiorBounds, configRegistry);
		
		if (borderStyle == null || borderStyle.getThickness() <= 0) {
			return;
		}
		
		// Save GC settings
		final Color originalForeground= gc.getForeground();
		final int originalLineWidth= gc.getLineWidth();
		final int originalLineStyle= gc.getLineStyle();
		
		final long borderOffset= borderStyle.getOffset();
		final int borderThickness= borderStyle.getThickness();
		final LRectangle borderArea= new LRectangle(
						lRectangle.x + borderOffset,
						lRectangle.y + borderOffset,
						lRectangle.width - (borderOffset * 2),
						lRectangle.height - (borderOffset * 2)
				);
		{
			long shift= 0;
			long areaShift= 0;
			if ((borderThickness % 2) == 0) {
				shift= borderThickness / 2;
				areaShift= (shift * 2);
			} else {
				shift= borderThickness / 2;
				areaShift= (shift * 2) + 1;
			}
			borderArea.x+= shift;
			borderArea.y+= shift;
			borderArea.width-= areaShift;
			borderArea.height-= areaShift;
		}

		gc.setLineWidth(borderThickness);
		gc.setLineStyle(SWTUtil.toSWT(borderStyle.getLineStyle()));
		gc.setForeground(borderStyle.getColor());
		gc.drawRectangle(GraphicsUtils.safe(borderArea));

		// Restore GC settings
		gc.setForeground(originalForeground);
		gc.setLineWidth(originalLineWidth);
		gc.setLineStyle(originalLineStyle);
	}
	
}
