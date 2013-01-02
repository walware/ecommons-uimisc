/*******************************************************************************
 * Copyright (c) 2012-2013 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.config;

import org.eclipse.nebula.widgets.nattable.style.ConfigAttribute;


public class LayoutSizeConfig {
	
	
	public static final ConfigAttribute<LayoutSizeConfig> CONFIG = new ConfigAttribute<LayoutSizeConfig>();
	
	
	private final int fSpaceWidth;
	
	private final int fTextHeight;
	
	private final int fCharWidth;
	
	
	public LayoutSizeConfig(int spaceWidth, int textHeight, int charWidth) {
		fSpaceWidth = spaceWidth;
		fTextHeight = textHeight;
		fCharWidth = charWidth;
	}
	
	public int getDefaultSpace() {
		return fSpaceWidth;
	}
	
	public int getTextHeight() {
		return fTextHeight;
	}
	
	public int getCharWidth() {
		return fCharWidth;
	}
	
	public int getRowHeight() {
		return fTextHeight + fSpaceWidth;
	}
	
}
