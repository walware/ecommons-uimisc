/*******************************************************************************
 * Copyright (c) 2011-2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.ui.IWorkbenchCommandConstants;

import de.walware.ecommons.ui.util.UIAccess;


/**
 * Handler for command {@link IWorkbenchCommandConstants#NAVIGATE_COLLAPSE_ALL}
 * for a single tree viewer.
 */
public class CollapseAllHandler extends AbstractHandler {
	
	
	private AbstractTreeViewer fViewer;
	
	
	public CollapseAllHandler(final AbstractTreeViewer viewer) {
		if (viewer == null) {
			throw new NullPointerException("viewer");
		}
		fViewer = viewer;
	}
	
	@Override
	public void dispose() {
		fViewer = null;
		super.dispose();
	}
	
	
	public Object execute(final ExecutionEvent event) {
		final AbstractTreeViewer viewer = fViewer;
		if (UIAccess.isOkToUse(viewer)) {
			viewer.collapseAll();
		}
		return null;
	}
	
}
