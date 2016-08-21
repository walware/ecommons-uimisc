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

import de.walware.ecommons.waltable.command.AbstractDimPositionCommand;
import de.walware.ecommons.waltable.command.ILayerCommand;
import de.walware.ecommons.waltable.layer.ILayerDim;


/**
 * Command that can be used to freeze a grid for whole columns/rows.
 */
public class FreezeDimPositionCommand extends AbstractDimPositionCommand implements IFreezeCommand {
	
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
	 * Creates a FreezeDimPositionCommand for the given column/row related to the given layer,
	 * that doesn't toggle or override a current frozen state.
	 * @param layerDim The layer to which the column/row position matches.
	 * @param position The column/row position that will be the right most
	 * 			column/row in the frozen part.
	 */
	public FreezeDimPositionCommand(final ILayerDim layerDim, final long position) {
		this(layerDim, position, false);
	}
	
	/**
	 * Creates a FreezeDimPositionCommand for the given column/row related to the given layer,
	 * that doesn't override a current frozen state. If it should toggle the current frozen
	 * state can be specified by parameter.
	 * @param layerDim The layer to which the column/row position matches.
	 * @param position The column/row position that will be the right most
	 * 			column/row in the frozen part.
	 * @param toggle whether this command should toggle the frozen state between
	 * 			frozen and unfrozen, or if it should always result in a frozen state.
	 */
	public FreezeDimPositionCommand(final ILayerDim layerDim, final long position,
			final boolean toggle) {
		this(layerDim, position, toggle, false);
	}
	
	/**
	 * Creates a FreezeDimPositionCommand for the given column/row related to the given layer.
	 * If it should toggle or override the current frozen state can be specified by parameter.
	 * @param layerDim The layer to which the column/row position matches.
	 * @param position The column/row position that will be the right most
	 * 			column/row in the frozen part.
	 * @param toggle whether this command should toggle the frozen state between
	 * 			frozen and unfrozen, or if it should always result in a frozen state.
	 * @param overrideFreeze whether this command should override a current frozen state
	 * 			or if it should be skipped if a frozen state is already applied.
	 */
	public FreezeDimPositionCommand(final ILayerDim layerDim, final long position,
			final boolean toggle, final boolean overrideFreeze) {
		super(layerDim, position);
		
		this.toggle= toggle;
		this.overrideFreeze= overrideFreeze;
	}
	
	/**
	 * Constructor used for cloning the command.
	 * @param command The command which is the base for the new cloned instance.
	 */
	protected FreezeDimPositionCommand(final FreezeDimPositionCommand command) {
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
		return new FreezeDimPositionCommand(this);
	}
	
}
