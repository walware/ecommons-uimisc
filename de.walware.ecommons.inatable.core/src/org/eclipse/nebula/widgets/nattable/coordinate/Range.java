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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Represents an Range of numbers.
 * Example a Range of selected rows: 1 - 100
 * Ranges are inclusive of their start value and not inclusive of their end value, i.e. start &lt;= x &lt; end
 */
public final class Range implements Comparable<Range> {


	public static void sortByStart(List<Range> ranges) {
		Collections.sort(ranges, new Comparator<Range>() {
			public int compare(Range range1, Range range2) {
				return Integer.valueOf(range1.start).compareTo(
						Integer.valueOf(range2.start));
			}
		});
	}


	public int start = 0;
	public int end = 0;


	public Range(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	public Range(int value) {
		this.start = value;
		this.end = value + 1;
	}
	
	public int size() {
		return end - start;
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
	 * @return TRUE if the range contains the given row position
	 */
	public boolean contains(int position) {
		return position >= start && position < end;
	}

	public boolean overlap(Range range) {
		return (start < end) &&  // this is a non-empty range
				(range.start < range.end) &&  // range parameter is non-empty
				(this.contains(range.start) || this.contains(range.end - 1) || range.contains(start) || range.contains(end - 1));
	}

	public List<Integer> getMembers() {
		final List<Integer> members = new ArrayList<Integer>(this.end - this.start);
		for (int i = start; i < end; i++) {
			members.add(Integer.valueOf(i));
		}
		return members;
	}


	@Override
	public int hashCode() {
		int h = start * 17 + start & 0xff000000 + Integer.rotateRight(end - start, 15) + end;
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
		return (start == other.start) && (end == other.end);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + start + "," + end + "]";
	}
	
}
