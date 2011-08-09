package net.sourceforge.nattable.selection;

import java.util.Set;

import net.sourceforge.nattable.command.AbstractLayerCommandHandler;
import net.sourceforge.nattable.selection.command.ISelectionCommand.SelectionFlag;
import net.sourceforge.nattable.selection.command.SelectCellCommand;

import org.eclipse.swt.graphics.Rectangle;


public class SelectCellCommandHandler extends AbstractLayerCommandHandler<SelectCellCommand> {
	
	
	private final SelectionLayer selectionLayer;
	
	
	public SelectCellCommandHandler(SelectionLayer selectionLayer) {
		this.selectionLayer = selectionLayer;
	}
	
	
	public Class<SelectCellCommand> getCommandClass() {
		return SelectCellCommand.class;
	}
	
	protected boolean doCommand(SelectCellCommand command) {
		toggleOrSelectCell(command.getColumnPosition(), command.getRowPosition(), 
				command.getSelectionFlags(), command.isForcingEntireCellIntoViewport());
		selectionLayer.fireCellSelectionEvent(command.getColumnPosition(), command.getRowPosition(),
				command.isForcingEntireCellIntoViewport());
		return true;
	}
	
	/**
	 * Toggles the selection state of the given row and column.
	 * @return <code>false</code> if the cell was unselected.
	 */
	protected void toggleOrSelectCell(final int columnPosition, final int rowPosition,
			final Set<SelectionFlag> selectionFlags, final boolean moveIntoViewport) {
		if (selectionFlags.contains(SelectionFlag.KEEP_SELECTION)
				&& !selectionFlags.contains(SelectionFlag.RANGE_SELECTION)) {
			if (selectionLayer.isCellPositionSelected(columnPosition, rowPosition)) {
				selectionLayer.clearSelection(columnPosition, rowPosition);
				return;
			}
		}
		
		selectCell(columnPosition, rowPosition, selectionFlags);
	}
	
	/**
	 * Selects a cell, optionally clearing current selection
	 */
	protected void selectCell(final int columnPosition, final int rowPosition,
			final Set<SelectionFlag> selectionFlags) {
		final boolean keep = selectionFlags.contains(SelectionFlag.KEEP_SELECTION);
		final boolean range = selectionFlags.contains(SelectionFlag.RANGE_SELECTION);
		
		if (!keep && !range) {
			selectionLayer.clearSelections();
		}
		selectionLayer.lastSelectedCell.columnPosition = columnPosition;
		selectionLayer.lastSelectedCell.rowPosition = rowPosition;
		
		if (range && selectionLayer.lastSelectedRegion != null
				&& selectionLayer.selectionAnchor.columnPosition >= 0) {
			if (keep) {
				selectionLayer.lastSelectedRegion = new Rectangle(0, 0, 0, 0);
			}
			
			selectionLayer.lastSelectedRegion.x = Math.min(selectionLayer.selectionAnchor.columnPosition, columnPosition);
			selectionLayer.lastSelectedRegion.width = Math.abs(selectionLayer.selectionAnchor.columnPosition - columnPosition) + 1;
			selectionLayer.lastSelectedRegion.y = Math.min(selectionLayer.selectionAnchor.rowPosition, rowPosition);
			selectionLayer.lastSelectedRegion.height = Math.abs(selectionLayer.selectionAnchor.rowPosition - rowPosition) + 1;
			
			selectionLayer.addSelection(selectionLayer.lastSelectedRegion);
		}
		else {
			selectionLayer.addSelection(new Rectangle(columnPosition, rowPosition, 1, 1 ));
		}
	}
	
}
