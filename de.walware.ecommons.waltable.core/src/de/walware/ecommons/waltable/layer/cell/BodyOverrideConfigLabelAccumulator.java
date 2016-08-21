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

import java.util.Arrays;
import java.util.List;

import de.walware.ecommons.waltable.layer.LabelStack;


/**
 * Applies the given labels to all the cells in the grid.
 * Used to apply styles to the entire grid.
 */
public class BodyOverrideConfigLabelAccumulator implements IConfigLabelAccumulator {

	private List<String> configLabels;

	@Override
	public void accumulateConfigLabels(final LabelStack configLabels, final long columnPosition, final long rowPosition) {
		configLabels.getLabels().addAll(this.configLabels);
	}

	public void registerOverrides(final String... configLabels) {
		this.configLabels= Arrays.asList(configLabels);
	}

	public void addOverride(final String configLabel) {
		this.configLabels.add(configLabel);
	}

}
