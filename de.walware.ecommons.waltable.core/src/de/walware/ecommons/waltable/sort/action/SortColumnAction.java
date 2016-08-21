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

package de.walware.ecommons.waltable.sort.action;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;

import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.sort.SortDimPositionCommand;
import de.walware.ecommons.waltable.ui.NatEventData;
import de.walware.ecommons.waltable.ui.action.IMouseAction;


public class SortColumnAction implements IMouseAction {
	
	
	private final boolean accumulate;
	
	
	public SortColumnAction(final boolean accumulate) {
		this.accumulate= accumulate;
	}
	
	@Override
	public void run(final NatTable natTable, final MouseEvent event) {
		final long columnPosition= ((NatEventData)event.data).getColumnPosition();
		natTable.doCommand(new SortDimPositionCommand(natTable.getDim(HORIZONTAL),
				columnPosition, this.accumulate ));
	}
	
}
