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
package de.walware.ecommons.waltable.painter.cell.decorator;


import static de.walware.ecommons.waltable.painter.cell.GraphicsUtils.safe;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.painter.cell.BackgroundPainter;
import de.walware.ecommons.waltable.painter.cell.ICellPainter;
import de.walware.ecommons.waltable.util.GUIHelper;

public class XPBackgroundDecorator extends BackgroundPainter {

	public final Color separatorColor;
	
	public final Color gradientColor1;
	public final Color gradientColor2;
	public final Color gradientColor3;
	
	public final Color highlightColor1;
	public final Color highlightColor2;
	public final Color highlightColor3;

	public XPBackgroundDecorator(final ICellPainter interiorPainter) {
		super(interiorPainter);
		
		this.separatorColor= GUIHelper.getColor(199, 197, 178);
		
		this.gradientColor1= GUIHelper.getColor(226, 222, 205);
		this.gradientColor2= GUIHelper.getColor(214, 210, 194);
		this.gradientColor3= GUIHelper.getColor(203, 199, 184);
		
		this.highlightColor1= GUIHelper.getColor(250, 171, 0);
		this.highlightColor2= GUIHelper.getColor(252, 194, 71);
		this.highlightColor3= GUIHelper.getColor(250, 178, 24);
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
		// Draw background
		super.paintCell(cell, gc, lRectangle, configRegistry);

		// Draw interior
		final LRectangle interiorBounds= new LRectangle(lRectangle.x + 2, lRectangle.y + 2, lRectangle.width - 4, lRectangle.height - 4);
		super.paintCell(cell, gc, interiorBounds, configRegistry);
		
		// Save GC settings
		final Color originalBackground= gc.getBackground();
		final Color originalForeground= gc.getForeground();
		
		// Draw separator
		final int x0= safe(lRectangle.x);
		int x= x0;
		gc.setForeground(GUIHelper.COLOR_WHITE);
		gc.drawLine(x, safe(lRectangle.y + 3), x, safe(lRectangle.y + lRectangle.height - 6));
		
		x= safe(lRectangle.x + lRectangle.width - 1);
		gc.setForeground(this.separatorColor);
		gc.drawLine(x, safe(lRectangle.y + 3), x, safe(lRectangle.y + lRectangle.height - 6));
		
		// Restore GC settings
		gc.setBackground(originalBackground);
		gc.setForeground(originalForeground);

		// Draw bottom edge
		final boolean isHighlight= false;
		
		final int x1= safe(lRectangle.x + lRectangle.width);
		int y= safe(lRectangle.y + lRectangle.height - 3);
		if (y >= Integer.MAX_VALUE - 3) {
			return;
		}
		gc.setForeground(isHighlight ? this.highlightColor1 : this.gradientColor1);
		gc.drawLine(x0, y, x1, y);
		
		y++;
		gc.setForeground(isHighlight ? this.highlightColor2 : this.gradientColor2);
		gc.drawLine(x0, y, x1, y);
		
		y++;
		gc.setForeground(isHighlight ? this.highlightColor3 : this.gradientColor3);
		gc.drawLine(x0, y, x1, y);
	}
	
}
