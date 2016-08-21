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
package de.walware.ecommons.waltable.resize;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.walware.ecommons.waltable.command.AbstractDimPositionsCommand;
import de.walware.ecommons.waltable.command.LayerCommandUtil;
import de.walware.ecommons.waltable.coordinate.ColumnPositionCoordinate;
import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.LRangeList;
import de.walware.ecommons.waltable.layer.ILayer;


public class MultiColumnResizeCommand extends AbstractDimPositionsCommand {

	private int commonColumnWidth= -1;
	protected Map<ColumnPositionCoordinate, Integer> colPositionToWidth= new HashMap<>();
	
	
	/**
	 * All columns are being resized to the same width e.g. during a drag resize
	 */
	public MultiColumnResizeCommand(final ILayer layer, final Collection<LRange> columnPositions, final int commonColumnWidth) {
		super(layer.getDim(HORIZONTAL), columnPositions);
		this.commonColumnWidth= commonColumnWidth;
	}
	
	/**
	 * Each column is being resized to a different size e.g. during auto resize
	 */
	public MultiColumnResizeCommand(final ILayer layer, final long[] columnPositions, final int[] columnWidths) {
		super(layer.getDim(HORIZONTAL), new LRangeList(columnPositions));
		for (int i= 0; i < columnPositions.length; i++) {
			this.colPositionToWidth.put(new ColumnPositionCoordinate(layer, columnPositions[i]), Integer.valueOf(columnWidths[i]));
		}
	}
	
	protected MultiColumnResizeCommand(final MultiColumnResizeCommand command) {
		super(command);
		this.commonColumnWidth= command.commonColumnWidth;
		this.colPositionToWidth= new HashMap<>(command.colPositionToWidth);
	}
	
	@Override
	public MultiColumnResizeCommand cloneCommand() {
		return new MultiColumnResizeCommand(this);
	}
	
	
	public long getCommonColumnWidth() {
		return this.commonColumnWidth;
	}
	
	public int getColumnWidth(final long columnPosition) {
		for (final ColumnPositionCoordinate columnPositionCoordinate : this.colPositionToWidth.keySet()) {
			if (columnPositionCoordinate.getColumnPosition() == columnPosition) {
				return this.colPositionToWidth.get(columnPositionCoordinate).intValue();
			}
		}
		return this.commonColumnWidth;
	}
	
	@Override
	public boolean convertToTargetLayer(final ILayer targetLayer) {
		if (super.convertToTargetLayer(targetLayer)) {
			// Ensure that the width associated with the column is now associated with the converted 
			// column position.
			final Map<ColumnPositionCoordinate, Integer> targetColPositionToWidth= new HashMap<>();
			
			for (final ColumnPositionCoordinate columnPositionCoordinate : this.colPositionToWidth.keySet()) {
				final ColumnPositionCoordinate targetColumnPositionCoordinate= LayerCommandUtil.convertColumnPositionToTargetContext(columnPositionCoordinate, targetLayer);
				if (targetColumnPositionCoordinate != null) {
					targetColPositionToWidth.put(targetColumnPositionCoordinate, this.colPositionToWidth.get(columnPositionCoordinate));
				}
			}
			
			this.colPositionToWidth= targetColPositionToWidth;
			return true;
		}
		return false;
	}
	
}
