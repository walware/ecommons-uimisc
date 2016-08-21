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
package de.walware.ecommons.waltable.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class PersistenceUtils {

	/**
	 * Parse the persisted property and create a TreeMap&lt;Long, String&gt; from it.
	 * Works in conjunction with the {@link PersistenceUtils#mapAsString(Map)}.
	 * 
	 * @param property from the properties file.
	 */
	public static Map<Long, String> parseString(final Object property) {
		final TreeMap<Long, String> map= new TreeMap<>();
		
		if (property != null) {
			final String value= (String) property;
			final String[] renamedColumns= value.split("\\|"); //$NON-NLS-1$
	
			for (final String token : renamedColumns) {
				final String[] split= token.split(":"); //$NON-NLS-1$
				final String id= split[0];
				final String label= split[1];
				map.put(Long.valueOf(id), label);
			}
		}
		return map;
	}

	/**
	 * Convert the Map to a String suitable for persisting in the Properties file.
	 * {@link PersistenceUtils#parseString(Object)} can be used to reconstruct this Map object from the String.
	 */
	public static String mapAsString(final Map<Long, String> map) {
		final StringBuffer buffer= new StringBuffer();
		for (final Entry<Long, String> entry : map.entrySet()) {
			buffer.append(entry.getKey() + ":" + entry.getValue() + "|"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return buffer.toString();
	}

}
