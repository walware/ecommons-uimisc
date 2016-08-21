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
package de.walware.ecommons.waltable.viewport;

import java.util.Collection;

import de.walware.ecommons.waltable.layer.event.StructuralDiff;
import de.walware.ecommons.waltable.layer.event.StructuralRefreshEvent;


public class ScrollEvent extends StructuralRefreshEvent {

	public ScrollEvent(final ViewportLayer viewportLayer) {
		super(viewportLayer);
	}
	
	protected ScrollEvent(final ScrollEvent event) {
		super(event);
	}
	
	@Override
	public ScrollEvent cloneEvent() {
		return new ScrollEvent(this);
	}
	
	@Override
	public Collection<StructuralDiff> getColumnDiffs() {
		// TODO this is bogus - should have a horiz/vert scroll event instead that are multi col/row structural changes
		return null;
	}
	
	@Override
	public Collection<StructuralDiff> getRowDiffs() {
		// TODO this is bogus - should have a horiz/vert scroll event instead that are multi col/row structural changes
		return null;
	}
	
}
