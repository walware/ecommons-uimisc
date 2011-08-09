package net.sourceforge.nattable.selection.event;

import net.sourceforge.nattable.layer.event.CellVisualChangeEvent;
import net.sourceforge.nattable.selection.SelectionLayer;

public class CellSelectionEvent extends CellVisualChangeEvent implements ISelectionEvent {

	private final SelectionLayer selectionLayer;
	private boolean forcingEntireCellIntoViewport = false;


	public CellSelectionEvent(SelectionLayer selectionLayer, int columnPosition, int rowPosition,
			boolean forcingEntireCellIntoViewport) {
		super(selectionLayer, columnPosition, rowPosition);
		this.selectionLayer = selectionLayer;
		this.forcingEntireCellIntoViewport = forcingEntireCellIntoViewport;
	}

	// Copy constructor
	protected CellSelectionEvent(CellSelectionEvent event) {
		super(event);
		this.selectionLayer = event.selectionLayer;
		this.forcingEntireCellIntoViewport = event.forcingEntireCellIntoViewport;
	}

	public SelectionLayer getSelectionLayer() {
		return selectionLayer;
	}

	public boolean isForcingEntireCellIntoViewport() {
		return forcingEntireCellIntoViewport;
	}

	@Override
	public CellSelectionEvent cloneEvent() {
		return new CellSelectionEvent(this);
	}

}