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

import static de.walware.ecommons.waltable.painter.cell.GraphicsUtils.safe;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import de.walware.ecommons.waltable.config.ConfigRegistry;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.style.CellStyleAttributes;
import de.walware.ecommons.waltable.style.CellStyleUtil;

/**
 * Paints the background of the cell using the color from the cell style.
 * If no background color is registered in the {@link ConfigRegistry} the painting
 * is skipped.
 * <p>
 * Example: The {@link TextPainter} inherits this and uses the paint method
 * in this class to paint the background of the cell.
 *
 * Can be used as a cell painter or a decorator.
 */
public class BackgroundPainter extends CellPainterWrapper {

	public BackgroundPainter() {}

	public BackgroundPainter(final ICellPainter painter) {
		super(painter);
	}

	@Override
	public void paintCell(final ILayerCell cell, final GC gc, final LRectangle bounds, final IConfigRegistry configRegistry) {
		final Color backgroundColor= getBackgroundColour(cell, configRegistry);
		if (backgroundColor != null) {
			final Color originalBackground= gc.getBackground();

			gc.setBackground(backgroundColor);
			gc.fillRectangle(safe(bounds));

			gc.setBackground(originalBackground);
		}

		super.paintCell(cell, gc, bounds, configRegistry);
	}
	
	protected Color getBackgroundColour(final ILayerCell cell, final IConfigRegistry configRegistry) {
		return CellStyleUtil.getCellStyle(cell, configRegistry).getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR);
	}

}
