/*=============================================================================#
 # Copyright (c) 2006-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ts.ui;

import org.eclipse.swt.graphics.Image;


/**
 * UI Adapter for IToolRunnable
 * 
 * @since 1.1
 */
public interface IToolRunnableDecorator {
	
	
	/**
	 * Returns an image to be used for displaying a runnable.
	 * 
	 * @return the image or <code>null</code>.
	 */
	public Image getImage();
	
}
