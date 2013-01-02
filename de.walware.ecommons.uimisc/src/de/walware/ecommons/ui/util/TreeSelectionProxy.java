/*******************************************************************************
 * Copyright (c) 2012-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.util;

import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;


public class TreeSelectionProxy extends StructuredSelectionProxy implements ITreeSelection {
	
	
	public TreeSelectionProxy(final ITreeSelection selection) {
		super(selection);
	}
	
	
	@Override
	public TreePath[] getPathsFor(final Object element) {
		return ((ITreeSelection) fSelection).getPathsFor(element);
	}
	
	@Override
	public TreePath[] getPaths() {
		return ((ITreeSelection) fSelection).getPaths();
	}
	
}
