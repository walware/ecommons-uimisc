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

import de.walware.ecommons.waltable.ui.action.IKeyAction;
import de.walware.ecommons.waltable.ui.matcher.IKeyEventMatcher;

public class KeyBinding {

	private final IKeyEventMatcher keyEventMatcher;
	
	private final IKeyAction action;
	
	public KeyBinding(final IKeyEventMatcher keyEventMatcher, final IKeyAction action) {
		this.keyEventMatcher= keyEventMatcher;
		this.action= action;
	}
	
	public IKeyEventMatcher getKeyEventMatcher() {
		return this.keyEventMatcher;
	}
	
	public IKeyAction getAction() {
		return this.action;
	}
	
}
