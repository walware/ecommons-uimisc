/*******************************************************************************
 * Copyright (c) 2012-2016 Dirk Fauth and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Dirk Fauth <dirk.fauth@gmail.com> - initial API and implementation
 ******************************************************************************/
// ~
package de.walware.ecommons.waltable.painter.cell.decorator;

import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import de.walware.ecommons.waltable.NatTable;
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


/**
 * {@link NatTable} decorator class which is used to draw borders for cells whose LabelStack
 * contains border related labels.
 * <p>Compared to the LineBorderDecorator which paints a border for every side of a cell,
 * with this implementation you are free to choose for which side a border should be painted
 * or not.</p>
 */
public class CustomLineBorderDecorator extends CellPainterWrapper {

	/**
	 * Label for adding a border at the top of a cell.
	 */
	public static final String TOP_LINE_BORDER_LABEL= "topLineBorderLabel"; //$NON-NLS-1$
	/**
	 * Label for adding a border at the bottom of a cell.
	 */
	public static final String BOTTOM_LINE_BORDER_LABEL= "bottomLineBorderLabel"; //$NON-NLS-1$
	/**
	 * Label for adding a border at the left of a cell.
	 */
	public static final String LEFT_LINE_BORDER_LABEL= "leftLineBorderLabel"; //$NON-NLS-1$
	/**
	 * Label for adding a border at the right of a cell.
	 */
	public static final String RIGHT_LINE_BORDER_LABEL= "rightLineBorderLabel"; //$NON-NLS-1$
	
	/**
	 * The default border style which will be used if no border style is configured via
	 * cell style configuration. Can be <code>null</code> if there should be no border
	 * rendered by default.
	 */
	private final BorderStyle defaultBorderStyle;

	/**
	 * Creates a new LabelLineBorderDecorator wrapping the given interior painter and no
	 * default border style.
	 * @param interiorPainter The painter to be wrapped by this decorator.
	 */
	public CustomLineBorderDecorator(final ICellPainter interiorPainter) {
		this(interiorPainter, null);
	}
	
	/**
	 * Creates a new LabelLineBorderDecorator wrapping the given interior painter using
	 * the given BorderStyle as default.
	 * @param interiorPainter The painter to be wrapped by this decorator.
	 * @param defaultBorderStyle The BorderStyle to use as default if there is no BorderStyle
	 * 			configured via cell styles. Can be <code>null</code>.
	 */
	public CustomLineBorderDecorator(final ICellPainter interiorPainter, final BorderStyle defaultBorderStyle) {
		super(interiorPainter);
		this.defaultBorderStyle= defaultBorderStyle;
	}

	@Override
	public long getPreferredWidth(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
		final BorderStyle borderStyle= getBorderStyle(cell, configRegistry);
		final long borderThickness= borderStyle != null ? borderStyle.getThickness() : 0;
		
		long borderLineCount= 0;
		//check how many border lines are configured for that cell
		final List<String> labels= cell.getConfigLabels().getLabels();
		if (labels.contains(RIGHT_LINE_BORDER_LABEL)) {
			borderLineCount++;
		}
		if (labels.contains(LEFT_LINE_BORDER_LABEL)) {
			borderLineCount++;
		}
		
		return super.getPreferredWidth(cell, gc, configRegistry) + (borderThickness * borderLineCount);
	}
	
