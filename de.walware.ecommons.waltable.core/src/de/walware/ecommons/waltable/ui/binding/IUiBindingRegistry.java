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
package de.walware.ecommons.waltable.ui.binding;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.ui.action.IDragMode;
import de.walware.ecommons.waltable.ui.action.IKeyAction;
import de.walware.ecommons.waltable.ui.action.IMouseAction;


public interface IUiBindingRegistry {
	
	public IKeyAction getKeyEventAction(KeyEvent event);
	
	public IDragMode getDragMode(MouseEvent event);
	
	public IMouseAction getMouseMoveAction(MouseEvent event);
	
	public IMouseAction getMouseDownAction(MouseEvent event);
	
	public IMouseAction getSingleClickAction(MouseEvent event);
	
	public IMouseAction getDoubleClickAction(MouseEvent event);
	
	public IMouseAction getMouseHoverAction(MouseEvent event);
	
	public IMouseAction getMouseEnterAction(MouseEvent event);
	
	public IMouseAction getMouseExitAction(MouseEvent event);

}
