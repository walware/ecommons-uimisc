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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.walware.ecommons.waltable.layer.LabelStack;


/**
 * An {@link IConfigLabelAccumulator} that can aggregate labels from other <code>IConfigLabelAccumulator</code>s. 
 * All the labels provided by the aggregated accumulators are applied to the cell.
 */
public class AggregrateConfigLabelAccumulator implements IConfigLabelAccumulator {
    
    private final List<IConfigLabelAccumulator> accumulators= new ArrayList<>();
    
    public void add(final IConfigLabelAccumulator r) {
        if (r == null)
		 {
			throw new IllegalArgumentException("null"); //$NON-NLS-1$
		}
        this.accumulators.add(r);
    }

    public void add(final IConfigLabelAccumulator... r) {
    	if (r == null)
		 {
			throw new IllegalArgumentException("null"); //$NON-NLS-1$
		}
    	this.accumulators.addAll(Arrays.asList(r));
    }

    @Override
	public void accumulateConfigLabels(final LabelStack configLabels, final long columnPosition, final long rowPosition) {
        for (final IConfigLabelAccumulator accumulator : this.accumulators) {
        	accumulator.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
        }
    }

}
