/*******************************************************************************
 * Copyright (c) 2012-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.components;

import org.eclipse.swt.events.TypedEvent;


public class ObjValueEvent<T> extends TypedEvent {
	
	private static final long serialVersionUID = 7509242153124380586L;
	
	public static final int DEFAULT_SELECTION = 1 << 0;
	
	
	public final int valueIdx;
	
	public final T oldValue;
	
	public T newValue;
	
	public final int flags;
	
	
	public ObjValueEvent(final IObjValueWidget<T> source, final int time, final int idx,
			final T oldValue, final T newValue, final int flags) {
		super(source);
		
		display = source.getControl().getDisplay();
		widget = source.getControl();
		this.time = time;
		
		valueIdx = idx;
		this.oldValue = oldValue;
		this.newValue = newValue;
		
		this.flags = flags;
	}
	
	
}
