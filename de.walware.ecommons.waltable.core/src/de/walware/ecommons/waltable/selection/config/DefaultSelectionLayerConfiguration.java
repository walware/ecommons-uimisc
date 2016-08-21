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
package de.walware.ecommons.waltable.selection.config;

import de.walware.ecommons.waltable.config.AggregateConfiguration;
import de.walware.ecommons.waltable.selection.SelectionLayer;
import de.walware.ecommons.waltable.tickupdate.config.DefaultTickUpdateConfiguration;


/**
 * Sets up default styling and UI bindings. Override the methods in here to
 * customize behavior. Added by the {@link SelectionLayer}
 */
public class DefaultSelectionLayerConfiguration extends AggregateConfiguration {

	public DefaultSelectionLayerConfiguration() {
		addSelectionStyleConfig();
		addSelectionUIBindings();
		addTickUpdateConfig();
		addMoveSelectionConfig();
	}

	protected void addSelectionStyleConfig() {
		addConfiguration(new DefaultSelectionStyleConfiguration());
	}

	protected void addSelectionUIBindings() {
		addConfiguration(new DefaultSelectionBindings());
	}

	protected void addTickUpdateConfig() {
		addConfiguration(new DefaultTickUpdateConfiguration());
	}

	protected void addMoveSelectionConfig() {
		addConfiguration(new DefaultMoveSelectionConfiguration());
	}
}
