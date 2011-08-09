package net.sourceforge.nattable.reorder.command;

import net.sourceforge.nattable.command.ILayerCommand;
import net.sourceforge.nattable.command.LayerCommandUtil;
import net.sourceforge.nattable.coordinate.ColumnPositionCoordinate;
import net.sourceforge.nattable.layer.ILayer;

public class ColumnReorderCommand implements ILayerCommand {
	
	private ColumnPositionCoordinate fromColumnPositionCoordinate;
	private ColumnPositionCoordinate toColumnPositionCoordinate;
	private boolean reorderToLeftEdge;
	
	public ColumnReorderCommand(ILayer layer, int fromColumnPosition, int toColumnPosition) {
		fromColumnPositionCoordinate = new ColumnPositionCoordinate(layer, fromColumnPosition);
		
		if (toColumnPosition < layer.getColumnCount()) {
 			reorderToLeftEdge = true;
		} else {
			reorderToLeftEdge = false;
			toColumnPosition--;
		}
		
		toColumnPositionCoordinate = new ColumnPositionCoordinate(layer, toColumnPosition);
	}
	
	protected ColumnReorderCommand(ColumnReorderCommand command) {
		this.fromColumnPositionCoordinate = command.fromColumnPositionCoordinate;
		this.toColumnPositionCoordinate = command.toColumnPositionCoordinate;
		this.reorderToLeftEdge = command.reorderToLeftEdge;
	}
	
	public int getFromColumnPosition() {
		return fromColumnPositionCoordinate.getColumnPosition();
	}
	
	public int getToColumnPosition() {
		return toColumnPositionCoordinate.getColumnPosition();
	}
	
	public boolean isReorderToLeftEdge() {
		return reorderToLeftEdge;
	}
	
	public boolean convertToTargetLayer(ILayer targetLayer) {
		fromColumnPositionCoordinate = LayerCommandUtil.convertColumnPositionToTargetContext(fromColumnPositionCoordinate, targetLayer);
		toColumnPositionCoordinate = LayerCommandUtil.convertColumnPositionToTargetContext(toColumnPositionCoordinate, targetLayer);
		return fromColumnPositionCoordinate != null && toColumnPositionCoordinate != null;
	}
	
	public ColumnReorderCommand cloneCommand() {
		return new ColumnReorderCommand(this);
	}
	
}
