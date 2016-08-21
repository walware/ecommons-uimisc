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
package de.walware.ecommons.waltable.tickupdate.config;

import org.eclipse.swt.SWT;

import de.walware.ecommons.waltable.config.AbstractLayerConfiguration;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.selection.SelectionLayer;
import de.walware.ecommons.waltable.tickupdate.ITickUpdateHandler;
import de.walware.ecommons.waltable.tickupdate.TickUpdateCommandHandler;
import de.walware.ecommons.waltable.tickupdate.TickUpdateConfigAttributes;
import de.walware.ecommons.waltable.tickupdate.action.TickUpdateAction;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;
import de.walware.ecommons.waltable.ui.matcher.KeyEventMatcher;

/**
 * The default configuration for tick update handling. Will register the default
 * {@link ITickUpdateHandler#DEFAULT_TICK_UPDATE_HANDLER} to be the update handler
 * for tick updates and key bindings on keypad add and keybadd subtract to call 
 * the corresponding {@link TickUpdateAction}.
 */
public class DefaultTickUpdateConfiguration extends AbstractLayerConfiguration<SelectionLayer> {
	
	@Override
	public void configureRegistry(final IConfigRegistry configRegistry) {
		configRegistry.registerConfigAttribute(
				TickUpdateConfigAttributes.UPDATE_HANDLER, ITickUpdateHandler.DEFAULT_TICK_UPDATE_HANDLER);
	}

	@Override
	public void configureTypedLayer(final SelectionLayer selectionLayer) {
		selectionLayer.registerCommandHandler(new TickUpdateCommandHandler(selectionLayer));
	}
	
	@Override
	public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
		uiBindingRegistry.registerKeyBinding(
				new KeyEventMatcher(SWT.NONE, SWT.KEYPAD_ADD), 
				new TickUpdateAction(true));

		uiBindingRegistry.registerKeyBinding(
				new KeyEventMatcher(SWT.NONE, SWT.KEYPAD_SUBTRACT), 
				new TickUpdateAction(false));
	}
	
}
