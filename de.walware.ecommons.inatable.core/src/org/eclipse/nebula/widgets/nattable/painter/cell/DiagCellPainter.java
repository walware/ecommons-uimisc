/*******************************************************************************
 * Copyright (c) 2012-2013 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.painter.cell;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;


/**
 * Cell painter painting a diagonal line from the top left to the bottom right corner
 */
public class DiagCellPainter extends BackgroundPainter {

	private Color color;


	public DiagCellPainter(Color color) {
		this.color = color;
	}


	@Override
	public void paintCell(ILayerCell cell, GC gc, Rectangle bounds, IConfigRegistry configRegistry) {
		super.paintCell(cell, gc, bounds, configRegistry);
		
		gc.setForeground(color);
		gc.setAntialias(SWT.ON);
		gc.drawLine(bounds.x, bounds.y, bounds.x + bounds.width - 1, bounds.y + bounds.height - 1);
		gc.setAntialias(GUIHelper.DEFAULT_ANTIALIAS);
	}

}
