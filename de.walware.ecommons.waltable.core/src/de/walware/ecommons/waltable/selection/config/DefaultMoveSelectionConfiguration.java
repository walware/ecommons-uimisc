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
import de.walware.ecommons.waltable.selection.SelectionLayer;

/**
 * Configure the behavior when the selection is moved. Example: by using arrow keys.<br/>
 * This default configuration moves by cell.<br/>
 * 
 * {@link MoveSelectionCommand} are fired by the {@link DefaultSelectionBindings}.<br/>
 * An suitable handler can be plugged in to handle the move commands as required.<br/>
 * 
 * @see MoveRowSelectionCommandHandler
 */
public class DefaultMoveSelectionConfiguration extends AbstractLayerConfiguration<SelectionLayer>{

	@Override
	public void configureTypedLayer(final SelectionLayer layer) {
//		layer.registerCommandHandler(new MoveCellSelectionCommandHandler(layer));
	}

}
