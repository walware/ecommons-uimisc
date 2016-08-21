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

package de.walware.ecommons.waltable.freeze;

import de.walware.ecommons.waltable.command.AbstractPositionCommand;
import de.walware.ecommons.waltable.command.ILayerCommand;
import de.walware.ecommons.waltable.layer.ILayer;


/**
 * Command to freeze at the specified position.
 */
public class FreezeCellPositionCommand extends AbstractPositionCommand implements IFreezeCommand {
	
	/**
	 * Indicates whether this command should toggle the frozen state between
	 * frozen and unfrozen, or if it should always result in a frozen state.	 
	 */
	private final boolean toggle;
	
	/**
	 * Indicates whether this command should override a current frozen state
	 * or if it should be skipped if a frozen state is already applied.
	 */
	private final boolean overrideFreeze;
	
	
	/**
	 * Creates a FreezeCellPositionCommand for the given column and row positions related to the given 
	 * layer, that doesn't toggle or override a current frozen state.
	 * @param layer The layer to which the position coordinates match.
	 * @param columnPosition The column position that will be the right most
	 * 			column in the frozen part.
	 * @param rowPosition The row position that will be the bottom
	 * 			row in the frozen part.
	 */
	public FreezeCellPositionCommand(final ILayer layer, final long columnPosition, final long rowPosition) {
		this(layer, columnPosition, rowPosition, false);
	}
	
	/**
	 * Creates a FreezeCellPositionCommand for the given column and row positions related to the given 
	 * layer, that doesn't override a current frozen state..
	 * If it should toggle the current frozen state can be specified by parameter.
	 * @param layer The layer to which the position coordinates match.
	 * @param columnPosition The column position that will be the right most
	 * 			column in the frozen part.
	 * @param rowPosition The row position that will be the bottom
	 * 			row in the frozen part.
	 * @param toggle whether this command should toggle the frozen state between
	 * 			frozen and unfrozen, or if it should always result in a frozen state.
	 */
	public FreezeCellPositionCommand(final ILayer layer, final long columnPosition, final long rowPosition, final boolean toggle) {
		this(layer, columnPosition, rowPosition, toggle, false);
	}
	
	/**
	 * Creates a FreezeCellPositionCommand for the given column and row positions related to the given layer.
	 * If it should toggle or override the current frozen state can be specified by parameter.
	 * @param layer The layer to which the position coordinates match.
	 * @param columnPosition The column position that will be the right most
	 * 			column in the frozen part.
	 * @param rowPosition The row position that will be the bottom
	 * 			row in the frozen part.
	 * @param toggle whether this command should toggle the frozen state between
	 * 			frozen and unfrozen, or if it should always result in a frozen state.
	 * @param overrideFreeze whether this command should override a current frozen state
	 * 			or if it should be skipped if a frozen state is already applied.
	 */
	public FreezeCellPositionCommand(final ILayer layer, final long columnPosition, final long rowPosition, final boolean toggle, final boolean overrideFreeze) {
		super(layer, columnPosition, rowPosition);
		this.toggle= toggle;
		this.overrideFreeze= overrideFreeze;
	}
	
	/**
	 * Constructor used for cloning the command.
	 * @param command The command which is the base for the new cloned instance.
	 */
	protected FreezeCellPositionCommand(final FreezeCellPositionCommand command) {
		super(command);
		this.toggle= command.toggle;
		this.overrideFreeze= command.overrideFreeze;
	}
	
	
	@Override
	public boolean isToggle() {
		return this.toggle;
	}
	
	@Override
	public boolean isOverrideFreeze() {
		return this.overrideFreeze;
	}
	
	@Override
	public ILayerCommand cloneCommand() {
		return new FreezeCellPositionCommand(this);
	}
	
}
