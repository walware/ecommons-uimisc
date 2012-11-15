/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// ~
package org.eclipse.nebula.widgets.nattable.layer;


public final class LayoutCoordinate {
	
	
	public final int x;
	
	public final int y;
	
	
	public LayoutCoordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	
	public int getColumnPosition() {
		return x;
	}
	
	public int getRowPosition() {
		return y;
	}
	
	
	@Override
	public int hashCode() {
		return (x << 13) + x * 17 + (y << 17) + y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof LayoutCoordinate)) {
			return false;
		}
		final LayoutCoordinate other = (LayoutCoordinate) obj;
		return (x == other.x && y == other.y);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + x + "," + y + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
}
