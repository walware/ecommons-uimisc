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
package de.walware.ecommons.waltable.ui.menu;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Widget;

import de.walware.ecommons.waltable.Messages;
import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.persistence.DisplayPersistenceDialogCommand;
import de.walware.ecommons.waltable.resize.InitializeAutoResizeCommand;
import de.walware.ecommons.waltable.style.editor.DisplayColumnStyleEditorCommand;
import de.walware.ecommons.waltable.ui.NatEventData;
import de.walware.ecommons.waltable.util.GUIHelper;


/**
 * Helper class that provides several {@link IMenuItemProvider} for menu items that can be
 * used within a popup menu in the NatTable to execute NatTable specific actions.
 */
public class MenuItemProviders {

	/**
	 * Walk up the MenuItems (in case they are nested) and find the parent {@link Menu}
	 *
	 * @param selectionEvent
	 *            on the {@link MenuItem}
	 * @return data associated with the parent {@link Menu}
	 */
	public static NatEventData getNatEventData(final SelectionEvent selectionEvent) {
		final Widget widget= selectionEvent.widget;
		if (widget == null || !(widget instanceof MenuItem)) {
			return null;
		}

		final MenuItem menuItem= (MenuItem) widget;
		Menu parentMenu= menuItem.getParent();
		Object data= null;
		while (parentMenu != null) {
			if (parentMenu.getData() == null) {
				parentMenu= parentMenu.getParentMenu();
			} else {
				data= parentMenu.getData();
				break;
			}
		}
		
		return data != null ? (NatEventData) data : null;
	}
	
	public static IMenuItemProvider autoResizeColumnMenuItemProvider() {
		return autoResizeColumnMenuItemProvider(Messages.getString("MenuItemProviders.autoResizeColumn")); //$NON-NLS-1$
	}
	
