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
package de.walware.ecommons.waltable.ui.menu;


import org.eclipse.swt.widgets.Menu;

import de.walware.ecommons.waltable.NatTable;

/**
 * This class is used to create a context menu.
 */
public class PopupMenuBuilder {

	/**
	 * The active NatTable instance the context menu should be added to.
	 * Needed in advance to be able to add custom menu items that need the
	 * NatTable instance.
	 */
	protected NatTable natTable;
	
	/**
	 * The {@link Menu} that is created with this popup menu builder.
	 */
	protected Menu popupMenu;

	/**
	 * Creates a new {@link Menu} that is only configurable with this instance of
	 * {@link PopupMenuBuilder}.
	 * @param parent The active NatTable instance the context menu should be added to.
	 */
	public PopupMenuBuilder(final NatTable parent) {
		this.natTable= parent;
		this.popupMenu= new Menu(parent.getShell());
	}

	/**
	 * Creates a popup menu builder based on the given menu. 
	 * Using this enables the possibility to use configured context menus from plugin.xml
	 * and adding NatTable commands programatically.
	 * <p>
	 * As an example you might want to create a PopupMenuBuilder by using a configured menu
	 * with the id <i>de.walware.ecommons.waltable.example.contextmenu</i>
	 * <p>
	 * <pre>
	 * ISelectionProvider isp= new RowSelectionProvider&lt;?&gt;(selectionLayer, bodyDataProvider, false);
	 * MenuManager menuManager= new MenuManager();
	 * menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	 * getSite().registerContextMenu("de.walware.ecommons.waltable.example.contextmenu", menuManager, isp);
	 * PopupMenuBuilder popupMenu= new PopupMenuBuilder(menuManager.createContextMenu(natTable));
	 * </pre>
	 * 
	 * @param natTable The active NatTable instance which might be needed for creation of 
	 * 			menu items that need the NatTable instance to work.
	 * @param menu The registered context menu.
	 */
	public PopupMenuBuilder(final NatTable natTable, final Menu menu) {
		this.natTable= natTable;
		this.popupMenu= menu;
	}

	/**
	 * Adds the menu item provided by the given {@link IMenuItemProvider} to the popup menu.
	 * You can use this to add your own item to the popup menu.
	 * @param menuItemProvider The {@link IMenuItemProvider} that provides the menu item
	 * 			that should be added to the popup menu.
	 */
	public PopupMenuBuilder withMenuItemProvider(final IMenuItemProvider menuItemProvider){
		menuItemProvider.addMenuItem(this.natTable, this.popupMenu);
		return this;
	}

	public PopupMenuBuilder withAutoResizeSelectedColumnsMenuItem() {
		return withMenuItemProvider(MenuItemProviders.autoResizeColumnMenuItemProvider());
	}

	public PopupMenuBuilder withAutoResizeSelectedColumnsMenuItem(final String menuLabel) {
		return withMenuItemProvider(MenuItemProviders.autoResizeColumnMenuItemProvider(menuLabel));
	}

	public PopupMenuBuilder withAutoResizeSelectedRowsMenuItem() {
		return withMenuItemProvider(MenuItemProviders.autoResizeRowMenuItemProvider());
	}

	public PopupMenuBuilder withAutoResizeSelectedRowsMenuItem(final String menuLabel) {
		return withMenuItemProvider(MenuItemProviders.autoResizeRowMenuItemProvider(menuLabel));
	}

	public PopupMenuBuilder withColumnStyleEditor() {
		return withMenuItemProvider(MenuItemProviders.columnStyleEditorMenuItemProvider());
	}

	public PopupMenuBuilder withColumnStyleEditor(final String menuLabel) {
		return withMenuItemProvider(MenuItemProviders.columnStyleEditorMenuItemProvider(menuLabel));
	}

	public PopupMenuBuilder withInspectLabelsMenuItem() {
		return withMenuItemProvider(MenuItemProviders.inspectLabelsMenuItemProvider());
	}

	/**
	 * Adds the menu item for opening the view management dialog to the popup menu. Uses the default text
	 * localized in NatTable core resource bundles. Uses the given String as label for the menu item.
	 * @return The {@link PopupMenuBuilder} with the menu item added for showing the view 
	 * 			management dialog for managing NatTable states.
	 * @see MenuItemProviders#stateManagerMenuItemProvider()
	 */
	public PopupMenuBuilder withStateManagerMenuItemProvider() {
		return withMenuItemProvider(MenuItemProviders.stateManagerMenuItemProvider());
	}

	/**
	 * Adds the menu item for opening the view management dialog to the popup menu. 
	 * @param menuLabel The label to use for showing the item in the popup menu.
	 * @return The {@link PopupMenuBuilder} with the menu item added for showing the view 
	 * 			management dialog for managing NatTable states.
	 * @see MenuItemProviders#stateManagerMenuItemProvider(String)
	 */
	public PopupMenuBuilder withStateManagerMenuItemProvider(final String menuLabel) {
		return withMenuItemProvider(MenuItemProviders.stateManagerMenuItemProvider(menuLabel));
	}

	/**
	 * Adds a separator to the popup menu.
	 * @return The {@link PopupMenuBuilder} with an added separator.
	 * @see MenuItemProviders#separatorMenuItemProvider()
	 */
	public PopupMenuBuilder withSeparator() {
		return withMenuItemProvider(MenuItemProviders.separatorMenuItemProvider());	
	}

	/**
	 * Builds and returns the created {@link Menu}.
	 * @return The {@link Menu} that is created by this builder.
	 */
	public Menu build() {
		return this.popupMenu;
	}

}

