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

import de.walware.ecommons.waltable.layer.ILayer;

/**
 * @see ColumnStructuralRefreshEvent
 */
public class RowStructuralRefreshEvent extends StructuralRefreshEvent {

	public RowStructuralRefreshEvent(final ILayer layer) {
		super(layer);
	}
	
	@Override
	public boolean isVerticalStructureChanged() {
		return true;
	}
	
	@Override
	public boolean isHorizontalStructureChanged() {
		return false;
	}
}
