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
// ~
package de.walware.ecommons.waltable.resize;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.coordinate.LPoint;
import de.walware.ecommons.waltable.grid.GridRegion;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.ui.matcher.MouseEventMatcher;
import de.walware.ecommons.waltable.ui.util.CellEdgeDetectUtil;


public class ColumnResizeEventMatcher extends MouseEventMatcher {
	
	
	public ColumnResizeEventMatcher(final int button, final boolean rowColumn) {
		super(SWT.NONE, GridRegion.COLUMN_HEADER, (rowColumn) ? GridRegion.CORNER : null, button);
	}
	
	public ColumnResizeEventMatcher(final int button) {
		this(SWT.NONE, GridRegion.COLUMN_HEADER, button);
	}
	
	public ColumnResizeEventMatcher(final int stateMask, final String eventRegion, final int button) {
		super(stateMask, eventRegion, button);
	}
	
	
	@Override
	public boolean matches(final NatTable natTable, final MouseEvent event, final LabelStack regionLabels) {
		return super.matches(natTable, event, regionLabels) && isResizable(natTable, event);
	}
	
	private boolean isResizable(final ILayer natLayer, final MouseEvent event) {
		final long columnPosition= CellEdgeDetectUtil.getPositionToResize(natLayer,
				new LPoint(event.x, event.y), HORIZONTAL );
		
		return (columnPosition >= 0
				&& natLayer.getDim(HORIZONTAL).isPositionResizable(columnPosition) );
	}
	
}
