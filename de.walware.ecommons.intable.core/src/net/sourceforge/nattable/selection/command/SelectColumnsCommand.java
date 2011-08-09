package net.sourceforge.nattable.selection.command;

import java.util.Collection;
import java.util.Set;

import net.sourceforge.nattable.command.AbstractMultiColumnCommand;
import net.sourceforge.nattable.command.LayerCommandUtil;
import net.sourceforge.nattable.coordinate.RowPositionCoordinate;
import net.sourceforge.nattable.layer.ILayer;


public class SelectColumnsCommand extends AbstractMultiColumnCommand implements ISelectionCommand {
	
	
	private final Set<SelectionFlag> selectionFlags;
	
	private RowPositionCoordinate rowPositionCoordinate;
	
	
	public SelectColumnsCommand(final ILayer layer, final Collection<Integer> columnPositions, final int rowPosition,
			final Set<SelectionFlag> selectionFlags) {
		super(layer, columnPositions);
		
		this.selectionFlags = selectionFlags;
		this.rowPositionCoordinate = new RowPositionCoordinate(layer, rowPosition);
	}
	
	public SelectColumnsCommand(final ILayer layer, final int columnPosition, final int rowPosition,
			final Set<SelectionFlag> selectionFlags) {
		super(layer, columnPosition);
		
		this.selectionFlags = selectionFlags;
		this.rowPositionCoordinate = new RowPositionCoordinate(layer, rowPosition);
	}
	
	protected SelectColumnsCommand(SelectColumnsCommand command) {
		super(command);
		
		this.selectionFlags = command.selectionFlags;
		this.rowPositionCoordinate = command.rowPositionCoordinate;
	}
	
	public SelectColumnsCommand cloneCommand() {
		return new SelectColumnsCommand(this);
	}
	
	
	@Override
	public boolean convertToTargetLayer(ILayer targetLayer) {
		if (super.convertToTargetLayer(targetLayer)) {
			this.rowPositionCoordinate = LayerCommandUtil.convertRowPositionToTargetContext(
					rowPositionCoordinate, targetLayer);
			return (this.rowPositionCoordinate != null);
		}
		return false;
	}
	
	
	public int getRowPosition() {
		return rowPositionCoordinate.rowPosition;
	}
	
	public Set<SelectionFlag> getSelectionFlags() {
		return selectionFlags;
	}
	
}
