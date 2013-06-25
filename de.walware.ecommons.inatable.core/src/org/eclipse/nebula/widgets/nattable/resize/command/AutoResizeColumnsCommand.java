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

import org.eclipse.nebula.widgets.nattable.command.AbstractMultiColumnCommand;
import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.RangeList;


/**
 * Command indicating that all selected columns have to be auto resized i.e made
 * wide enough to just fit the widest cell. This should also take the column
 * header into account
 * 
 * Note: The {@link InitializeAutoResizeColumnsCommand} has to be fired first
 * when autoresizing columns.
 */

public class AutoResizeColumnsCommand extends AbstractMultiColumnCommand {


	public AutoResizeColumnsCommand(InitializeAutoResizeColumnsCommand initCommand) {
		super(initCommand.getLayer(), RangeList.listRanges(initCommand.getColumnPositions()));
	}

	protected AutoResizeColumnsCommand(AutoResizeColumnsCommand command) {
		super(command);
	}

	public ILayerCommand cloneCommand() {
		return new AutoResizeColumnsCommand(this);
	}

}
