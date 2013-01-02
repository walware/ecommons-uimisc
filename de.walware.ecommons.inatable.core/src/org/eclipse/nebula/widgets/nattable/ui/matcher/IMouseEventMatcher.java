/*******************************************************************************
 * Copyright (c) 2012-2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// +
package org.eclipse.nebula.widgets.nattable.ui.matcher;

import org.eclipse.swt.events.MouseEvent;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;

public interface IMouseEventMatcher {


	int NO_BUTTON = 0;
	int LEFT_BUTTON = 1;
	int RIGHT_BUTTON = 3;


	/**
	 * Figures out if the mouse event occured in the suplied region.
	 * 
	 * @param event  SWT mouse event
	 * @param region Region object indicating a regoin of the NatTable display area. Example: body, header etc.
	 */
	public boolean matches(NatTable natTable, MouseEvent event, LabelStack regionLabels);

}
