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

	long depth(long index);

	boolean isLeaf(long index);

	String getObjectAtIndexAndDepth(long index, long depth);

	boolean hasChildren(long index);

	boolean isCollapsed(long index);

	List<Long> collapse(long parentIndex);

	List<Long> expand(long parentIndex);

	List<Long> getChildIndexes(long parentIndex);
}
