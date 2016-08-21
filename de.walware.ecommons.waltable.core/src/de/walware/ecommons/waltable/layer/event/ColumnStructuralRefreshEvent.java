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
 * General event indicating that columns cached by the layers need refreshing. 
 * 
 * Note: As opposed to the the {@link ColumnStructuralChangeEvent} this event does not 
 * indicate the specific columns which have changed.
 */
public class ColumnStructuralRefreshEvent extends StructuralRefreshEvent {

	public ColumnStructuralRefreshEvent(final ILayer layer) {
		super(layer);
	}
	
	@Override
	public boolean isHorizontalStructureChanged() {
		return true;
	}
	
	@Override
	public boolean isVerticalStructureChanged() {
		return false;
	}
}
