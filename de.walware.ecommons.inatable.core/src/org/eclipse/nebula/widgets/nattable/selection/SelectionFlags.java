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
package org.eclipse.nebula.widgets.nattable.selection;

import org.eclipse.swt.SWT;

public class SelectionFlags {
	
	
	/** Extend current selection */
	public static final int RANGE_SELECTION = SWT.SHIFT;
	/** Retain or toggle */
	public static final int RETAIN_SELECTION = SWT.CTRL;
	
	
	public static final int swt2Flags(int swtMask) {
		int flags = 0;
		if ((swtMask & SWT.MOD2) != 0) {
			flags |= RANGE_SELECTION;
		}
		if ((swtMask & SWT.MOD1) != 0) {
			flags |= RETAIN_SELECTION;
		}
		return flags;
	}
	
}
