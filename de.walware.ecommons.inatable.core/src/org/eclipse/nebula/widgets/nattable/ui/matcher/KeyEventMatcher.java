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
// -depend
package org.eclipse.nebula.widgets.nattable.ui.matcher;

import org.eclipse.swt.events.KeyEvent;

public class KeyEventMatcher implements IKeyEventMatcher {

	private int stateMask;
	
	private int keyCode;
	
	public KeyEventMatcher(int keyCode) {
		this(0, keyCode);
	}
	
	public KeyEventMatcher(int stateMask, int keyCode) {
		this.stateMask = stateMask;
		this.keyCode = keyCode;
	}
	
	public int getStateMask() {
		return stateMask;
	}
	
	public int getKeyCode() {
		return keyCode;
	}
	
	public boolean matches(KeyEvent event) {
		boolean stateMaskMatches = stateMask == event.stateMask;
		
		boolean keyCodeMatches = keyCode == event.keyCode;
		
		return stateMaskMatches && keyCodeMatches;
	}
	
	
	public int hashCode() {
		return stateMask * 17 + keyCode * 119;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof KeyEventMatcher)) {
			return false;
		}
		final KeyEventMatcher other = (KeyEventMatcher) obj;
		return (stateMask == other.stateMask
				&& keyCode == other.keyCode);
	}
	
}
