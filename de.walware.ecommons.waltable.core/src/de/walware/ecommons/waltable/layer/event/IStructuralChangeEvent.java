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

import java.util.Collection;

/**
 * An event indicating a structural change to the layer. A structural change is
 * defined as something that modifies the number of columns/rows in the layer or
 * their associated widths/heights.
 */
public interface IStructuralChangeEvent extends IVisualChangeEvent {

	public boolean isHorizontalStructureChanged();
	
	public Collection<StructuralDiff> getColumnDiffs();
	
	public boolean isVerticalStructureChanged();
	
	public Collection<StructuralDiff> getRowDiffs();
	
}
