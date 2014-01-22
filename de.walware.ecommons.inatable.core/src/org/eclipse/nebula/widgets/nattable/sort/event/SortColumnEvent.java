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
// ~
package org.eclipse.nebula.widgets.nattable.sort.event;

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.event.ColumnVisualChangeEvent;


public class SortColumnEvent extends ColumnVisualChangeEvent {


	public SortColumnEvent(final ILayer layer, final long columnPosition) {
		super(layer, new Range(columnPosition));
	}
	
	public SortColumnEvent(final ILayer layer, final Collection<Range> columnPositions) {
		super(layer, columnPositions);
	}
	
	protected SortColumnEvent(final SortColumnEvent event) {
		super(event);
	}
	
	@Override
	public SortColumnEvent cloneEvent() {
		return new SortColumnEvent(this);
	}

}
