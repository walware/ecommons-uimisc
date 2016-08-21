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

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.layer.event.ILayerEvent;
import de.walware.ecommons.waltable.layer.event.IVisualChangeEvent;

/**
 * Gathers all the VisualChangeEvents. When its run, it refreshes/repaints the table. 
 *
 */
public class VisualChangeEventConflater extends AbstractEventConflater { 

	private final NatTable natTable;

	public VisualChangeEventConflater(final NatTable ownerLayer) {
		this.natTable= ownerLayer;
	}

	@Override
	public void addEvent(final ILayerEvent event) {
		if(event instanceof IVisualChangeEvent){
			super.addEvent(event);
		}
	}
	
	@Override
	public Runnable getConflaterTask() {
		return new Runnable() {

			@Override
			public void run() {
				if (VisualChangeEventConflater.this.queue.size() > 0) {
					VisualChangeEventConflater.this.natTable.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							VisualChangeEventConflater.this.natTable.updateResize();
						}
					});

					clearQueue();
				}
			}
		};
	}

}
