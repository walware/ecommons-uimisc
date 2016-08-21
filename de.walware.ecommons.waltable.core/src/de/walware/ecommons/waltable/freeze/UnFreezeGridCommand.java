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

import de.walware.ecommons.waltable.command.AbstractContextFreeCommand;


/**
 * Simple command to unfreeze a frozen state.
 */
public class UnFreezeGridCommand extends AbstractContextFreeCommand implements IFreezeCommand {
	
	
	@Override
	public boolean isToggle() {
		return false;
	}
	
	@Override
	public boolean isOverrideFreeze() {
		return false;
	}
	
}
