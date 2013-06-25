/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// -GC
package org.eclipse.nebula.widgets.nattable.resize.command;

import java.util.Collections;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.command.AbstractColumnCommand;
import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;

/**
 * This command triggers the AutoResizeColumms command. It collects the selected
 * columns from the {@link SelectionLayer} and fires the
 * {@link AutoResizeColumnsCommand} on the {@link GridLayer}
 */

public class InitializeAutoResizeColumnsCommand extends AbstractColumnCommand {


	private final ILayer sourceLayer;
	private List<Range> selectedColumnPositions = Collections.emptyList();


	public InitializeAutoResizeColumnsCommand(ILayer layer, long columnPosition) {
		super(layer, columnPosition);

		this.sourceLayer = layer;
	}

	protected InitializeAutoResizeColumnsCommand(InitializeAutoResizeColumnsCommand command) {
		super(command);

		this.sourceLayer = command.sourceLayer;
	}

	public ILayerCommand cloneCommand() {
		return new InitializeAutoResizeColumnsCommand(this);
	}

	// Accessors

	public ILayer getSourceLayer() {
		return sourceLayer;
	}

	public void setSelectedColumnPositions(List<Range> selectedColumnPositions) {
		this.selectedColumnPositions = selectedColumnPositions;
	}

	public List<Range> getColumnPositions() {
		return selectedColumnPositions;
	}
}
