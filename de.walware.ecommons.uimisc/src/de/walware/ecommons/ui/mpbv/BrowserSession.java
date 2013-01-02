/*******************************************************************************
 * Copyright (c) 2009-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.mpbv;

import org.eclipse.jface.resource.ImageDescriptor;

import de.walware.ecommons.collections.IntArrayMap;


/**
 * Session for page browser views of the type {@link PageBookBrowserView}.
 */
public class BrowserSession implements ISession {
	
	
	boolean fBound;
	String fTitle;
	String fUrl;
	
	IntArrayMap<String> fStaticContent;
	
	ImageDescriptor fImageDescriptor;
	
	
	public BrowserSession() {
		fTitle = "";
		fUrl = "";
	}
	
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return fImageDescriptor;
	}
	
	@Override
	public String getLabel() {
		return fTitle;
	}
	
	public String getUrl() {
		return fUrl;
	}
	
	int putStatic(final String html) {
		if (fStaticContent == null) {
			fStaticContent = new IntArrayMap<String>();
		}
		final int id = fStaticContent.size();
		fStaticContent.put(id, html);
		return id;
	}
	
	String getStatic(final int id) {
		return fStaticContent.get(id);
	}
	
}
