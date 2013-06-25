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
// ~
package org.eclipse.nebula.widgets.nattable.columnCategories;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.coordinate.Direction;


public interface IColumnCategoriesDialogListener {

	void itemsSelected(List<Long> addedColumnIndexes);

	void itemsRemoved(List<Long> removedColumnPositions);

	void itemsMoved(Direction direction, List<Long> selectedPositions);

}
