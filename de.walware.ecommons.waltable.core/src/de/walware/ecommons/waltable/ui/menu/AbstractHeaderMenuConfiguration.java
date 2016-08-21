/*******************************************************************************
 * Copyright (c) 2013-2016 Dirk Fauth and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Dirk Fauth <dirk.fauth@gmail.com> - initial API and implementation
 *******************************************************************************/
package de.walware.ecommons.waltable.ui.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Menu;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.config.AbstractUiBindingConfiguration;
import de.walware.ecommons.waltable.grid.GridRegion;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;
import de.walware.ecommons.waltable.ui.matcher.MouseEventMatcher;

/**
 * Abstract implementation for adding header menus to a NatTable.
 * There will be header menus attached to the column header, the row header
 * and the corner region. By default empty menus will be attached, which will
 * result in not showing a menu. On creating a specialized header menu configuration
 * you can choose for which header region you want to add a menu.
 */
public class AbstractHeaderMenuConfiguration extends AbstractUiBindingConfiguration {

	/**
	 * The column header menu.
	 */
	private final Menu colHeaderMenu;
	/**
	 * The row header menu
	 */
	private final Menu rowHeaderMenu;
	/**
	 * The corner region menu
	 */
	private final Menu cornerMenu;

	/**
	 * Creates a header menu configuration that attaches menus to the row header,
	 * the column header and the corner region.
	 */
	public AbstractHeaderMenuConfiguration(final NatTable natTable) {
		this.colHeaderMenu= createColumnHeaderMenu(natTable).build();
		this.rowHeaderMenu= createRowHeaderMenu(natTable).build();
		this.cornerMenu= createCornerMenu(natTable).build();
		
		//ensure that the menus will be disposed when the NatTable is disposed
		natTable.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(final DisposeEvent e) {
				if (AbstractHeaderMenuConfiguration.this.colHeaderMenu != null) {
					AbstractHeaderMenuConfiguration.this.colHeaderMenu.dispose();
				}
				
				if (AbstractHeaderMenuConfiguration.this.rowHeaderMenu != null) {
					AbstractHeaderMenuConfiguration.this.rowHeaderMenu.dispose();
				}
				
				if (AbstractHeaderMenuConfiguration.this.cornerMenu != null) {
					AbstractHeaderMenuConfiguration.this.cornerMenu.dispose();
				}
			}

		});
	}

	/**
	 * Creates the {@link PopupMenuBuilder} for the column header menu with the menu 
	 * items that should be added to the menu.
	 * @param natTable The NatTable where the menu should be attached.
	 * @return The {@link PopupMenuBuilder} that is used to build the column 
	 * 			header menu.
	 */
	protected PopupMenuBuilder createColumnHeaderMenu(final NatTable natTable) {
		return new PopupMenuBuilder(natTable);
	}

	/**
	 * Creates the {@link PopupMenuBuilder} for the row header menu with the menu 
	 * items that should be added to the menu.
	 * @param natTable The NatTable where the menu should be attached.
	 * @return The {@link PopupMenuBuilder} that is used to build the row 
	 * 			header menu.
	 */
	protected PopupMenuBuilder createRowHeaderMenu(final NatTable natTable) {
		return new PopupMenuBuilder(natTable);
	}
	
	/**
	 * Creates the {@link PopupMenuBuilder} for the corner menu with the menu 
	 * items that should be added to the menu.
	 * @param natTable The NatTable where the menu should be attached.
	 * @return The {@link PopupMenuBuilder} that is used to build the corner menu. 
	 */
	protected PopupMenuBuilder createCornerMenu(final NatTable natTable) {
		return new PopupMenuBuilder(natTable);
	}
	
	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.config.IConfiguration#configureUiBindings(de.walware.ecommons.waltable.ui.binding.UiBindingRegistry)
	 */
	@Override
	public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
		if (this.colHeaderMenu != null) {
			uiBindingRegistry.registerMouseDownBinding(
					new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),
					new PopupMenuAction(this.colHeaderMenu));
		}

		if (this.rowHeaderMenu != null) {
			uiBindingRegistry.registerMouseDownBinding(
					new MouseEventMatcher(SWT.NONE, GridRegion.ROW_HEADER, MouseEventMatcher.RIGHT_BUTTON),
					new PopupMenuAction(this.rowHeaderMenu));
		}

		if (this.cornerMenu != null) {
			uiBindingRegistry.registerMouseDownBinding(
					new MouseEventMatcher(SWT.NONE, GridRegion.CORNER, MouseEventMatcher.RIGHT_BUTTON),
					new PopupMenuAction(this.cornerMenu));
		}
	}

}
