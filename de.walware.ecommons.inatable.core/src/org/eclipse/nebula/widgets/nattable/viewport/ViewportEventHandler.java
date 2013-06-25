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
package org.eclipse.nebula.widgets.nattable.viewport;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;
import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEventHandler;
import org.eclipse.nebula.widgets.nattable.layer.event.IStructuralChangeEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.StructuralDiff;


public class ViewportEventHandler implements ILayerEventHandler<IStructuralChangeEvent> {
	
	
	private final ViewportLayer viewportLayer;
	
	
	public ViewportEventHandler(final ViewportLayer viewportLayer) {
		this.viewportLayer = viewportLayer;
	}
	
	public Class<IStructuralChangeEvent> getLayerEventClass() {
		return IStructuralChangeEvent.class;
	}
	
	
	public void handleLayerEvent(final IStructuralChangeEvent event) {
		if (event.isHorizontalStructureChanged()) {
			handle(this.viewportLayer.get(HORIZONTAL), event.getColumnDiffs());
		}
		if (event.isVerticalStructureChanged()) {
			handle(this.viewportLayer.get(VERTICAL), event.getRowDiffs());
		}
	}
	
	protected void handle(final ViewportDim dim, final Collection<StructuralDiff> diffs) {
		dim.invalidateStructure();
		if (diffs != null) {
			long change = 0;
			
			final long minimumOriginPosition = dim.getMinimumOriginPosition();
			for (final StructuralDiff diff : diffs) {
				switch (diff.getDiffType()) {
				case ADD:
					final Range afterPositionRange = diff.getAfterPositionRange();
					if (afterPositionRange.start < minimumOriginPosition) {
						change += afterPositionRange.size();
					}
					continue;
				case DELETE:
					final Range beforePositionRange = diff.getBeforePositionRange();
					if (beforePositionRange.start < minimumOriginPosition) {
						change -= Math.min(beforePositionRange.end, minimumOriginPosition + 1) - beforePositionRange.start;
					}
					continue;
				default:
					continue;
				}
			}
			
			dim.setMinimumOriginPosition(minimumOriginPosition + change);
		}
	}
	
}
