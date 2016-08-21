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
package de.walware.ecommons.waltable.freeze;

import java.util.Collection;

import de.walware.ecommons.waltable.coordinate.PositionCoordinate;
import de.walware.ecommons.waltable.layer.event.ILayerEventHandler;
import de.walware.ecommons.waltable.layer.event.IStructuralChangeEvent;
import de.walware.ecommons.waltable.layer.event.StructuralDiff;


public class FreezeEventHandler implements ILayerEventHandler<IStructuralChangeEvent> {

	private final FreezeLayer freezeLayer;
	
	public FreezeEventHandler(final FreezeLayer freezeLayer) {
		this.freezeLayer= freezeLayer;
	}

	@Override
	public Class<IStructuralChangeEvent> getLayerEventClass() {
		return IStructuralChangeEvent.class;
	}

	@Override
	public void handleLayerEvent(final IStructuralChangeEvent event) {
		final PositionCoordinate topLeftPosition= this.freezeLayer.getTopLeftPosition();
		final PositionCoordinate bottomRightPosition= this.freezeLayer.getBottomRightPosition();
		
		// The handling of diffs have to be in sync with ViewportLayerDim#handleStructuralChanges
		final Collection<StructuralDiff> columnDiffs= event.getColumnDiffs();
		if (columnDiffs != null) {
			int leftOffset= 0;
			int rightOffset= 0;
			int freezeMove= 0; // 0= unset, 1 == true, -1 == false
			
			for (final StructuralDiff diff : columnDiffs) {
				final long start= diff.getBeforePositionRange().start;
				switch (diff.getDiffType()) {
				case ADD:
					if (start < topLeftPosition.columnPosition) {
						leftOffset+= diff.getAfterPositionRange().size();
					}
					if (start <= bottomRightPosition.columnPosition
							|| (freezeMove == 1 && start == bottomRightPosition.columnPosition + 1) ) {
						rightOffset+= diff.getAfterPositionRange().size();
					}
					continue;
				case DELETE:
					if (start < topLeftPosition.columnPosition) {
						leftOffset-= Math.min(diff.getBeforePositionRange().end, topLeftPosition.columnPosition + 1) - start;
					}
					if (start <= bottomRightPosition.columnPosition) {
						rightOffset-= Math.min(diff.getBeforePositionRange().end, bottomRightPosition.columnPosition + 1) - start;
						if (freezeMove == 0) {
							freezeMove= 1;
						}
					}
					else {
						freezeMove= -1;
					}
					continue;
				default:
					continue;
				}
			}
			
			topLeftPosition.columnPosition+= leftOffset;
			bottomRightPosition.columnPosition+= rightOffset;
		}
		
		final Collection<StructuralDiff> rowDiffs= event.getRowDiffs();
		if (rowDiffs != null) {
			int leftOffset= 0;
			int rightOffset= 0;
			int freezeMove= 0; // 0= unset, 1 == true, -1 == false
			
			for (final StructuralDiff diff : rowDiffs) {
				final long start= diff.getBeforePositionRange().start;
				switch (diff.getDiffType()) {
				case ADD:
					if (start < topLeftPosition.rowPosition) {
						leftOffset+= diff.getAfterPositionRange().size();
					}
					if (start <= bottomRightPosition.rowPosition
							|| (freezeMove == 1 && start == bottomRightPosition.rowPosition + 1) ) {
						rightOffset+= diff.getAfterPositionRange().size();
					}
					continue;
				case DELETE:
					if (start < topLeftPosition.rowPosition) {
						leftOffset-= Math.min(diff.getBeforePositionRange().end, topLeftPosition.rowPosition + 1) - start;
					}
					if (start <= bottomRightPosition.rowPosition) {
						rightOffset-= Math.min(diff.getBeforePositionRange().end, bottomRightPosition.rowPosition + 1) - start;
						if (freezeMove == 0) {
							freezeMove= 1;
						}
					}
					else {
						freezeMove= -1;
					}
					continue;
				default:
					continue;
				}
			}
			
			topLeftPosition.rowPosition+= leftOffset;
			bottomRightPosition.rowPosition+= rightOffset;
		}
	}
	
}
