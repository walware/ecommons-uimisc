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

package de.walware.ecommons.waltable.sort.config;

import org.eclipse.swt.SWT;

import de.walware.ecommons.waltable.config.CellConfigAttributes;
import de.walware.ecommons.waltable.config.DefaultComparator;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.config.IConfiguration;
import de.walware.ecommons.waltable.grid.GridRegion;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.painter.cell.ICellPainter;
import de.walware.ecommons.waltable.painter.cell.decorator.BeveledBorderDecorator;
import de.walware.ecommons.waltable.sort.SortConfigAttributes;
import de.walware.ecommons.waltable.sort.action.SortColumnAction;
import de.walware.ecommons.waltable.sort.painter.SortableHeaderTextPainter;
import de.walware.ecommons.waltable.style.DisplayMode;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;
import de.walware.ecommons.waltable.ui.matcher.MouseEventMatcher;


public class DefaultSortConfiguration implements IConfiguration {
	
	public static final String SORT_DOWN_CONFIG_TYPE= "SORT_DOWN"; //$NON-NLS-1$
	public static final String SORT_UP_CONFIG_TYPE= "SORT_UP"; //$NON-NLS-1$
	
	/** The sort sequence can be appended to this base */
	public static final String SORT_SEQ_CONFIG_TYPE= "SORT_SEQ_"; //$NON-NLS-1$
	
	private final ICellPainter cellPainter;
	
	public DefaultSortConfiguration() {
		this(new BeveledBorderDecorator(new SortableHeaderTextPainter()));
	}
	
	public DefaultSortConfiguration(final ICellPainter cellPainter) {
		this.cellPainter= cellPainter;
	}
	
	@Override
	public void configureLayer(final ILayer layer) {}
	
	@Override
	public void configureRegistry(final IConfigRegistry configRegistry) {
		configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, new DefaultComparator());
		
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, this.cellPainter, DisplayMode.NORMAL, SORT_DOWN_CONFIG_TYPE);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, this.cellPainter, DisplayMode.NORMAL, SORT_UP_CONFIG_TYPE);
	}
	
	@Override
	public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
		uiBindingRegistry.registerSingleClickBinding(
				new MouseEventMatcher(SWT.ALT, GridRegion.COLUMN_HEADER.toString(), 1),	new SortColumnAction(false));
		
		uiBindingRegistry.registerSingleClickBinding(
				new MouseEventMatcher(SWT.ALT | SWT.MOD2, GridRegion.COLUMN_HEADER.toString(), 1), new SortColumnAction(true));
	}
	
}
