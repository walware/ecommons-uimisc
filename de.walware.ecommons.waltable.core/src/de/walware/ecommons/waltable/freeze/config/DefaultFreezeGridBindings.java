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
package de.walware.ecommons.waltable.freeze.config;


import org.eclipse.swt.SWT;

import de.walware.ecommons.waltable.config.AbstractUiBindingConfiguration;
import de.walware.ecommons.waltable.freeze.action.FreezeGridAction;
import de.walware.ecommons.waltable.freeze.action.UnFreezeGridAction;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;
import de.walware.ecommons.waltable.ui.matcher.KeyEventMatcher;

public class DefaultFreezeGridBindings extends AbstractUiBindingConfiguration {

	@Override
	public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD1 | SWT.MOD2, 'f'), new FreezeGridAction());
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD1 | SWT.MOD2, 'u'), new UnFreezeGridAction());
	}
}
