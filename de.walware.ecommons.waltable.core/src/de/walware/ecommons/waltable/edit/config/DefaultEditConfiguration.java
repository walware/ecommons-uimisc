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
package de.walware.ecommons.waltable.edit.config;

import de.walware.ecommons.waltable.config.AbstractLayerConfiguration;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.config.IEditableRule;
import de.walware.ecommons.waltable.data.validate.DefaultDataValidator;
import de.walware.ecommons.waltable.edit.EditCellCommandHandler;
import de.walware.ecommons.waltable.edit.EditConfigAttributes;
import de.walware.ecommons.waltable.edit.InlineCellEditEventHandler;
import de.walware.ecommons.waltable.edit.editor.TextCellEditor;
import de.walware.ecommons.waltable.layer.AbstractLayer;

/**
 * Default configuration for edit behaviour in a NatTable.
 * Will register the {@link EditCellCommandHandler} and the {@link InlineCellEditEventHandler}
 * to the layer this configuration is added to. Usually this configuration is added to 
 * a GridLayer.
 * <p>
 * It also registers default values on top-level for the following {@link EditConfigAttributes}:
 * <ul>
 * <li>{@link EditConfigAttributes#CELL_EDITABLE_RULE} - IEditableRule.NEVER_EDITABLE<br>
 * by default a NatTable is not editable</li>
 * <li>{@link EditConfigAttributes} - {@link TextCellEditor}<br>
 * by default a TextCellEditor will be used for editing cells in a NatTable</li>
 * <li>{@link EditConfigAttributes} - {@link DefaultDataValidator}<br>
 * by default a validator is registered that always returns <code>true</code>, regardless of the 
 * entered value</li>
 * </ul>
 */
public class DefaultEditConfiguration extends AbstractLayerConfiguration<AbstractLayer> {

	@Override
	public void configureTypedLayer(final AbstractLayer layer) {
		layer.registerCommandHandler(new EditCellCommandHandler());
		layer.registerEventHandler(new InlineCellEditEventHandler(layer));
	}
	
	@Override
	public void configureRegistry(final IConfigRegistry configRegistry) {
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.NEVER_EDITABLE);
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new TextCellEditor());
		configRegistry.registerConfigAttribute(EditConfigAttributes.DATA_VALIDATOR, new DefaultDataValidator());
	}
	
}
