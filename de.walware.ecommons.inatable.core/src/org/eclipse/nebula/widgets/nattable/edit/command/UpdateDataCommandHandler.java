/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// -depend
package org.eclipse.nebula.widgets.nattable.edit.command;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.command.ILayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.internal.NatTablePlugin;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.event.CellVisualChangeEvent;


/**
 * {@link ILayerCommandHandler} that handles {@link UpdateDataCommand}s by updating
 * the data model. It is usually directly registered to the {@link DataLayer} this
 * command handler is associated with.
 */
public class UpdateDataCommandHandler extends AbstractLayerCommandHandler<UpdateDataCommand> {
	
	
	/**
	 * The {@link DataLayer} on which the data model updates should be executed.
	 */
	private final DataLayer dataLayer;
	
	/**
	 * @param dataLayer The {@link DataLayer} on which the data model updates should be executed.
	 */
	public UpdateDataCommandHandler(DataLayer dataLayer) {
		this.dataLayer = dataLayer;
	}
	
	public Class<UpdateDataCommand> getCommandClass() {
		return UpdateDataCommand.class;
	}
	
	@Override
	protected boolean doCommand(UpdateDataCommand command) {
		try {
			long columnPosition = command.getColumnPosition();
			long rowPosition = command.getRowPosition();
			dataLayer.getDataProvider().setDataValue(columnPosition, rowPosition, command.getNewValue());
			dataLayer.fireLayerEvent(new CellVisualChangeEvent(dataLayer, columnPosition, rowPosition));
			return true;
		} catch (UnsupportedOperationException e) {
			NatTablePlugin.log(new Status(IStatus.ERROR, NatTablePlugin.PLUGIN_ID,
					"Failed to update value to: " + command.getNewValue(), e ));
			return false;
		}
	}
	
}
