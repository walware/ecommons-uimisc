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
package de.walware.ecommons.waltable.painter.cell;

import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;
import static de.walware.ecommons.waltable.painter.cell.GraphicsUtils.safe;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.resize.DimPositionResizeCommand;
import de.walware.ecommons.waltable.style.CellStyleUtil;
import de.walware.ecommons.waltable.style.IStyle;


/**
 * TextPainter that draws text into a cell horizontally.
 * Can handle word wrapping and/or word cutting and/or automatic calculation and resizing of the
 * cell width and height if the text does not fit into the cell.
 */
public class TextPainter extends AbstractTextPainter {

	public TextPainter() {
		this(false, true);
	}

	/**
	 * @param wrapText split text over multiple lines
	 * @param paintBg skips painting the background if is FALSE
	 */
	public TextPainter(final boolean wrapText, final boolean paintBg) {
		this(wrapText, paintBg, 0);
	}
	
	/**
	 * @param wrapText split text over multiple lines
	 * @param paintBg skips painting the background if is FALSE
	 * @param spacing The space between text and cell border
	 */
	public TextPainter(final boolean wrapText, final boolean paintBg, final int spacing) {
		this(wrapText, paintBg, spacing, false);
	}
	
	/**
	 * @param wrapText split text over multiple lines
	 * @param paintBg skips painting the background if is FALSE
	 * @param calculate tells the text painter to calculate the cell borders regarding the content
	 */
	public TextPainter(final boolean wrapText, final boolean paintBg, final boolean calculate) {
		this(wrapText, paintBg, 0, calculate);
	}
	
	/**
	 * @param wrapText split text over multiple lines
	 * @param paintBg skips painting the background if is FALSE
	 * @param spacing The space between text and cell border
	 * @param calculate tells the text painter to calculate the cell borders regarding the content
	 */
	public TextPainter(final boolean wrapText, final boolean paintBg, final int spacing, final boolean calculate) {
		super(wrapText, paintBg, spacing, calculate);
	}
	
//	/**
//	 * @param wrapText split text over multiple lines
//	 * @param paintBg skips painting the background if is FALSE
//	 * @param spacing The space between text and cell border
//	 * @param calculateByTextLength tells the text painter to calculate the cell border by containing
//	 * 			text length. For horizontal text rendering, this means the width of the cell is calculated
//	 * 			by content, for vertical text rendering the height is calculated
//	 * @param calculateByTextHeight tells the text painter to calculate the cell border by containing
//	 * 			text height. For horizontal text rendering, this means the height of the cell is calculated
//	 * 			by content, for vertical text rendering the width is calculated
//	 */
//	public TextPainter(boolean wrapText, boolean paintBg, long spacing, 
//			boolean calculateByTextLength, boolean calculateByTextHeight) {
//		super(wrapText, paintBg, spacing, calculateByTextLength, calculateByTextHeight);
//	}
	
	
	@Override
	public long getPreferredWidth(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry){
		setupGCFromConfig(gc, CellStyleUtil.getCellStyle(cell, configRegistry));
		return getWidthFromCache(gc, convertDataType(cell, configRegistry)) + (this.spacing*2) + 1;
	}

	@Override
	public long getPreferredHeight(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
		setupGCFromConfig(gc, CellStyleUtil.getCellStyle(cell, configRegistry));
		return gc.textExtent(convertDataType(cell, configRegistry)).y + (this.spacing*2) + 1;
	}


