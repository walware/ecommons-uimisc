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
package de.walware.ecommons.waltable.layer;

import de.walware.ecommons.waltable.layer.event.ILayerEvent;


/**
 * Object interested in receiving events related to a {@link ILayer}.
 */
public interface ILayerListener {
	
	
	/**
	 * Handle an event notification from an {@link ILayer}
	 * @param event the event
	 */
	public void handleLayerEvent(ILayerEvent event);
	
}
