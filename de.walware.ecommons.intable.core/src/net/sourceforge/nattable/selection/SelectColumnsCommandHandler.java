package net.sourceforge.nattable.selection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.nattable.command.AbstractLayerCommandHandler;
import net.sourceforge.nattable.selection.command.ISelectionCommand.SelectionFlag;
import net.sourceforge.nattable.selection.command.SelectColumnsCommand;
import net.sourceforge.nattable.selection.event.ColumnSelectionEvent;

import org.eclipse.swt.graphics.Rectangle;


public class SelectColumnsCommandHandler extends AbstractLayerCommandHandler<SelectColumnsCommand> {
	
	
	private final SelectionLayer selectionLayer;
	
	
	public SelectColumnsCommandHandler(SelectionLayer selectionLayer) {
		this.selectionLayer = selectionLayer;
	}
	
	
	public Class<SelectColumnsCommand> getCommandClass() {
		return SelectColumnsCommand.class;
	}
	
	protected boolean doCommand(final SelectColumnsCommand command) {
		toggleOrSelectColumn(command.getColumnPositions(), command.getRowPosition(),
				command.getSelectionFlags() );
		return true;
	}
	
	protected void toggleOrSelectColumn(Collection<Integer> columnPositions, int rowPosition,
			final Set<SelectionFlag> selectionFlags) {
		int singleColumnPosition;
		if (selectionFlags.contains(SelectionFlag.KEEP_SELECTION)
				&& !selectionFlags.contains(SelectionFlag.RANGE_SELECTION)
				&& columnPositions.size() == 1
				&& selectionLayer.isColumnFullySelected(
						singleColumnPosition = columnPositions.iterator().next()) ) {
			final Rectangle columnRegion = new Rectangle(
					singleColumnPosition, 0, 1, selectionLayer.getRowCount());
			selectionLayer.clearSelection(columnRegion);
			selectionLayer.fireLayerEvent(new ColumnSelectionEvent(selectionLayer, singleColumnPosition));
			return;
		}
		selectColumn(columnPositions, rowPosition, selectionFlags);
	}
	
	protected void selectColumn(Collection<Integer> columnPositions, int rowPosition,
			final Set<SelectionFlag> selectionFlags) {
		final boolean keep = selectionFlags.contains(SelectionFlag.KEEP_SELECTION);
		final boolean range = selectionFlags.contains(SelectionFlag.RANGE_SELECTION);
		
		int columnPosition = -1;
		if (!keep && !range) {
			selectionLayer.clearSelections();
		}
		if (columnPositions.isEmpty() || (range && columnPositions.size() > 1)) {
		}
		if (range && selectionLayer.lastSelectedRegion != null
				&& selectionLayer.selectionAnchor.columnPosition >= 0) {
			if (keep) {
				selectionLayer.lastSelectedRegion = new Rectangle(0, 0, 0, 0);
			}
			
			columnPosition = columnPositions.iterator().next();
			selectionLayer.lastSelectedRegion.x = Math.min(selectionLayer.selectionAnchor.columnPosition, columnPosition);
			selectionLayer.lastSelectedRegion.width = Math.abs(selectionLayer.selectionAnchor.columnPosition - columnPosition) + 1;
			selectionLayer.lastSelectedRegion.y = 0;
			selectionLayer.lastSelectedRegion.height = selectionLayer.getRowCount();
			
			selectionLayer.addSelection(selectionLayer.lastSelectedRegion);
		}
		else {
			for (Iterator<Integer> iterator = columnPositions.iterator(); iterator.hasNext();) {
				columnPosition = iterator.next();
				selectionLayer.addSelection(new Rectangle(columnPosition, 0,
						1, selectionLayer.getRowCount() ));
			}
			
			selectionLayer.selectionAnchor.columnPosition = columnPosition;
			selectionLayer.selectionAnchor.rowPosition = rowPosition;
		}
		
		if (columnPosition >= 0) {
			selectionLayer.lastSelectedCell.columnPosition = columnPosition;
			selectionLayer.lastSelectedCell.rowPosition = selectionLayer.getRowCount() - 1;
		}
		
		// TODO orrect change set
		selectionLayer.fireLayerEvent(new ColumnSelectionEvent(selectionLayer, columnPosition));
	}
	
}
