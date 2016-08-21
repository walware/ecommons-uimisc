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
package de.walware.ecommons.waltable.tickupdate.action;

import org.eclipse.swt.events.KeyEvent;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.tickupdate.TickUpdateCommand;
import de.walware.ecommons.waltable.ui.action.IKeyAction;

/**
 * {@link IKeyAction} that will execute the {@link TickUpdateCommand}
 * with the additional information if the update increments or decrements
 * the current value.
 */
public class TickUpdateAction implements IKeyAction {

	/**
	 * Flag to determine whether the current value in the data model
	 * should be incremented or decremented. 
	 */
	private final boolean increment;

	/**
	 * @param increment Flag to determine whether the current value in the data model
	 * 			should be incremented or decremented. 
	 */
	public TickUpdateAction(final boolean increment) {
		this.increment= increment;
	}

	@Override
	public void run(final NatTable natTable, final KeyEvent event) {
		natTable.doCommand(new TickUpdateCommand(natTable.getConfigRegistry(), this.increment));
	}

}
