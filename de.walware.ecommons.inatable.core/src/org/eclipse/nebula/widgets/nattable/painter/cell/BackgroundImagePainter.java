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
package org.eclipse.nebula.widgets.nattable.painter.cell;

import static org.eclipse.nebula.widgets.nattable.painter.cell.GraphicsUtils.safe;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.widgets.Display;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;


/**
 * Paints the cell background using an image.
 * Image is repeated to cover the background. Similar to HTML table painting.
 */
public class BackgroundImagePainter extends CellPainterWrapper {

	public final Color separatorColor;
	private final Image bgImage;

	/**
	 * @param interiorPainter used for painting the cell contents
	 * @param bgImage to be used for painting the background
	 * @param separatorColor to be used for drawing left and right borders for the cell.
	 * 	Set to null if the borders are not required.
	 */
	public BackgroundImagePainter(ICellPainter interiorPainter, Image bgImage, Color separatorColor) {
		super(interiorPainter);
		this.bgImage = bgImage;
		this.separatorColor = separatorColor;
	}

	@Override
	public long getPreferredWidth(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
		return super.getPreferredWidth(cell, gc, configRegistry) + 4;
	}

	@Override
	public long getPreferredHeight(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
		return super.getPreferredHeight(cell, gc, configRegistry) + 4;
	}

	@Override
	public void paintCell(ILayerCell cell, GC gc, Rectangle rectangle, IConfigRegistry configRegistry) {
		// Save GC settings
		Color originalBackground = gc.getBackground();
		Color originalForeground = gc.getForeground();

		Pattern pattern = new Pattern(Display.getCurrent(), bgImage);
		gc.setBackgroundPattern(pattern);

		gc.fillRectangle(safe(rectangle));
		
		gc.setBackgroundPattern(null);
		pattern.dispose();

		if (separatorColor != null) {
			gc.setForeground(separatorColor);
			gc.drawLine(safe(rectangle.x - 1), safe(rectangle.y), safe(rectangle.x - 1), safe(rectangle.y + rectangle.height));
			gc.drawLine(safe(rectangle.x - 1 + rectangle.width), safe(rectangle.y), safe(rectangle.x - 1 + rectangle.width), safe(rectangle.y + rectangle.height));
		}

		// Restore original GC settings
		gc.setBackground(originalBackground);
		gc.setForeground(originalForeground);

		// Draw interior
		Rectangle interiorBounds = new Rectangle(safe(rectangle.x + 2), safe(rectangle.y + 2), safe(rectangle.width - 4), safe(rectangle.height - 4));
		super.paintCell(cell, gc, interiorBounds, configRegistry);
	}

}