	@Override
	public long getPreferredHeight(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
		final BorderStyle borderStyle= getBorderStyle(cell, configRegistry);
		final long borderThickness= borderStyle != null ? borderStyle.getThickness() : 0;
		
		long borderLineCount= 0;
		//check how many border lines are configured for that cell
		final List<String> labels= cell.getConfigLabels().getLabels();
		if (labels.contains(TOP_LINE_BORDER_LABEL)) {
			borderLineCount++;
		}
		if (labels.contains(BOTTOM_LINE_BORDER_LABEL)) {
			borderLineCount++;
		}
		
		return super.getPreferredHeight(cell, gc, configRegistry) + (borderThickness * borderLineCount);
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
		final int borderThickness= borderStyle != null ? borderStyle.getThickness() : 0;
		
		//check how many border lines are configured for that cell
		final List<String> labels= cell.getConfigLabels().getLabels();

		long leftBorderThickness= 0;
		long rightBorderThickness= 0;
		long topBorderThickness= 0;
		long bottomBorderThickness= 0;
		
		if (labels.contains(LEFT_LINE_BORDER_LABEL)) {
			leftBorderThickness= borderThickness;
		}
		if (labels.contains(RIGHT_LINE_BORDER_LABEL)) {
			rightBorderThickness= borderThickness;
		}
		if (labels.contains(TOP_LINE_BORDER_LABEL)) {
			topBorderThickness= borderThickness;
		}
		if (labels.contains(BOTTOM_LINE_BORDER_LABEL)) {
			bottomBorderThickness= borderThickness;
		}

		final LRectangle interiorBounds =
			new LRectangle(
					lRectangle.x + leftBorderThickness,
					lRectangle.y + topBorderThickness,
					(lRectangle.width - leftBorderThickness - rightBorderThickness),
					(lRectangle.height - topBorderThickness - bottomBorderThickness)
			);
		super.paintCell(cell, gc, interiorBounds, configRegistry);
		
		if (borderStyle == null || borderThickness <= 0 || 
				(leftBorderThickness == 0 && rightBorderThickness == 0 
						&& topBorderThickness == 0 && bottomBorderThickness == 0)) {
			return;
		}
		
		// Save GC settings
		final Color originalForeground= gc.getForeground();
		final int originalLineWidth= gc.getLineWidth();
		final int originalLineStyle= gc.getLineStyle();

		gc.setLineWidth(borderThickness);

		final LRectangle borderArea= new LRectangle(lRectangle.x, lRectangle.y, lRectangle.width, lRectangle.height);
		if (borderThickness >= 1) {
			int shift= 0;
			int correction= 0;

			if ((borderThickness % 2) == 0) {
				shift= borderThickness / 2;
			} else {
				shift= borderThickness / 2;
				correction= 1;
			}

			if (leftBorderThickness >= 1) {
				borderArea.x+= shift;
				borderArea.width-= shift;
			}
			
			if (rightBorderThickness >= 1) {
				borderArea.width-= shift + correction;
			}
			
			if (topBorderThickness >= 1) {
				borderArea.y+= shift;
				borderArea.height-= shift;
			}
			
			if (bottomBorderThickness >= 1) {
				borderArea.height-= shift + correction;
			}
		}

		gc.setLineStyle(SWTUtil.toSWT(borderStyle.getLineStyle()));
		gc.setForeground(borderStyle.getColor());
		
		final org.eclipse.swt.graphics.Rectangle rect= GraphicsUtils.safe(borderArea);
		//if all borders are set draw a rectangle
		if (leftBorderThickness > 0 && rightBorderThickness > 0 
						&& topBorderThickness > 0 && bottomBorderThickness > 0) {
			gc.drawRectangle(rect);
		}
		//else draw a line for every set border
		else {
			final Point topLeftPos= new Point(rect.x, rect.y); 
			final Point topRightPos= new Point(rect.x + rect.width, rect.y); 
			final Point bottomLeftPos= new Point(rect.x, rect.y + rect.height); 
			final Point bottomRightPos= new Point(rect.x + rect.width, rect.y + rect.height); 
			
			if (leftBorderThickness > 0) {
				gc.drawLine(topLeftPos.x, topLeftPos.y, bottomLeftPos.x, bottomLeftPos.y);
			}
			if (rightBorderThickness > 0) {
				gc.drawLine(topRightPos.x, topRightPos.y, bottomRightPos.x, bottomRightPos.y);
			}
			if (topBorderThickness > 0) {
				gc.drawLine(topLeftPos.x, topLeftPos.y, topRightPos.x, topRightPos.y);
			}
			if (bottomBorderThickness > 0) {
				gc.drawLine(bottomLeftPos.x, bottomLeftPos.y, bottomRightPos.x, bottomRightPos.y);
			}
		}

		// Restore GC settings
		gc.setForeground(originalForeground);
		gc.setLineWidth(originalLineWidth);
		gc.setLineStyle(originalLineStyle);
	}
	
}
