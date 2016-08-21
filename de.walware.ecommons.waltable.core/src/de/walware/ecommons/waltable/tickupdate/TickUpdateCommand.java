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
package de.walware.ecommons.waltable.tickupdate;

import de.walware.ecommons.waltable.command.ILayerCommand;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.layer.ILayer;

/**
 * Command to trigger a tick update on the current selected cells.
 * Note: This command only works if the layer composition contains a
 * 		 SelectionLayer and the corresponding {@link TickUpdateCommandHandler}
 * 		 is registered.
 */
public class TickUpdateCommand implements ILayerCommand {

	/**
	 * The {@link IConfigRegistry} of the current NatTable instance this
	 * command is executed in. Needed to determine several configurations
	 * on handling this command by its handler.
	 * As the command handler is not aware of the NatTable instance it
	 * is running in, and there is no kind of context, the {@link IConfigRegistry}
	 * needs to be transported.
	 */
	private final IConfigRegistry configRegistry;
	/**
	 * Flag to determine whether the current value in the data model
	 * should be incremented or decremented. 
	 */
	private final boolean increment;

	/**
	 * @param configRegistry The {@link IConfigRegistry} of the current NatTable instance this
	 * 			command is executed in. Needed to determine several configurations
	 * 			on handling this command by its handler.
	 * 			As the command handler is not aware of the NatTable instance it
	 * 			is running in, and there is no kind of context, the {@link IConfigRegistry}
	 * 			needs to be transported.
	 * @param increment Flag to determine whether the current value in the data model
	 * 			should be incremented or decremented. 
	 */
	public TickUpdateCommand(final IConfigRegistry configRegistry, final boolean increment) {
		this.configRegistry= configRegistry;
		this.increment= increment;
	}

	/**
	 * Constructor that is used by local cloning operation.
	 * @param command The command to create a new instance of.
	 */
	protected TickUpdateCommand(final TickUpdateCommand command) {
		this.configRegistry= command.configRegistry;
		this.increment= command.increment;
	}
	
	@Override
	public TickUpdateCommand cloneCommand() {
		return new TickUpdateCommand(this);
	}

	@Override
	public boolean convertToTargetLayer(final ILayer targetLayer) {
		// No op.
		return true;
	}
	
	/**
	 * @return The {@link IConfigRegistry} of the current NatTable instance this
	 * 			command is executed in. Needed to determine several configurations
	 * 			on handling this command by its handler.
	 * 			As the command handler is not aware of the NatTable instance it
	 * 			is running in, and there is no kind of context, the {@link IConfigRegistry}
	 * 			needs to be transported.
	 */
	public IConfigRegistry getConfigRegistry() {
		return this.configRegistry;
	}

	/**
	 * @return Whether the current value in the data model
	 * 			should be incremented or decremented.
	 */
	public boolean isIncrement() {
		return this.increment;
	}
}
