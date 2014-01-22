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
// ~
package org.eclipse.nebula.widgets.nattable.coordinate;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents an Range of numbers.
 * Example a Range of selected rows: 1 - 100
 * Ranges are inclusive of their start value and not inclusive of their end value, i.e. start &lt;= x &lt; end
 */
public final class Range implements Comparable<Range> {
	
	
	public long start;
	public long end;
	
	
	/**
	 * Creates a new range with the specified start and end values.
	 */
	public Range(final long start, final long end) {
		this.start = start;
		this.end = end;
	}
	
	/**
	 * Creates a new range which contains the specified single value.
	 */
	public Range(final long value) {
		this.start = value;
		this.end = value + 1;
	}
	
	
	public long size() {
		return this.end - this.start;
	}
	
	
	@Override
	public int compareTo(Range o) {
		if (this.start < o.start) {
			return -1;
		}
		if (this.start > o.start) {
			return 1;
		}
		if (this.end < o.end) {
			return -1;
		}
		if (this.end > o.end) {
			return 1;
		}
		return 0;
	}
	
	/**
	 * @return <code>true</code> if the range contains the given row position
	 */
	public boolean contains(long position) {
		return ((position >= start) && (position < end));
	}

	public boolean overlap(Range range) {
		return ((this.start < this.end) &&  // this is a non-empty range
				(range.start < range.end) &&  // range parameter is non-empty
				(contains(range.start) || contains(range.end - 1) || range.contains(this.start) || range.contains(this.end - 1)) );
	}

	public List<Long> getMembers() {
		final long l = size();
		if (l > Integer.MAX_VALUE) {
			throw new RuntimeException("too long: " + l);
		}
		final List<Long> members = new ArrayList<Long>((int) l);
		for (long i = start; i < end; i++) {
			members.add(Long.valueOf(i));
		}
		return members;
	}


	@Override
	public int hashCode() {
		int h = (int) (this.start ^ (this.start >>> 32));
		h = Integer.rotateRight(h, 15);
		h ^= (int) (this.end ^ (this.end >>> 32));
		return h ^ (h >>> 7);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Range)) {
			return false;
		}
		final Range other = (Range) obj;
		return ((this.start == other.start) && (this.end == other.end));
	}
	
	@Override
	public String toString() {
		return "Range {" + start + ", " + end + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
}
