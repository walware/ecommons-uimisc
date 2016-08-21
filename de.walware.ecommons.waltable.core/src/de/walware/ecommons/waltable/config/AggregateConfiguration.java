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

import java.util.Collection;
import java.util.LinkedList;

import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;


/**
 * Aggregates {@link IConfiguration} objects and invokes configure methods on all its members.
 */
public class AggregateConfiguration implements IConfiguration {

	private final Collection<IConfiguration> configurations= new LinkedList<>();

	public void addConfiguration(final IConfiguration configuration) {
		this.configurations.add(configuration);
	}

	@Override
	public void configureLayer(final ILayer layer) {
		for (final IConfiguration configuration : this.configurations) {
			configuration.configureLayer(layer);
		}
	}

	@Override
	public void configureRegistry(final IConfigRegistry configRegistry) {
		for (final IConfiguration configuration : this.configurations) {
			configuration.configureRegistry(configRegistry);
		}
	}

	@Override
	public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
		for (final IConfiguration configuration : this.configurations) {
			configuration.configureUiBindings(uiBindingRegistry);
		}
	}

}
