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
 * Event indicating a change in the structure of the columns. 
 * This event carried ColumnDiffs (Collection&lt;StructuralDiff&gt;) indicating the columns which have changed. 
 */
public abstract class ColumnStructuralChangeEvent extends ColumnVisualChangeEvent implements IStructuralChangeEvent {

	/**
	 * Creates a new ColumnStructuralChangeEvent based on the given information.
	 * @param layer The ILayer to which the given column positions match.
	 * @param columnPositionRanges The column position ranges for the columns that have changed.
	 */
	public ColumnStructuralChangeEvent(final ILayer layer, final LRange...columnPositionRanges) {
		this(layer, Arrays.asList(columnPositionRanges));
	}
	
	/**
	 * Creates a new ColumnStructuralChangeEvent based on the given information.
	 * @param layer The ILayer to which the given column positions match.
	 * @param columnPositionRanges The column position ranges for the columns that have changed.
	 */
	public ColumnStructuralChangeEvent(final ILayer layer, final Collection<LRange> columnPositionRanges) {
		super(layer, columnPositionRanges);
	}
	
	/**
	 * Creates a new ColumnStructuralChangeEvent based on the given instance.
	 * Mainly needed for cloning.
	 * @param event The ColumnStructuralChangeEvent out of which the new instance should be created.
	 */
	protected ColumnStructuralChangeEvent(final ColumnStructuralChangeEvent event) {
		super(event);
	}
	
	@Override
	public Collection<LRectangle> getChangedPositionRectangles() {
		final Collection<LRectangle> changedPositionRectangles= new ArrayList<>();
		
		final Collection<LRange> columnPositionRanges= getColumnPositionRanges();
		if (columnPositionRanges != null && columnPositionRanges.size() > 0) {
			long leftmostColumnPosition= Long.MAX_VALUE;
			for (final LRange lRange : columnPositionRanges) {
				if (lRange.start < leftmostColumnPosition) {
					leftmostColumnPosition= lRange.start;
				}
			}
			
			final long columnCount= getLayer().getColumnCount();
			final long rowCount= getLayer().getRowCount();
			changedPositionRectangles.add(new LRectangle(leftmostColumnPosition, 0, columnCount - leftmostColumnPosition, rowCount));
		}
		
		return changedPositionRectangles;
	}
	
	@Override
	public boolean isHorizontalStructureChanged() {
		return true;
	}
	
	@Override
	public boolean isVerticalStructureChanged() {
		return false;
	}
	
	@Override
	public Collection<StructuralDiff> getRowDiffs() {
		return null;
	}
	
}
