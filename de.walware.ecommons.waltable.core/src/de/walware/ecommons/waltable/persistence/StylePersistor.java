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
package de.walware.ecommons.waltable.persistence;

import static de.walware.ecommons.waltable.persistence.IPersistable.DOT;
import static de.walware.ecommons.waltable.style.CellStyleAttributes.BACKGROUND_COLOR;
import static de.walware.ecommons.waltable.style.CellStyleAttributes.BORDER_STYLE;
import static de.walware.ecommons.waltable.style.CellStyleAttributes.FONT;
import static de.walware.ecommons.waltable.style.CellStyleAttributes.FOREGROUND_COLOR;
import static de.walware.ecommons.waltable.style.CellStyleAttributes.HORIZONTAL_ALIGNMENT;
import static de.walware.ecommons.waltable.style.CellStyleAttributes.VERTICAL_ALIGNMENT;

import java.util.Properties;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import de.walware.ecommons.waltable.style.BorderStyle;
import de.walware.ecommons.waltable.style.CellStyleAttributes;
import de.walware.ecommons.waltable.style.HorizontalAlignment;
import de.walware.ecommons.waltable.style.Style;
import de.walware.ecommons.waltable.style.VerticalAlignmentEnum;
import de.walware.ecommons.waltable.util.GUIHelper;

/**
 * Saves and loads the following components of a style to a properties object.
 * 	- Foreground color
 * 	- Background color
 * 	- Horizontal alignment
 * 	- Vertical alignment
 * 	- Font
 * 	- Border style
 */
public class StylePersistor {

	// Style prefix constants
	public static final String STYLE_PERSISTENCE_PREFIX= "style"; //$NON-NLS-1$
	public static final String BLUE_COLOR_PREFIX= "blue"; //$NON-NLS-1$
	public static final String GREEN_COLOR_PREFIX= "green"; //$NON-NLS-1$
	public static final String RED_COLOR_PREFIX= "red"; //$NON-NLS-1$
	public static final String V_ALIGNMENT_PREFIX= "verticalAlignment"; //$NON-NLS-1$
	public static final String H_ALIGNMENT_PREFIX= "horizontalAlignment"; //$NON-NLS-1$
	public static final String BG_COLOR_PREFIX= "bg"; //$NON-NLS-1$
	public static final String FG_COLOR_PREFIX= "fg"; //$NON-NLS-1$
	public static final String FONT_PREFIX= "font"; //$NON-NLS-1$
	public static final String BORDER_PREFIX= "border"; //$NON-NLS-1$

	// Save

	public static void saveStyle(String prefix, final Properties properties, final Style style) {
		prefix= prefix + DOT + STYLE_PERSISTENCE_PREFIX;

		saveColor(prefix + DOT + BG_COLOR_PREFIX, properties, style.getAttributeValue(BACKGROUND_COLOR));
		saveColor(prefix + DOT + FG_COLOR_PREFIX, properties, style.getAttributeValue(FOREGROUND_COLOR));

		saveHAlign(prefix, properties, style.getAttributeValue(HORIZONTAL_ALIGNMENT));
		saveVAlign(prefix, properties, style.getAttributeValue(VERTICAL_ALIGNMENT));

		saveFont(prefix, properties, style.getAttributeValue(FONT));

		saveBorder(prefix, properties, style.getAttributeValue(BORDER_STYLE));
	}

	protected static void saveVAlign(final String prefix, final Properties properties, final VerticalAlignmentEnum vAlign) {
		if (vAlign == null) {
			return;
		}
		properties.setProperty(prefix + DOT + V_ALIGNMENT_PREFIX, vAlign.name());
	}

	protected static void saveHAlign(final String prefix, final Properties properties, final HorizontalAlignment hAlign) {
		if (hAlign == null) {
			return;
		}
		properties.setProperty(prefix + DOT + H_ALIGNMENT_PREFIX, hAlign.name());
	}

	protected static void saveBorder(final String prefix, final Properties properties, final BorderStyle borderStyle) {
		if (borderStyle == null) {
			return;
		}
		properties.setProperty(prefix + DOT + BORDER_PREFIX, String.valueOf(borderStyle.toString()));
	}

	protected static void saveFont(final String prefix, final Properties properties, final Font font) {
		if (font == null) {
			return;
		}
		properties.setProperty(prefix + DOT + FONT_PREFIX, String.valueOf(font.getFontData()[0].toString()));
	}

	protected static void saveColor(final String prefix, final Properties properties, final Color color) {
		if (color == null) {
			return;
		}
		ColorPersistor.saveColor(prefix, properties, color);
	}

	// Load

	public static Style loadStyle(String prefix, final Properties properties) {
		final Style style= new Style();
		prefix= prefix + DOT + STYLE_PERSISTENCE_PREFIX;

		// BG Color
		final String bgColorPrefix= prefix + DOT + BG_COLOR_PREFIX;
		final Color bgColor= loadColor(bgColorPrefix, properties);
		if (bgColor != null) {
			style.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, bgColor);
		}

		// FG Color
		final String fgColorPrefix= prefix + DOT + FG_COLOR_PREFIX;
		final Color fgColor= loadColor(fgColorPrefix, properties);
		if (fgColor != null) {
			style.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, fgColor);
		}

		// Alignment
		final String hAlignPrefix= prefix + DOT + H_ALIGNMENT_PREFIX;
		final HorizontalAlignment hAlign= loadHAlignment(hAlignPrefix, properties);
		if (hAlign != null) {
			style.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, hAlign);
		}

		final String vAlignPrefix= prefix + DOT + V_ALIGNMENT_PREFIX;
		final VerticalAlignmentEnum vAlign= loadVAlignment(vAlignPrefix, properties);
		if (vAlign != null) {
			style.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT, vAlign);
		}

		// Font
		final String fontPrefix= prefix + DOT + FONT_PREFIX;
		final Font font= loadFont(fontPrefix, properties);
		if (font != null) {
			style.setAttributeValue(CellStyleAttributes.FONT, font);
		}

		// Border Style
		final String borderPrefix= prefix + DOT + BORDER_PREFIX;
		final BorderStyle borderStyle= loadBorderStyle(borderPrefix, properties);
		if (borderStyle != null) {
			style.setAttributeValue(CellStyleAttributes.BORDER_STYLE, borderStyle);
		}

		return style;
	}

	private static BorderStyle loadBorderStyle(final String borderPrefix, final Properties properties) {
		final String borderStyle= properties.getProperty(borderPrefix);
		if (borderStyle != null) {
			return new BorderStyle(borderStyle);
		}
		return null;
	}

	private static Font loadFont(final String fontPrefix, final Properties properties) {
		final String fontdata= properties.getProperty(fontPrefix);
		if (fontdata != null) {
			return GUIHelper.getFont(new FontData(fontdata));
		}
		return null;
	}

	private static HorizontalAlignment loadHAlignment(final String hAlignPrefix, final Properties properties) {
		final String enumName= properties.getProperty(hAlignPrefix);
		if (enumName != null) {
			return HorizontalAlignment.valueOf(enumName);
		}
		return null;
	}

	private static VerticalAlignmentEnum loadVAlignment(final String vAlignPrefix, final Properties properties) {
		final String enumName= properties.getProperty(vAlignPrefix);
		if (enumName != null) {
			return VerticalAlignmentEnum.valueOf(enumName);
		}
		return null;
	}

	protected static Color loadColor(final String prefix, final Properties properties) {
		return ColorPersistor.loadColor(prefix, properties);
	}
}
