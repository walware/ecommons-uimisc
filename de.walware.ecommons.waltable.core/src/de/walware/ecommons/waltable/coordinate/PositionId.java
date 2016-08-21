/*******************************************************************************
 * Copyright (c) 2013-2016 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package de.walware.ecommons.waltable.coordinate;


public class PositionId {
	
	
	private static final int CAT_SHIFT= 52;
	
	
	public static final long CAT_MASK= 0x0_7FFL << CAT_SHIFT;
	public static final long NUM_MASK= ~(0x0_FFFL << CAT_SHIFT);
	
	public static final long BODY_CAT= 0x001L << CAT_SHIFT;
	public static final long HEADER_CAT= 0x002L << CAT_SHIFT;
	
	public static final long PLACEHOLDER_CAT= 0x010L << CAT_SHIFT;
	
	
	public static String toString(final long id) {
		return String.format("<%03X:%d>", (id & CAT_MASK) >>> CAT_SHIFT, id & NUM_MASK); //$NON-NLS-1$
	}
	
}