	@Override
	public void paintCell(final ILayerCell cell, final GC gc, final LRectangle lRectangle, final IConfigRegistry configRegistry) {
		if (this.paintBg) {
			super.paintCell(cell, gc, lRectangle, configRegistry);
		}

		if (this.paintFg) {
			final org.eclipse.swt.graphics.Rectangle originalClipping= gc.getClipping();
			gc.setClipping(safe(lRectangle).intersection(originalClipping));
	
			final IStyle cellStyle= CellStyleUtil.getCellStyle(cell, configRegistry);
			setupGCFromConfig(gc, cellStyle);
			
			final boolean underline= renderUnderlined(cellStyle);
			final boolean strikethrough= renderStrikethrough(cellStyle);
			
			final int fontHeight= gc.getFontMetrics().getHeight();
			String text= convertDataType(cell, configRegistry);
	
			// Draw Text
			text= getTextToDisplay(cell, gc, lRectangle.width, text);
	
			final int numberOfNewLines= getNumberOfNewLines(text);
			
			//if the content height is bigger than the available row height
			//we're extending the row height (only if word wrapping is enabled)
			final int contentHeight= (fontHeight * numberOfNewLines) + (this.spacing*2);
			final long contentToCellDiff= (cell.getBounds().height - lRectangle.height);
	
			if (performRowResize(contentHeight, lRectangle)) {
				final ILayer layer= cell.getLayer();
				layer.doCommand(new DimPositionResizeCommand(layer.getDim(VERTICAL), 
						cell.getRowPosition(), 
						(int) Math.min(contentHeight + contentToCellDiff, 0x7fff) ));
			}
			
			if (numberOfNewLines == 1) {
				final long contentWidth= Math.min(getWidthFromCache(gc, text), lRectangle.width);
				
				gc.drawText(
						text,
						safe(lRectangle.x + CellStyleUtil.getHorizontalAlignmentPadding(cellStyle, lRectangle, contentWidth) + this.spacing),
						safe(lRectangle.y + CellStyleUtil.getVerticalAlignmentPadding(cellStyle, lRectangle, contentHeight) + this.spacing),
						SWT.DRAW_TRANSPARENT | SWT.DRAW_DELIMITER
				);
				
				if (underline || strikethrough) {
					//start x of line= start x of text
					final int x= safe(lRectangle.x + CellStyleUtil.getHorizontalAlignmentPadding(cellStyle, lRectangle, contentWidth) + this.spacing);
					//y= start y of text
					final int y= safe(lRectangle.y + CellStyleUtil.getVerticalAlignmentPadding(cellStyle, lRectangle, contentHeight) + this.spacing); 
					
					//check and draw underline and strikethrough separately so it is possible to combine both
					if (underline) {
						//y= start y of text + font height 
						// - half of the font descent so the underline is between the baseline and the bottom
						final int underlineY= safe(y + fontHeight - (gc.getFontMetrics().getDescent() / 2));
						gc.drawLine(
								x, 
								underlineY, 
								x + gc.textExtent(text).x, 
								underlineY);
					}
					
					if (strikethrough) {
						//y= start y of text + half of font height + ascent so lower case characters are
						//also strikethrough
						final int strikeY= safe(y + (fontHeight / 2) + (gc.getFontMetrics().getLeading() / 2));
						gc.drawLine(
								x, 
								strikeY, 
								x + gc.textExtent(text).x, 
								strikeY);
					}
				}
			}
			else {
				//draw every line by itself because of the alignment, otherwise the whole text
				//is always aligned right
				long yStartPos= lRectangle.y + CellStyleUtil.getVerticalAlignmentPadding(cellStyle, lRectangle, contentHeight);
				final String[] lines= text.split("\n"); //$NON-NLS-1$
				for (final String line : lines) {
					final long lineContentWidth= Math.min(getWidthFromCache(gc, line), lRectangle.width);
					
					gc.drawText(
							line,
							safe(lRectangle.x + CellStyleUtil.getHorizontalAlignmentPadding(cellStyle, lRectangle, lineContentWidth) + this.spacing),
							safe(yStartPos + this.spacing),
							SWT.DRAW_TRANSPARENT | SWT.DRAW_DELIMITER
					);
					
					if (underline || strikethrough) {
						//start x of line= start x of text
						final int x= safe(lRectangle.x + CellStyleUtil.getHorizontalAlignmentPadding(cellStyle, lRectangle, lineContentWidth) + this.spacing);
						//y= start y of text
						final int y= safe(yStartPos + this.spacing); 
								
						
						//check and draw underline and strikethrough separately so it is possible to combine both
						if (underline) {
							//y= start y of text + font height 
							// - half of the font descent so the underline is between the baseline and the bottom
							final int underlineY= safe(y + fontHeight - (gc.getFontMetrics().getDescent() / 2));
							gc.drawLine(
									x, 
									underlineY, 
									x + gc.textExtent(line).x, 
									underlineY);
						}
						
						if (strikethrough) {
							//y= start y of text + half of font height + ascent so lower case characters are
							//also strikethrough
							final int strikeY= safe(y + (fontHeight / 2) + (gc.getFontMetrics().getLeading() / 2));
							gc.drawLine(
									x, 
									strikeY, 
									x + gc.textExtent(line).x, 
									strikeY);
						}
					}
					
					//after every line calculate the y start pos new
					yStartPos+= fontHeight;
				}
			}
	
			gc.setClipping(originalClipping);
		}
	}


//	@Override
//	protected void setNewMinLength(ILayerCell cell, long contentWidth) {
//		long cellLength= cell.getBounds().width;
//		if (cellLength < contentWidth) {
//			//execute DimPositionResizeCommand
//			ILayer layer= cell.getLayer();
//			layer.doCommand(new DimPositionResizeCommand(layer, cell.getColumnPosition(), 
//					contentWidth));
//		}
//	}
//
//	@Override
//	protected long calculatePadding(ILayerCell cell, long availableLength) {
//		return cell.getBounds().width - availableLength;
//	}
	
	/**
	 * Checks if a row resize needs to be triggered.
	 * @param contentHeight The necessary height to show the content completely
	 * @param lRectangle The available rectangle to render to
	 * @return <code>true</code> if a row resize needs to be performed, <code>false</code> if not
	 */
	protected boolean performRowResize(final long contentHeight, final LRectangle lRectangle) {
		return (this.calculateByTextHeight && (contentHeight > lRectangle.height));
	}
	
}
