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


public class PositionOutOfBoundsException extends IllegalArgumentException {
	
	
	private static final long serialVersionUID= 1L;
	
	
	public static PositionOutOfBoundsException position(final long position,
			final Orientation orientation) {
		return new PositionOutOfBoundsException("position", position, orientation); //$NON-NLS-1$
	}
	
	public static PositionOutOfBoundsException position(final long position) {
		return new PositionOutOfBoundsException("position", position); //$NON-NLS-1$
	}
	
	public static PositionOutOfBoundsException underlyingPosition(final long position) {
		return new PositionOutOfBoundsException("underlyingPosition", position); //$NON-NLS-1$
	}
	
	public static PositionOutOfBoundsException refPosition(final long position,
			final Orientation orientation) {
		return new PositionOutOfBoundsException("refPosition", position, orientation); //$NON-NLS-1$
	}
	
	
	public PositionOutOfBoundsException(final String message) {
		super(message);
	}
	
	public PositionOutOfBoundsException(final String label, final long position) {
		super(label + ": " + position); //$NON-NLS-1$
	}
	
	public PositionOutOfBoundsException(final String label, final long position,
			final Orientation orientation) {
		super(label + " (" + orientation + "): " + position); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
}
