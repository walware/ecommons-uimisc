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
// -deprecated
package org.eclipse.nebula.widgets.nattable.config;

import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

public class DefaultEditableRule implements IEditableRule {

	private boolean defaultEditable;
	
	public DefaultEditableRule(boolean defaultEditable) {
		this.defaultEditable = defaultEditable;
	}
	
	@Override
	public boolean isEditable(ILayerCell cell, IConfigRegistry configRegistry) {
		return defaultEditable;
	}

}
