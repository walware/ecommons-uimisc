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
package de.walware.ecommons.waltable.layer.cell;

import de.walware.ecommons.waltable.layer.LabelStack;


/**
 * Accumulator for column labels allowing to configure cells by their column position.
 * 
 * The label of a column is {@link #COLUMN_LABEL_PREFIX} + column position.
 */
public class ColumnLabelAccumulator implements IConfigLabelAccumulator {

	/**
	 * The common prefix of column labels (value is {@value}).
	 */
	public static final String COLUMN_LABEL_PREFIX= "COLUMN_"; //$NON-NLS-1$


	@Override
	public void accumulateConfigLabels(final LabelStack configLabels, final long columnPosition, final long rowPosition) {
		configLabels.addLabel(COLUMN_LABEL_PREFIX + columnPosition);
	}

}
