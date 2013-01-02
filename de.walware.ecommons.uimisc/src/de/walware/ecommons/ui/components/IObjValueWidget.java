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

import org.eclipse.swt.widgets.Control;


public interface IObjValueWidget<T> {
	
	
	Control getControl();
	
	Class<T> getValueType();
	
	void addValueListener(IObjValueListener<T> listener);
	void removeValueListener(IObjValueListener<T> listener);
	
	T getValue(int idx);
	
	void setValue(int idx, T value);
	
}
