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

import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.data.convert.IDisplayConverter;
import de.walware.ecommons.waltable.layer.cell.CellDisplayConversionUtils;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.style.CellStyleAttributes;
import de.walware.ecommons.waltable.style.HorizontalAlignment;
import de.walware.ecommons.waltable.style.IStyle;
import de.walware.ecommons.waltable.style.TextDecorationEnum;
import de.walware.ecommons.waltable.util.GUIHelper;

/**
 * Abstract TextPainter the contains general methods for drawing text into a cell.
 * Can handle word wrapping and/or word cutting and/or automatic calculation and resizing of the
 * cell height or width if the text does not fit into the cell.
 */
public abstract class AbstractTextPainter extends BackgroundPainter {

	protected static final String EMPTY= ""; //$NON-NLS-1$
	protected static final char DOT= '\u2026';

	/**
	 * The regular expression to find predefined new lines in the text to show.
	 * Is used for word wrapping to preserve user defined new lines.
	 * To be platform independent \n and \r and the combination of both are used
	 * to find user defined new lines.
	 */
	private static final Pattern NEW_LINE_PATTERN= Pattern.compile("\\n\\r|\\r\\n|\\n|\\r"); //$NON-NLS-1$

	private static final String NEW_LINE_SEPARATOR= System.getProperty("line.separator"); //$NON-NLS-1$

	private static final int DEFAULT_SPACING= 3;
	
	public static final int DEFAULT_DRAW_STYLES= SWT.DRAW_TRANSPARENT | SWT.DRAW_DELIMITER;


	private static final class TemporaryMapKey {
	
		private final String text;
		private final Font font;
	
		public TemporaryMapKey(final String text, final Font font) {
			this.text= text;
			this.font= font;
		}


