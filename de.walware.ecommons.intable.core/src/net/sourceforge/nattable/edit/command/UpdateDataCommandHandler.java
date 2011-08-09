package net.sourceforge.nattable.edit.command;

import net.sourceforge.nattable.command.AbstractLayerCommandHandler;
import net.sourceforge.nattable.internal.NatTablePlugin;
import net.sourceforge.nattable.layer.DataLayer;
import net.sourceforge.nattable.layer.event.CellVisualChangeEvent;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class UpdateDataCommandHandler extends AbstractLayerCommandHandler<UpdateDataCommand> {

	private final DataLayer dataLayer;

	public UpdateDataCommandHandler(DataLayer dataLayer) {
		this.dataLayer = dataLayer;
	}
	
	public Class<UpdateDataCommand> getCommandClass() {
		return UpdateDataCommand.class;
	}

	@Override
	protected boolean doCommand(UpdateDataCommand command) {
		try {
			int columnPosition = command.getColumnPosition();
			int rowPosition = command.getRowPosition();
			dataLayer.getDataProvider().setDataValue(columnPosition, rowPosition, command.getNewValue());
			dataLayer.fireLayerEvent(new CellVisualChangeEvent(dataLayer, columnPosition, rowPosition));
			return true;
		} catch (UnsupportedOperationException e) {
			NatTablePlugin.getDefault().log(new Status(IStatus.INFO, NatTablePlugin.PLUGIN_ID,
					"Setting data is not supported by the model (value = " + command.getNewValue() + ").", e));
			return false;
		}
	}

}
