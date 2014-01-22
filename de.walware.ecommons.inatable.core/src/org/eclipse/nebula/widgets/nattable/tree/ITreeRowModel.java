/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.tree;

import java.util.List;

public interface ITreeRowModel<T> {

	int depth(long index);

	boolean isLeaf(long index);

	String getObjectAtIndexAndDepth(long index, int depth);

	boolean hasChildren(long index);

	boolean isCollapsed(long index);

	boolean isCollapseable(long index);
	
	List<Long> collapse(long parentIndex);

	List<Long> expand(long parentIndex);

	/**
	 * This method returns <b>all visible</b> child indexes below the node at the given index.
	 * It search all the way down the tree structure to find every child, even the
	 * sub children, sub sub children and so on.
	 * <p>
	 * If you only need to get the direct child indexes of the node at the given index
	 * you need to use {@link ITreeRowModel#getDirectChildIndexes(int)} instead.
	 * @param parentIndex The index for which the child indexes are requested.
	 * @return The list of all child indexes for the node at the given index.
	 */
	List<Long> getChildIndexes(long parentIndex);
	
	/**
	 * This method returns only the direct <b>visible</b> child indexes of the node at the given index.
	 * It does not search all the way down for further sub children.
	 * <p>
	 * If you need to get all child indexes of the node at the given index
	 * you need to use {@link ITreeRowModel#getChildIndexes(int)} instead.
	 * @param parentIndex The index for which the direct child indexes are requested.
	 * @return The list of the direct child indexes for the node at the given index.
	 */
	List<Long> getDirectChildIndexes(long parentIndex);
	
	/**
	 * @return The indexes of the root nodes in the tree.
	 */
	List<Long> getRootIndexes();
	
	/**
	 * This method returns <b>all</b> children below the node at the given index.
	 * It search all the way down the tree structure to find every child, even the
	 * sub children, sub sub children and so on.
	 * <p>
	 * If you only need to get the direct children of the node at the given index
	 * you need to use {@link ITreeRowModel#getDirectChildren(int)} instead.
	 * @param parentIndex The index for which the children are requested.
	 * @return The list of all children for the node at the given index.
	 */
	public List<T> getChildren(long parentIndex);
	
	/**
	 * This method returns only the direct children of the node at the given index.
	 * It does not search all the way down for further sub children.
	 * <p>
	 * If you need to get all children of the node at the given index
	 * you need to use {@link ITreeRowModel#getChildren(int)} instead.
	 * @param parentIndex The index for which the direct children are requested.
	 * @return The list of the direct children for the node at the given index.
	 */
	public List<T> getDirectChildren(long parentIndex);
}
