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
package de.walware.ecommons.waltable.layer.config;

import de.walware.ecommons.waltable.config.AggregateConfiguration;
import de.walware.ecommons.waltable.grid.GridRegion;
import de.walware.ecommons.waltable.grid.layer.RowHeaderLayer;
import de.walware.ecommons.waltable.resize.config.DefaultRowResizeBindings;

/**
 * Default setup for the Row header area. Added by the {@link RowHeaderLayer}
 * Override the methods in this class to customize style / UI bindings.
 *
 * @see GridRegion
 */
public class DefaultRowHeaderLayerConfiguration extends AggregateConfiguration {

	public DefaultRowHeaderLayerConfiguration() {
		addRowHeaderStyleConfig();
		addRowHeaderUIBindings();
	}

	protected void addRowHeaderStyleConfig() {
		addConfiguration(new DefaultRowHeaderStyleConfiguration());
	}

	protected void addRowHeaderUIBindings() {
		addConfiguration(new DefaultRowResizeBindings());
	}

}
