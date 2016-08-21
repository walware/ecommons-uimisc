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


import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.swt.SWT;

import de.walware.ecommons.waltable.config.AbstractUiBindingConfiguration;
import de.walware.ecommons.waltable.edit.action.CellEditDragMode;
import de.walware.ecommons.waltable.edit.action.KeyEditAction;
import de.walware.ecommons.waltable.edit.action.MouseEditAction;
import de.walware.ecommons.waltable.edit.editor.ICellEditor;
import de.walware.ecommons.waltable.grid.GridRegion;
import de.walware.ecommons.waltable.painter.cell.CheckBoxPainter;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;
import de.walware.ecommons.waltable.ui.matcher.CellEditorMouseEventMatcher;
import de.walware.ecommons.waltable.ui.matcher.CellPainterMouseEventMatcher;
import de.walware.ecommons.waltable.ui.matcher.IMouseEventMatcher;
import de.walware.ecommons.waltable.ui.matcher.KeyEventMatcher;
import de.walware.ecommons.waltable.ui.matcher.LetterOrDigitKeyEventMatcher;
import de.walware.ecommons.waltable.ui.matcher.MouseEventMatcher;

/**
 * Default configuration for edit related bindings. Adds bindings that support opening
 * cell editors via keypress and mouse click.
 * <p>
 * By default {@link GridRegion#BODY} is used for the matchers to evaluate if an editor 
 * should be activated. By doing this only the editing in the body layer stack of a grid
 * is enabled.
 * </p>
 * <p>
 * Note: For typical {@link ICellEditor}s there is no special registering necessary like
 * 		 it was previous to 1.0.0. Only {@link ICellEditor}s that return <code>false</code>
 * 		 for {@link ICellEditor#activateAtAnyPosition()} need to register a custom
 * 		 {@link IMouseEventMatcher} to determine whether to activate the editor or not,
 * 		 regarding the correct position. This is for example necessary for the {@link CheckboxCellEditor}
 * 		 that is configured with the corresponding {@link CheckBoxPainter}, so the editor is only
 * 		 activated if the checkbox item is clicked, not any other position in the cell. 
 * </p>
 */
public class DefaultEditBindings extends AbstractUiBindingConfiguration {

	@Override
	public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
		//configure the space key to activate a cell editor via keyboard
		//this is especially useful for changing the value for a checkbox
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.NONE, 32), new KeyEditAction());
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.NONE, SWT.F2), new KeyEditAction());
		uiBindingRegistry.registerKeyBinding(new LetterOrDigitKeyEventMatcher(), new KeyEditAction());
		uiBindingRegistry.registerKeyBinding(new LetterOrDigitKeyEventMatcher(SWT.MOD2), new KeyEditAction());
		
		uiBindingRegistry.registerSingleClickBinding(
				new CellEditorMouseEventMatcher(GridRegion.BODY),
				new MouseEditAction());
		
		uiBindingRegistry.registerMouseDragMode(
				new CellEditorMouseEventMatcher(GridRegion.BODY),
				new CellEditDragMode());

		uiBindingRegistry.registerFirstSingleClickBinding(
                new CellPainterMouseEventMatcher(GridRegion.BODY, MouseEventMatcher.LEFT_BUTTON, CheckBoxPainter.class),
                new MouseEditAction());
		
		uiBindingRegistry.registerFirstMouseDragMode(
                new CellPainterMouseEventMatcher(GridRegion.BODY, MouseEventMatcher.LEFT_BUTTON, CheckBoxPainter.class),
				new CellEditDragMode());

	}

}
