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
package de.walware.ecommons.waltable.ui.matcher;

import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.layer.LabelStack;


public interface IMouseEventMatcher {
	
	
	int NO_BUTTON= 0;
	int LEFT_BUTTON= 1;
	int RIGHT_BUTTON= 3;
	
	
	/**
	 * Figures out if the mouse event occurred in the supplied region.
	 * 
	 * @param event SWT mouse event
	 */
	public boolean matches(NatTable natTable, MouseEvent event, LabelStack regionLabels);
	
}
