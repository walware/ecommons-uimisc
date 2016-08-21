/*******************************************************************************
 * Copyright (c) 2012-2016 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package de.walware.ecommons.waltable.painter.cell;

import static de.walware.ecommons.waltable.painter.cell.GraphicsUtils.safe;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.util.GUIHelper;


/**
 * Cell painter painting a diagonal line from the top left to the bottom right corner
 */
public class DiagCellPainter extends BackgroundPainter {
	
	
	private final Color color;
	
	
	public DiagCellPainter(final Color color) {
		this.color= color;
	}
	
	
	@Override
	public void paintCell(final ILayerCell cell, final GC gc, final LRectangle bounds, final IConfigRegistry configRegistry) {
		super.paintCell(cell, gc, bounds, configRegistry);
		
		gc.setForeground(this.color);
		gc.setAntialias(SWT.ON);
		gc.drawLine(safe(bounds.x), safe(bounds.y), safe(bounds.x + bounds.width - 1), safe(bounds.y + bounds.height - 1));
		gc.setAntialias(GUIHelper.DEFAULT_ANTIALIAS);
	}
	
}
