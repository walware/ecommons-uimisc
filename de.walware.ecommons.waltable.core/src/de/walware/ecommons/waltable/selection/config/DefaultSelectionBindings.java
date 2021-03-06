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

import de.walware.ecommons.waltable.config.AbstractUiBindingConfiguration;
import de.walware.ecommons.waltable.copy.action.CopyDataAction;
import de.walware.ecommons.waltable.selection.action.CellSelectionDragMode;
import de.walware.ecommons.waltable.selection.action.SelectAllAction;
import de.walware.ecommons.waltable.selection.action.SelectCellAction;
import de.walware.ecommons.waltable.ui.action.IMouseAction;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;
import de.walware.ecommons.waltable.ui.matcher.KeyEventMatcher;
import de.walware.ecommons.waltable.ui.matcher.MouseEventMatcher;

public class DefaultSelectionBindings extends AbstractUiBindingConfiguration {
	
	
	@Override
	public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
		// Move up
//		configureMoveUpBindings(uiBindingRegistry, new MoveSelectionAction(Direction.UP));

		// Move down
//		configureMoveDownBindings(uiBindingRegistry, new MoveSelectionAction(Direction.DOWN));

		// Move left
//		configureMoveLeftBindings(uiBindingRegistry, new MoveSelectionAction(Direction.LEFT));

		// Move right
//		configureMoveRightBindings(uiBindingRegistry, new MoveSelectionAction(Direction.RIGHT));

		// Page Up
//		configurePageUpButtonBindings(uiBindingRegistry, new PageUpAction());

		// Page down
//		configurePageDownButtonBindings(uiBindingRegistry, new PageDownAction());

		// Home - Move to first column
//		configureHomeButtonBindings(uiBindingRegistry, new MoveToFirstColumnAction());

		// End - Move to last column
//		configureEndButtonBindings(uiBindingRegistry, new MoveToLastColumnAction());

		// Select all
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD1, 'a'), new SelectAllAction());

		// Copy
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD1, 'c'), new CopyDataAction());

		// Mouse bindings - select Cell
		configureBodyMouseClickBindings(uiBindingRegistry);

		// Mouse bindings - select columns
//		configureColumnHeaderMouseClickBindings(uiBindingRegistry);

		// Mouse bindings - select rows
//		configureRowHeaderMouseClickBindings(uiBindingRegistry);

		// Mouse bindings - Drag
		configureBodyMouseDragMode(uiBindingRegistry);
	}

	// *** pg. up, pg. down, home, end keys selection bindings ***

//	protected void configureEndButtonBindings(UiBindingRegistry uiBindingRegistry, IKeyAction action) {
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.NONE, SWT.END), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2, SWT.END), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD1, SWT.END), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2 | SWT.MOD1, SWT.END), action);
//	}

//	protected void configureHomeButtonBindings(UiBindingRegistry uiBindingRegistry, IKeyAction action) {
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.NONE, SWT.HOME), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2, SWT.HOME), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD1, SWT.HOME), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2 | SWT.MOD1, SWT.HOME), action);
//	}

//	protected void configurePageDownButtonBindings(UiBindingRegistry uiBindingRegistry, IKeyAction action) {
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.NONE, SWT.PAGE_DOWN), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2, SWT.PAGE_DOWN), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD1, SWT.PAGE_DOWN), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2 | SWT.MOD1, SWT.PAGE_DOWN), action);
//	}

//	protected void configurePageUpButtonBindings(UiBindingRegistry uiBindingRegistry, PageUpAction action) {
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.NONE, SWT.PAGE_UP), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2, SWT.PAGE_UP), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD1, SWT.PAGE_UP), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2 | SWT.MOD1, SWT.PAGE_UP), action);
//	}

	// *** Arrow keys selection bindings ***

//	protected void configureMoveRightBindings(UiBindingRegistry uiBindingRegistry, IKeyAction action) {
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.NONE, SWT.ARROW_RIGHT), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2, SWT.ARROW_RIGHT), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD1, SWT.ARROW_RIGHT), new MoveToLastColumnAction());
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2 | SWT.MOD1, SWT.ARROW_RIGHT), new MoveToLastColumnAction());
//
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.NONE, SWT.TAB), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD1, SWT.TAB), action);
//	}

//	protected void configureMoveLeftBindings(UiBindingRegistry uiBindingRegistry, IKeyAction action) {
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.NONE, SWT.ARROW_LEFT), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2, SWT.ARROW_LEFT), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD1, SWT.ARROW_LEFT), new MoveToFirstColumnAction());
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2 | SWT.MOD1, SWT.ARROW_LEFT), new MoveToFirstColumnAction());
//
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2, SWT.TAB), new MoveSelectionAction(Direction.LEFT, false, false));
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2 | SWT.MOD1, SWT.TAB), action);
//	}

