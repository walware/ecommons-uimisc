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
package de.walware.ecommons.waltable.selection.config;

import de.walware.ecommons.waltable.config.AbstractLayerConfiguration;
import de.walware.ecommons.waltable.layer.event.ILayerEventHandler;
import de.walware.ecommons.waltable.selection.SelectionLayer;


/**
 * Configure the move selection behavior so that we always move by a row.
 * Add {@link ILayerEventHandler} to preserve row selection.
 * 
 * @see DefaultMoveSelectionConfiguration
 */
public class RowOnlySelectionConfiguration<T> extends AbstractLayerConfiguration<SelectionLayer> {

	@Override
	public void configureTypedLayer(final SelectionLayer layer) {
//		layer.registerCommandHandler(new MoveRowSelectionCommandHandler(layer));
	}
	
}
