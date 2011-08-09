package net.sourceforge.nattable.sort.event;

import java.util.Collection;

import net.sourceforge.nattable.coordinate.PositionUtil;
import net.sourceforge.nattable.coordinate.Range;
import net.sourceforge.nattable.layer.ILayer;
import net.sourceforge.nattable.layer.event.ColumnVisualChangeEvent;


public class SortColumnsEvent extends ColumnVisualChangeEvent {
	
	
	public SortColumnsEvent(final ILayer layer, final int columnPosition) {
		super(layer, new Range(columnPosition, columnPosition + 1));
	}
	
	public SortColumnsEvent(final ILayer layer, final Collection<Integer> columnPositions) {
		super(layer, PositionUtil.getRanges(columnPositions));
	}
	
	protected SortColumnsEvent(final SortColumnsEvent event) {
		super(event);
	}
	
	public SortColumnsEvent cloneEvent() {
		return new SortColumnsEvent(this);
	}
	
	
}
