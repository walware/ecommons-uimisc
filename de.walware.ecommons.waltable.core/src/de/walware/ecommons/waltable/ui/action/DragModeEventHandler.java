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
import de.walware.ecommons.waltable.ui.mode.AbstractModeEventHandler;
import de.walware.ecommons.waltable.ui.mode.Mode;
import de.walware.ecommons.waltable.ui.mode.ModeSupport;

public class DragModeEventHandler extends AbstractModeEventHandler {

	private final NatTable natTable;
	
	private final IDragMode dragMode;
	
	public DragModeEventHandler(final ModeSupport modeSupport, final NatTable natTable, final IDragMode dragMode) {
		super(modeSupport);
		
		this.natTable= natTable;
		this.dragMode= dragMode;
	}
	
	@Override
	public void mouseMove(final MouseEvent event) {
		this.dragMode.mouseMove(this.natTable, event);
	}
	
	@Override
	public void mouseUp(final MouseEvent event) {
		this.dragMode.mouseUp(this.natTable, event);
		switchMode(Mode.NORMAL_MODE);
	}
	
}
