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
package org.eclipse.nebula.widgets.nattable.resize.command;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.nebula.widgets.nattable.command.AbstractRowCommand;
import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;

/**
 * @see InitializeAutoResizeColumnsCommand
 */
public class InitializeAutoResizeRowsCommand extends AbstractRowCommand {

	private final ILayer sourceLayer;
	private Collection<Range> selectedRowPositions = Collections.emptyList();
	
	
	public InitializeAutoResizeRowsCommand(ILayer layer, long rowPosition) {
		super(layer, rowPosition);
		
		this.sourceLayer = layer;
	}

	protected InitializeAutoResizeRowsCommand(InitializeAutoResizeRowsCommand command) {
		super(command);
		
		this.sourceLayer = command.sourceLayer;
	}

	public ILayerCommand cloneCommand() {
		return new InitializeAutoResizeRowsCommand(this);
	}

	// Accessors

	public ILayer getSourceLayer() {
		return sourceLayer;
	}

	public void setSelectedRowPositions(final Collection<Range> selectedRowPositions) {
		this.selectedRowPositions = selectedRowPositions;
	}

	public Collection<Range> getRowPositions() {
		return selectedRowPositions;
	}
	
}
