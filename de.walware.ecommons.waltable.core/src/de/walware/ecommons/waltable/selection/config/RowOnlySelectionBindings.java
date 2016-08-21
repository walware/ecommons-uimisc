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


import org.eclipse.swt.SWT;

import de.walware.ecommons.waltable.selection.action.RowSelectionDragMode;
import de.walware.ecommons.waltable.selection.action.SelectRowAction;
import de.walware.ecommons.waltable.ui.action.IDragMode;
import de.walware.ecommons.waltable.ui.action.IMouseAction;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;
import de.walware.ecommons.waltable.ui.matcher.MouseEventMatcher;

public class RowOnlySelectionBindings extends DefaultSelectionBindings {

	@Override
	protected void configureBodyMouseClickBindings(final UiBindingRegistry uiBindingRegistry) {
		final IMouseAction action= new SelectRowAction();
		uiBindingRegistry.registerMouseDownBinding(MouseEventMatcher.bodyLeftClick(SWT.NONE), action);
		uiBindingRegistry.registerMouseDownBinding(MouseEventMatcher.bodyLeftClick(SWT.MOD2), action);
		uiBindingRegistry.registerMouseDownBinding(MouseEventMatcher.bodyLeftClick(SWT.MOD1), action);
		uiBindingRegistry.registerMouseDownBinding(MouseEventMatcher.bodyLeftClick(SWT.MOD2 | SWT.MOD1), action);
	}

	@Override
	protected void configureBodyMouseDragMode(final UiBindingRegistry uiBindingRegistry) {
		final IDragMode dragMode= new RowSelectionDragMode();
		uiBindingRegistry.registerFirstMouseDragMode(MouseEventMatcher.bodyLeftClick(SWT.NONE), dragMode);
		uiBindingRegistry.registerFirstMouseDragMode(MouseEventMatcher.bodyLeftClick(SWT.MOD2), dragMode);
		uiBindingRegistry.registerFirstMouseDragMode(MouseEventMatcher.bodyLeftClick(SWT.MOD1), dragMode);
		uiBindingRegistry.registerFirstMouseDragMode(MouseEventMatcher.bodyLeftClick(SWT.MOD2 | SWT.MOD1), dragMode);
	}
}
