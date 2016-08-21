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
package de.walware.ecommons.waltable.ui.menu;


import org.eclipse.swt.widgets.Menu;

import de.walware.ecommons.waltable.NatTable;

public interface IMenuItemProvider {
	
	/**
	 * Add an item to the popup menu.
	 * 
	 * @param natTable active table instance.
	 * @param popupMenu the SWT {@link Menu} which popus up. 
	 */
	public void addMenuItem(final NatTable natTable, final Menu popupMenu);

}
