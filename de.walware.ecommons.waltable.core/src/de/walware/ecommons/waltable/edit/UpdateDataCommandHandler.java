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
package de.walware.ecommons.waltable.edit;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;
import de.walware.ecommons.waltable.command.ILayerCommandHandler;
import de.walware.ecommons.waltable.data.IDataProvider;
import de.walware.ecommons.waltable.internal.WaLTablePlugin;
import de.walware.ecommons.waltable.layer.DataLayer;
import de.walware.ecommons.waltable.layer.event.CellVisualChangeEvent;


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
	public UpdateDataCommandHandler(final DataLayer dataLayer) {
		this.dataLayer= dataLayer;
	}
	
	@Override
	public Class<UpdateDataCommand> getCommandClass() {
		return UpdateDataCommand.class;
	}
	
	@Override
	protected boolean doCommand(final UpdateDataCommand command) {
		try {
			final long columnPosition= command.getColumnPosition();
			final long rowPosition= command.getRowPosition();
			final IDataProvider dataProvider= this.dataLayer.getDataProvider();
			final Object oldValue= dataProvider.getDataValue(columnPosition, rowPosition, 0);
			final Object newValue= command.getNewValue();
			if ((oldValue != null) ? !oldValue.equals(newValue) : null != newValue)  {
				dataProvider.setDataValue(columnPosition, rowPosition, newValue);
				this.dataLayer.fireLayerEvent(new CellVisualChangeEvent(this.dataLayer, columnPosition, rowPosition));
				
				//TODO implement a new event which is a mix of PropertyUpdateEvent and CellVisualChangeEvent
			}
			return true;
		} catch (final UnsupportedOperationException e) {
			WaLTablePlugin.log(new Status(IStatus.ERROR, WaLTablePlugin.PLUGIN_ID,
					"Failed to update value to: " + command.getNewValue(), e ));
			return false;
		}
	}
	
}
