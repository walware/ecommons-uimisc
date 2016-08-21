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
// ~
package de.walware.ecommons.waltable.selection.config;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import de.walware.ecommons.waltable.config.AbstractRegistryConfiguration;
import de.walware.ecommons.waltable.config.CellConfigAttributes;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.grid.GridRegion;
import de.walware.ecommons.waltable.style.BorderStyle;
import de.walware.ecommons.waltable.style.BorderStyle.LineStyle;
import de.walware.ecommons.waltable.style.CellStyleAttributes;
import de.walware.ecommons.waltable.style.DisplayMode;
import de.walware.ecommons.waltable.style.SelectionStyleLabels;
import de.walware.ecommons.waltable.style.Style;
import de.walware.ecommons.waltable.util.GUIHelper;


/**
 * Sets up rendering style used for selected areas and the selection anchor.
 */
public class DefaultSelectionStyleConfiguration extends AbstractRegistryConfiguration {
	
	// Selection style
	public Font selectionFont= GUIHelper.getFont(new FontData("Verdana", 8, SWT.BOLD | SWT.ITALIC)); //$NON-NLS-1$
	public Color selectionBgColor= GUIHelper.COLOR_TITLE_INACTIVE_BACKGROUND;
	public Color selectionFgColor= GUIHelper.COLOR_BLACK;
	
	// Anchor style
	public Color anchorBorderColor= GUIHelper.COLOR_DARK_GRAY;
	public BorderStyle anchorBorderStyle= new BorderStyle(1, this.anchorBorderColor, LineStyle.SOLID);
	public Color anchorBgColor= GUIHelper.COLOR_GRAY;
	public Color anchorFgColor= GUIHelper.COLOR_WHITE;
	
	// Selected headers style
	public Color selectedHeaderBgColor= GUIHelper.COLOR_GRAY;
	public Color selectedHeaderFgColor= GUIHelper.COLOR_WHITE;
	public Font selectedHeaderFont= GUIHelper.getFont(new FontData("Verdana", 10, SWT.BOLD)); //$NON-NLS-1$
	public BorderStyle selectedHeaderBorderStyle= new BorderStyle(-1, this.selectedHeaderFgColor, LineStyle.SOLID);
	
	// Anchor grid line style
	public Color anchorGridBorderColor= GUIHelper.COLOR_BLACK;
	public BorderStyle anchorGridBorderStyle= new BorderStyle(1, this.anchorGridBorderColor, LineStyle.DOTTED);
	
	
	@Override
	public void configureRegistry(final IConfigRegistry configRegistry) {
		configureSelectionStyle(configRegistry);
		configureSelectionAnchorStyle(configRegistry);
		configureSelectionAnchorGridLineStyle(configRegistry);
		configureHeaderHasSelectionStyle(configRegistry);
		configureHeaderFullySelectedStyle(configRegistry);
	}
	
	protected void configureSelectionStyle(final IConfigRegistry configRegistry) {
		final Style cellStyle= new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.FONT, this.selectionFont);
		cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, this.selectionBgColor);
		cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, this.selectionFgColor);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
				DisplayMode.SELECT );
	}
	
	protected void configureSelectionAnchorStyle(final IConfigRegistry configRegistry) {
		// Selection anchor style for normal display mode
		Style cellStyle= new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, this.anchorBorderStyle);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
				DisplayMode.NORMAL, SelectionStyleLabels.SELECTION_ANCHOR_STYLE );
		
		// Selection anchor style for select display mode
		cellStyle= new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, this.anchorBgColor);
		cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, this.anchorFgColor);
		cellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, this.anchorBorderStyle);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
				DisplayMode.SELECT, SelectionStyleLabels.SELECTION_ANCHOR_STYLE );
	}
	
	protected void configureSelectionAnchorGridLineStyle(final IConfigRegistry configRegistry) {
		final Style cellStyle= new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, this.anchorGridBorderStyle);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
				DisplayMode.SELECT, SelectionStyleLabels.SELECTION_ANCHOR_GRID_LINE_STYLE );
	}
	
	protected void configureHeaderHasSelectionStyle(final IConfigRegistry configRegistry) {
		final Style cellStyle= new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, this.selectedHeaderFgColor);
		cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, this.selectedHeaderBgColor);
		cellStyle.setAttributeValue(CellStyleAttributes.FONT, this.selectedHeaderFont);
		cellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, this.selectedHeaderBorderStyle);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
				DisplayMode.SELECT, GridRegion.COLUMN_HEADER );
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
				DisplayMode.SELECT, GridRegion.CORNER );
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
				DisplayMode.SELECT, GridRegion.ROW_HEADER );
	}
	
	protected void configureHeaderFullySelectedStyle(final IConfigRegistry configRegistry) {
		// Header fully selected
		final Style cellStyle= new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_NORMAL_SHADOW);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
				DisplayMode.SELECT, SelectionStyleLabels.COLUMN_FULLY_SELECTED_STYLE);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
				DisplayMode.SELECT, SelectionStyleLabels.ROW_FULLY_SELECTED_STYLE);
	}
	
}
