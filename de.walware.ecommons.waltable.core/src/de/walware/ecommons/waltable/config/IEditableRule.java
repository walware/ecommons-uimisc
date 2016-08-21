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


public interface IEditableRule {
	
	public boolean isEditable(ILayerCell cell, IConfigRegistry configRegistry);
	
	public static final IEditableRule ALWAYS_EDITABLE= new IEditableRule() {

		@Override
		public boolean isEditable(final ILayerCell cell, final IConfigRegistry configRegistry) {
			return true;
		}

	};
	
	public static final IEditableRule NEVER_EDITABLE= new IEditableRule() {

		@Override
		public boolean isEditable(final ILayerCell cell, final IConfigRegistry configRegistry) {
			return false;
		}

	};

}
