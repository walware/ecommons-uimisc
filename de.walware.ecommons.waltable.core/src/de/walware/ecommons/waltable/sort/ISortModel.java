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

import java.util.List;


/**
 * Interface providing sorting functionality.
 */
public interface ISortModel {
	
	/**
	 * @return List of column ids that are sorted.
	 */
	public List<Long> getSortedColumnIds();
	
	/**
	 * @return TRUE if the column with the given id is sorted at the moment.
	 */
	public boolean isSorted(long columnId);
	
	/**
	 * @return the direction in which the column with the given id is
	 * currently sorted
	 */
	public SortDirection getSortDirection(long columnId);
	
	/**
	 * @return when multiple columns are sorted, this returns the order of the
	 * column id in the sort
	 * <p>
	 * Example: If column ids 3, 6, 9 are sorted (in that order) the sort order
	 * for id 6 is 1.
	 */
	public int getSortOrder(long columnId);
	
	/**
	 * This method is called by the {@link SortCommandHandler} in response to a sort command.
	 * It is responsible for sorting the requested column.
	 *
	 * @param accumulate flag indicating if the column should added to a previous sort.
	 */
	public void sort(long columnId, SortDirection sortDirection, boolean accumulate);
	
	/**
	 * Remove all sorting
	 */
	public void clear();
	
}
