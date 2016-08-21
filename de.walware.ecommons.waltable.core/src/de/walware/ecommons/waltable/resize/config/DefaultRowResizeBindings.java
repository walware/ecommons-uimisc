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

package de.walware.ecommons.waltable.resize.config;

import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import de.walware.ecommons.waltable.config.AbstractUiBindingConfiguration;
import de.walware.ecommons.waltable.resize.PositionResizeDragMode;
import de.walware.ecommons.waltable.resize.RowResizeEventMatcher;
import de.walware.ecommons.waltable.resize.action.AutoResizeRowAction;
import de.walware.ecommons.waltable.resize.action.RowResizeCursorAction;
import de.walware.ecommons.waltable.ui.action.ClearCursorAction;
import de.walware.ecommons.waltable.ui.action.NoOpMouseAction;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;
import de.walware.ecommons.waltable.ui.matcher.MouseEventMatcher;


public class DefaultRowResizeBindings extends AbstractUiBindingConfiguration {
	
	
	@Override
	public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
		// Mouse move - Show resize cursor
		uiBindingRegistry.registerFirstMouseMoveBinding(new RowResizeEventMatcher(0), new RowResizeCursorAction());
		uiBindingRegistry.registerMouseMoveBinding(new MouseEventMatcher(), new ClearCursorAction());
		
		// Row resize
		uiBindingRegistry.registerFirstMouseDragMode(new RowResizeEventMatcher(1), new PositionResizeDragMode(VERTICAL));
		
		uiBindingRegistry.registerDoubleClickBinding(new RowResizeEventMatcher(1), new AutoResizeRowAction());
		uiBindingRegistry.registerSingleClickBinding(new RowResizeEventMatcher(1), new NoOpMouseAction());
	}
	
}
