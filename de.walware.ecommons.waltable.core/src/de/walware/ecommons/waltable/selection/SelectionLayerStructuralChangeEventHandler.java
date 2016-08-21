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
package de.walware.ecommons.waltable.selection;

import java.util.Collection;
import java.util.List;

import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.event.ILayerEventHandler;
import de.walware.ecommons.waltable.layer.event.IStructuralChangeEvent;
import de.walware.ecommons.waltable.layer.event.StructuralDiff;
import de.walware.ecommons.waltable.layer.event.StructuralDiff.DiffTypeEnum;

public class SelectionLayerStructuralChangeEventHandler implements ILayerEventHandler<IStructuralChangeEvent> {

	private final ISelectionModel selectionModel;
	private final SelectionLayer selectionLayer;
	
	public SelectionLayerStructuralChangeEventHandler(final SelectionLayer selectionLayer, final ISelectionModel selectionModel) {
		this.selectionLayer= selectionLayer;
		this.selectionModel= selectionModel;
	}

	@Override
	public Class<IStructuralChangeEvent> getLayerEventClass() {
		return IStructuralChangeEvent.class;
	}

	@Override
	public void handleLayerEvent(final IStructuralChangeEvent event) {
		if (event.isHorizontalStructureChanged()) {
			// TODO handle column deletion
		}
		
		if (event.isVerticalStructureChanged()) {
			//if there are no row diffs, it seems to be a complete refresh
			if (event.getRowDiffs() == null) {
				final Collection<LRectangle> lRectangles= event.getChangedPositionRectangles();
				for (final LRectangle lRectangle : lRectangles) {
					final LRange changedRange= new LRange(lRectangle.y, lRectangle.y + lRectangle.height);
					if (selectedRowModified(changedRange)) {
						this.selectionLayer.clear();
						break;
					}
				}
			}
			else {
				//there are row diffs so we try to determine the diffs to process
				for (final StructuralDiff diff : event.getRowDiffs()) {
					//DiffTypeEnum.CHANGE is used for resizing and shouldn't result in clearing the selection
					if (diff.getDiffType() != DiffTypeEnum.CHANGE) {
						if (selectedRowModified(diff.getBeforePositionRange())) {
							this.selectionLayer.clear();
							break;
						}
					}
				}
			}
		}
	}
	
	private boolean selectedRowModified(final LRange changedRange){
		final List<LRange> selectedRows= this.selectionModel.getSelectedRowPositions();
		for (final LRange rowRange : selectedRows) {
			if (rowRange.overlap(changedRange)){
				return true;
			}
		}
		return false;
	}

}
