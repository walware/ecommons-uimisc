/*******************************************************************************
 * Copyright (c) 2009-2010 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.mpbv;


public class BrowserBookmark {
	
	
	private final String fLabel;
	
	private final String fUrl;
	
	
	public BrowserBookmark(final String label, final String url) {
		fLabel = label;
		fUrl = url;
	}
	
	
	public String getLabel() {
		return fLabel;
	}
	
	public String getUrl() {
		return fUrl;
	}
	
	
	@Override
	public String toString() {
		return fLabel + " [" + fUrl + "]";
	}
	
}
