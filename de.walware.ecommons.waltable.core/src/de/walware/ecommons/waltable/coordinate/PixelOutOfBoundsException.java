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


public class PixelOutOfBoundsException extends IllegalArgumentException {
	
	
	private static final long serialVersionUID= 1L;
	
	
	public static PixelOutOfBoundsException pixel(final long pixel,
			final Orientation orientation) {
		return new PixelOutOfBoundsException("pixel", pixel, orientation); //$NON-NLS-1$
	}
	
	public static PixelOutOfBoundsException pixel(final long pixel) {
		return new PixelOutOfBoundsException("pixel", pixel); //$NON-NLS-1$
	}
	
	public PixelOutOfBoundsException(final String message) {
		super(message);
	}
	
	public PixelOutOfBoundsException(final String label, final long position) {
		super(label + ": " + position); //$NON-NLS-1$
	}
	
	public PixelOutOfBoundsException(final String label, final long position,
			final Orientation orientation) {
		super(label + " (" + orientation + "): " + position); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
}
