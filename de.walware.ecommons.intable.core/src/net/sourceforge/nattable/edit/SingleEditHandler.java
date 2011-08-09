package net.sourceforge.nattable.edit;

import net.sourceforge.nattable.coordinate.IRelative.Direction;
import net.sourceforge.nattable.coordinate.IRelative.Scale;
import net.sourceforge.nattable.edit.command.UpdateDataCommand;
import net.sourceforge.nattable.edit.editor.ICellEditor;
import net.sourceforge.nattable.layer.ILayer;
import net.sourceforge.nattable.selection.command.ISelectionCommand.SelectionFlag;
import net.sourceforge.nattable.selection.command.SelectRelativelyCommand;

public class SingleEditHandler implements ICellEditHandler {

	private final ICellEditor cellEditor;
	private final ILayer layer;
	private final int columnPosition;
	private final int rowPosition;

	public SingleEditHandler(ICellEditor cellEditor, ILayer layer, int columnPosition, int rowPosition) {
		this.cellEditor = cellEditor;
		this.layer = layer;
		this.columnPosition = columnPosition;
		this.rowPosition = rowPosition;
	}
	
	/**
	 * {@inheritDoc}
 	 * Note: Assumes that the value is valid.<br/>
	 */
	public boolean commit(Direction direction, boolean closeEditorAfterCommit) {
		Object canonicalValue = cellEditor.getCanonicalValue();
		
		layer.doCommand(new SelectRelativelyCommand(direction, Scale.CELL, SelectionFlag.NONE));
		
		boolean committed = layer.doCommand(new UpdateDataCommand(layer, columnPosition, rowPosition, canonicalValue));
		if (committed && closeEditorAfterCommit){
			cellEditor.close();
			return true;
		}
		
		return committed;
	}
	
}
