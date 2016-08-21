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

public class SimpleConfigLabelAccumulator implements IConfigLabelAccumulator {

	private final String configLabel;

	public SimpleConfigLabelAccumulator(final String configLabel) {
		this.configLabel= configLabel;
	}

	@Override
	public void accumulateConfigLabels(final LabelStack configLabels, final long columnPosition, final long rowPosition) {
		configLabels.addLabel(this.configLabel);
	}
	
}
