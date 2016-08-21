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
package de.walware.ecommons.waltable.grid;

/**
 * A region is simply an area on the Grid.
 * Diving the table/grid into regions makes it easier to manage areas with similar behavior.
 *
 * For example all the cells in the column header are painted differently
 * and can respond to sorting actions.
 */
public interface GridRegion {
	
	public static final String CORNER= "CORNER"; //$NON-NLS-1$
	public static final String COLUMN_HEADER= "COLUMN_HEADER"; //$NON-NLS-1$
	public static final String COLUMN_GROUP_HEADER= "COLUMN_GROUP_HEADER"; //$NON-NLS-1$
	public static final String ROW_HEADER= "ROW_HEADER"; //$NON-NLS-1$
	public static final String ROW_GROUP_HEADER= "ROW_GROUP_HEADER"; //$NON-NLS-1$
	public static final String COLUMN_HEADER_LABEL= COLUMN_HEADER + "_LABEL"; //$NON-NLS-1$
	public static final String ROW_HEADER_LABEL= ROW_HEADER + "_LABEL"; //$NON-NLS-1$
	public static final String HEADER_PLACEHOLDER= "HEADER_PLACEHOLDER"; //$NON-NLS-1$
	
	public static final String BODY= "BODY"; //$NON-NLS-1$
	
	public static final String FILTER_ROW = "FILTER_ROW"; //$NON-NLS-1$
	
}
