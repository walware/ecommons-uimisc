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
package de.walware.ecommons.waltable.layer.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.ILayer;

/**
 * @see ColumnStructuralChangeEvent
 */
public abstract class RowStructuralChangeEvent extends RowVisualChangeEvent implements IStructuralChangeEvent {

	public RowStructuralChangeEvent(final ILayer layer, final LRange...rowPositionRanges) {
		this(layer, Arrays.asList(rowPositionRanges));
	}
	
	public RowStructuralChangeEvent(final ILayer layer, final Collection<LRange> rowPositionRanges) {
		super(layer, rowPositionRanges);
	}
	
	protected RowStructuralChangeEvent(final RowStructuralChangeEvent event) {
		super(event);
	}
	
	@Override
	public Collection<LRectangle> getChangedPositionRectangles() {
		final Collection<LRectangle> changedPositionRectangles= new ArrayList<>();
		
		final long columnCount= getLayer().getColumnCount();
		final long rowCount= getLayer().getRowCount();
		for (final LRange lRange : getRowPositionRanges()) {
			changedPositionRectangles.add(new LRectangle(0, lRange.start, columnCount, rowCount - lRange.start));
		}
		
		return changedPositionRectangles;
	}
	
	@Override
	public boolean isHorizontalStructureChanged() {
		return false;
	}
	
	@Override
	public Collection<StructuralDiff> getColumnDiffs() {
		return null;
	}
	
	@Override
	public boolean isVerticalStructureChanged() {
		return true;
	}
	
}
