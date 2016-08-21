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
// -depend
package de.walware.ecommons.waltable.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.edit.editor.ICellEditor;
import de.walware.ecommons.waltable.edit.gui.CellEditDialogFactory;
import de.walware.ecommons.waltable.edit.gui.ICellEditDialog;
import de.walware.ecommons.waltable.internal.WaLTablePlugin;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.style.DisplayMode;
import de.walware.ecommons.waltable.swt.SWTUtil;


/**
 * Controller to handle the activation of the edit mode of NatTable cells.
 */
public class EditController {


	/**
	 * Activates the edit mode for the given cell. Will determine whether the editor
	 * should be opened inline or in a subdialog.
	 * 
	 * @param cell The cell that should be put into the edit mode.
	 * @param parent The parent Composite, needed for the creation of the editor control.
	 * @param initialCanonicalValue The value that should be put to the activated editor
	 * 			control. Usually this value should be the same as calling 
	 * 			<code>cell.getDataValue()</code>, but for the special case that an editor
	 * 			should be activated pressing a letter or digit key on the current selection,
	 * 			the initial value should be the Character representing that key.
	 * @param configRegistry The {@link IConfigRegistry} containing the configuration of the
	 * 			current NatTable instance the command should be executed for.
	 * 			This is necessary because the edit controllers in the current architecture
	 * 			are not aware of the instance they are running in.
	 */
	public static void editCell(
			final ILayerCell cell, final Composite parent, 
			final Object initialCanonicalValue, final IConfigRegistry configRegistry) {
		
		try {
			//determine the position of the cell to put into edit mode
			final LRectangle cellBounds= cell.getBounds();
			final ILayer layer= cell.getLayer();
			
			final long columnPosition= cell.getColumnPosition();
			final long rowPosition= cell.getRowPosition();

			//read the configuration for the specified cell for
			//- which editor to use for that cell
			final List<String> configLabels= cell.getConfigLabels().getLabels();
			
			//check which editor to use
			final ICellEditor cellEditor= configRegistry.getConfigAttribute(
					EditConfigAttributes.CELL_EDITOR, DisplayMode.EDIT, configLabels);
			
			if (cellEditor.openInline(configRegistry, configLabels)) {
				//edit inline
				final ICellEditHandler editHandler= new InlineEditHandler(
						layer,
						columnPosition,
						rowPosition);
				
				org.eclipse.swt.graphics.Rectangle editorBounds= SWTUtil.toSWT(
								layer.getLayerPainter().adjustCellBounds(
						columnPosition, rowPosition, 
						new LRectangle(cellBounds.x, cellBounds.y, cellBounds.width, cellBounds.height)));
				
				cellEditor.activateCell(
						parent, 
						initialCanonicalValue, 
						EditMode.INLINE, 
						editHandler, 
						cell, 
						configRegistry);
				
				final Control editorControl= cellEditor.getEditorControl();
				
				editorBounds= cellEditor.calculateControlBounds(editorBounds);
				
				if (editorControl != null && !editorControl.isDisposed()) {
					editorControl.setBounds(editorBounds);
					//We need to add the control listeners after setting the bounds to it 
					//because of the strange behaviour on Mac OS where a control loses focus 
					//if its bounds are set
					cellEditor.addEditorControlListeners();
					ActiveCellEditorRegistry.registerActiveCellEditor(cellEditor);
				}
			}
			else {
				final List<ILayerCell> cells= new ArrayList<>();
				cells.add(cell);
				editCells(cells, parent, initialCanonicalValue, configRegistry);
			}
		}
		catch (final Exception e) {
			if (cell == null){
				WaLTablePlugin.log(new Status(IStatus.ERROR, WaLTablePlugin.PLUGIN_ID,
						"Cell being edited is no longer available. Initial value: " + initialCanonicalValue, e )); //$NON-NLS-1$
			} else {
				WaLTablePlugin.log(new Status(IStatus.ERROR, WaLTablePlugin.PLUGIN_ID,
						"Error while editing cell: Cell: " + cell + "; Initial value: " + initialCanonicalValue, e )); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * This method is used to edit cells in a sub dialog. In every case this method will
	 * open a dialog for editing, regardless if the list of cells to edit contain several 
	 * or only one value. Only if the given list of cells to edit is <code>null</code> or
	 * empty, there is no action performed. 
	 * @param cells The list of cells to edit.
	 * @param parent The parent composite to access the parent shell, or <code>null</code> to 
	 * 			create a top-level shell dialog. In the last case, the dialog will be opened
	 * 			as non modal.
	 * @param initialCanonicalValue The value that should be propagated to the editor 
	 * 			control. Needed because for multi cell editing or editor activation by
	 * 			letter/digit key will result in a different value to populate for some 
	 * 			editors than populating the value out of the cell/data model directly.
	 * @param configRegistry The {@link IConfigRegistry} containing the configuration of 
	 * 			the current NatTable instance the command should be executed for. This is 
	 * 			necessary because the edit controllers in the current architecture are not 
	 * 			aware of the instance they are running in and therefore it is needed for 
	 * 			activation of editors.
	 */
	public static void editCells(
			final Collection<ILayerCell> cells, final Composite parent, 
			final Object initialCanonicalValue, final IConfigRegistry configRegistry) {
		
		if (cells != null && !cells.isEmpty()) {
			//get the editor to use, because the editor contains information if
			//it allows editing on a multi edit dialog
			//Note: this works because previous to calling this method it is checked
			//		if all cells have the same editor configured. Otherwise this method
			//		will have serious issues further on.
			final ICellEditor cellEditor= configRegistry.getConfigAttribute(
					EditConfigAttributes.CELL_EDITOR, 
					DisplayMode.EDIT, 
					cells.iterator().next().getConfigLabels().getLabels());

			if (cells.size() == 1 || 
					(cells.size() > 1 && supportMultiEdit(cells, cellEditor, configRegistry))) {
				
				if (cellEditor.openMultiEditDialog()) {
					//as the EditSelectionCommandHandler already ensured that all cells have the same
					//configuration, we can simply use any cell for multi cell edit handling
					final ICellEditDialog dialog= CellEditDialogFactory.createCellEditDialog(
							parent != null ? parent.getShell() : null, initialCanonicalValue, 
									cells.iterator().next(), cellEditor, configRegistry);
					
					final int returnValue= dialog.open();
					
					if (returnValue == Window.OK) {
						for (final ILayerCell selectedCell : cells) {
							Object editorValue= dialog.getCommittedValue();
							if (!(dialog.getEditType() == EditTypeEnum.SET)) {
								editorValue= dialog.calculateValue(selectedCell.getDataValue(0, null), editorValue);
							}
							final ILayer layer= selectedCell.getLayer();
							
							layer.doCommand(new UpdateDataCommand(
									layer, selectedCell.getColumnPosition(), selectedCell.getRowPosition(), editorValue));
						}
					}
				}
				else {
					//if the editor is configured to do not open a multi edit dialog for 
					//multi editing, we simply activate all editors for cells that are
					//selected for multi editing
					//this only works for editors that have no interactive control for
					//editing, like for example the CheckBoxCellEditor that directly
					//changes the value and closes right away.
					for (final ILayerCell cell : cells) {
						final ICellEditHandler editHandler= new InlineEditHandler(
								cell.getLayer(),
								cell.getColumnPosition(),
								cell.getRowPosition());

						cellEditor.activateCell(
								parent, 
								initialCanonicalValue, 
								EditMode.INLINE, 
								editHandler, 
								cell, 
								configRegistry);
					}
				}
			}
		}
	}
	
	/**
	 * Will check if multi editing is supported. Usually it should be enough checking the editor once.
	 * But as this can be configured via configuration attribute, and this can differ from cell to cell,
	 * all cells are checked to be sure.
	 * @param cells The selected cells that should be multi edited.
	 * @param cellEditor The cell editor that is the same for every cell.
	 * @param configRegistry The {@link IConfigRegistry} containing the configuration of 
	 * 			the current NatTable instance the command should be executed for. This is 
	 * 			necessary because the edit controllers in the current architecture are not 
	 * 			aware of the instance they are running in and therefore it is needed for 
	 * 			activation of editors.
	 * @return <code>true</code> if the editor supports multi edit for all selected cells,
	 * 			<code>false</code> if at least one cell does specify to not support multi edit.
	 */
	private static boolean supportMultiEdit(final Collection<ILayerCell> cells, final ICellEditor cellEditor, final IConfigRegistry configRegistry) {
		for (final ILayerCell cell : cells) {
			if (!cellEditor.supportMultiEdit(configRegistry, cell.getConfigLabels().getLabels())) {
				return false;
			}
		}
		return true;
	}
	
}
