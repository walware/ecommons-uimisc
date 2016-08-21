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

import de.walware.ecommons.waltable.command.AbstractPositionCommand;
import de.walware.ecommons.waltable.layer.ILayer;

/**
 * Command that will trigger a data model update.
 */
public class UpdateDataCommand extends AbstractPositionCommand {

	/**
	 * The value to update the data model to.
	 */
	private final Object newValue;

	/**
	 * Create a new {@link UpdateDataCommand} based on the specified information.
	 * @param layer The {@link ILayer} to which the columnPosition and rowPosition are resolved
	 * 			to. This is needed to support conversion of column and row positions from this
	 * 			layer to NatTable coordinates, which may be different e.g. in case of grid
	 * 			composition.
	 * @param columnPosition The column position of the value to update.
	 * @param rowPosition The row position of the value to update.
	 * @param newValue The value to update the data model value to.
	 */
	public UpdateDataCommand(final ILayer layer, final long columnPosition, final long rowPosition, final Object newValue) {
		super(layer, columnPosition, rowPosition);
		this.newValue= newValue;
	}
	
	/**
	 * Create a new {@link UpdateDataCommand} based on the specified instance.
	 * Mainly needed for cloning purposes.
	 * @param command The command to create a new instance from.
	 */
	protected UpdateDataCommand(final UpdateDataCommand command) {
		super(command);
		this.newValue= command.newValue;
	}
	
	/**
	 * @return The value to update the data model to.
	 */
	public Object getNewValue() {
		return this.newValue;
	}
	
	@Override
	public UpdateDataCommand cloneCommand() {
		return new UpdateDataCommand(this);
	}

}
