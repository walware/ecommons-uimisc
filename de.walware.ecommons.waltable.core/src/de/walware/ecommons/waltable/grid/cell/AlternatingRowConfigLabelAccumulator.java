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
package de.walware.ecommons.waltable.grid.cell;

import de.walware.ecommons.waltable.grid.GridRegion;
import de.walware.ecommons.waltable.grid.layer.config.DefaultRowStyleConfiguration;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.layer.cell.IConfigLabelAccumulator;

/**
 * Applies 'odd'/'even' labels to all the rows. These labels are
 * the used to apply color to alternate rows.
 *
 * @see DefaultRowStyleConfiguration
 */
public class AlternatingRowConfigLabelAccumulator implements IConfigLabelAccumulator {

	public static final String ODD_ROW_CONFIG_TYPE= "ODD_" + GridRegion.BODY; //$NON-NLS-1$

	public static final String EVEN_ROW_CONFIG_TYPE= "EVEN_" + GridRegion.BODY; //$NON-NLS-1$

	@Override
	public void accumulateConfigLabels(final LabelStack configLabels, final long columnPosition, final long rowPosition) {
		configLabels.addLabel((rowPosition % 2 == 0 ? EVEN_ROW_CONFIG_TYPE : ODD_ROW_CONFIG_TYPE));
	}
}
