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
package de.walware.ecommons.waltable.layer.cell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class AbstractOverrider implements IConfigLabelAccumulator {
	
	
	private final Map<Serializable, List<String>> overrides= new HashMap<>();
	
	
	public void removeOverride(final Serializable key) {
		this.overrides.remove(key);
	}
	
	public void registerOverrides(final Serializable key, final String...configLabels) {
		List<String> list= getOverrides(key);
		if (list == null) {
			list= new ArrayList<>();
			registerOverrides(key, list);
		}
		for (int i= 0; i < configLabels.length; i++) {
			if (list.contains(configLabels[i])) {
				continue;
			}
			list.add(configLabels[i]);
		}
	}
	
	public void registerOverridesOnTop(final Serializable key, final String...configLabels) {
		List<String> list= getOverrides(key);
		if (list == null) {
			list= new ArrayList<>();
			registerOverrides(key, list);
		}
		for (int i= 0, j= 0; i < configLabels.length; i++) {
			final int k= list.indexOf(configLabels);
			if (k > j) {
				list.remove(k);
			}
			else if (k >= 0) {
				continue;
			}
			list.add(j++, configLabels[i]);
		}
	}
	
	public void registerOverrides(final Serializable key, final List<String> configLabels) {
		this.overrides.put(key, configLabels);
	}
	
	public Map<Serializable, List<String>> getOverrides() {
		return this.overrides;
	}
	
	public List<String> getOverrides(final Serializable key) {
		return this.overrides.get(key);
	}
	
	public void addOverrides(final Map<Serializable, List<String>> overrides) {
		this.overrides.putAll(overrides);
	}
	
}
