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

package de.walware.ecommons.waltable.edit.action;

import org.eclipse.swt.events.KeyEvent;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.edit.EditSelectionCommand;
import de.walware.ecommons.waltable.ui.action.IKeyAction;
import de.walware.ecommons.waltable.ui.matcher.LetterOrDigitKeyEventMatcher;


/**
 * Action that will execute an {@link EditSelectionCommand}.
 * The cell(s) to edit are determined using the SelectionLayer.
 * Therefore this action will only work if a SelectionLayer is
 * in the NatTable layer composition.
 */
public class KeyEditAction implements IKeyAction {
	
	
	public KeyEditAction() {
	}
	
	
	@Override
	public void run(final NatTable natTable, final KeyEvent event) {
		natTable.doCommand(new EditSelectionCommand(
						natTable, 
						natTable.getConfigRegistry(), 
						convertCharToCharacterObject(event) ));
	}
	
	/**
	 * @param event The KeyEvent that triggered the execution of this
	 * 			KeyEditAction.
	 * @return The Character represented by the key that was typed in case
	 * 			it was a letter or digit key, or <code>null</code> if it
	 * 			was a control (like F2) or other key.
	 */
	protected Character convertCharToCharacterObject(final KeyEvent event) {
		Character character= null;
		if (LetterOrDigitKeyEventMatcher.isLetterOrDigit(event.character)) {
			character= Character.valueOf(event.character);
		}
		return character;
	}
	
}
