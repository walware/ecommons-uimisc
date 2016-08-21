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
package de.walware.ecommons.waltable.sort;

import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.coordinate.LPoint;
import de.walware.ecommons.waltable.grid.GridRegion;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.ui.matcher.MouseEventMatcher;
import de.walware.ecommons.waltable.ui.util.CellEdgeDetectUtil;
import de.walware.ecommons.waltable.ui.util.CellEdgeEnum;
import de.walware.ecommons.waltable.util.GUIHelper;


/**
 * Matches a click on the column header, except if the click is on the column edge.
 */
public class ColumnHeaderClickEventMatcher extends MouseEventMatcher {

	public ColumnHeaderClickEventMatcher(final int stateMask, final int button) {
		super(stateMask, GridRegion.COLUMN_HEADER, button);
	}

	@Override
    public boolean matches(final NatTable natTable, final MouseEvent event, final LabelStack regionLabels) {
	    return super.matches(natTable, event, regionLabels) && isNearTheHeaderEdge(natTable, event) && isNotFilterRegion(regionLabels);
	}

	private boolean isNearTheHeaderEdge(final ILayer natLayer, final MouseEvent event) {
		final CellEdgeEnum cellEdge= CellEdgeDetectUtil.getHorizontalCellEdge(
                                                           natLayer, 
                                                           new LPoint(event.x, event.y), 
                                                           GUIHelper.DEFAULT_RESIZE_HANDLE_SIZE);
		return cellEdge == CellEdgeEnum.NONE;
	}

    private boolean isNotFilterRegion(final LabelStack regionLabels) {
        if (regionLabels != null) {
            return !regionLabels.getLabels().contains(GridRegion.FILTER_ROW);
        }
        return true;
    }
}