		@Override
		public int hashCode() {
			return this.text.hashCode() + this.font.hashCode();
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof TemporaryMapKey)) {
				return false;
			}
			final TemporaryMapKey other= (TemporaryMapKey) obj;
			return (this.text.equals(other.text) && this.font.equals(other.font));
		}

	}

	private static final Map<TemporaryMapKey, Integer> temporaryMap= new WeakHashMap<>();


	protected final boolean wrapText;
	protected final boolean paintBg;
	protected final boolean paintFg;
	protected final int spacing;

	//can only grow but will not calculate the minimal length
	protected final boolean calculateByTextLength;
	protected final boolean calculateByTextHeight;

	protected final int swtDrawStyle;
	
	private boolean underline;
	private boolean strikethrough;


	public AbstractTextPainter() {
		this(false, true, SWT.DEFAULT, false);
	}

	/**
	 * @param wrapText split text over multiple lines
	 * @param paintBg skips painting the background if is FALSE
	 */
	public AbstractTextPainter(final boolean wrapText, final boolean paintBg) {
		this(wrapText, paintBg, SWT.DEFAULT, false);
	}

	/**
	 * @param wrapText split text over multiple lines
	 * @param paintBg skips painting the background if is FALSE
	 * @param spacing The space between text and cell border
	 */
	public AbstractTextPainter(final boolean wrapText, final boolean paintBg, final int spacing) {
		this(wrapText, paintBg, spacing, false);
	}
	
	/**
	 * @param wrapText split text over multiple lines
	 * @param paintBg skips painting the background if is FALSE
	 * @param spacing The space between text and cell border
	 * @param calculate tells the text painter to calculate the cell borders regarding the content
	 */
	public AbstractTextPainter(final boolean wrapText, final boolean paintBg, final int spacing, final boolean calculate) {
		this(wrapText, paintBg, spacing, calculate, calculate, DEFAULT_DRAW_STYLES);
	}

	/**
	 * @param wrapText split text over multiple lines
	 * @param paintBg skips painting the background if is FALSE
	 * @param spacing The space between text and cell border
	 * @param calculateByTextLength tells the text painter to calculate the cell border by containing
	 * 			text length. For horizontal text rendering, this means the width of the cell is calculated
	 * 			by content, for vertical text rendering the height is calculated
	 * @param calculateByTextHeight tells the text painter to calculate the cell border by containing
	 * 			text height. For horizontal text rendering, this means the height of the cell is calculated
	 * 			by content, for vertical text rendering the width is calculated
	 */
	public AbstractTextPainter(final boolean wrapText, final boolean paintBg, final int spacing, 
			final boolean calculateByTextLength, final boolean calculateByTextHeight,
			int swtDrawStyle) {
		this.wrapText= wrapText;
		this.paintBg= paintBg;
		this.paintFg= true;
		this.spacing= (spacing >= 0) ? spacing : DEFAULT_SPACING;
		this.calculateByTextLength= calculateByTextLength;
		this.calculateByTextHeight= calculateByTextHeight;
		if (wrapText) {
			swtDrawStyle |= SWT.DRAW_DELIMITER;
		}
		this.swtDrawStyle= swtDrawStyle;
	}

	/**
	 * Convert the data value of the cell using the {@link IDisplayConverter} from the {@link IConfigRegistry}
	 */
	protected String convertDataType(final ILayerCell cell, final IConfigRegistry configRegistry) {
		return CellDisplayConversionUtils.convertDataType(cell, configRegistry);
	}

	/**
	 * Setup the GC by the values defined in the given cell style.
	 * @param gc
	 * @param cellStyle
	 */
	protected void setupGCFromConfig(final GC gc, final IStyle cellStyle) {
		final Color fg= cellStyle.getAttributeValue(CellStyleAttributes.FOREGROUND_COLOR);
		final Color bg= cellStyle.getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR);
		final Font font= cellStyle.getAttributeValue(CellStyleAttributes.FONT);

		gc.setAntialias(GUIHelper.DEFAULT_ANTIALIAS);
		gc.setTextAntialias(GUIHelper.DEFAULT_TEXT_ANTIALIAS);
		gc.setFont(font);
		gc.setForeground(fg != null ? fg : GUIHelper.COLOR_LIST_FOREGROUND);
		gc.setBackground(bg != null ? bg : GUIHelper.COLOR_LIST_BACKGROUND);
	}

	/**
	 * Checks if there is a underline text decoration configured within the given cell style.
	 * @param cellStyle The cell style of the current cell to check for the text decoration.
	 * @return <code>true</code> if there is a underline text decoration configured, 
	 * 			<code>false</code> otherwise.
	 */
	protected boolean renderUnderlined(final IStyle cellStyle) {
		final TextDecorationEnum decoration= cellStyle.getAttributeValue(CellStyleAttributes.TEXT_DECORATION);
		if (decoration != null) {
				return (decoration.equals(TextDecorationEnum.UNDERLINE) 
						|| decoration.equals(TextDecorationEnum.UNDERLINE_STRIKETHROUGH));
		}
		return this.underline;
	}

	/**
	 * Checks if there is a strikethrough text decoration configured within the given cell style.
	 * @param cellStyle The cell style of the current cell to check for the text decoration.
	 * @return <code>true</code> if there is a strikethrough text decoration configured, 
	 * 			<code>false</code> otherwise.
	 */
	protected boolean renderStrikethrough(final IStyle cellStyle) {
		final TextDecorationEnum decoration= cellStyle.getAttributeValue(CellStyleAttributes.TEXT_DECORATION);
		if (decoration != null) {
			return (decoration.equals(TextDecorationEnum.STRIKETHROUGH) 
						|| decoration.equals(TextDecorationEnum.UNDERLINE_STRIKETHROUGH));
		}
		return this.strikethrough;
	}

	/**
	 * Scans for new line characters and counts the number of lines
	 * for the given text.
	 * @param text the text to scan
	 * @return the number of lines for the given text
	 */
	protected int getNumberOfNewLines(final String text) {
		final String[] lines= NEW_LINE_PATTERN.split(text);
		return lines.length;
	}

	protected long getHorizontalAlignmentIndent(HorizontalAlignment horizontalAlignment, final long width, final long textWidth) {
		if (horizontalAlignment == null) {
			horizontalAlignment= HorizontalAlignment.CENTER;
		}
		switch (horizontalAlignment) {
		case CENTER:
			return (width - textWidth) / 2;
		case RIGHT:
			return (width - textWidth);
		default:
			return 0;
		}
	}

	/**
	 * Calculates the length of a given text by using the GC.
	 * To minimize the count of calculations, the calculation
	 * result will be stored within a Map, so the next time
	 * the length of the same text is asked for, the result
	 * is only returned by cache and is not calculated again.
	 * @param gc the current GC
	 * @param text the text to get the length for
	 * @return the length of the text
	 */
	protected int getWidthFromCache(final GC gc, final String text) {
		final TemporaryMapKey key= new TemporaryMapKey(text, gc.getFont());
		Integer width= temporaryMap.get(key);
		if (width == null) {
			width= Integer.valueOf(gc.textExtent(text, this.swtDrawStyle).x);
			temporaryMap.put(key, width);
		}
		return width.intValue();
	}

	/**
	 * Computes dependent on the configuration of the TextPainter the text to display.
	 * If word wrapping is enabled new lines are inserted if the available space is not
	 * enough. If calculation of available space is enabled, the space is automatically
	 * widened for the text to display, and if no calculation is enabled the text is cut
	 * and modified to end with "..." to fit into the available space
	 * @param cell the current cell to paint
	 * @param gc the current GC
	 * @param availableLength the available space for the text to display
	 * @param text the text that should be modified for display
	 * @return the modified text
	 */
	protected String getTextToDisplay(final ILayerCell cell, final GC gc, final long availableLength, String text) {
		final StringBuilder output= new StringBuilder();

		text= text.trim();

		//take the whole width of the text
		long textLength= getWidthFromCache(gc, text);
		if (this.calculateByTextLength && this.wrapText) {
			if (availableLength < textLength) {
				//calculate length by finding the longest word in text
				textLength= availableLength;
				
				final String[] lines= NEW_LINE_PATTERN.split(text);
				for (final String line : lines) {
					if (output.length() > 0) {
						output.append(NEW_LINE_SEPARATOR);
					}
					
					final String[] words= line.split("\\s"); //$NON-NLS-1$
					for (final String word : words) {
						textLength= Math.max(textLength, getWidthFromCache(gc, word));
					}
					
					//concat the words with spaces and newlines to be always smaller then available
					String computedText= ""; //$NON-NLS-1$
					for (final String word : words) {
						computedText= computeTextToDisplay(computedText, word, gc, textLength);
					}
					output.append(computedText);
				}
			}
			else {
				output.append(text);
			}
			
//			setNewMinLength(cell, textLength + calculatePadding(cell, availableLength));
		}
		else if (this.calculateByTextLength && !this.wrapText){
			output.append(modifyTextToDisplay(text, gc, textLength));
			
			//add padding and spacing to textLength because they are needed for correct sizing
			//padding can occur on using decorators like the BeveledBorderDecorator or the 
			//PaddingDecorator
//			setNewMinLength(cell, textLength + (2*spacing));
		}
		else if (!this.calculateByTextLength && this.wrapText) {
			final String[] lines= NEW_LINE_PATTERN.split(text);
			for (final String line : lines) {
				if (output.length() > 0) {
					output.append(NEW_LINE_SEPARATOR);
				}
				
				final String[] words= line.split("\\s"); //$NON-NLS-1$
				
				//concat the words with spaces and newlines
				String computedText= ""; //$NON-NLS-1$
				for (final String word : words) {
					computedText= computeTextToDisplay(computedText, word, gc, availableLength);
				}
				
				output.append(computedText);
			}
			
		}
		else if (!this.calculateByTextLength && !this.wrapText) {
			output.append(modifyTextToDisplay(text, gc, availableLength));
		}
		
		return output.toString();
	}
	
	/**
	 * This method gets only called if word wrapping is enabled.
	 * Concatenates the two given words by taking the availableSpace into account.
	 * If concatenating those two words with a space as delimiter does fit into
	 * the available space the return value is exactly this. Else instead of a 
	 * space there will be a new line character used as delimiter.
	 * @param one the first word or the whole text before the next word
	 * @param two the next word to add to the first parameter
	 * @param gc the current GC
	 * @param availableSpace the available space
	 * @return the concatenated String of the first two parameters
	 */
	private String computeTextToDisplay(final String one, final String two, final GC gc, final long availableSpace) {
		String result= one;
		//if one is empty or one ends with newline just add two
		if (one == null || one.length() == 0 || one.endsWith(NEW_LINE_SEPARATOR)) {
			result+= two;
		}
		//if one does not contain a newline
		else if (one.indexOf(NEW_LINE_SEPARATOR) == -1) {
			//
			if (getWidthFromCache(gc, one) == availableSpace 
				|| getWidthFromCache(gc, one+" "+two) >= availableSpace) { //$NON-NLS-1$
				result+= NEW_LINE_SEPARATOR;
				result+= modifyTextToDisplay(two, gc, availableSpace);
			}
			else {
				result+= ' ';
				result+= two;
			}
		}
		else {
			//get the end of the last part after the last newline
			final String endString= one.substring(one.lastIndexOf(NEW_LINE_SEPARATOR) + 1);
			if (getWidthFromCache(gc, endString) == availableSpace 
					|| getWidthFromCache(gc, endString+" "+two) >= availableSpace) { //$NON-NLS-1$
					result+= NEW_LINE_SEPARATOR;
					result+= two;
				}
				else {
					result+= ' ';
					result+= two;
				}
		}
		return result;
	}

	/**
	 * Checks if the given text is bigger than the available space. If not the given
	 * text is simply returned without modification. If the text does not fit into
	 * the available space, it will be modified by cutting and adding three dots. 
	 * @param text the text to compute
	 * @param gc the current GC
	 * @param availableLength the available space
	 * @return the modified text if it is bigger than the available space or the 
	 * text as it was given if it fits into the available space
	 */
	private String modifyTextToDisplay(final String text, final GC gc, final long availableLength) {
		//length of the text on GC taking new lines into account
		//this means the textLength is the value of the longest line
		long textLength= getWidthFromCache(gc, text);
		if (textLength > availableLength) {
			//as looking at the text length without taking new lines into account
			//we have to look at every line itself
			final StringBuilder result= new StringBuilder();
			final String[] lines= ((this.swtDrawStyle & SWT.DRAW_DELIMITER) == 0) ?
					NEW_LINE_PATTERN.split(text, 1) : NEW_LINE_PATTERN.split(text);
			for (String line : lines) {
				if (result.length() > 0) {
					result.append(NEW_LINE_SEPARATOR);
				}
				
				//now modify every line if it is longer than the available space
				//this way every line will get ... if it doesn't fit
				final long lineLength= getWidthFromCache(gc, line);
				if (lineLength > availableLength) {
					int numExtraChars= 0;

					int newStringLength= line.length();
					String trialLabelText= line + DOT;
					long newTextExtent= getWidthFromCache(gc, trialLabelText);

					while (newTextExtent > availableLength + 1 && newStringLength > 0) {
						final double avgWidthPerChar= (double) newTextExtent / trialLabelText.length();
						numExtraChars+= 1 + (int) ((newTextExtent - availableLength) / avgWidthPerChar);

						newStringLength= line.length() - numExtraChars;
						if (newStringLength > 0) {
							trialLabelText= line.substring(0, newStringLength) + DOT;
							newTextExtent= getWidthFromCache(gc, trialLabelText);
						}
					}

					if (numExtraChars > line.length()) {
						numExtraChars= line.length();
					}
					
					// now we have gone too short, lets add chars one at a time to exceed the width...
					String testString= line;
					for (int i= 0; i < line.length(); i++){
						testString= line.substring(0, line.length() + i - numExtraChars) + DOT;
						textLength= getWidthFromCache(gc, testString);
						
						if (textLength >= availableLength) {
							
							//  now roll back one as this was the first number that exceeded
							if (line.length() + i - numExtraChars < 1){
								line= EMPTY;
							} else {
								line= line.substring(0, line.length() + i - numExtraChars - 1 ) + DOT;
							}
							break;
						}
					}
				}
				result.append(line);
			}
			
			return result.toString();
		}
		return text;
	}

