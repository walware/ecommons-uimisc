/*******************************************************************************
 * Copyright (c) 2012-2016 Dirk Fauth and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Dirk Fauth - initial API and implementation
 ******************************************************************************/
package de.walware.ecommons.waltable.layer.event;

import java.util.ArrayList;
import java.util.Collection;

import de.walware.ecommons.waltable.layer.DataLayer;
import de.walware.ecommons.waltable.layer.ILayer;

/**
 * Special {@link StructuralRefreshEvent} that returns empty lists for column and 
 * row diffs to avoid complete resetting of changes made to the NatTable by the user
 * (e.g. resetting changed column order like reported in https://bugs.eclipse.org/bugs/show_bug.cgi?id=384795).
 * 
 * <p>This event should only be fired be the {@link DataLayer} if columns or rows are configured
 * to use percentage sizing.
 * 
 * @author Dirk Fauth
 *
 */
public class ResizeStructuralRefreshEvent extends StructuralRefreshEvent {

	public ResizeStructuralRefreshEvent(final ILayer layer) {
		super(layer);
	}

	protected ResizeStructuralRefreshEvent(final ResizeStructuralRefreshEvent event) {
		super(event);
	}

	@Override
	public Collection<StructuralDiff> getColumnDiffs() {
		return new ArrayList<>();
	}
	
	@Override
	public Collection<StructuralDiff> getRowDiffs() {
		return new ArrayList<>();
	}
}
