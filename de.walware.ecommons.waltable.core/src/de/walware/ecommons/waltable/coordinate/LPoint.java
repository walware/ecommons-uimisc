/*******************************************************************************
 * Copyright (c) 2000-2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.waltable.coordinate;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;


/**
 * Instances of this class represent places on the (x, y)
 * coordinate plane.
 * <p>
 * The coordinate space for rectangles and points is considered
 * to have increasing values downward and to the right from its
 * origin making this the normal, computer graphics oriented notion
 * of (x, y) coordinates rather than the strict mathematical one.
 * </p>
 * <p>
 * The hashCode() method in this class uses the values of the public
 * fields to compute the hash value. When storing instances of the
 * class in hashed collections, do not modify these fields after the
 * object has been inserted.  
 * </p>
 *
 * @see LRectangle
 */

public final class LPoint {
	
	/**
	 * the x coordinate of the point
	 */
	public long x;
	
	/**
	 * the y coordinate of the point
	 */
	public long y;
	
	
	/**
	 * Constructs a new point with the given x and y coordinates.
	 *
	 * @param x the x coordinate of the new point
	 * @param y the y coordinate of the new point
	 */
	public LPoint (final long x, final long y) {
		this.x= x;
		this.y= y;
	}
	
	
	public long get(final Orientation orientation) {
		return (orientation == HORIZONTAL) ?
				this.x :
				this.y;
	}
	
	
	@Override
	public int hashCode() {
		int h= (int) (this.x ^ (this.x >>> 32));
		h= Integer.rotateRight(h, 15);
		h ^= (int) (this.y ^ (this.y >>> 32));
		return h ^ (h >>> 7);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof LPoint) {
			final LPoint other= (LPoint) obj;
			return (this.x == other.x && this.y == other.y);
		}
		return false;
	}
	
	
	@Override
	public String toString () {
		return "LPoint {" + this.x + ", " + this.y + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
}
