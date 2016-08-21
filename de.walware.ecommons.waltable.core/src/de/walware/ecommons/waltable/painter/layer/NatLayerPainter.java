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
// -depend
package de.walware.ecommons.waltable.painter.layer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.internal.WaLTablePlugin;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.painter.IOverlayPainter;
import de.walware.ecommons.waltable.swt.SWTUtil;


public class NatLayerPainter implements ILayerPainter {
	
	
	private final NatTable natTable;
	
	
	public NatLayerPainter(final NatTable natTable) {
		this.natTable= natTable;
	}
	
	
	@Override
	public void paintLayer(final ILayer natLayer, final GC gc,
			final int xOffset, final int yOffset, final Rectangle pixelRectangle,
			final IConfigRegistry configRegistry) {
		try {
			paintBackground(natLayer, gc, xOffset, yOffset, pixelRectangle, configRegistry);
			
			gc.setForeground(this.natTable.getForeground());
			
			final Rectangle paintRectangle= pixelRectangle.intersection(SWTUtil.toSWT(
					new LRectangle(xOffset, yOffset, natLayer.getWidth(), natLayer.getHeight()) ));
			
			if (!paintRectangle.isEmpty()) {
				this.natTable.getLayer().getLayerPainter().paintLayer(natLayer, gc,
						xOffset, yOffset, paintRectangle, configRegistry );
			}
			
			paintOverlays(natLayer, gc, xOffset, yOffset, pixelRectangle, configRegistry);
		} catch (final Exception e) {
			WaLTablePlugin.log(new Status(IStatus.ERROR, WaLTablePlugin.PLUGIN_ID,
					"An error occurred while painting the table.", e ));
		}
	}
	
	protected void paintBackground(final ILayer natLayer, final GC gc, final long xOffset, final long yOffset, final org.eclipse.swt.graphics.Rectangle rectangle, final IConfigRegistry configRegistry) {
		gc.setBackground(this.natTable.getBackground());
		
		// Clean Background
		gc.fillRectangle(rectangle);
	}
	
	protected void paintOverlays(final ILayer natLayer, final GC gc, final long xOffset, final long yOffset, final org.eclipse.swt.graphics.Rectangle rectangle, final IConfigRegistry configRegistry) {
		for (final IOverlayPainter overlayPainter : this.natTable.getOverlayPainters()) {
			overlayPainter.paintOverlay(gc, this.natTable);
		}
	}
	
	@Override
	public LRectangle adjustCellBounds(final long columnPosition, final long rowPosition, final LRectangle cellBounds) {
		final ILayerPainter layerPainter= this.natTable.getLayer().getLayerPainter();
		return layerPainter.adjustCellBounds(columnPosition, rowPosition, cellBounds);
	}
	
}
