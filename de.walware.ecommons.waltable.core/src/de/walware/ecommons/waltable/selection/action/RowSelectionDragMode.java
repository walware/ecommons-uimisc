/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/

package de.walware.ecommons.waltable.selection.action;

import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.selection.SelectDimPositionsCommand;
import de.walware.ecommons.waltable.selection.config.RowOnlySelectionBindings;


/**
 * Selects the entire row when the mouse is dragged on the body.
 * <i>Multiple</i> rows are selected as the user drags.
 *
 * @see RowOnlySelectionBindings
 */
public class RowSelectionDragMode extends CellSelectionDragMode {
	
	
	public RowSelectionDragMode() {
	}
	
	
	@Override
	protected void fireSelectionCommand(final NatTable natTable,
			final long columnPosition,	final long rowPosition, final int selectionFlags) {
		natTable.doCommand(new SelectDimPositionsCommand(natTable.getDim(VERTICAL),
				rowPosition, columnPosition, selectionFlags ));
	}
	
}
