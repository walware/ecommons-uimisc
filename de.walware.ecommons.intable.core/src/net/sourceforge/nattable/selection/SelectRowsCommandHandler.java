package net.sourceforge.nattable.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.nattable.command.AbstractLayerCommandHandler;
import net.sourceforge.nattable.coordinate.Range;
import net.sourceforge.nattable.selection.command.ISelectionCommand.SelectionFlag;
import net.sourceforge.nattable.selection.command.SelectRowsCommand;
import net.sourceforge.nattable.selection.event.RowSelectionEvent;

import org.eclipse.swt.graphics.Rectangle;


public class SelectRowsCommandHandler extends AbstractLayerCommandHandler<SelectRowsCommand> {
	
	
	private final SelectionLayer selectionLayer;
	
	
	public SelectRowsCommandHandler(SelectionLayer selectionLayer) {
		this.selectionLayer = selectionLayer;
	}
	
	
	public Class<SelectRowsCommand> getCommandClass() {
		return SelectRowsCommand.class;
	}
	
	protected boolean doCommand(SelectRowsCommand command) {
		toggleOrSelectRows(command.getColumnPosition(), command.getRowPositions(),
				command.getSelectionFlags(), command.getRowPositionToMoveIntoViewport() );
		return true;
	}
	
	protected void toggleOrSelectRows(final int columnPosition, final Collection<Integer> rowPositions,
			final Set<SelectionFlag> selectionFlags, final int rowPositionToMoveIntoViewport) {
		int singleRowPosition;
		if (selectionFlags.contains(SelectionFlag.KEEP_SELECTION)
				&& !selectionFlags.contains(SelectionFlag.RANGE_SELECTION)
				&& rowPositions.size() == 1
				&& selectionLayer.isRowFullySelected(
						singleRowPosition = rowPositions.iterator().next()) ) {
			final Rectangle columnRegion = new Rectangle(
					0, singleRowPosition, selectionLayer.getColumnCount(), 1);
			selectionLayer.clearSelection(columnRegion);
			selectionLayer.fireLayerEvent(new RowSelectionEvent(selectionLayer, singleRowPosition, false));
			return;
		}
		selectRows(columnPosition, rowPositions, selectionFlags, rowPositionToMoveIntoViewport);
	}
	
	protected void selectRows(final int columnPosition, Collection<Integer> rowPositions,
			final Set<SelectionFlag> selectionFlags, final int rowPositionToMoveIntoViewport) {
		final boolean keep = selectionFlags.contains(SelectionFlag.KEEP_SELECTION);
		final boolean range = selectionFlags.contains(SelectionFlag.RANGE_SELECTION);
		
		if (!(rowPositions instanceof Set)) {
			rowPositions = new HashSet<Integer>(rowPositions);
		}
		
		final Set<Range> changedRowRanges = new HashSet<Range>();
		
		int rowPosition = -1;
		if (!keep && !range) {
			changedRowRanges.addAll(selectionLayer.getSelectedRows());
			selectionLayer.clearSelections();
		}
		if (rowPositions.isEmpty() || (range && rowPositions.size() > 1)) {
		}
		else if (range && selectionLayer.lastSelectedRegion != null
				&& selectionLayer.selectionAnchor.columnPosition >= 0) {
			if (keep) {
				selectionLayer.lastSelectedRegion = new Rectangle(0, 0, 0, 0);
			}
			
			rowPosition = rowPositions.iterator().next();
			selectionLayer.lastSelectedRegion.x = 0;
			selectionLayer.lastSelectedRegion.width = selectionLayer.getColumnCount();
			selectionLayer.lastSelectedRegion.y = Math.min(selectionLayer.selectionAnchor.rowPosition, rowPosition);
			selectionLayer.lastSelectedRegion.height = Math.abs(selectionLayer.selectionAnchor.rowPosition - rowPosition) + 1;
			
			selectionLayer.addSelection(selectionLayer.lastSelectedRegion);
			changedRowRanges.add(new Range(selectionLayer.lastSelectedRegion.y,
					selectionLayer.lastSelectedRegion.y + selectionLayer.lastSelectedRegion.height ));
		}
		else {
			for (Iterator<Integer> iterator = rowPositions.iterator(); iterator.hasNext();) {
				rowPosition = iterator.next();
				changedRowRanges.add(new Range(rowPosition, rowPosition + 1));
				selectionLayer.addSelection(new Rectangle(0, rowPosition,
						selectionLayer.getColumnCount(), 1 ));
			}
			
			selectionLayer.selectionAnchor.columnPosition = columnPosition;
			selectionLayer.selectionAnchor.rowPosition = rowPosition;
		}
		
		if (rowPosition >= 0) {
			selectionLayer.lastSelectedCell.columnPosition = selectionLayer.getColumnCount() - 1;
			selectionLayer.lastSelectedCell.rowPosition = rowPosition;
		}
		
		final List<Integer> changedRowPositions = new ArrayList<Integer>(changedRowRanges.size());
		for (Range rowRange : changedRowRanges) {
			for (int i = rowRange.start; i < rowRange.end; i++) {
				changedRowPositions.add(Integer.valueOf(i));
			}
		}
		selectionLayer.fireLayerEvent(new RowSelectionEvent(selectionLayer, changedRowPositions, rowPositionToMoveIntoViewport));
	}
	
}
