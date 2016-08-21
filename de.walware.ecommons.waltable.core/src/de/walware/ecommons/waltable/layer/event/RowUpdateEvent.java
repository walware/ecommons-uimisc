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

import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.layer.ILayer;

public class RowUpdateEvent extends RowVisualChangeEvent {
	
	public RowUpdateEvent(final ILayer layer, final long rowPosition) {
		this(layer, new LRange(rowPosition));
	}
	
	public RowUpdateEvent(final ILayer layer, final LRange rowPositionRange) {
		super(layer, rowPositionRange);
	}
	
	public RowUpdateEvent(final RowUpdateEvent event) {
		super(event);
	}
	
	@Override
	public RowUpdateEvent cloneEvent() {
		return new RowUpdateEvent(this);
	}

}
