package net.sourceforge.nattable.resize.config;

import net.sourceforge.nattable.config.AbstractUiBindingConfiguration;
import net.sourceforge.nattable.ui.binding.UiBindingRegistry;

public class DefaultColumnResizeBindings extends AbstractUiBindingConfiguration {

	public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
		// Mouse move - Show resize cursor
//		uiBindingRegistry.registerFirstMouseMoveBinding(new ColumnResizeEventMatcher(SWT.NONE, 0), new ColumnResizeCursorAction());
//		uiBindingRegistry.registerMouseMoveBinding(new MouseEventMatcher(), new ClearCursorAction());
		
		// Column resize
//		uiBindingRegistry.registerFirstMouseDragMode(new ColumnResizeEventMatcher(SWT.NONE, 1), new ColumnResizeDragMode());
		
//		uiBindingRegistry.registerDoubleClickBinding(new ColumnResizeEventMatcher(SWT.NONE, 1), new AutoResizeColumnAction());
//		uiBindingRegistry.registerSingleClickBinding(new ColumnResizeEventMatcher(SWT.NONE, 1), new NoOpMouseAction());
	}
	
}
