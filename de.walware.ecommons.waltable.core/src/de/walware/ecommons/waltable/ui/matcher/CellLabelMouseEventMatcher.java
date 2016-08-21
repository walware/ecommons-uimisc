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

package de.walware.ecommons.waltable.ui.matcher;

import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.ui.NatEventData;


/**
 * Matches mouse clicks on cells to which a specified configuration label has been applied.
 */
public class CellLabelMouseEventMatcher extends MouseEventMatcher {
	
	
	private final String labelToMatch;
	
	
	public CellLabelMouseEventMatcher(final String regionName, final int button, final String labelToMatch) {
		super(regionName, button);
		this.labelToMatch= labelToMatch;
	}
	
	public CellLabelMouseEventMatcher(final int stateMask, final String regionName, final int button, final String labelToMatch) {
		super(stateMask, regionName, button);
		this.labelToMatch= labelToMatch;
	}
	
	
	@Override
	public boolean matches(final NatTable natTable, final MouseEvent event, final LabelStack regionLabels) {
		final NatEventData eventData= NatEventData.createInstanceFromEvent(event);
		final ILayerCell cell= natTable.getCellByPosition(eventData.getColumnPosition(), eventData.getRowPosition());
		return (cell != null
				&& super.matches(natTable, event, regionLabels)
				&& cell.getConfigLabels().getLabels().contains(this.labelToMatch) );
	}
	
}
