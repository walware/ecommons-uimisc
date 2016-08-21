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

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.ILayer;

/**
 * An event that indicates a visible change to one ore more columns in the layer.
 */
public abstract class ColumnVisualChangeEvent implements IVisualChangeEvent {

	/**
	 * The ILayer to which the given column positions match
	 */
	private ILayer layer;
	/**
	 * The column position ranges for the columns that have changed.
	 * They are related to the set ILayer.
	 */
	private Collection<LRange> columnPositionRanges;
	
	/**
	 * Creates a new ColumnVisualChangeEvent based on the given information.
	 * @param layer The ILayer to which the given column positions match.
	 * @param columnPositionRanges The column position ranges for the columns that have changed.
	 */
	public ColumnVisualChangeEvent(final ILayer layer, final LRange...columnPositionRanges) {
		this(layer, Arrays.asList(columnPositionRanges));
	}
	
	/**
	 * Creates a new ColumnVisualChangeEvent based on the given information.
	 * @param layer The ILayer to which the given column positions match.
	 * @param columnPositionRanges The column position ranges for the columns that have changed.
	 */
	public ColumnVisualChangeEvent(final ILayer layer, final Collection<LRange> columnPositionRanges) {
		this.layer= layer;
		this.columnPositionRanges= columnPositionRanges;
	}
	
	/**
	 * Creates a new ColumnVisualChangeEvent based on the given instance.
	 * Mainly needed for cloning.
	 * @param event The ColumnVisualChangeEvent out of which the new instance should be created.
	 */
	protected ColumnVisualChangeEvent(final ColumnVisualChangeEvent event) {
		this.layer= event.layer;
		this.columnPositionRanges= event.columnPositionRanges;
	}
	
	@Override
	public ILayer getLayer() {
		return this.layer;
	}
	
	/**
	 * @return The column position ranges for the columns that have changed.
	 */
	public Collection<LRange> getColumnPositionRanges() {
		return this.columnPositionRanges;
	}
	
	/**
	 * Sets the column position ranges for the columns that have changed. 
	 * Only for internal use in cases where the constructor needs to calculate the column
	 * position ranges within the child constructor.
	 * @param columnPositionRanges The column position ranges for the columns that have changed.
	 */
	protected void setColumnPositionRanges(final Collection<LRange> columnPositionRanges) {
		this.columnPositionRanges= columnPositionRanges;
	}
	
	@Override
	public boolean convertToLocal(final ILayer localLayer) {
		this.columnPositionRanges= localLayer.getDim(HORIZONTAL).underlyingToLocalPositions(
				this.layer.getDim(HORIZONTAL), this.columnPositionRanges );
		this.layer= localLayer;
		
		return this.columnPositionRanges != null && this.columnPositionRanges.size() > 0;
	}
	
	@Override
	public Collection<LRectangle> getChangedPositionRectangles() {
		final Collection<LRectangle> changedPositionRectangles= new ArrayList<>();
		
		final long rowCount= this.layer.getRowCount();
		for (final LRange lRange : this.columnPositionRanges) {
			changedPositionRectangles.add(new LRectangle(lRange.start, 0, lRange.end - lRange.start, rowCount));
		}
		
		return changedPositionRectangles;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
}
