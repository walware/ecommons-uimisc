package net.sourceforge.nattable.sort.command;

import net.sourceforge.nattable.command.AbstractColumnCommand;
import net.sourceforge.nattable.layer.ILayer;
import net.sourceforge.nattable.sort.SortDirectionEnum;

public class SortColumnCommand extends AbstractColumnCommand {
	
	
	private boolean accumulate;
	
	private SortDirectionEnum direction;
	
	
	public SortColumnCommand(final ILayer layer, final int columnPosition,
			final boolean accumulate) {
		super(layer, columnPosition);
		this.accumulate = accumulate;
	}
	
	/**
	 * 
	 * @param layer the initial layer
	 * @param columnPosition the column position in the layer
	 * @param direction the sort direction or <code>null</code> for automatic iteration.
	 * @param accumulate
	 */
	public SortColumnCommand(final ILayer layer, final int columnPosition,
			final SortDirectionEnum direction, boolean accumulate) {
		super(layer, columnPosition);
		this.direction = direction;
		this.accumulate = accumulate;
	}
	
	protected SortColumnCommand(SortColumnCommand command) {
		super(command);
		this.accumulate = command.accumulate;
		this.direction = command.direction;
	}
	
	public SortColumnCommand cloneCommand() {
		return new SortColumnCommand(this);
	}
	
	
	public boolean isAccumulate() {
		return accumulate;
	}
	
	/**
	 * The sort direction, if specified.
	 * 
	 * @return the sort direction or <code>null</code> for automatic iteration
	 */
	public SortDirectionEnum getDirection() {
		return direction;
	}
	
}
