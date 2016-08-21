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
// -depend
package de.walware.ecommons.waltable.tickupdate;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.config.IEditableRule;
import de.walware.ecommons.waltable.coordinate.PositionCoordinate;
import de.walware.ecommons.waltable.data.validate.IDataValidator;
import de.walware.ecommons.waltable.edit.EditConfigAttributes;
import de.walware.ecommons.waltable.edit.EditUtils;
import de.walware.ecommons.waltable.edit.UpdateDataCommand;
import de.walware.ecommons.waltable.internal.WaLTablePlugin;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.selection.SelectionLayer;
import de.walware.ecommons.waltable.style.DisplayMode;


/**
 * The command handler that will handle {@link TickUpdateCommand}s on selected cells.
 */
public class TickUpdateCommandHandler extends AbstractLayerCommandHandler<TickUpdateCommand> {
	
	
	/**
	 * The {@link SelectionLayer} needed to retrieve the selected cells on
	 * which the tick update should be processed.
	 */
	private final SelectionLayer selectionLayer;

	/**
	 * @param selectionLayer The {@link SelectionLayer} needed to retrieve the selected cells on
	 * 			which the tick update should be processed.
	 */
	public TickUpdateCommandHandler(final SelectionLayer selectionLayer) {
		this.selectionLayer= selectionLayer;
	}

	@Override
	public boolean doCommand(final TickUpdateCommand command) {
		final List<PositionCoordinate> selectedPositions= this.selectionLayer.getSelectedCellPositions();
		final IConfigRegistry configRegistry= command.getConfigRegistry();
		
		// Tick update for multiple cells in selection 
		if (selectedPositions.size() > 1) {
			// Can all cells be updated ?
			if (EditUtils.allCellsEditable(this.selectionLayer, configRegistry)
					&& EditUtils.isEditorSame(this.selectionLayer, configRegistry) 
					&& EditUtils.isConverterSame(this.selectionLayer, configRegistry)) {
				for (final PositionCoordinate position : selectedPositions) {
					updateSingleCell(command, position);
				}
			}
		} else {
			// Tick update for single selected cell
			updateSingleCell(command, this.selectionLayer.getLastSelectedCellPosition());
		}

		return true;
	}

	/**
	 * Will calculate the new value after tick update processing for the cell at the given coordinates,
	 * trying to update the value represented by that cell. The update will only be processed if the
	 * new value is valid. 
	 * @param command The command to process
 	 * @param selectedPosition The coordinates of the cell on which the tick update
 	 * 			should be executed
	 */
	private void updateSingleCell(final TickUpdateCommand command, final PositionCoordinate selectedPosition) {
		final ILayerCell cell= this.selectionLayer.getCellByPosition(
				selectedPosition.columnPosition, selectedPosition.rowPosition);
		
		final IConfigRegistry configRegistry= command.getConfigRegistry();
		
		final IEditableRule editableRule= configRegistry.getConfigAttribute(
				EditConfigAttributes.CELL_EDITABLE_RULE, 
				DisplayMode.EDIT,
				cell.getConfigLabels().getLabels());
		
		final IDataValidator validator= configRegistry.getConfigAttribute(
				EditConfigAttributes.DATA_VALIDATOR, 
				DisplayMode.EDIT, 
				cell.getConfigLabels().getLabels());
		
		if (editableRule.isEditable(cell, configRegistry)) {
			//process the tick update
			final Object newValue= getNewCellValue(command, cell);
			//validate the value
			try {
				if (validator == null || validator.validate(cell, configRegistry, newValue)) {
					this.selectionLayer.doCommand(new UpdateDataCommand(
							this.selectionLayer,
							selectedPosition.columnPosition, 
							selectedPosition.rowPosition,
							newValue));
				}
				else {
					WaLTablePlugin.log(new Status(IStatus.WARNING, WaLTablePlugin.PLUGIN_ID,
							"Tick update failed for cell at " + selectedPosition + " and value " + newValue //$NON-NLS-1$ //$NON-NLS-2$
							+ ". New value is not valid!" )); //$NON-NLS-1$
				}
			}
			catch (final Exception e) {
				WaLTablePlugin.log(new Status(IStatus.ERROR, WaLTablePlugin.PLUGIN_ID,
						"Tick update failed for cell at " + selectedPosition + " and value " + newValue //$NON-NLS-1$ //$NON-NLS-2$
						+ ". " + e.getLocalizedMessage() )); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Will calculate the new value for the given cell after tick update is processed.
	 * @param command The command to process
	 * @param cell The cell on which the command should be processed
	 * @return The processed value after the command was executed on the current cell value
	 */
	private Object getNewCellValue(final TickUpdateCommand command, final ILayerCell cell) {
		final ITickUpdateHandler tickUpdateHandler= command.getConfigRegistry().getConfigAttribute(
				TickUpdateConfigAttributes.UPDATE_HANDLER,
				DisplayMode.EDIT, 
				cell.getConfigLabels().getLabels());

		final Object dataValue= cell.getDataValue(0, null);

		if (tickUpdateHandler != null && tickUpdateHandler.isApplicableFor(dataValue)) {
			if (command.isIncrement()) {
				return tickUpdateHandler.getIncrementedValue(dataValue);
			} else {
				return tickUpdateHandler.getDecrementedValue(dataValue);
			}
		} else {
			return dataValue;
		}
	}

	@Override
	public Class<TickUpdateCommand> getCommandClass() {
		return TickUpdateCommand.class;
	}
	
}
