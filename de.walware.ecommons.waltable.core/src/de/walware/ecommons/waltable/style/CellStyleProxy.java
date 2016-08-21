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

import de.walware.ecommons.waltable.config.CellConfigAttributes;
import de.walware.ecommons.waltable.config.IConfigRegistry;


public class CellStyleProxy extends StyleProxy {
	
	
	public CellStyleProxy(final IConfigRegistry configRegistry,
			final DisplayMode targetDisplayMode, final List<String> configLabels) {
		super(CellConfigAttributes.CELL_STYLE, configRegistry, targetDisplayMode, configLabels);
	}
	
	
	@Override
	public <T> void setAttributeValue(final ConfigAttribute<T> styleAttribute, final T value) {
		throw new UnsupportedOperationException("Not implemented yet"); //$NON-NLS-1$
	}

}
