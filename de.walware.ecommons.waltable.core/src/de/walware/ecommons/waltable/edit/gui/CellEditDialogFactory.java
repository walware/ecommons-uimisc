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
package de.walware.ecommons.waltable.edit.gui;

import org.eclipse.swt.widgets.Shell;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.edit.DialogEditHandler;
import de.walware.ecommons.waltable.edit.EditConfigAttributes;
import de.walware.ecommons.waltable.edit.EditMode;
import de.walware.ecommons.waltable.edit.editor.ICellEditor;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.style.DisplayMode;
import de.walware.ecommons.waltable.tickupdate.ITickUpdateHandler;
import de.walware.ecommons.waltable.tickupdate.TickUpdateConfigAttributes;

/**
 * Factory to create {@link ICellEditDialog} instances that should be opened for editing
 * cell values.
 */
public class CellEditDialogFactory {

	/**
	 * Will determine and return the {@link ICellEditDialog} to open for editing a cell
	 * value. For this the type of the {@link ICellEditor} and the configuration for tick 
	 * updates are checked. Will return the default {@link CellEditDialog} for the given
	 * {@link ICellEditor} for cell editors that wrap an edit control. If the 
	 * {@link ICellEditor} itself is a {@link ICellEditDialog}, it will returned itself
	 * without creating a new one.
	 * @param parentShell the parent shell, or <code>null</code> to create a top-level
	 * 			shell
	 * @param originalCanonicalValue The value that should be propagated to the editor 
	 * 			control. Needed because for multi cell editing or editor activation by
	 * 			letter/digit key will result in a different value to populate for some 
	 * 			editors than populating the value out of the cell/data model directly.
	 * @param cell The cell that should be edited. Needed because editor activation
	 * 			retrieves the configuration for editing directly out of the cell.
	 * @param cellEditor The cell editor that should be used for multi cell editing.
	 * @param configRegistry The {@link IConfigRegistry} containing the configuration of 
	 * 			the current NatTable instance the command should be executed for. This is 
	 * 			necessary because the edit controllers in the current architecture are not 
	 * 			aware of the instance they are running in and therefore it is needed for 
	 * 			activation of editors.
	 * @return The {@link ICellEditDialog} that should be opened for editing a cell value.
	 */
	public static ICellEditDialog createCellEditDialog(final Shell parentShell,
			final Object originalCanonicalValue,
			final ILayerCell cell,
			final ICellEditor cellEditor,
			final IConfigRegistry configRegistry) {
		
		ICellEditDialog result= null;
		
		//if the cell editor itself is a ICellEditDialog, simply return it
		if (cellEditor instanceof ICellEditDialog) {
			//activate the editor and then return it
			cellEditor.activateCell(
					parentShell, originalCanonicalValue, EditMode.DIALOG, 
					new DialogEditHandler(), cell, configRegistry);
			result= (ICellEditDialog) cellEditor;
		}
		else {
			final ITickUpdateHandler tickUpdateHandler= configRegistry.getConfigAttribute(
					TickUpdateConfigAttributes.UPDATE_HANDLER, 
					DisplayMode.EDIT, 
					cell.getConfigLabels().getLabels());
			if (tickUpdateHandler != null && tickUpdateHandler.isApplicableFor(cell.getDataValue(0, null))) {
				//if a tick update handler is applicable, return the TickUpdateCellEditDialog
				result= new TickUpdateCellEditDialog(
						parentShell, originalCanonicalValue, cell, cellEditor, configRegistry, tickUpdateHandler);
			}
			else {
				//return the default edit dialog that will show the underlying editor
				result= new CellEditDialog(parentShell, originalCanonicalValue, cell, cellEditor, configRegistry);
			}
		}
		
		//check if there are custom edit dialog settings registered
		result.setDialogSettings(configRegistry.getConfigAttribute(
				EditConfigAttributes.EDIT_DIALOG_SETTINGS, 
				DisplayMode.EDIT, 
				cell.getConfigLabels().getLabels()));
		
		return result;
	}
}
