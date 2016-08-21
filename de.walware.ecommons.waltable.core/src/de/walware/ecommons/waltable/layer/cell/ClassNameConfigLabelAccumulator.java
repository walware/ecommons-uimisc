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

import de.walware.ecommons.waltable.data.IRowDataProvider;
import de.walware.ecommons.waltable.layer.LabelStack;


/**
 * Adds the Java class name of the cell's data value as a label.   
 */
public class ClassNameConfigLabelAccumulator implements IConfigLabelAccumulator {
	
	
	private final IRowDataProvider<?> dataProvider;
	
	
	public ClassNameConfigLabelAccumulator(final IRowDataProvider<?> dataProvider) {
		this.dataProvider= dataProvider;
	}
	
	@Override
	public void accumulateConfigLabels(final LabelStack configLabel, final long columnPosition, final long rowPosition) {
		final Object value= this.dataProvider.getDataValue(columnPosition, rowPosition, 0, null);
		if (value != null) {
			configLabel.addLabel(value.getClass().getName());
		}
	}
	
}
