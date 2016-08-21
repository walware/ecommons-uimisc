/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Dirk Fauth
 ******************************************************************************/
package de.walware.ecommons.waltable.painter.cell;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import de.walware.ecommons.waltable.config.ConfigRegistry;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.style.CellStyleAttributes;
import de.walware.ecommons.waltable.style.CellStyleUtil;

/**
 * Paints the background of the cell with a gradient sweeping using the style configuration.
 * To configure the gradient sweeping the following style attributes need to be configured
 * in the {@link ConfigRegistry}: 
 * <ul>
 * 	<li>{@link CellStyleAttributes#GRADIENT_FOREGROUND_COLOR} or {@link CellStyleAttributes#FOREGROUND_COLOR}</li>
 * 	<li>{@link CellStyleAttributes#GRADIENT_BACKGROUND_COLOR} or {@link CellStyleAttributes#BACKGROUND_COLOR}</li>
 * </ul>
 * If none of these values are registered in the {@link ConfigRegistry} the painting is skipped.
 * <p>
 * Can be used as a cell painter or a decorator.

 * @author Dirk Fauth
 *
 */
public class GradientBackgroundPainter extends CellPainterWrapper {

	/**
	 * @param vertical if <code>true</code> sweeps from top to bottom, else 
	 *        sweeps from left to right. <code>false</code> is default
	 */
	private final boolean vertical;
	
	/**
	 * Creates a {@link GradientBackgroundPainter} with a gradient sweeping from
	 * left to right.
	 */
	public GradientBackgroundPainter() {
		this(false);
	}

	/**
	 * Creates a {@link GradientBackgroundPainter} where the sweeping direction
	 * can be set.
	 * @param vertical if <code>true</code> sweeps from top to bottom, else 
	 *        sweeps from left to right. <code>false</code> is default
	 */
	public GradientBackgroundPainter(final boolean vertical) {
		this.vertical= vertical;
	}

	/**
	 * Creates a {@link GradientBackgroundPainter} as wrapper for the given painter with a gradient sweeping from
	 * left to right.
	 * @param painter The {@link ICellPainter} that is wrapped by this {@link GradientBackgroundPainter}
	 */
	public GradientBackgroundPainter(final ICellPainter painter) {
		this(painter, false);
	}

	/**
	 * Creates a {@link GradientBackgroundPainter} as wrapper for the given painter where the sweeping direction
	 * can be set.
	 * @param painter The {@link ICellPainter} that is wrapped by this {@link GradientBackgroundPainter}
	 * @param vertical if <code>true</code> sweeps from top to bottom, else 
	 *        sweeps from left to right. <code>false</code> is default
	 */
	public GradientBackgroundPainter(final ICellPainter painter, final boolean vertical) {
		super(painter);
		this.vertical= vertical;
	}

	@Override
	public void paintCell(final ILayerCell cell, final GC gc, final LRectangle bounds, final IConfigRegistry configRegistry) {
		final Color foregroundColor= getForeGroundColour(cell, configRegistry);
		final Color backgroundColor= getBackgroundColour(cell, configRegistry);
		if (backgroundColor != null && foregroundColor != null) {
			final Color originalForeground= gc.getForeground();
			final Color originalBackground= gc.getBackground();

			gc.setForeground(foregroundColor);
			gc.setBackground(backgroundColor);
			final org.eclipse.swt.graphics.Rectangle rect= GraphicsUtils.safe(bounds);
			gc.fillGradientRectangle(rect.x, rect.y, rect.width, rect.height, this.vertical);

			gc.setForeground(originalForeground);
			gc.setBackground(originalBackground);
		}

		super.paintCell(cell, gc, bounds, configRegistry);
	}
	
	/**
	 * Searches the foreground color to be used for gradient sweeping. First checks the {@link de.walware.ecommons.waltable.config.ConfigRegistry} if there
	 * is a value for the attribute {@link CellStyleAttributes#GRADIENT_FOREGROUND_COLOR} is registered. If there is one
	 * this value will be returned, if not it is checked if there is a value registered for {@link CellStyleAttributes#FOREGROUND_COLOR}
	 * and returned. If there is no value registered for any of these attributes, <code>null</code> will be returned which
	 * will skip the painting.
	 * @param cell The {@link de.walware.ecommons.waltable.layer.cell.ForwardLayerCell} for which the style attributes should be retrieved out of the {@link de.walware.ecommons.waltable.config.ConfigRegistry}
	 * @param configRegistry The {@link de.walware.ecommons.waltable.config.ConfigRegistry} to retrieve the attribute values from.
	 * @return The {@link Color} to use as foreground color of the gradient sweeping or <code>null</code> if none was configured.
	 */
	protected Color getForeGroundColour(final ILayerCell cell, final IConfigRegistry configRegistry) {
		final Color fgColor= CellStyleUtil.getCellStyle(cell, configRegistry).getAttributeValue(CellStyleAttributes.GRADIENT_FOREGROUND_COLOR);
		return fgColor != null ? fgColor : CellStyleUtil.getCellStyle(cell, configRegistry).getAttributeValue(CellStyleAttributes.FOREGROUND_COLOR);
	}
	
	/**
	 * Searches the background color to be used for gradient sweeping. First checks the {@link de.walware.ecommons.waltable.config.ConfigRegistry} if there
	 * is a value for the attribute {@link CellStyleAttributes#GRADIENT_BACKGROUND_COLOR} is registered. If there is one
	 * this value will be returned, if not it is checked if there is a value registered for {@link CellStyleAttributes#BACKGROUND_COLOR}
	 * and returned. If there is no value registered for any of these attributes, <code>null</code> will be returned which
	 * will skip the painting.
	 * @param cell The {@link de.walware.ecommons.waltable.layer.cell.ForwardLayerCell} for which the style attributes should be retrieved out of the {@link de.walware.ecommons.waltable.config.ConfigRegistry}
	 * @param configRegistry The {@link de.walware.ecommons.waltable.config.ConfigRegistry} to retrieve the attribute values from.
	 * @return The {@link Color} to use as background color of the gradient sweeping or <code>null</code> if none was configured.
	 */
	protected Color getBackgroundColour(final ILayerCell cell, final IConfigRegistry configRegistry) {
		final Color bgColor= CellStyleUtil.getCellStyle(cell, configRegistry).getAttributeValue(CellStyleAttributes.GRADIENT_BACKGROUND_COLOR);
		return bgColor != null ? bgColor : CellStyleUtil.getCellStyle(cell, configRegistry).getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR);
	}

}
