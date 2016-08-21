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
// -depend, ~LineStyleEnum#toSWT
package de.walware.ecommons.waltable.style;

import org.eclipse.swt.graphics.Color;

import de.walware.ecommons.waltable.persistence.ColorPersistor;
import de.walware.ecommons.waltable.util.GUIHelper;


/**
 * This class defines the visual attributes of a border:
 * 
 * <dl>
 * <dt>Thickness</dt>
 *   <dd>The width of the border in pixel</dd>
 * <dt>Color</dt>
 *   <dd>The color of the border</dd>
 * <dt>Line Style</dt>
 *   <dd>The line style of the border, a constant of {@link LineStyle}</dd>
 * <dt>Offset</dt>
 *   <dd>The offset of the border toward the regular painting area.
 *       If not specified otherwise, the value is 0, meaning no offset.  A value &gt; 0 shifts the border inwards, a 
 *       value &lt; 0 shifts the border outwards. For example -1 can be used to paint the border over the grid.</dd>
 * </dl> 
 */
public class BorderStyle {
	
	
	public static enum LineStyle {
		
		SOLID,
		DASHED,
		DOTTED,
		DASHDOT,
		DASHDOTDOT,
		
		
	}


	private int thickness;

	private Color color;

	private LineStyle lineStyle;

	private int offset;


	/**
	 * Creates a new border style to paint a thin, black, solid line.
	 */
	public BorderStyle() {
		this(1, GUIHelper.COLOR_BLACK, LineStyle.SOLID, 0);
	}

	public BorderStyle(final int thickness, final Color color, final LineStyle lineStyle) {
		this(thickness, color, lineStyle, 0);
	}

	/**
	 * Creates a new border style.
	 * 
	 * @param thickness the width of the border in pixel
	 * @param color the color of the border
	 * @param lineStyle the line style of the border
	 * @param offset the offset of the border
	 */
	public BorderStyle(final int thickness, final Color color, final LineStyle lineStyle, final int offset) {
		setThickness(thickness); 
		setColor(color);
		setLineStyle(lineStyle);
		setOffset(offset);
	}

	/**
	 * Reconstruct this instance from the persisted String.
	 * 
	 * @see BorderStyle#toString()
	 */
	public BorderStyle(final String string) {
		final String[] tokens= string.split("\\|"); //$NON-NLS-1$

		this.thickness= Integer.parseInt(tokens[0]);
		this.color= ColorPersistor.asColor(tokens[1]);
		this.lineStyle= LineStyle.valueOf(tokens[2]);
		if (tokens.length > 3) {
			this.offset= Integer.parseInt(tokens[3]);
		}
	}


	public int getThickness() {
		return this.thickness;
	}

	public Color getColor() {
		return this.color;
	}

	public LineStyle getLineStyle() {
		return this.lineStyle;
	}

	public int getOffset() {
		return this.offset;
	}

	public void setThickness(final int thickness) {
		this.thickness= (thickness > 0) ? thickness : 0;
	}

	public void setColor(final Color color) {
		if (color == null) {
			throw new NullPointerException("color"); //$NON-NLS-1$
		}
		this.color= color;
	}

	public void setLineStyle(final LineStyle lineStyle) {
		if (lineStyle == null) {
			throw new NullPointerException("lineStyle"); //$NON-NLS-1$
		}
		this.lineStyle= lineStyle;
	}

	public void setOffset(final int offset) {
		this.offset= offset;
	}


	@Override
	public int hashCode() {
		return ((this.thickness
				* 13 + this.color.hashCode())
				* 17 + this.lineStyle.hashCode())
				+ this.offset * 31;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BorderStyle)) {
			return false;
		}

		final BorderStyle that= (BorderStyle) obj;
		return (this.thickness == that.thickness
				&& this.color.equals(that.color)
				&& this.lineStyle == that.lineStyle
				&& this.offset == that.offset );
	}

	/**
	 * @return a human readable representation of the border style. This is
	 *         suitable for constructing an equivalent instance using the
	 *         BorderStyle(String) constructor
	 */
	@Override
	public String toString() {
		return "" + this.thickness + '|' + ColorPersistor.asString(this.color) + '|' + this.lineStyle + '|' + this.offset; //$NON-NLS-1$
	}

}
