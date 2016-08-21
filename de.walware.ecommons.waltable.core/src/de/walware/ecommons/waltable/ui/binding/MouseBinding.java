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

import de.walware.ecommons.waltable.ui.action.IMouseAction;
import de.walware.ecommons.waltable.ui.matcher.IMouseEventMatcher;

public class MouseBinding {

	private final IMouseEventMatcher mouseEventMatcher;
	
	private final IMouseAction action;
	
	public MouseBinding(final IMouseEventMatcher mouseEventMatcher, final IMouseAction action) {
		this.mouseEventMatcher= mouseEventMatcher;
		this.action= action;
	}
	
	public IMouseEventMatcher getMouseEventMatcher() {
		return this.mouseEventMatcher;
	}
	
	public IMouseAction getAction() {
		return this.action;
	}
	
	@Override
    public String toString() {
	    return getClass().getSimpleName() + "[mouseEventMatcher=" + this.mouseEventMatcher + " action=" + this.action  + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
