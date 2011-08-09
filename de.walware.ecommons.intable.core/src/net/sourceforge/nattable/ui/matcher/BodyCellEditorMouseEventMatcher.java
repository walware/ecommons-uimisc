package net.sourceforge.nattable.ui.matcher;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.edit.EditConfigAttributes;
import net.sourceforge.nattable.edit.editor.ICellEditor;
import net.sourceforge.nattable.grid.GridRegion;
import net.sourceforge.nattable.layer.LabelStack;
import net.sourceforge.nattable.layer.cell.LayerCell;

import org.eclipse.swt.events.MouseEvent;

public class BodyCellEditorMouseEventMatcher implements IMouseEventMatcher {
	
	
	private final Class<?> cellEditorClass;
	
	
	public BodyCellEditorMouseEventMatcher(final Class<?> cellEditorClass) {
		if (cellEditorClass == null) {
			throw new NullPointerException();
		}
		this.cellEditorClass = cellEditorClass;
	}
	
	
	public boolean matches(final NatTable natTable, final MouseEvent event,
			final LabelStack regionLabels) {
		if (regionLabels != null && regionLabels.hasLabel(GridRegion.BODY)) {
			final LayerCell cell = natTable.getCellByPosition(
					natTable.getColumnPositionByX(event.x),
					natTable.getRowPositionByY(event.y) );
			
			final ICellEditor cellEditor = natTable.getConfigRegistry().getConfigAttribute(
					EditConfigAttributes.CELL_EDITOR,
					cell.getDisplayMode(), cell.getConfigLabels().getLabels() );
			if (cellEditorClass.isInstance(cellEditor)) {
				return true;
			}
		}
		return false;
	}
	
	
	@Override
	public int hashCode() {
		return cellEditorClass.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BodyCellEditorMouseEventMatcher)) {
			return false;
		}
		final BodyCellEditorMouseEventMatcher other = (BodyCellEditorMouseEventMatcher) obj;
		return (cellEditorClass == other.cellEditorClass);
	}
	
}
