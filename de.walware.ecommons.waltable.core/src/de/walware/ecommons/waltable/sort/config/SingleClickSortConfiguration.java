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

import de.walware.ecommons.waltable.painter.cell.ICellPainter;
import de.walware.ecommons.waltable.sort.ColumnHeaderClickEventMatcher;
import de.walware.ecommons.waltable.sort.action.SortColumnAction;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;
import de.walware.ecommons.waltable.ui.matcher.MouseEventMatcher;

/**
 * Modifies the default sort configuration to sort on a <i>single left</i>
 * click on the column header.
 */
public class SingleClickSortConfiguration extends DefaultSortConfiguration {

	public SingleClickSortConfiguration() {
		super();
	}
	
	public SingleClickSortConfiguration(final ICellPainter cellPainter) {
		super(cellPainter);
	}
	
	/**
	 * Remove the original key bindings and implement new ones.
	 */
	@Override
	public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
		// Register new bindings
		uiBindingRegistry.registerFirstSingleClickBinding(
              new ColumnHeaderClickEventMatcher(SWT.NONE, 1), new SortColumnAction(false));

		uiBindingRegistry.registerSingleClickBinding(
             MouseEventMatcher.columnHeaderLeftClick(SWT.ALT), new SortColumnAction(true));
	}

}
