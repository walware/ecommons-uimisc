/*******************************************************************************
 * Copyright (c) 2012 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.util;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;


public class StructuredSelectionProxy implements IStructuredSelection {
	
	
	protected final IStructuredSelection fSelection;
	
	
	public StructuredSelectionProxy(final IStructuredSelection selection) {
		fSelection = selection;
	}
	
	
	@Override
	public boolean isEmpty() {
		return fSelection.isEmpty();
	}
	
	@Override
	public int size() {
		return fSelection.size();
	}
	
	@Override
	public Object getFirstElement() {
		return fSelection.getFirstElement();
	}
	
	@Override
	public Iterator iterator() {
		return fSelection.iterator();
	}
	
	@Override
	public List toList() {
		return fSelection.toList();
	}
	
	@Override
	public Object[] toArray() {
		return fSelection.toArray();
	}
	
}
