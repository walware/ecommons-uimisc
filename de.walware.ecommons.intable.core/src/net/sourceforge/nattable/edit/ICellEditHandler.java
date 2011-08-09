package net.sourceforge.nattable.edit;

import net.sourceforge.nattable.coordinate.IRelative.Direction;
import net.sourceforge.nattable.edit.editor.ICellEditor;


/**
 * Handles the updating of the data bean with the new value provided<br/>
 * by the {@link ICellEditor}
 */
public interface ICellEditHandler {
	
	
	/**
	 * Commit the new value.<br/>
	 * 
	 * @param direction to move the selection after the commit.<br/>
	 * 	Example: when TAB key is pressed, we commit and move the selection.
	 * @return TRUE if the data source was successfully updated
	 */
	public boolean commit(Direction direction, boolean closeEditorAfterCommit);
	
}
