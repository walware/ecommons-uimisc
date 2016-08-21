/*******************************************************************************
 * Copyright (c) 2012-2016 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package de.walware.ecommons.waltable.config;

import de.walware.ecommons.waltable.style.ConfigAttribute;


public class LayoutSizeConfig {
	
	
	public static final ConfigAttribute<LayoutSizeConfig> CONFIG= new ConfigAttribute<>();
	
	
	private final int fSpaceWidth;
	
	private final int fTextHeight;
	
	private final int fCharWidth;
	
	
	public LayoutSizeConfig(final int spaceWidth, final int textHeight, final int charWidth) {
		this.fSpaceWidth= spaceWidth;
		this.fTextHeight= textHeight;
		this.fCharWidth= charWidth;
	}
	
	public int getDefaultSpace() {
		return this.fSpaceWidth;
	}
	
	public int getTextHeight() {
		return this.fTextHeight;
	}
	
	public int getCharWidth() {
		return this.fCharWidth;
	}
	
	public int getRowHeight() {
		return this.fTextHeight + this.fSpaceWidth;
	}
	
}