	public static IMenuItemProvider autoResizeColumnMenuItemProvider(final String menuLabel) {
		return new IMenuItemProvider() {

			@Override
			public void addMenuItem(final NatTable natTable, final Menu popupMenu) {
				final MenuItem autoResizeColumns= new MenuItem(popupMenu, SWT.PUSH);
				autoResizeColumns.setText(menuLabel);
				autoResizeColumns.setImage(GUIHelper.getImage("auto_resize")); //$NON-NLS-1$
				autoResizeColumns.setEnabled(true);

				autoResizeColumns.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent event) {
						final long columnPosition= getNatEventData(event).getColumnPosition();
						natTable.doCommand(new InitializeAutoResizeCommand(
								natTable.getDim(HORIZONTAL), columnPosition ));
					}
				});
			}
		};
	}
	
	public static IMenuItemProvider autoResizeRowMenuItemProvider() {
		return autoResizeRowMenuItemProvider(Messages.getString("MenuItemProviders.autoResizeRow")); //$NON-NLS-1$
	}
	
	public static IMenuItemProvider autoResizeRowMenuItemProvider(final String menuLabel) {
		return new IMenuItemProvider() {
			
			@Override
			public void addMenuItem(final NatTable natTable, final Menu popupMenu) {
				final MenuItem autoResizeRows= new MenuItem(popupMenu, SWT.PUSH);
				autoResizeRows.setText(menuLabel);
				autoResizeRows.setEnabled(true);
				
				autoResizeRows.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent event) {
						final long rowPosition= getNatEventData(event).getRowPosition();
						natTable.doCommand(new InitializeAutoResizeCommand(
								natTable.getDim(VERTICAL), rowPosition ));
					}
				});
			}
		};
	}
	
	public static IMenuItemProvider autoResizeAllSelectedColumnMenuItemProvider() {
		return autoResizeAllSelectedColumnMenuItemProvider(Messages.getString("MenuItemProviders.autoResizeAllSelectedColumns")); //$NON-NLS-1$
	}
	
	public static IMenuItemProvider autoResizeAllSelectedColumnMenuItemProvider(final String menuLabel) {
		return new IMenuItemProvider() {
			
			@Override
			public void addMenuItem(final NatTable natTable, final Menu popupMenu) {
				final MenuItem autoResizeColumns= new MenuItem(popupMenu, SWT.PUSH);
				autoResizeColumns.setText(menuLabel); 
				autoResizeColumns.setEnabled(true);
				
				autoResizeColumns.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent event) {
						final long columnPosition= getNatEventData(event).getColumnPosition();
						natTable.doCommand(new InitializeAutoResizeCommand(
								natTable.getDim(HORIZONTAL), columnPosition ));
					}
				});
			}
			
		};
	}
	
	public static IMenuItemProvider columnStyleEditorMenuItemProvider() {
		return columnStyleEditorMenuItemProvider(Messages.getString("MenuItemProviders.editStyles")); //$NON-NLS-1$
	}
	
	public static IMenuItemProvider columnStyleEditorMenuItemProvider(final String menuLabel) {
		return new IMenuItemProvider() {

			@Override
			public void addMenuItem(final NatTable natTable, final Menu popupMenu) {
				final MenuItem columnStyleEditor= new MenuItem(popupMenu, SWT.PUSH);
				columnStyleEditor.setText(menuLabel);
				columnStyleEditor.setImage(GUIHelper.getImage("preferences")); //$NON-NLS-1$
				columnStyleEditor.setEnabled(true);

				columnStyleEditor.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent event) {
						final long rowPosition= getNatEventData(event).getRowPosition();
						final long columnPosition= getNatEventData(event).getColumnPosition();
						natTable.doCommand(new DisplayColumnStyleEditorCommand(natTable, natTable.getConfigRegistry(), columnPosition, rowPosition));
					}
				});
			}

		};
	}

	public static IMenuItemProvider inspectLabelsMenuItemProvider() {
		return new IMenuItemProvider() {

			@Override
			public void addMenuItem(final NatTable natTable, final Menu popupMenu) {
				final MenuItem inspectLabelsMenuItem= new MenuItem(popupMenu, SWT.PUSH);
				inspectLabelsMenuItem.setText(Messages.getString("MenuItemProviders.debugInfo")); //$NON-NLS-1$
				inspectLabelsMenuItem.setEnabled(true);

				inspectLabelsMenuItem.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						final NatEventData natEventData= getNatEventData(e);
						final NatTable natTable= natEventData.getNatTable();
						final long columnPosition= natEventData.getColumnPosition();
						final long rowPosition= natEventData.getRowPosition();
						
						final ILayerCell cell= natTable.getCellByPosition(columnPosition, rowPosition);
						
						final String msg= "Display mode: " + cell.getDisplayMode() + //$NON-NLS-1$
								"\nConfig labels: " + cell.getConfigLabels() + //$NON-NLS-1$
								"\nData value: " + cell.getDataValue(0, null) + //$NON-NLS-1$
								"\n\nColumn position: " + columnPosition + //$NON-NLS-1$
								"\nColumn id: " + cell.getDim(Orientation.HORIZONTAL).getId() + //$NON-NLS-1$
								"\n\nRow position: " + rowPosition + //$NON-NLS-1$
								"\nRow id: " + cell.getDim(Orientation.VERTICAL).getId(); //$NON-NLS-1$

						final MessageBox messageBox= new MessageBox(natTable.getShell(), SWT.ICON_INFORMATION | SWT.OK);
						messageBox.setText(Messages.getString("MenuItemProviders.debugInformation")); //$NON-NLS-1$
						messageBox.setMessage(msg);
						messageBox.open();
					}
				});
			}
		};
	}

	/**
	 * Will create and return the {@link IMenuItemProvider} that adds the action for executing the
	 * {@link DisplayPersistenceDialogCommand} to a popup menu. This command is intended to open the 
	 * DisplayPersistenceDialog for managing NatTable states (also called view management).
	 * @return The {@link IMenuItemProvider} for the {@link MenuItem} that executes the 
	 * 			{@link DisplayPersistenceDialogCommand}
	 * 			The {@link MenuItem} will be shown with the localized default text configured in NatTable core.
	 */
	public static IMenuItemProvider stateManagerMenuItemProvider() {
		return stateManagerMenuItemProvider(Messages.getString("MenuItemProviders.stateManager")); //$NON-NLS-1$
	}

	/**
	 * Will create and return the {@link IMenuItemProvider} that adds the action for executing the
	 * {@link DisplayPersistenceDialogCommand} to a popup menu. This command is intended to open the 
	 * DisplayPersistenceDialog for managing NatTable states (also called view management).
	 * <p>
	 * The {@link MenuItem} will be shown with the given menu label.
	 * @param menuLabel The text that will be showed for the generated {@link MenuItem}
	 * @return The {@link IMenuItemProvider} for the {@link MenuItem} that executes the 
	 * 			{@link DisplayPersistenceDialogCommand}
	 * 			The {@link MenuItem} will be shown with the localized default text configured in NatTable core.
	 */
	public static IMenuItemProvider stateManagerMenuItemProvider(final String menuLabel) {
		return new IMenuItemProvider() {

			@Override
			public void addMenuItem(final NatTable natTable, final Menu popupMenu) {
				final MenuItem saveState= new MenuItem(popupMenu, SWT.PUSH);
				saveState.setText(menuLabel);
				saveState.setImage(GUIHelper.getImage("table_icon")); //$NON-NLS-1$
				saveState.setEnabled(true);

				saveState.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						natTable.doCommand(new DisplayPersistenceDialogCommand(natTable));
					}
				});
			}
		};
	}

	/**
	 * @return An {@link IMenuItemProvider} for adding a separator to the popup menu.
	 */
	public static IMenuItemProvider separatorMenuItemProvider() {
		return new IMenuItemProvider() {
			@Override
			public void addMenuItem(final NatTable natTable, final Menu popupMenu) {
				 new MenuItem(popupMenu, SWT.SEPARATOR);
			}
		};
	}

}
