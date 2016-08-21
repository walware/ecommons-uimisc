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
package de.walware.ecommons.waltable.grid;

import org.eclipse.swt.widgets.Scrollable;

import de.walware.ecommons.waltable.command.AbstractContextFreeCommand;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.swt.SWTUtil;

/**
 * Command that gives the layers access to ClientArea and the Scrollable 
 */
public class ClientAreaResizeCommand extends AbstractContextFreeCommand {
	
	/**
	 * The {@link Scrollable}, normally the NatTable itself.
	 */
	private final Scrollable scrollable;
	
	/**
	 * This is the area within the client area that is used for percentage calculation.
	 * Without using a GridLayer, this will be the client area of the scrollable.
	 * On using a GridLayer this value will be overriden with the body region area.
	 */
	private LRectangle calcArea;

	public ClientAreaResizeCommand(final Scrollable scrollable) {
		super();
		this.scrollable= scrollable;
	}

	public Scrollable getScrollable() {
		return this.scrollable;
	}
	
	public LRectangle getCalcArea() {
		if (this.calcArea == null) {
			return SWTUtil.toNatTable(this.scrollable.getClientArea());
		}
		return this.calcArea;
	}
	
	public void setCalcArea(final LRectangle calcArea) {
		this.calcArea= calcArea;
	}
	
}
