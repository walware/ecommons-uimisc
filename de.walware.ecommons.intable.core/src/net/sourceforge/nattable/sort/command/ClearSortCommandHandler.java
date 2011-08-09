package net.sourceforge.nattable.sort.command;

import java.util.ArrayList;
import java.util.Collection;

import net.sourceforge.nattable.command.AbstractLayerCommandHandler;
import net.sourceforge.nattable.sort.ISortModel;
import net.sourceforge.nattable.sort.SortHeaderLayer;
import net.sourceforge.nattable.sort.event.SortColumnsEvent;

import org.eclipse.swt.custom.BusyIndicator;


public class ClearSortCommandHandler extends AbstractLayerCommandHandler<ClearSortCommand> {
	
	
	private final ISortModel sortModel;
	private final SortHeaderLayer<?> sortHeaderLayer;
	
	
	public ClearSortCommandHandler(ISortModel sortModel, SortHeaderLayer<?> sortHeaderLayer) {
		this.sortModel = sortModel;
		this.sortHeaderLayer = sortHeaderLayer;
	}
	
	
	public Class<ClearSortCommand> getCommandClass() {
		return ClearSortCommand.class;
	}
	
	@Override
	protected boolean doCommand(ClearSortCommand command) {
		// with busy indicator
		Runnable sortRunner = new Runnable() {
			public void run() {
				// collect sorted columns for event
				final int columnCount = sortHeaderLayer.getColumnCount();
				Collection<Integer> sortedColumns = new ArrayList<Integer>();
				for (int i = 0; i < columnCount; i++) {
					if (sortModel.isColumnIndexSorted(i)) {
						sortedColumns.add(Integer.valueOf(i));
					}
				}
				
				sortModel.clear();
				
				// Fire event
				SortColumnsEvent sortEvent = new SortColumnsEvent(sortHeaderLayer, sortedColumns);
				sortHeaderLayer.fireLayerEvent(sortEvent);
			}
		};
		BusyIndicator.showWhile(null, sortRunner);
		
		return true;
	}
	
}
