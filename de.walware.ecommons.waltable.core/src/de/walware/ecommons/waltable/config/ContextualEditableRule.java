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
// -depend
package de.walware.ecommons.waltable.config;

import de.walware.ecommons.waltable.layer.cell.ILayerCell;


public abstract class ContextualEditableRule implements IEditableRule {

	public boolean isEditable(final long columnIndex, final long rowIndex) {
		throw new UnsupportedOperationException(this.getClass().getName() 
				+ " is a ContextualEditableRule and has therefore to be called with context informations."); //$NON-NLS-1$
	}

	@Override
	public abstract boolean isEditable(ILayerCell cell, IConfigRegistry configRegistry);

}
