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
// -depend
package de.walware.ecommons.waltable.ui.matcher;

import org.eclipse.swt.events.KeyEvent;

public class KeyEventMatcher implements IKeyEventMatcher {

	private final int stateMask;
	
	private final int keyCode;
	
	public KeyEventMatcher(final int keyCode) {
		this(0, keyCode);
	}
	
	public KeyEventMatcher(final int stateMask, final int keyCode) {
		this.stateMask= stateMask;
		this.keyCode= keyCode;
	}
	
	public int getStateMask() {
		return this.stateMask;
	}
	
	public int getKeyCode() {
		return this.keyCode;
	}
	
	@Override
	public boolean matches(final KeyEvent event) {
		final boolean stateMaskMatches= this.stateMask == event.stateMask;
		
		final boolean keyCodeMatches= this.keyCode == event.keyCode;
		
		return stateMaskMatches && keyCodeMatches;
	}
	
	
	@Override
	public int hashCode() {
		return this.stateMask * 17 + this.keyCode * 119;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof KeyEventMatcher)) {
			return false;
		}
		final KeyEventMatcher other= (KeyEventMatcher) obj;
		return (this.stateMask == other.stateMask
				&& this.keyCode == other.keyCode);
	}
	
}
