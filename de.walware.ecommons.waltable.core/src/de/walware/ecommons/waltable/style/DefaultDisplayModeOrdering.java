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

package de.walware.ecommons.waltable.style;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class DefaultDisplayModeOrdering implements IDisplayModeLookupStrategy {
	
	private static final List<DisplayMode> NORMAL_ORDERING= Arrays.asList(DisplayMode.NORMAL);
	
	private static final List<DisplayMode> HOVER_ORDERING= Arrays.asList(DisplayMode.HOVER, DisplayMode.NORMAL);
	
	private static final List<DisplayMode> SELECT_ORDERING= Arrays.asList(DisplayMode.SELECT, DisplayMode.NORMAL);
	
	private static final List<DisplayMode> EDIT_ORDERING= Arrays.asList(DisplayMode.EDIT, DisplayMode.NORMAL);
	
	private static final List<DisplayMode> EMPTY_ORDERING= Collections.emptyList();
	
	
	/**
	 * See DefaultDisplayModeOrderingTest
	 */
	@Override
	public List<DisplayMode> getDisplayModeOrdering(final DisplayMode targetDisplayMode) {
		switch (targetDisplayMode) {
		case NORMAL:
			return NORMAL_ORDERING;
		case HOVER:
			return HOVER_ORDERING;
		case SELECT:
			return SELECT_ORDERING;
		case EDIT:
			return EDIT_ORDERING;
		default:
			return EMPTY_ORDERING;
		}
	}
	
}
