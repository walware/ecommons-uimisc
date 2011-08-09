package net.sourceforge.nattable.selection.command;

import java.util.Set;

import net.sourceforge.nattable.command.AbstractMultiRowCommand;
import net.sourceforge.nattable.command.LayerCommandUtil;
import net.sourceforge.nattable.coordinate.ColumnPositionCoordinate;
import net.sourceforge.nattable.coordinate.RowPositionCoordinate;
import net.sourceforge.nattable.layer.ILayer;
import net.sourceforge.nattable.util.ArrayUtil;


public class SelectRowsCommand extends AbstractMultiRowCommand implements ISelectionCommand {
	
	
	private ColumnPositionCoordinate columnPositionCoordinate;
	
	private RowPositionCoordinate rowPositionCoordinateToMoveIntoViewport;
	
	private final Set<SelectionFlag> selectionFlags;
	
	
	public SelectRowsCommand(ILayer layer, int columnPosition, int rowPosition,
			final Set<SelectionFlag> selectionFlags) {
		this(layer, columnPosition, ArrayUtil.asIntArray(rowPosition), rowPosition, selectionFlags);
	}
	
	public SelectRowsCommand(final ILayer layer, final int columnPosition, final int[] rowPositions,
			final int rowPositionToMoveIntoViewport, final Set<SelectionFlag> selectionFlags) {
		super(layer, rowPositions);
		columnPositionCoordinate = new ColumnPositionCoordinate(layer, columnPosition);
		rowPositionCoordinateToMoveIntoViewport = new RowPositionCoordinate(layer, rowPositionToMoveIntoViewport);
		this.selectionFlags = selectionFlags;
	}
	
	protected SelectRowsCommand(SelectRowsCommand command) {
		super(command);
		columnPositionCoordinate = command.columnPositionCoordinate;
		rowPositionCoordinateToMoveIntoViewport = command.rowPositionCoordinateToMoveIntoViewport;
		selectionFlags = command.selectionFlags;
	}
	
	public SelectRowsCommand cloneCommand() {
		return new SelectRowsCommand(this);
	}
	
	
	@Override
	public boolean convertToTargetLayer(ILayer targetLayer) {
		super.convertToTargetLayer(targetLayer);
		
		columnPositionCoordinate = LayerCommandUtil.convertColumnPositionToTargetContext(columnPositionCoordinate, targetLayer);
		rowPositionCoordinateToMoveIntoViewport = LayerCommandUtil.convertRowPositionToTargetContext(rowPositionCoordinateToMoveIntoViewport, targetLayer);
		
		return (columnPositionCoordinate != null && columnPositionCoordinate.getColumnPosition() >= 0);
	}
	
	public int getColumnPosition() {
		return columnPositionCoordinate.getColumnPosition();
	}
	
	public int getRowPositionToMoveIntoViewport() {
		if (rowPositionCoordinateToMoveIntoViewport != null) {
			return rowPositionCoordinateToMoveIntoViewport.getRowPosition();
		} else {
			return -1;
		}
	}
	
	public Set<SelectionFlag> getSelectionFlags() {
		return selectionFlags;
	}
	
}
