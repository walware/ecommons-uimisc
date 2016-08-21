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
package de.walware.ecommons.waltable.edit;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.PositionCoordinate;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.selection.SelectionLayer;

/**
 * Command handler for handling {@link EditSelectionCommand}s.
 * Will first check if all selected cells are editable and if they have the same
 * editor configured. Will call the {@link EditController} for activation of the 
 * edit mode if these checks succeed.
 */
public class EditSelectionCommandHandler extends AbstractLayerCommandHandler<EditSelectionCommand> {

	private final SelectionLayer selectionLayer;
	
	public EditSelectionCommandHandler(final SelectionLayer selectionLayer) {
		this.selectionLayer= selectionLayer;
	}
	
	@Override
	public Class<EditSelectionCommand> getCommandClass() {
		return EditSelectionCommand.class;
	}
	
	@Override
    public boolean doCommand(final EditSelectionCommand command) {
		final Composite parent= command.getParent();
		final IConfigRegistry configRegistry= command.getConfigRegistry();
		final Character initialValue= command.getCharacter();
		
		if (EditUtils.allCellsEditable(this.selectionLayer, configRegistry)
				&& EditUtils.isEditorSame(this.selectionLayer, configRegistry)
				&& EditUtils.isConverterSame(this.selectionLayer, configRegistry)) {
			//check how many cells are selected
			final Collection<ILayerCell> selectedCells= this.selectionLayer.getSelectedCells();
			if (selectedCells.size() == 1) {
				//editing is triggered by key for a single cell
				//we need to fire the InlineCellEditEvent here because we don't know the correct bounds
				//of the cell to edit inline corresponding to the NatTable. On firing the event, a
				//translation process is triggered, converting the information to the correct values
				//needed for inline editing
				final ILayerCell cell= selectedCells.iterator().next();
				this.selectionLayer.fireLayerEvent(
						new InlineCellEditEvent(
								this.selectionLayer, 
								new PositionCoordinate(this.selectionLayer, cell.getColumnPosition(), cell.getRowPosition()), 
								parent, 
								configRegistry, 
								(initialValue != null ? initialValue : cell.getDataValue(0, null))));
			}
			else if (selectedCells.size() > 1) {
				//determine the initial value
				Object initialEditValue= initialValue;
				if (initialValue == null && EditUtils.isValueSame(this.selectionLayer)) {
					final ILayerCell cell= selectedCells.iterator().next();
					initialEditValue= cell.getDataValue(0, null);
				}
				
				EditController.editCells(selectedCells, parent, initialEditValue, configRegistry);
			}
		}

		//as commands by default are intended to be consumed by the handler, always
		//return true, whether the activation of the edit mode was successfull or not
		return true;
	}

}
