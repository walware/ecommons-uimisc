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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Style implements IStyle {

	private final Map<ConfigAttribute<?>, Object> styleAttributeValueMap= new HashMap<>();

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getAttributeValue(final ConfigAttribute<T> styleAttribute) {
		return (T) this.styleAttributeValueMap.get(styleAttribute);
	}

	@Override
	public <T> void setAttributeValue(final ConfigAttribute<T> styleAttribute, final T value) {
		this.styleAttributeValueMap.put(styleAttribute, value);
	}

	@Override
	public String toString() {
		final StringBuilder resultBuilder= new StringBuilder();
		resultBuilder.append(this.getClass().getSimpleName() + ": "); //$NON-NLS-1$

		final Set<Entry<ConfigAttribute<?>, Object>> entrySet= this.styleAttributeValueMap.entrySet();

		for (final Entry<ConfigAttribute<?>, Object> entry : entrySet) {
			resultBuilder.append(entry.getKey() + ": " + entry.getValue() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return resultBuilder.toString();
	}

}
