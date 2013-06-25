/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class TreeRowModel<T> implements ITreeRowModel<T>{

	private final HashSet<Long> parentIndexes = new HashSet<Long>();

	private final Collection<ITreeRowModelListener> listeners = new HashSet<ITreeRowModelListener>();

	private final ITreeData<T> treeData;

	public TreeRowModel(ITreeData<T> treeData) {
		this.treeData = treeData;
	}

	public void registerRowGroupModelListener(ITreeRowModelListener listener) {
		this.listeners.add(listener);
	}

	public void notifyListeners() {
		for (ITreeRowModelListener listener : this.listeners) {
			listener.treeRowModelChanged();
		}
	}

	public long depth(long index) {
		return this.treeData
				.getDepthOfData(this.treeData.getDataAtIndex(index));
	}

	public boolean isLeaf(long index) {
		return !hasChildren(index);
	}

	public String getObjectAtIndexAndDepth(long index, long depth) {
		return this.treeData.formatDataForDepth(depth,this.treeData.getDataAtIndex(index));
	}

	public boolean hasChildren(long index) {
		return this.treeData.hasChildren(this.treeData.getDataAtIndex(index));
	}

	public boolean isCollapsed(long index) {
		return this.parentIndexes.contains(index);
	}

	public void clear() {
		this.parentIndexes.clear();
	}

	/**
	 * @return TRUE if the row group this index is collapseable
	 */
	public boolean isCollapseable(long index) {
		return hasChildren(index);
	}

	public List<Long> collapse(long index) {
		this.parentIndexes.add(index);
		notifyListeners();
		return getChildIndexes(index);
	}

	public List<Long> expand(long index) {
		this.parentIndexes.remove(index);
		notifyListeners();
		List<Long> children = getChildIndexes(index);
		this.parentIndexes.removeAll(children);
		return children;
	}

	public List<Long> getChildIndexes(long parentIndex) {
		List<Long> result = new ArrayList<Long>();
		List<T> children = this.treeData.getChildren(this.treeData
				.getDataAtIndex(parentIndex));
		for (T child : children) {
			long index = this.treeData.indexOf(child);
			result.add(index);
			result.addAll(getChildIndexes(index));
		}
		return result;
	}

}
