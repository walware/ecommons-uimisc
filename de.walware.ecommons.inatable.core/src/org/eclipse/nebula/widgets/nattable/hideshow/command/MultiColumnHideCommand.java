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
package org.eclipse.nebula.widgets.nattable.hideshow.command;

import org.eclipse.nebula.widgets.nattable.command.AbstractMultiColumnCommand;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;

public class MultiColumnHideCommand extends AbstractMultiColumnCommand {

	public MultiColumnHideCommand(ILayer layer, long columnPosition) {
		this(layer, new long[] { columnPosition });
	}

	public MultiColumnHideCommand(ILayer layer, long[] columnPositions) {
		super(layer, columnPositions);
	}
	
	protected MultiColumnHideCommand(MultiColumnHideCommand command) {
		super(command);
	}
	
	public MultiColumnHideCommand cloneCommand() {
		return new MultiColumnHideCommand(this);
	}
	
}
