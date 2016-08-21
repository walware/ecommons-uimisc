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
package de.walware.ecommons.waltable.ui.action;


import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;

public class NoOpMouseAction implements IMouseAction {

	@Override
	public void run(final NatTable natTable, final MouseEvent event) {}

}
