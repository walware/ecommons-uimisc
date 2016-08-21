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
import de.walware.ecommons.waltable.layer.event.ColumnStructuralChangeEvent;
import de.walware.ecommons.waltable.layer.event.StructuralDiff;
import de.walware.ecommons.waltable.layer.event.StructuralDiff.DiffTypeEnum;


public class ColumnResizeEvent extends ColumnStructuralChangeEvent {

	public ColumnResizeEvent(final ILayer layer, final long columnPosition) {
		super(layer, new LRange(columnPosition));
	}
	
	protected ColumnResizeEvent(final ColumnResizeEvent event) {
		super(event);
	}
	
	@Override
	public ColumnResizeEvent cloneEvent() {
		return new ColumnResizeEvent(this);
	}
	
	@Override
	public Collection<StructuralDiff> getColumnDiffs() {
		final Collection<StructuralDiff> rowDiffs= new ArrayList<>();
		
		for (final LRange lRange : getColumnPositionRanges()) {
			rowDiffs.add(new StructuralDiff(DiffTypeEnum.CHANGE, lRange, lRange));
		}
		
		return rowDiffs;
	}
	
}
