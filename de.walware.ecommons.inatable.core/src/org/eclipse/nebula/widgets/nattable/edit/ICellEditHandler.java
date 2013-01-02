/*******************************************************************************
 * Copyright (c) 2012-2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// ~Direction
package org.eclipse.nebula.widgets.nattable.edit;

import org.eclipse.nebula.widgets.nattable.coordinate.Direction;
import org.eclipse.nebula.widgets.nattable.edit.editor.ICellEditor;


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
