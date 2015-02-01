/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.content;


public interface ITextElementFilter extends IElementFilter {
	
	
	/**
	 * Sets the text to search for
	 * 
	 * @param text the text to search for
	 * @return if the text of this filter changed
	 */
	boolean setText(String text);
	
}
