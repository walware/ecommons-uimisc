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

import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.walware.ecommons.waltable.command.AbstractDimPositionsCommand;
import de.walware.ecommons.waltable.command.LayerCommandUtil;
import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.LRangeList;
import de.walware.ecommons.waltable.coordinate.RowPositionCoordinate;
import de.walware.ecommons.waltable.layer.ILayer;


public class MultiRowResizeCommand extends AbstractDimPositionsCommand {

	private int commonRowHeight= -1;
	protected Map<RowPositionCoordinate, Integer> rowPositionToHeight= new HashMap<>();
	
	
	/**
	 * All rows are being resized to the same height e.g. during a drag resize
	 */
	public MultiRowResizeCommand(final ILayer layer, final Collection<LRange> rowPositions, final int commonRowHeight) {
		super(layer.getDim(VERTICAL), rowPositions);
		this.commonRowHeight= commonRowHeight;
	}
	
	/**
	 * Each row is being resized to a different size e.g. during auto resize
	 */
	public MultiRowResizeCommand(final ILayer layer, final long[] rowPositions, final int[] rowHeights) {
		super(layer.getDim(VERTICAL), new LRangeList(rowPositions));
		for (int i= 0; i < rowPositions.length; i++) {
			this.rowPositionToHeight.put(new RowPositionCoordinate(layer, rowPositions[i]), Integer.valueOf(rowHeights[i]));
		}
	}
	
	protected MultiRowResizeCommand(final MultiRowResizeCommand command) {
		super(command);
		this.commonRowHeight= command.commonRowHeight;
		this.rowPositionToHeight= new HashMap<>(command.rowPositionToHeight);
	}
	
	@Override
	public MultiRowResizeCommand cloneCommand() {
		return new MultiRowResizeCommand(this);
	}
	
	
	public long getCommonRowHeight() {
		return this.commonRowHeight;
	}
	
	public int getRowHeight(final long rowPosition) {
		for (final RowPositionCoordinate rowPositionCoordinate : this.rowPositionToHeight.keySet()) {
			if (rowPositionCoordinate.getRowPosition() == rowPosition) {
				return this.rowPositionToHeight.get(rowPositionCoordinate).intValue();
			}
		}
		return this.commonRowHeight;
	}
	
	@Override
	public boolean convertToTargetLayer(final ILayer targetLayer) {
		if (super.convertToTargetLayer(targetLayer)) {
			// Ensure that the height associated with the row is now associated with the converted 
			// row position.
			final Map<RowPositionCoordinate, Integer> targetRowPositionToHeight= new HashMap<>();
			
			for (final RowPositionCoordinate rowPositionCoordinate : this.rowPositionToHeight.keySet()) {
				final RowPositionCoordinate targetRowPositionCoordinate= LayerCommandUtil.convertRowPositionToTargetContext(rowPositionCoordinate, targetLayer);
				if (targetRowPositionCoordinate != null) {
					targetRowPositionToHeight.put(targetRowPositionCoordinate, this.rowPositionToHeight.get(rowPositionCoordinate));
				}
			}
			
			this.rowPositionToHeight= targetRowPositionToHeight;
			return true;
		}
		return false;
	}
	
}
