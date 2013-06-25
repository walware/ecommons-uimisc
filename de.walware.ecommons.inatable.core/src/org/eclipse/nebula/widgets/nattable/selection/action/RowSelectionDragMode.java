/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// ~Selection
package org.eclipse.nebula.widgets.nattable.selection.action;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectRowsCommand;


/**
 * Selects the entire row when the mouse is dragged on the body.
 * <i>Multiple</i> rows are selected as the user drags.
 *
 * @see RowOnlySelectionBindings
 */
public class RowSelectionDragMode extends CellSelectionDragMode {

	@Override
	protected void fireSelectionCommand(NatTable natTable, int columnPosition,	int rowPosition, int selectionFlags) {
		natTable.doCommand(new SelectRowsCommand(natTable, columnPosition, rowPosition, selectionFlags));
	}

}
