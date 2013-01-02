/*******************************************************************************
 * Copyright (c) 2009-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.IHandler2;


/**
 * Utility to manage the command handler of a service layer (view, dialog, etc.).
 */
public class HandlerCollection {
	
	
	private final Map<String, IHandler2> fHandlers = new HashMap<String, IHandler2>();
	
	
	public HandlerCollection() {
	}
	
	
	public void add(final String commandId, final IHandler2 handler) {
		if (commandId == null || handler == null) {
			throw new NullPointerException();
		}
		fHandlers.put(commandId, handler);
	}
	
	public IHandler2 get(final String commandId) {
		return fHandlers.get(commandId);
	}
	
	public void update(final Object evaluationContext) {
		for (final IHandler2 handler : fHandlers.values()) {
			handler.setEnabled(evaluationContext);
		}
	}
	
	
	public void dispose() {
		for (final IHandler2 handler : fHandlers.values()) {
			handler.dispose();
		}
		fHandlers.clear();
	}
	
}
