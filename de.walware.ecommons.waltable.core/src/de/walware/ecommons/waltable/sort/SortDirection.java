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

package de.walware.ecommons.waltable.sort;


public enum SortDirection {
	
	
	NONE("Unsorted"), //$NON-NLS-1$
	ASC("Ascending"), //$NON-NLS-1$
	DESC("Ascending"); //$NON-NLS-1$
	
	
	private final String description;
	
	
	private SortDirection(final String description) {
		this.description= description;
	}
	
	
	public String getDescription() {
		return this.description;
	}
	
	public SortDirection getNextSortDirection() {
		switch (this) {
		case NONE:
			return SortDirection.ASC;
		case ASC:
			return SortDirection.DESC;
		case DESC:
		default:
			return SortDirection.NONE;
		}
	}
	
}
