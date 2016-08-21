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

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.data.validate.IDataValidator;
import de.walware.ecommons.waltable.layer.AbstractLayer;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.painter.cell.ICellPainter;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;

/**
 * Configurations can be added to NatTable/ILayer to modify default behavior.
 * These will be processed when {@link NatTable#configure()} is invoked.
 *
 * Default configurations are added to most layers {@link AbstractLayer#addConfiguration(IConfiguration)}.
 * You can turn off default configuration for an {@link ILayer} by setting auto configure to false
 * in the constructor.
 */
public interface IConfiguration {

	public void configureLayer(ILayer layer);

	/**
	 * Configure NatTable's {@link IConfigRegistry} upon receiving this call back.
	 * A mechanism to plug-in custom {@link ICellPainter}, {@link IDataValidator} etc.
	 */
	public void configureRegistry(IConfigRegistry configRegistry);

	/**
	 * Configure NatTable's {@link IConfigRegistry} upon receiving this call back
	 * A mechanism to customize key/mouse bindings.
	 */
	public void configureUiBindings(UiBindingRegistry uiBindingRegistry);

}
