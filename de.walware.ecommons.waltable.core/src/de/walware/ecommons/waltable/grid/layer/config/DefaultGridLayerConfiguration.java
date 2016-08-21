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
package de.walware.ecommons.waltable.grid.layer.config;

import de.walware.ecommons.waltable.config.AggregateConfiguration;
import de.walware.ecommons.waltable.edit.config.DefaultEditBindings;
import de.walware.ecommons.waltable.edit.config.DefaultEditConfiguration;
import de.walware.ecommons.waltable.export.config.DefaultExportBindings;
import de.walware.ecommons.waltable.grid.GridRegion;
import de.walware.ecommons.waltable.grid.cell.AlternatingRowConfigLabelAccumulator;
import de.walware.ecommons.waltable.grid.layer.GridLayer;
import de.walware.ecommons.waltable.layer.CompositeLayer;
import de.walware.ecommons.waltable.print.config.DefaultPrintBindings;

/**
 * Sets up features handled at the grid level. Added by {@link GridLayer}
 */
public class DefaultGridLayerConfiguration extends AggregateConfiguration {

	public DefaultGridLayerConfiguration(final CompositeLayer gridLayer) {
		addAlternateRowColoringConfig(gridLayer);
		addEditingHandlerConfig();
		addEditingUIConfig();
		addPrintUIBindings();
		addExcelExportUIBindings();
	}

	protected void addExcelExportUIBindings() {
		addConfiguration(new DefaultExportBindings());
	}

	protected void addPrintUIBindings() {
		addConfiguration(new DefaultPrintBindings());
	}

	protected void addEditingUIConfig() {
		addConfiguration(new DefaultEditBindings());
	}

	protected void addEditingHandlerConfig() {
		addConfiguration(new DefaultEditConfiguration());
	}

	protected void addAlternateRowColoringConfig(final CompositeLayer gridLayer) {
		addConfiguration(new DefaultRowStyleConfiguration());
		gridLayer.addConfigLabelAccumulatorForRegion(GridRegion.BODY, new AlternatingRowConfigLabelAccumulator());
	}

}
