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

package de.walware.ecommons.waltable.ui;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.ILayer;


/**
 * Specifies the rectangular area available to an {@link ILayer}
 * Note: All layers get the client area from {@link NatTable} which implements this interface. 
 * 
 * @see ILayer#getClientAreaProvider()
 */
public interface IClientAreaProvider {
	
	
	IClientAreaProvider DEFAULT= new IClientAreaProvider() {
		@Override
		public LRectangle getClientArea() {
			return new LRectangle(0, 0, 0, 0);
		}
	};
	
	
	LRectangle getClientArea();
	
}