//	/**
//	 * This method gets only called if automatic length calculation is enabled.
//	 * Calculate the new cell width/height by using the given content length and the
//	 * difference from current cell width/height to available length.
//	 * If the calculated cell is greater than the current set contentLength,
//	 * update the contentLength and execute a corresponding resize command.
//	 * @param cell the current cell that is painted
//	 * @param contentLength the length of the content
//	 */
//	protected abstract void setNewMinLength(ILayerCell cell, long contentLength);
//	
//	/**
//	 * This method is used to determine the padding from the cell to the available length.
//	 * A padding can occur for example by using a BeveledBorderDecorator or PaddingDecorator.
//	 * This TextPainter is called with the available space rectangle which is calculated
//	 * by the wrapping painters and decorators by subtracting paddings.
//	 * As this TextPainter does not know his wrapping painters and decorators the existing
//	 * padding needs to be calculated for automatic resizing.
//	 * Abstract because a horizontal TextPainter uses the width while a VerticalTextPainter
//	 * uses the height of the cell and the LRectangle.
//	 * @param cell the current cell which should be resized
//	 * @param availableLength the length value that is available and was given into paintCell()
//	 * as LRectangle argument
//	 * @return the padding between the current cell length - availableLength
//	 */
//	protected abstract long calculatePadding(ILayerCell cell, long availableLength);

	/**
	 * Set if the text should be rendered underlined or not.
	 * @param underline <code>true</code> if the text should be printed underlined,
	 * 			<code>false</code> if not
	 */
	public void setUnderline(final boolean underline) {
		this.underline= underline;
	}

	/**
	 * Set if the text should be rendered strikethrough or not.
	 * @param strikethrough <code>true</code> if the text should be printed strikethrough,
	 * 			<code>false</code> if not
	 */
	public void setStrikethrough(final boolean strikethrough) {
		this.strikethrough= strikethrough;
	}
}
