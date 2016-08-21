/*******************************************************************************
 * Copyright (c) 2012-2016 Dirk Fauth and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Dirk Fauth - initial API and implementation
 ******************************************************************************/
package de.walware.ecommons.waltable.painter.cell;

import org.eclipse.swt.graphics.GC;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.edit.editor.PasswordCellEditor;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.style.CellStyleAttributes;
import de.walware.ecommons.waltable.style.CellStyleUtil;
import de.walware.ecommons.waltable.style.IStyle;

/**
 * Specialized {@link TextPainter} that will paint the text that should be showed within
 * a cell anonymized. Like in {@link PasswordCellEditor} every character will be replaced 
 * with another echo character. The echo character can be configured by setting the 
 * attribute {@link CellStyleAttributes#PASSWORD_ECHO_CHAR} to the cell style to use. 
 * If no echo character is configured, the bullet character will be used as echo character.
 * 
 * @author Dirk Fauth
 *
 * @see PasswordCellEditor
 */
public class PasswordTextPainter extends TextPainter {

	/**
	 * The echo character to use for anonymization.
	 * Stored as member variable because getTextToDisplay() has no context information.
	 * Will be set on every paintCell() so changes to the cell style will be taken
	 * into account on runtime.
	 * Default value is the bullet character.
	 */
	private Character echoChar= '\u2022';
	
	public PasswordTextPainter() {
		super();
	}

	public PasswordTextPainter(final boolean wrapText, final boolean paintBg) {
		super(wrapText, paintBg);
	}

	public PasswordTextPainter(final boolean wrapText, final boolean paintBg, final int spacing) {
		super(wrapText, paintBg, spacing);
	}

	public PasswordTextPainter(final boolean wrapText, final boolean paintBg, final boolean calculate) {
		super(wrapText, paintBg, calculate);
	}

	public PasswordTextPainter(final boolean wrapText, final boolean paintBg, final int spacing, final boolean calculate) {
		super(wrapText, paintBg, spacing, calculate);
	}

	
	@Override
	public void paintCell(final ILayerCell cell, final GC gc, final LRectangle lRectangle, final IConfigRegistry configRegistry) {
		//check for the configuration of a echo character in the corresponding cell style
		final IStyle cellStyle= CellStyleUtil.getCellStyle(cell, configRegistry);
		final Character configEchoChar= cellStyle.getAttributeValue(CellStyleAttributes.PASSWORD_ECHO_CHAR);
		if (configEchoChar != null) {
			this.echoChar= configEchoChar;
		}
		super.paintCell(cell, gc, lRectangle, configRegistry);
	}
	
	@Override
	protected String getTextToDisplay(final ILayerCell cell, final GC gc, final long availableLength, final String text) {
		final String result= super.getTextToDisplay(cell, gc, availableLength, text);
		//replace all characters with the configured echo character
		return result.replaceAll(".", this.echoChar.toString()); //$NON-NLS-1$
	}
}
