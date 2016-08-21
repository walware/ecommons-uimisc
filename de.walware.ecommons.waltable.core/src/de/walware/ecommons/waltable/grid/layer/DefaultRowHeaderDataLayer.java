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

package de.walware.ecommons.waltable.grid.layer;

import de.walware.ecommons.waltable.coordinate.PositionId;
import de.walware.ecommons.waltable.data.IDataProvider;
import de.walware.ecommons.waltable.layer.DataLayer;


public class DefaultRowHeaderDataLayer extends DataLayer {
	
	
	public DefaultRowHeaderDataLayer(final IDataProvider rowHeaderDataProvider) {
		super(rowHeaderDataProvider,
				PositionId.HEADER_CAT, 40,
				PositionId.BODY_CAT, 40);
	}
	
}