//	protected void configureMoveDownBindings(UiBindingRegistry uiBindingRegistry, IKeyAction action) {
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.NONE, SWT.ARROW_DOWN), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2, SWT.ARROW_DOWN), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD1, SWT.ARROW_DOWN), new MoveToLastRowAction());
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2 | SWT.MOD1, SWT.ARROW_DOWN), new MoveToLastRowAction());
//
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.NONE, SWT.CR), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD1, SWT.CR), action);
//	}

//	protected void configureMoveUpBindings(UiBindingRegistry uiBindingRegistry, IKeyAction action) {
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.NONE, SWT.ARROW_UP), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2, SWT.ARROW_UP), action);
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD1, SWT.ARROW_UP), new MoveToFirstRowAction());
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2 | SWT.MOD1, SWT.ARROW_UP), new MoveToFirstRowAction());
//
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2, SWT.CR), new MoveSelectionAction(Direction.UP, false, false));
//		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2 | SWT.MOD1, SWT.CR), action);
//	}

	// *** Mouse bindings ***

	protected void configureBodyMouseClickBindings(final UiBindingRegistry uiBindingRegistry) {
		final IMouseAction action= new SelectCellAction();
		uiBindingRegistry.registerMouseDownBinding(MouseEventMatcher.bodyLeftClick(SWT.NONE), action);
		uiBindingRegistry.registerMouseDownBinding(MouseEventMatcher.bodyLeftClick(SWT.MOD2), action);
		uiBindingRegistry.registerMouseDownBinding(MouseEventMatcher.bodyLeftClick(SWT.MOD1), action);
		uiBindingRegistry.registerMouseDownBinding(MouseEventMatcher.bodyLeftClick(SWT.MOD2 | SWT.MOD1), action);
	}

//	protected void configureColumnHeaderMouseClickBindings(UiBindingRegistry uiBindingRegistry) {
//		uiBindingRegistry.registerSingleClickBinding(MouseEventMatcher.columnHeaderLeftClick(SWT.NONE), new ViewportSelectColumnAction(false, false));
//		uiBindingRegistry.registerSingleClickBinding(MouseEventMatcher.columnHeaderLeftClick(SWT.MOD2), new ViewportSelectColumnAction(true, false));
//		uiBindingRegistry.registerSingleClickBinding(MouseEventMatcher.columnHeaderLeftClick(SWT.MOD1), new ViewportSelectColumnAction(false, true));
//		uiBindingRegistry.registerSingleClickBinding(MouseEventMatcher.columnHeaderLeftClick(SWT.MOD2 | SWT.MOD1), new ViewportSelectColumnAction(true, true));
//	}

//	protected void configureRowHeaderMouseClickBindings(UiBindingRegistry uiBindingRegistry) {
//		uiBindingRegistry.registerMouseDownBinding(MouseEventMatcher.rowHeaderLeftClick(SWT.NONE), new ViewportSelectRowAction(false, false));
//		uiBindingRegistry.registerMouseDownBinding(MouseEventMatcher.rowHeaderLeftClick(SWT.MOD2), new ViewportSelectRowAction(true, false));
//		uiBindingRegistry.registerMouseDownBinding(MouseEventMatcher.rowHeaderLeftClick(SWT.MOD1), new ViewportSelectRowAction(false, true));
//		uiBindingRegistry.registerMouseDownBinding(MouseEventMatcher.rowHeaderLeftClick(SWT.MOD2 | SWT.MOD1), new ViewportSelectRowAction(true, true));
//	}

	protected void configureBodyMouseDragMode(final UiBindingRegistry uiBindingRegistry) {
		final CellSelectionDragMode dragMode= new CellSelectionDragMode();
		uiBindingRegistry.registerMouseDragMode(MouseEventMatcher.bodyLeftClick(SWT.NONE), dragMode);
		uiBindingRegistry.registerMouseDragMode(MouseEventMatcher.bodyLeftClick(SWT.MOD2), dragMode);
		uiBindingRegistry.registerMouseDragMode(MouseEventMatcher.bodyLeftClick(SWT.MOD1), dragMode);
		uiBindingRegistry.registerMouseDragMode(MouseEventMatcher.bodyLeftClick(SWT.MOD2 | SWT.MOD1), dragMode);
	}

}
