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
package org.eclipse.nebula.widgets.nattable.sort.command;

import org.eclipse.nebula.widgets.nattable.command.AbstractColumnCommand;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.sort.SortDirectionEnum;

public class SortColumnCommand extends AbstractColumnCommand {


	private boolean accumulate;
	
	private SortDirectionEnum direction;


	public SortColumnCommand(final ILayer layer, final long columnPosition,
			final boolean accumulate) {
		super(layer, columnPosition);
		this.accumulate = accumulate;
	}
	
	/**
	 * 
	 * @param layer the initial layer
	 * @param columnPosition the column position in the layer
	 * @param direction the sort direction or <code>null</code> for automatic iteration.
	 * @param accumulate
	 */
	public SortColumnCommand(final ILayer layer, final long columnPosition,
			final SortDirectionEnum direction, boolean accumulate) {
		super(layer, columnPosition);
		this.direction = direction;
		this.accumulate = accumulate;
	}

	protected SortColumnCommand(SortColumnCommand command) {
		super(command);
		this.accumulate = command.accumulate;
		this.direction = command.direction;
	}

	public SortColumnCommand cloneCommand() {
		return new SortColumnCommand(this);
	}


	public boolean isAccumulate() {
		return accumulate;
	}

	/**
	 * The sort direction, if specified.
	 * 
	 * @return the sort direction or <code>null</code> for automatic iteration
	 */
	public SortDirectionEnum getDirection() {
		return direction;
	}

}
