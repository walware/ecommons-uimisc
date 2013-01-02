/*******************************************************************************
 * Copyright (c) 2010-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.util;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;


/**
 * Viewer Comparator using a common comparator to compare the elements.
 */
public class ComparatorViewerComparator extends ViewerComparator {
	
	
	private final Comparator fComparator;
	
	
	public ComparatorViewerComparator(Comparator comparator) {
		fComparator = comparator;
	}
	
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		return fComparator.compare(e1, e2);
	}
	
	@Override
	public void sort(Viewer viewer, Object[] elements) {
		Arrays.sort(elements, fComparator);
	}
	
}
