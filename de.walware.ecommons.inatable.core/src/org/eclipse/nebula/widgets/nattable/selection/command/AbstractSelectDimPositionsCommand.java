package org.eclipse.nebula.widgets.nattable.selection.command;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.nebula.widgets.nattable.command.AbstractDimPositionsCommand;
import org.eclipse.nebula.widgets.nattable.command.LayerCommandUtil;
import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerDim;


/**
 * Abstract command to select column(s)/row(s).
 */
public abstract class AbstractSelectDimPositionsCommand extends AbstractDimPositionsCommand {
	
	
	private final int selectionFlags;
	
	private int positionToReveal;
	
	
	public AbstractSelectDimPositionsCommand(final Orientation orienation,
			final ILayer layer, final int position,
			final int selectionFlags) {
		this(orienation, layer, position,
				Collections.singletonList(new Range(position)),
				position, selectionFlags );
	}
	
	public AbstractSelectDimPositionsCommand(final Orientation orientation,
			final ILayer layer, final int refPosition, final Collection<Range> positions,
			final int positionToReveal, final int selectionFlags) {
		super(orientation, layer, refPosition, positions);
		
		this.positionToReveal = positionToReveal;
		this.selectionFlags = selectionFlags;
	}
	
	protected AbstractSelectDimPositionsCommand(final AbstractSelectDimPositionsCommand command) {
		super(command);
		
		this.positionToReveal = command.positionToReveal;
		this.selectionFlags = command.selectionFlags;
	}
	
	
	public int getSelectionFlags() {
		return this.selectionFlags;
	}
	
	public int getPositionToReveal() {
		return this.positionToReveal;
	}
	
	
	@Override
	protected boolean convertToTargetLayer(final ILayerDim dim, final int refPosition,
			final ILayerDim targetDim) {
		if (super.convertToTargetLayer(dim, refPosition, targetDim)) {
			if (this.positionToReveal != Integer.MIN_VALUE) {
				this.positionToReveal = (this.positionToReveal == refPosition) ?
						getRefPosition() :
						LayerCommandUtil.convertPositionToTargetContext(dim,
								refPosition, this.positionToReveal, targetDim );
			}
			return true;
		}
		return false;
	}
	
}
