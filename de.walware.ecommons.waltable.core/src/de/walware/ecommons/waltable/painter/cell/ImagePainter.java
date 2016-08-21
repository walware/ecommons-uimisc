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

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.style.CellStyleAttributes;
import de.walware.ecommons.waltable.style.CellStyleUtil;
import de.walware.ecommons.waltable.style.IStyle;

/**
 * Paints an image. If no image is provided, it will attempt to look up an image from the cell style.
 */
public class ImagePainter extends BackgroundPainter {

	private final Image image;
	private final boolean paintBg;

	public ImagePainter() {
		this(null);
	}

	public ImagePainter(final Image image) {
		this(image, true);
	}

	public ImagePainter(final Image image, final boolean paintBg) {
		this.image= image;
		this.paintBg= paintBg;
	}

	@Override
	public long getPreferredWidth(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
		final Image image= getImage(cell, configRegistry);
		if (image != null) {
			return image.getBounds().width;
		} else {
			return 0;
		}
	}

	@Override
	public long getPreferredHeight(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
		final Image image= getImage(cell, configRegistry);
		if (image != null) {
			return image.getBounds().height;
		} else {
			return 0;
		}
	}

	@Override
	public ICellPainter getCellPainterAt(final long x, final long y, final ILayerCell cell, final GC gc, final LRectangle bounds, final IConfigRegistry configRegistry) {
		final Image image= getImage(cell, configRegistry);
		if (image != null) {
			final org.eclipse.swt.graphics.Rectangle imageBounds= image.getBounds();
			final IStyle cellStyle= CellStyleUtil.getCellStyle(cell, configRegistry);
			final long x0= bounds.x + CellStyleUtil.getHorizontalAlignmentPadding(cellStyle, bounds, imageBounds.width);
			final long y0= bounds.y + CellStyleUtil.getVerticalAlignmentPadding(cellStyle, bounds, imageBounds.height);
			if (	x >= x0 &&
					x < x0 + imageBounds.width &&
					y >= y0 &&
					y < y0 + imageBounds.height) {
				return super.getCellPainterAt(x, y, cell, gc, bounds, configRegistry);
			}
		}
		return null;
	}
	
	@Override
	public void paintCell(final ILayerCell cell, final GC gc, final LRectangle bounds, final IConfigRegistry configRegistry) {
		if (this.paintBg) {
			super.paintCell(cell, gc, bounds, configRegistry);
		}

		final Image image= getImage(cell, configRegistry);
		if (image != null) {
			final org.eclipse.swt.graphics.Rectangle imageBounds= image.getBounds();
			final IStyle cellStyle= CellStyleUtil.getCellStyle(cell, configRegistry);
			gc.drawImage(image,
					safe(bounds.x + CellStyleUtil.getHorizontalAlignmentPadding(cellStyle, bounds, imageBounds.width)),
					safe(bounds.y + CellStyleUtil.getVerticalAlignmentPadding(cellStyle, bounds, imageBounds.height)));
		}
	}
	
	protected Image getImage(final ILayerCell cell, final IConfigRegistry configRegistry) {
		return this.image != null ? this.image : CellStyleUtil.getCellStyle(cell, configRegistry).getAttributeValue(CellStyleAttributes.IMAGE);
	}

}
