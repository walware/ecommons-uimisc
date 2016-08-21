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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.widgets.Display;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;


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
	public BackgroundImagePainter(final ICellPainter interiorPainter, final Image bgImage, final Color separatorColor) {
		super(interiorPainter);
		this.bgImage= bgImage;
		this.separatorColor= separatorColor;
	}

	@Override
	public long getPreferredWidth(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
		return super.getPreferredWidth(cell, gc, configRegistry) + 4;
	}

	@Override
	public long getPreferredHeight(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
		return super.getPreferredHeight(cell, gc, configRegistry) + 4;
	}

	@Override
	public void paintCell(final ILayerCell cell, final GC gc, final LRectangle lRectangle, final IConfigRegistry configRegistry) {
		// Save GC settings
		final Color originalBackground= gc.getBackground();
		final Color originalForeground= gc.getForeground();

		final Pattern pattern= new Pattern(Display.getCurrent(), this.bgImage);
		gc.setBackgroundPattern(pattern);

		gc.fillRectangle(safe(lRectangle));
		
		gc.setBackgroundPattern(null);
		pattern.dispose();

		if (this.separatorColor != null) {
			gc.setForeground(this.separatorColor);
			gc.drawLine(safe(lRectangle.x - 1), safe(lRectangle.y), safe(lRectangle.x - 1), safe(lRectangle.y + lRectangle.height));
			gc.drawLine(safe(lRectangle.x - 1 + lRectangle.width), safe(lRectangle.y), safe(lRectangle.x - 1 + lRectangle.width), safe(lRectangle.y + lRectangle.height));
		}

		// Restore original GC settings
		gc.setBackground(originalBackground);
		gc.setForeground(originalForeground);

		// Draw interior
		final LRectangle interiorBounds= new LRectangle(safe(lRectangle.x + 2), safe(lRectangle.y + 2), safe(lRectangle.width - 4), safe(lRectangle.height - 4));
		super.paintCell(cell, gc, interiorBounds, configRegistry);
	}

}
