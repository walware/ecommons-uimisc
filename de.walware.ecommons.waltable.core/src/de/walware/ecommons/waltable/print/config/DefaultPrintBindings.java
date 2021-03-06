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
package de.walware.ecommons.waltable.print.config;

import org.eclipse.swt.SWT;

import de.walware.ecommons.waltable.config.AbstractUiBindingConfiguration;
import de.walware.ecommons.waltable.print.action.PrintAction;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;
import de.walware.ecommons.waltable.ui.matcher.KeyEventMatcher;


/**
 * Simple UI binding configuration that adds the binding for [Ctrl] + [P] to trigger
 * the PrintAction.
 * 
 * @see PrintAction
 */
public class DefaultPrintBindings extends AbstractUiBindingConfiguration {

	@Override
	public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD1, 'p'), new PrintAction());
	}

}
