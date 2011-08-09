package net.sourceforge.nattable.selection.event;

import java.util.Collection;

import net.sourceforge.nattable.coordinate.PositionUtil;
import net.sourceforge.nattable.coordinate.Range;
import net.sourceforge.nattable.layer.ILayer;
import net.sourceforge.nattable.layer.event.RowVisualChangeEvent;
import net.sourceforge.nattable.selection.SelectionLayer;


public class RowSelectionEvent extends RowVisualChangeEvent implements ISelectionEvent {
	
	
	private final SelectionLayer selectionLayer;
	private int rowPositionToMoveIntoViewport;
	
	
	public RowSelectionEvent(final SelectionLayer selectionLayer,
			final Collection<Integer> rowPositions, final int rowPositionToMoveIntoViewport) {
		super(selectionLayer, PositionUtil.getRanges(rowPositions));
		this.selectionLayer = selectionLayer;
		this.rowPositionToMoveIntoViewport = rowPositionToMoveIntoViewport;
	}
	
	public RowSelectionEvent(final SelectionLayer selectionLayer,
			final int rowPosition, final boolean moveIntoViewPort) {
		super(selectionLayer, new Range(rowPosition, rowPosition+1));
		this.selectionLayer = selectionLayer;
		this.rowPositionToMoveIntoViewport = (moveIntoViewPort) ? rowPosition : -1;
	}
	
	protected RowSelectionEvent(RowSelectionEvent event) {
		super(event);
		this.selectionLayer = event.selectionLayer;
		this.rowPositionToMoveIntoViewport = event.rowPositionToMoveIntoViewport;
	}
	
	public RowSelectionEvent cloneEvent() {
		return new RowSelectionEvent(this);
	}
	
	
	public SelectionLayer getSelectionLayer() {
		return selectionLayer;
	}
	
	public int getRowPositionToMoveIntoViewport() {
		return rowPositionToMoveIntoViewport;
	}
	
	@Override
	public boolean convertToLocal(ILayer localLayer) {
		rowPositionToMoveIntoViewport = localLayer.underlyingToLocalRowPosition(getLayer(), rowPositionToMoveIntoViewport);
		
		return super.convertToLocal(localLayer);
	}
	
}
