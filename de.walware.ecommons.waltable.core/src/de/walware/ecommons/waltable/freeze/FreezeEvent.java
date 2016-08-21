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
package de.walware.ecommons.waltable.freeze;

import java.util.Collection;

import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.event.StructuralDiff;
import de.walware.ecommons.waltable.layer.event.StructuralRefreshEvent;


public class FreezeEvent extends StructuralRefreshEvent {

	public FreezeEvent(final ILayer layer) {
		super(layer);
	}
	
	protected FreezeEvent(final FreezeEvent event) {
		super(event);
	}
	
	@Override
	public FreezeEvent cloneEvent() {
		return new FreezeEvent(this);
	}
	
	@Override
	public Collection<StructuralDiff> getColumnDiffs() {
		return null;
	}
	
	@Override
	public Collection<StructuralDiff> getRowDiffs() {
		return null;
	}
	
}
