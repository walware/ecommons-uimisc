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
package de.walware.ecommons.waltable.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.walware.ecommons.waltable.style.ConfigAttribute;
import de.walware.ecommons.waltable.style.DefaultDisplayModeOrdering;
import de.walware.ecommons.waltable.style.DisplayMode;
import de.walware.ecommons.waltable.style.IDisplayModeLookupStrategy;


public class ConfigRegistry implements IConfigRegistry {
	
	
	// Map<configAttributeType, Map<displayMode, Map<configLabel, value>>>
	final Map<ConfigAttribute<?>, Map<DisplayMode, Map<String, ?>>> configRegistry= new HashMap<>();
	
	private IDisplayModeLookupStrategy displayModeLookupStrategy= new DefaultDisplayModeOrdering();
	
	
	public ConfigRegistry() {
	}
	
	
	@Override
	public IDisplayModeLookupStrategy getDisplayModeOrdering() {
		return this.displayModeLookupStrategy;
	}
	
	public void setDisplayModeOrdering(final IDisplayModeLookupStrategy displayModeLookupStrategy) {
		this.displayModeLookupStrategy= displayModeLookupStrategy;
	}
	
	
	@Override
	public <T> T getConfigAttribute(final ConfigAttribute<T> configAttribute,
			final DisplayMode targetDisplayMode, final String...configLabels) {
		return getConfigAttribute(configAttribute, targetDisplayMode, Arrays.asList(configLabels));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getConfigAttribute(final ConfigAttribute<T> configAttribute,
			final DisplayMode targetDisplayMode, final List<String> configLabels) {
		T attributeValue= null;
		
		final Map<DisplayMode, Map<String, ?>> displayModeConfigAttributeMap= this.configRegistry.get(configAttribute);
		if (displayModeConfigAttributeMap != null) {
			for (final DisplayMode displayMode : this.displayModeLookupStrategy.getDisplayModeOrdering(targetDisplayMode)) {
				final Map<String, T> configAttributeMap= (Map<String, T>) displayModeConfigAttributeMap.get(displayMode);
				if (configAttributeMap != null) {
					for (final String configLabel : configLabels) {
						attributeValue= configAttributeMap.get(configLabel);
						if (attributeValue != null) {
							return attributeValue;
						}
					}
					
					// default config type
					attributeValue= configAttributeMap.get(null);
					if (attributeValue != null) {
						return attributeValue;
					}
				}
			}
		}
		
		return attributeValue;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getSpecificConfigAttribute(final ConfigAttribute<T> configAttribute,
			final DisplayMode displayMode, final String configLabel) {
		T attributeValue= null;
		
		final Map<DisplayMode, Map<String, ?>> displayModeConfigAttributeMap= this.configRegistry.get(configAttribute);
		if (displayModeConfigAttributeMap != null) {
			final Map<String, T> configAttributeMap= (Map<String, T>) displayModeConfigAttributeMap.get(displayMode);
			if (configAttributeMap != null) {
				attributeValue= configAttributeMap.get(configLabel);
				if (attributeValue != null) {
					return attributeValue;
				}
			}
		}
		
		return attributeValue;
	}
	
	@Override
	public <T> void registerConfigAttribute(final ConfigAttribute<T> configAttribute, final T attributeValue) {
		registerConfigAttribute(configAttribute, attributeValue, DisplayMode.NORMAL);
	}
	
	@Override
	public <T> void registerConfigAttribute(final ConfigAttribute<T> configAttribute, final T attributeValue,
			final DisplayMode displayMode) {
		registerConfigAttribute(configAttribute, attributeValue, displayMode, null);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> void registerConfigAttribute(final ConfigAttribute<T> configAttribute, final T attributeValue,
			final DisplayMode displayMode, final String configLabel) {
		Map<DisplayMode, Map<String, ?>> displayModeConfigAttributeMap= this.configRegistry.get(configAttribute);
		if (displayModeConfigAttributeMap == null) {
			displayModeConfigAttributeMap= new HashMap<>();
			this.configRegistry.put(configAttribute, displayModeConfigAttributeMap);
		}
		
		Map<String, T> configAttributeMap= (Map<String, T>) displayModeConfigAttributeMap.get(displayMode);
		if (configAttributeMap == null) {
			configAttributeMap= new HashMap<>();
			displayModeConfigAttributeMap.put(displayMode, configAttributeMap);
		}
		
		configAttributeMap.put(configLabel, attributeValue);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> void unregisterConfigAttribute(final ConfigAttribute<T> configAttributeType,
			final DisplayMode displayMode, final String configLabel) {
		final Map<DisplayMode, Map<String, ?>> displayModeConfigAttributeMap= this.configRegistry.get(configAttributeType);
		if (displayModeConfigAttributeMap != null) {
			final Map<String, T> configAttributeMap= (Map<String, T>) displayModeConfigAttributeMap.get(displayMode);
			if (configAttributeMap != null) {
				configAttributeMap.remove(configLabel);
			}
		}
	}
	
}
