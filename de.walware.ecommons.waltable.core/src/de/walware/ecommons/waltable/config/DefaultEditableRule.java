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
// -deprecated
package de.walware.ecommons.waltable.config;

import de.walware.ecommons.waltable.layer.cell.ILayerCell;

public class DefaultEditableRule implements IEditableRule {

	private final boolean defaultEditable;
	
	public DefaultEditableRule(final boolean defaultEditable) {
		this.defaultEditable= defaultEditable;
	}
	
	@Override
	public boolean isEditable(final ILayerCell cell, final IConfigRegistry configRegistry) {
		return this.defaultEditable;
	}

}
