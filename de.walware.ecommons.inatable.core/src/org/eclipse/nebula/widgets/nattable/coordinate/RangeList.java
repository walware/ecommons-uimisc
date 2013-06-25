/*******************************************************************************
 * Copyright (c) 2013 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.coordinate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


public class RangeList extends ArrayList<Range> {
	
	
	private static final long serialVersionUID = 1L;
	
	
	public RangeList() {
	}
	
	
	private int indexOfStart(final long value) {
		int low = 0;
		int high = super.size() - 1;
		while (low <= high) {
			final int mid = (low + high) >>> 1;
			final long midValue = get(mid).start;
			if (value > midValue) {
				low = mid + 1;
			}
			else if (value < midValue) {
				high = mid - 1;
			}
			else {
				return mid;
			}
		}
		return -(low + 1);
	}
	
	
	public boolean addValue(final long value) {
		int idx = indexOfStart(value);
		if (idx >= 0) { // value == range1.start
			return false;
		}
		idx = -(idx + 1); // value > range1.start && value < range2.start
		if (idx > 0) {
			final Range range1 = get(idx - 1);
			if (value < range1.end) {
				return false;
			}
			if (value == range1.end) {
				range1.end = value + 1;
				checkMergeNext(range1, idx);
				return true;
			}
		}
		if (idx < size()) {
			final Range range2 = get(idx);
			if (value == range2.start - 1) {
				range2.start = value;
				return true;
			}
		}
		super.add(idx, new Range(value));
		return true;
	}
	
	@Override
	public boolean add(final Range range) {
		if (range.start == range.end) {
			return false;
		}
		int idx = indexOfStart(range.start);
		if (idx >= 0) { // range.start == range1.start
			final Range range1 = get(idx);
			if (range.end <= range1.end) {
				return false;
			}
			range1.end = range.end;
			checkMergeNext(range1, idx + 1);
			return true;
		}
		idx = -(idx + 1); // value > range1.start && value < range2.start
		if (idx > 0) {
			final Range range1 = get(idx - 1);
			if (range.end <= range1.end) {
				return false;
			}
			if (range.start <= range1.end) {
				range1.end = range.end;
				checkMergeNext(range1, idx);
				return true;
			}
		}
		if (idx < size()) {
			final Range range2 = get(idx);
			if (range.end >= range2.start) {
				range2.start = range.start;
				if (range.end > range2.end) {
					range2.end = range.end;
					checkMergeNext(range2, idx + 1);
				}
				return true;
			}
		}
		super.add(idx, range);
		return true;
	}
	
	private void checkMergeNext(final Range range, final int nextIdx) {
		while (nextIdx < size()) {
			final Range range2 = get(nextIdx);
			if (range.end < range2.start) {
				return;
			}
			remove(nextIdx);
			if (range.end <= range2.end) {
				range.end = range2.end;
				return;
			}
		}
	}
	
	public boolean removeValue(final int value) {
		int idx = indexOfStart(value);
		if (idx >= 0) { // value == range1.start
			final Range range1 = get(idx);
			if (value == range1.end - 1) { // single value
				super.remove(idx);
				return true;
			}
			range1.start++;
			return true;
		}
		idx = -(idx + 1); // value > range1.start && value < range2.start
		if (idx > 0) {
			final Range range1 = get(idx - 1);
			if (value >= range1.end) {
				return false;
			}
			if (value == range1.end - 1) {
				range1.end--;
				return true;
			}
			super.add(idx, new Range(value + 1, range1.end));
			range1.end = value;
			return true;
		}
		return false;
	}
	
//	TODO
//	public boolean remove(final Range range) {
//		if (range.size() == 0) {
//			return false;
//		}
//		int idx = indexOfStart(range.start);
//		
//	}
	
	@Override
	public void add(final int index, final Range element) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(final Collection<? extends Range> c) {
		boolean changed = false;
		for (final Range range : c) {
			changed |= add(range);
		}
		return changed;
	}
	
	public boolean containsValue(final long value) {
		int idx = indexOfStart(value);
		if (idx >= 0) {
			return true;
		}
		idx = -(idx + 1);
		if (idx > 0) {
			final Range range1 = get(idx - 1);
			return (value < range1.end);
		}
		return false;
	}
	
	public Range getRange(final long value) {
		int idx = indexOfStart(value);
		if (idx >= 0) { // value == range1.start
			return get(idx);
		}
		idx = -(idx + 1); // value > range1.start && value < range2.start
		if (idx > 0) {
			final Range range1 = get(idx - 1);
			if (value < range1.end) {
				return range1;
			}
		}
		return null;
	}
	
	public long getValueCount() {
		long count = 0;
		final int size = size();
		for (int i = 0; i < size; i++) {
			count += get(i).size();
		}
		return count;
	}
	
	
	@Deprecated // not recommend
	public static Collection<Long> listRanges(final Collection<Range> positions) {
		final ArrayList<Long> list = new ArrayList<Long>();
		for (final Iterator<Range> iter = positions.iterator(); iter.hasNext(); ) {
			final Range range = iter.next();
			final long sum = (long) list.size() + range.size();
			if (sum > 0xffffff) {
				throw new IndexOutOfBoundsException("" + sum); // TODO implement ranges
			}
			list.ensureCapacity((int) sum);
			for (long position = range.start; position < range.end; position++) {
				list.add(position);
			}
		}
		return list;
	}
	
}
