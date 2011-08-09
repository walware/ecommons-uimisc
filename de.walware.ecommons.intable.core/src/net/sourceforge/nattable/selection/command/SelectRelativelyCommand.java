/*******************************************************************************
 * Copyright (c) 2010 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package net.sourceforge.nattable.selection.command;

import java.util.Set;

import net.sourceforge.nattable.command.AbstractNavigationCommand;
import net.sourceforge.nattable.coordinate.IRelative.Direction;
import net.sourceforge.nattable.coordinate.IRelative.Scale;



public class SelectRelativelyCommand extends AbstractNavigationCommand
		implements ISelectionCommand {
	
	
	private final Set<SelectionFlag> fSelectionFlags;
	
	
	public SelectRelativelyCommand(final Direction direction,
			final Scale stepSize, final Set<SelectionFlag> selectionFlags) {
		this(direction, stepSize, 1, selectionFlags);
	}
	
	public SelectRelativelyCommand(final Direction direction,
			Scale scale, int stepCount, final Set<SelectionFlag> selectionFlags) {
		super(direction, scale, stepCount);
		
		fSelectionFlags = selectionFlags;
	}
	
	protected SelectRelativelyCommand(SelectRelativelyCommand command) {
		super(command);
		
		fSelectionFlags = command.fSelectionFlags;
	}
	
	@Override
	public SelectRelativelyCommand cloneCommand() {
		return new SelectRelativelyCommand(this);
	}
	
	
	public Set<SelectionFlag> getSelectionFlags() {
		return fSelectionFlags;
	}
	
}
