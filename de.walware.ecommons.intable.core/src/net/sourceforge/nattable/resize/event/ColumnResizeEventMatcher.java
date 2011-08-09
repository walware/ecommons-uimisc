package net.sourceforge.nattable.resize.event;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.grid.GridRegion;
import net.sourceforge.nattable.layer.ILayer;
import net.sourceforge.nattable.layer.LabelStack;
import net.sourceforge.nattable.ui.matcher.IMouseEventMatcher;
import net.sourceforge.nattable.ui.util.CellEdgeDetectUtil;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;


public class ColumnResizeEventMatcher implements IMouseEventMatcher {
	
	
	private final int button;
	
	private final boolean allowRowResize;
	
	
	public ColumnResizeEventMatcher(int button, boolean allowRowResize) {
		this.button = button;
		this.allowRowResize = allowRowResize;
	}
	
	
	public boolean matches(NatTable natTable, MouseEvent event, LabelStack regionLabels) {
		return (event.stateMask == 0
				&& event.button == button
				&& regionLabels != null
				&& (regionLabels.hasLabel(GridRegion.COLUMN_HEADER)
						|| (allowRowResize && regionLabels.hasLabel(GridRegion.CORNER)) )
				&& isColumnResizable(natTable, event));
	}
	
	private boolean isColumnResizable(ILayer natLayer, MouseEvent event) {
		int columnPosition = 
			CellEdgeDetectUtil.getColumnPositionToResize(natLayer, new Point(event.x, event.y));
		
		if (columnPosition < 0) {
			return false;
		} else {
			return natLayer.isColumnPositionResizable(columnPosition);
		}
	}
	
}
