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
package de.walware.ecommons.waltable.export.config;

import org.eclipse.swt.SWT;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.config.IConfiguration;
import de.walware.ecommons.waltable.export.ExportConfigAttributes;
import de.walware.ecommons.waltable.export.action.ExportAction;
import de.walware.ecommons.waltable.export.excel.DefaultExportFormatter;
import de.walware.ecommons.waltable.export.excel.ExcelExporter;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;
import de.walware.ecommons.waltable.ui.matcher.KeyEventMatcher;

public class DefaultExportBindings implements IConfiguration {

	@Override
	public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD1, 'e'), new ExportAction());
	}

	@Override
	public void configureRegistry(final IConfigRegistry configRegistry) {
		configRegistry.registerConfigAttribute(
				ExportConfigAttributes.EXPORTER, new ExcelExporter());
		configRegistry.registerConfigAttribute(
				ExportConfigAttributes.EXPORT_FORMATTER, new DefaultExportFormatter());
		configRegistry.registerConfigAttribute(
				ExportConfigAttributes.DATE_FORMAT, "m/d/yy h:mm"); //$NON-NLS-1$
	}

	@Override
	public void configureLayer(final ILayer layer) {
	}

}
