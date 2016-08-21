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

import java.util.ArrayList;
import java.util.Collection;

import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.event.RowStructuralChangeEvent;
import de.walware.ecommons.waltable.layer.event.StructuralDiff;
import de.walware.ecommons.waltable.layer.event.StructuralDiff.DiffTypeEnum;


public class RowResizeEvent extends RowStructuralChangeEvent {

	public RowResizeEvent(final ILayer layer, final long rowPosition) {
		super(layer, new LRange(rowPosition));
	}
	
	public RowResizeEvent(final ILayer layer, final LRange rowPositionRange) {
		super(layer, rowPositionRange);
	}
	
	protected RowResizeEvent(final RowResizeEvent event) {
		super(event);
	}
	
	@Override
	public RowResizeEvent cloneEvent() {
		return new RowResizeEvent(this);
	}
	
	@Override
	public Collection<StructuralDiff> getRowDiffs() {
		final Collection<StructuralDiff> rowDiffs= new ArrayList<>();
		
		for (final LRange lRange : getRowPositionRanges()) {
			new StructuralDiff(DiffTypeEnum.CHANGE, lRange, lRange);
		}
		
		return rowDiffs;
	}
	
}
