package net.sourceforge.nattable.resize.command;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.command.AbstractColumnCommand;
import net.sourceforge.nattable.command.ILayerCommand;
import net.sourceforge.nattable.grid.layer.GridLayer;
import net.sourceforge.nattable.layer.ILayer;
import net.sourceforge.nattable.selection.SelectionLayer;

/**
 * This command triggers the AutoResizeColumms command. It collects the selected
 * columns from the {@link SelectionLayer} and fires the
 * {@link AutoResizeColumnsCommand} on the {@link GridLayer}
 */

public class InitializeAutoResizeColumnsCommand extends AbstractColumnCommand {
	
	
	private final ILayer sourceLayer;
	private int[] selectedColumnPositions = new int[0];
	
	private final NatTable natTable;
	
	
	public InitializeAutoResizeColumnsCommand(ILayer layer, int columnPosition, final NatTable natTable) {
		super(layer, columnPosition);
		
		this.sourceLayer = layer;
		this.natTable = natTable;
	}

	protected InitializeAutoResizeColumnsCommand(InitializeAutoResizeColumnsCommand command) {
		super(command);
		
		this.sourceLayer = command.sourceLayer;
		this.natTable = command.natTable;
	}

	public ILayerCommand cloneCommand() {
		return new InitializeAutoResizeColumnsCommand(this);
	}

	// Accessors
	public NatTable getNatTable() {
		return natTable;
	}

	public ILayer getSourceLayer() {
		return sourceLayer;
	}

	public void setSelectedColumnPositions(int[] selectedColumnPositions) {
		this.selectedColumnPositions = selectedColumnPositions;
	}

	public int[] getColumnPositions() {
		return selectedColumnPositions;
	}
}