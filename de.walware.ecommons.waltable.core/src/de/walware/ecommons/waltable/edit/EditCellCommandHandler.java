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

import org.eclipse.swt.widgets.Composite;

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.config.IEditableRule;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.style.DisplayMode;

/**
 * Command handler for handling {@link EditCellCommand}s.
 * Will first check if putting the cell into edit mode is allowed. If it is
 * allowed it will call the {@link EditController} for activation of the edit
 * mode.
 */
public class EditCellCommandHandler extends AbstractLayerCommandHandler<EditCellCommand> {

	@Override
	public Class<EditCellCommand> getCommandClass() {
		return EditCellCommand.class;
	}
	
	@Override
	public boolean doCommand(final EditCellCommand command) {
		final ILayerCell cell= command.getCell();
		final Composite parent= command.getParent();
		final IConfigRegistry configRegistry= command.getConfigRegistry();
		
		//check if the cell is editable
		final IEditableRule rule= configRegistry.getConfigAttribute(
				EditConfigAttributes.CELL_EDITABLE_RULE, 
				DisplayMode.EDIT, cell.getConfigLabels().getLabels());
		
		if (rule.isEditable(cell, configRegistry)) {
			EditController.editCell(cell, parent, cell.getDataValue(0), configRegistry);
		}

		//as commands by default are intended to be consumed by the handler, always
		//return true, whether the activation of the edit mode was successfull or not
		return true;
	}

}
