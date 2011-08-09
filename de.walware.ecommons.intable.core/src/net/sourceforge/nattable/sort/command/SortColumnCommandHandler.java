package net.sourceforge.nattable.sort.command;

import net.sourceforge.nattable.command.AbstractLayerCommandHandler;
import net.sourceforge.nattable.sort.ISortModel;
import net.sourceforge.nattable.sort.SortDirectionEnum;
import net.sourceforge.nattable.sort.SortHeaderLayer;
import net.sourceforge.nattable.sort.event.SortColumnsEvent;

import org.eclipse.swt.custom.BusyIndicator;


/**
 * Handle sort commands
 */
public class SortColumnCommandHandler extends AbstractLayerCommandHandler<SortColumnCommand> {
	
	
	private final ISortModel sortModel;
	private final SortHeaderLayer<?> sortHeaderLayer;
	
	
	public SortColumnCommandHandler(final ISortModel sortModel,
			final SortHeaderLayer<?> sortHeaderLayer) {
		this.sortModel = sortModel;
		this.sortHeaderLayer = sortHeaderLayer;
	}
	
	
	public Class<SortColumnCommand> getCommandClass() {
		return SortColumnCommand.class;
	}
	
	@Override
	protected boolean doCommand(final SortColumnCommand command) {
		// with busy indicator
		Runnable sortRunner = new Runnable() {
			public void run() {
				final int columnIndex = command.getLayer().getColumnIndexByPosition(
						command.getColumnPosition());
				final SortDirectionEnum newSortDirection = (command.getDirection() != null) ?
						command.getDirection() : 
						sortModel.getSortDirection(columnIndex).getNextSortDirection();
				
				sortModel.sort(columnIndex, newSortDirection, command.isAccumulate());
				
				// Fire event
				SortColumnsEvent sortEvent = new SortColumnsEvent(sortHeaderLayer, command.getColumnPosition());
				sortHeaderLayer.fireLayerEvent(sortEvent);
			}
		};
		BusyIndicator.showWhile(null, sortRunner);
		
		return true;
	}
	
}
