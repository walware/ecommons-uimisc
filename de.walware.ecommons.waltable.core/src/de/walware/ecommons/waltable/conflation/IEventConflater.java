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
package de.walware.ecommons.waltable.conflation;

import de.walware.ecommons.waltable.layer.event.ILayerEvent;

/**
 * A Conflater queues events and periodically runs a task to 
 * handle those Events. This prevents the table from
 * being overwhelmed by ultra fast updates.
 */
public interface IEventConflater {

	public abstract void addEvent(ILayerEvent event);

	public abstract void clearQueue();

	/**
	 * @return Number of events currently waiting to be handled
	 */
	public abstract int getCount();
	
	public Runnable getConflaterTask();

}
