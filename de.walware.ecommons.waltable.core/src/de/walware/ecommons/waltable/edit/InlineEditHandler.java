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
// ~
package de.walware.ecommons.waltable.edit;

import de.walware.ecommons.waltable.coordinate.Direction;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.selection.SelectRelativeCellCommand;


/**
 * {@link ICellEditHandler} that handles inline editing of single values.
 * On commit it will execute an {@link UpdateDataCommand} for the specified value
 * and move the selection in the NatTable.
 */
public class InlineEditHandler implements ICellEditHandler {

	/**
	 * The {@link ILayer} to which the column and row positions are related to
	 * 			and on which the update command should be executed
	 */
	private final ILayer layer;
	/**
	 * The column position of the cell that is edited
	 */
	private final long columnPosition;
	/**
	 * The row position of the cell that is edited
	 */
	private final long rowPosition;

	/**
	 * 
	 * @param layer The {@link ILayer} to which the column and row positions are related to
	 * 			and on which the update command should be executed
	 * @param columnPosition The column position of the cell that is edited
	 * @param rowPosition The row position of the cell that is edited
	 */
	public InlineEditHandler(final ILayer layer, final long columnPosition, final long rowPosition) {
		this.layer= layer;
		this.columnPosition= columnPosition;
		this.rowPosition= rowPosition;
	}

	@Override
	public boolean commit(final Object canonicalValue, final Direction direction) {
		final boolean committed= this.layer.doCommand(
				new UpdateDataCommand(this.layer, this.columnPosition, this.rowPosition, canonicalValue));
		
		//only move the selection if the update succeeded, otherwise the editor will stay open
		if (committed) {
			switch (direction) {
			case LEFT:
				this.layer.doCommand(new SelectRelativeCellCommand(Direction.LEFT));
				break;
			case UP:
				this.layer.doCommand(new SelectRelativeCellCommand(Direction.UP));
				break;
			case RIGHT:
				this.layer.doCommand(new SelectRelativeCellCommand(Direction.RIGHT));
				break;
			case DOWN:
				this.layer.doCommand(new SelectRelativeCellCommand(Direction.DOWN));
				break;
			}
		}
		
		return committed;
	}
	
}
