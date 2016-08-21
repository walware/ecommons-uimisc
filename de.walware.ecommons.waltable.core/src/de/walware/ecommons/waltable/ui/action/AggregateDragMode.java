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
package de.walware.ecommons.waltable.ui.action;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;

public class AggregateDragMode implements IDragMode {
	
	private MouseEvent initialEvent;
	private MouseEvent currentEvent;
	
	private final Collection<IDragMode> dragModes= new LinkedHashSet<>();
	
	public AggregateDragMode() {
	}
	
	public AggregateDragMode(final IDragMode...dragModes) {
		for (final IDragMode dragMode : dragModes) {
			addDragMode(dragMode);
		}
	}
	
	public void addDragMode(final IDragMode dragMode) {
		this.dragModes.add(dragMode);
	}
	
	public void removeDragMode(final IDragMode dragMode) {
		this.dragModes.remove(dragMode);
	}
	
	@Override
	public void mouseDown(final NatTable natTable, final MouseEvent event) {
		this.initialEvent= event;
		this.currentEvent= this.initialEvent;
		
		for (final IDragMode dragMode : this.dragModes) {
			dragMode.mouseDown(natTable, event);
		}
		
		natTable.forceFocus();
	}

	@Override
	public void mouseMove(final NatTable natTable, final MouseEvent event) {
		this.currentEvent= event;
		
		for (final IDragMode dragMode : this.dragModes) {
			dragMode.mouseMove(natTable, event);
		}
	}

	@Override
	public void mouseUp(final NatTable natTable, final MouseEvent event) {
		for (final IDragMode dragMode : this.dragModes) {
			dragMode.mouseUp(natTable, event);
		}
	}
	
	protected MouseEvent getInitialEvent() {
		return this.initialEvent;
	}
	
	protected MouseEvent getCurrentEvent() {
		return this.currentEvent;
	}
	
}
