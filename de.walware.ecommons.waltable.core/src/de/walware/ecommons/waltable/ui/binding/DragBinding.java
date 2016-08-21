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

import de.walware.ecommons.waltable.ui.action.IDragMode;
import de.walware.ecommons.waltable.ui.matcher.IMouseEventMatcher;

public class DragBinding {
	
	private final IMouseEventMatcher mouseEventMatcher;
	
	private final IDragMode dragMode;
	
	public DragBinding(final IMouseEventMatcher mouseEventMatcher, final IDragMode dragMode) {
		this.mouseEventMatcher= mouseEventMatcher;
		this.dragMode= dragMode;
	}
	
	public IMouseEventMatcher getMouseEventMatcher() {
		return this.mouseEventMatcher;
	}
	
	public IDragMode getDragMode() {
		return this.dragMode;
	}
	
}
