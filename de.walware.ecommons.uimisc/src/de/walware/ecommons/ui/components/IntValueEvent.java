/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.components;

import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Control;


public class IntValueEvent extends TypedEvent {
	
	private static final long serialVersionUID = 7509242153124380586L;
	
	
	public final int valueIdx;
	
	public final int oldValue;
	
	public int newValue;
	
	
	IntValueEvent(final Control source, final int time, final int idx, final int oldValue, final int newValue) {
		super(source);
		
		display = source.getDisplay();
		widget = source;
		this.time = time;
		
		valueIdx = idx;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	
}
