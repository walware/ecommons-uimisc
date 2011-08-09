package net.sourceforge.nattable.sort.config;

import net.sourceforge.nattable.painter.cell.ICellPainter;
import net.sourceforge.nattable.sort.action.SortColumnAction;
import net.sourceforge.nattable.sort.event.ColumnHeaderClickEventMatcher;
import net.sourceforge.nattable.ui.binding.UiBindingRegistry;
import net.sourceforge.nattable.ui.matcher.MouseEventMatcher;

import org.eclipse.swt.SWT;

/**
 * Modifies the default sort configuration to sort on a <i>single left</i> <br/>
 * click on the column header.
 */
public class SingleClickSortConfiguration extends DefaultSortConfiguration {

	public SingleClickSortConfiguration() {
		super();
	}
	
	public SingleClickSortConfiguration(ICellPainter cellPainter) {
		super(cellPainter);
	}
	
	/**
	 * Remove the original key bindings and implement new ones.
	 */
	@Override
	public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
		// Register new bindings
		uiBindingRegistry.registerFirstSingleClickBinding(
              new ColumnHeaderClickEventMatcher(SWT.NONE, 1), new SortColumnAction(false));

		uiBindingRegistry.registerSingleClickBinding(
             MouseEventMatcher.columnHeaderLeftClick(SWT.ALT), new SortColumnAction(true));
	}

}
