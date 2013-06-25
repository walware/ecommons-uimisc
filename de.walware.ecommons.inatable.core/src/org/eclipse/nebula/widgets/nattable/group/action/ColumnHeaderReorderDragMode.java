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
package org.eclipse.nebula.widgets.nattable.group.action;


import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupUtils;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.reorder.action.ColumnReorderDragMode;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;

/**
 * Extends the regular column drag functionality to work with Column groups.
 * It does the following checks:
 * <ol>
 * <li>Checks that the destination is not part of a Unbreakable column group</li>
 * <li>Checks if the destination is between two adjoining column groups</li>
 * </ol>
 */
public class ColumnHeaderReorderDragMode extends ColumnReorderDragMode {

	private final ColumnGroupModel model;

	public ColumnHeaderReorderDragMode(ColumnGroupModel model) {
		this.model = model;
	}

	@Override
	public boolean isValidTargetColumnPosition(ILayer natLayer, long fromGridColumnPosition, long toGridColumnPosition) {
		if (this.currentEvent != null) {
			//if this method was triggered by a mouse event, we determine the to column position by the event
			//if there is no current mouse event referenced it means the reorder is triggered programmatically
			toGridColumnPosition = natLayer.getColumnPositionByX(this.currentEvent.x);
		}
		long toColumnIndex = natLayer.getColumnIndexByPosition(toGridColumnPosition);
		long fromColumnIndex = natLayer.getColumnIndexByPosition(fromGridColumnPosition);

		// Allow moving within the unbreakable group
		if (model.isPartOfAnUnbreakableGroup(fromColumnIndex)){
			return ColumnGroupUtils.isInTheSameGroup(fromColumnIndex, toColumnIndex, model);
		}

		boolean betweenTwoGroups = false;
		if (this.currentEvent != null) {
			long minX = this.currentEvent.x - GUIHelper.DEFAULT_RESIZE_HANDLE_SIZE;
			long maxX = this.currentEvent.x + GUIHelper.DEFAULT_RESIZE_HANDLE_SIZE;
			betweenTwoGroups = ColumnGroupUtils.isBetweenTwoGroups(natLayer, minX, maxX, model);
		}

		return (!model.isPartOfAnUnbreakableGroup(toColumnIndex)) || betweenTwoGroups;
	}
}
