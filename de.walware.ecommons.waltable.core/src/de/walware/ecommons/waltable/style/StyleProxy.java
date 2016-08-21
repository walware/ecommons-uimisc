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
package de.walware.ecommons.waltable.style;

import java.util.List;

import de.walware.ecommons.waltable.config.IConfigRegistry;


public abstract class StyleProxy implements IStyle {
	
	
	private final ConfigAttribute<IStyle> styleConfigAttribute;
	private final IConfigRegistry configRegistry;
	private final DisplayMode targetDisplayMode;
	private final List<String> configLabels;
	
	
	public StyleProxy(final ConfigAttribute<IStyle> styleConfigAttribute,
			final IConfigRegistry configRegistry,
			final DisplayMode targetDisplayMode, final List<String> configLabels) {
		this.styleConfigAttribute= styleConfigAttribute;
		this.configRegistry= configRegistry;
		this.targetDisplayMode= targetDisplayMode;
		this.configLabels= configLabels;
	}
	
	
	@Override
	public <T> T getAttributeValue(final ConfigAttribute<T> styleAttribute) {
		T styleAttributeValue= null;
		final IDisplayModeLookupStrategy displayModeLookupStrategy= this.configRegistry.getDisplayModeOrdering();
		
		for (final DisplayMode displayMode : displayModeLookupStrategy.getDisplayModeOrdering(this.targetDisplayMode)) {
			for (final String configLabel : this.configLabels) {
				final IStyle cellStyle= this.configRegistry.getSpecificConfigAttribute(this.styleConfigAttribute, displayMode, configLabel);
				if (cellStyle != null) {
					styleAttributeValue= cellStyle.getAttributeValue(styleAttribute);
					if (styleAttributeValue != null) {
						return styleAttributeValue;
					}
				}
			}
			
			// default
			final IStyle cellStyle= this.configRegistry.getSpecificConfigAttribute(this.styleConfigAttribute, displayMode, null);
			if (cellStyle != null) {
				styleAttributeValue= cellStyle.getAttributeValue(styleAttribute);
				if (styleAttributeValue != null) {
					return styleAttributeValue;
				}
			}
		}
		
		return null;
	}
	
}
