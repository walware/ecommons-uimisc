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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * A special list for {@link LRange ranges}, which sorts and merges automatically all added ranges.
 * <p>
 * Important Note: Added range objects may be changed if the list is modified. The range objects 
 * must not be changed outside of the list.</p>
 * <p>
 * The add and remove methods of LRangeList guarantees that the ranges in a list object are never 
 * empty, do not intersect and touch and are always sorted.</p>
 * <p>
 * The class provides additionally direct {@link #values() access} to the single values described by
 * the ranges. 
 */
public final class LRangeList extends ArrayList<LRange> implements Set<LRange> {
	
	
	private static final long serialVersionUID= 1L;
	
	
	/**
	 * Iterator which allows to iterate over the values of a collection with {@link LRange} elements.
	 * 
	 * @see LRangeList#valuesIterator()
	 */
	public static final class ValueIterator implements ILValueIterator {
		
		
		private final Iterator<LRange> rangeIter;
		
		private long nextValue;
		private long rangeEnd= -1;
		
		
		/**
		 * Creates a new iterator.
		 * 
		 * @param c the collection to iterate over
		 */
		public ValueIterator(/*@NonNull*/ final Collection<LRange> c) {
			this.rangeIter= c.iterator();
		}
		
		
		@Override
		public boolean hasNext() {
			while (this.nextValue >= this.rangeEnd) {
				if (!this.rangeIter.hasNext()) {
					return false;
				}
				final LRange lRange= this.rangeIter.next();
				this.nextValue= lRange.start;
				this.rangeEnd= lRange.end;
			}
			return true;
		}
		
		@Override
		public Long next() {
			return Long.valueOf(nextValue());
		}
		
		@Override
		public long nextValue() {
			while (this.nextValue >= this.rangeEnd) {
				final LRange lRange= this.rangeIter.next();
				this.nextValue= lRange.start;
				this.rangeEnd= lRange.end;
			}
			return this.nextValue++;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	
	public static LRangeList toRangeList(final Collection<LRange> collection) {
		if (collection instanceof LRangeList) {
			return (LRangeList) collection;
		}
		final LRangeList list= new LRangeList();
		for (final LRange lRange : collection) {
			list.add(lRange);
		}
		return list;
	}
	
	
	private final Values values= new Values();
	
	
	/**
	 * Creates a new empty list
	 */
	public LRangeList() {
	}
	
	/**
	 * Creates a new list initially filled with the specified ranges.
	 * 
	 * @param initialRanges the ranges initially added to the list
	 */
	public LRangeList(final LRange... initialRanges) {
		this();
		
		for (int i= 0; i < initialRanges.length; i++) {
			add(initialRanges[i]);
		}
	}
	
	/**
	 * Creates a new list initially filled with the specified values.
	 * 
	 * @param initialValues the values initially added to the list
	 */
	public LRangeList(final long... initialValues) {
		this();
		
		for (int i= 0; i < initialValues.length; i++) {
			this.values.add(initialValues[i]);
		}
	}
	
	
	private int indexOfStart(final long value) {
		int low= 0;
		int high= super.size() - 1;
		while (low <= high) {
			final int mid= (low + high) >>> 1;
			final long midValue= get(mid).start;
			if (value > midValue) {
				low= mid + 1;
			}
			else if (value < midValue) {
				high= mid - 1;
			}
			else {
				return mid;
			}
		}
		return -(low + 1);
	}
	
	
	@Override
	public boolean add(final LRange lRange) {
		if (lRange.start == lRange.end) {
			return false;
		}
		int idx= indexOfStart(lRange.start);
		if (idx >= 0) { // range.start == range1.start
			final LRange range1= get(idx);
			if (lRange.end <= range1.end) {
				return false;
			}
			range1.end= lRange.end;
			checkMergeNext(range1, idx + 1);
			return true;
		}
		idx= -(idx + 1); // value > range1.start && value < range2.start
		if (idx > 0) {
			final LRange range1= get(idx - 1);
			if (lRange.end <= range1.end) {
				return false;
			}
			if (lRange.start <= range1.end) {
				range1.end= lRange.end;
				checkMergeNext(range1, idx);
				return true;
			}
		}
		if (idx < size()) {
			final LRange range2= get(idx);
			if (lRange.end >= range2.start) {
				range2.start= lRange.start;
				if (lRange.end > range2.end) {
					range2.end= lRange.end;
					checkMergeNext(range2, idx + 1);
				}
				return true;
			}
		}
		super.add(idx, lRange);
		return true;
	}
	
	@Override
	public void add(final int index, final LRange element) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(final Collection<? extends LRange> c) {
		boolean changed= false;
		for (final LRange lRange : c) {
			changed |= add(lRange);
		}
		return changed;
	}
	
	@Override
	public boolean addAll(final int index, final Collection<? extends LRange> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public LRange set(final int index, final LRange element) {
		throw new UnsupportedOperationException();
	}
	
	private void checkMergeNext(final LRange lRange, final int nextIdx) {
		// range.start < range2.start
		while (nextIdx < size()) {
			final LRange range2= get(nextIdx);
			if (lRange.end < range2.start) {
				break;
			}
			remove(nextIdx);
			if (lRange.end <= range2.end) {
				lRange.end= range2.end;
				return;
			}
		}
	}
	
	public boolean remove(final LRange lRange) {
		if (lRange.size() == 0) {
			return false;
		}
		int idx= indexOfStart(lRange.start);
		if (idx >= 0) { // range.start == range1.start
			final LRange range1= get(idx);
			if (lRange.end < range1.end) {
				range1.start= lRange.end;
				return true;
			}
			super.remove(idx);
			if (lRange.end == range1.end) {
				return true;
			}
			checkRemoveNext(lRange, idx);
			return true;
		}
		idx= -(idx + 1); // range.start > range1.start && range.start < range2.start
		if (idx > 0) {
			final LRange range1= get(idx - 1);
			if (lRange.start < range1.end) {
				if (lRange.end < range1.end) {
					super.add(idx++, new LRange(lRange.end, range1.end));
				}
				range1.end= lRange.start;
				checkRemoveNext(lRange, idx);
				return true;
			}
		}
		return checkRemoveNext(lRange, idx);
	}
	
	@Override
	public boolean remove(final Object o) {
		if (o instanceof LRange) {
			return remove((LRange) o);
		}
		return false;
	}
	
	@Override
	public boolean removeAll(final Collection<?> c) {
		boolean changed= false;
		for (final Object o : c) {
			if (o instanceof LRange) {
				changed |= remove((LRange) o);
			}
		}
		return changed;
	}
	
	private boolean checkRemoveNext(final LRange lRange, final int nextIdx) {
		boolean changed= false;
		// range.start < range2.start
		while (nextIdx < size()) {
			final LRange range2= get(nextIdx);
			if (lRange.end < range2.start) {
				break;
			}
			if (lRange.end < range2.end) {
				range2.start= lRange.end;
				return true;
			}
			remove(nextIdx);
			changed= true;
			if (lRange.end == range2.end) {
				return true;
			}
		}
		return changed;
	}
	
	
	/**
	 * Ordered set of single values described by the range list.
	 * 
	 * The class provides a similar interface like other Java collections to add, remove and
	 * check for containment and access of values. But the set works on primitive values and allows
	 * to support larger sizes in future.
	 */
	public final class Values implements /*OrderedSet<int>*/ Iterable<Long> {
		
		
		public boolean isEmpty() {
			return LRangeList.this.isEmpty();
		}
		
		public long size() {
			long count= 0;
			final int size= LRangeList.super.size();
			for (int i= 0; i < size; i++) {
				count+= LRangeList.super.get(i).size();
			}
			return count;
		}
		
		public boolean contains(final long value) {
			int idx= indexOfStart(value);
			if (idx >= 0) { // value == range1.start
				return true;
			}
			idx= -(idx + 1); // value > range1.start && value < range2.start
			if (idx > 0) {
				final LRange range1= LRangeList.super.get(idx - 1);
				return (value < range1.end);
			}
			return false;
		}
		
		@Override
		public ILValueIterator iterator() {
			return new ValueIterator(LRangeList.this);
		}
		
		public boolean add(final long value) {
			int idx= indexOfStart(value);
			if (idx >= 0) { // value == range1.start
				return false;
			}
			idx= -(idx + 1); // value > range1.start && value < range2.start
			if (idx > 0) {
				final LRange range1= LRangeList.super.get(idx - 1);
				if (value < range1.end) {
					return false;
				}
				if (value == range1.end) {
					range1.end= value + 1;
					checkMergeNext(range1, idx);
					return true;
				}
			}
			if (idx < LRangeList.super.size()) {
				final LRange range2= LRangeList.super.get(idx);
				if (value == range2.start - 1) {
					range2.start= value;
					return true;
				}
			}
			LRangeList.super.add(idx, new LRange(value));
			return true;
		}
		
		public boolean remove(final long value) {
			int idx= indexOfStart(value);
			if (idx >= 0) { // value == range1.start
				final LRange range1= LRangeList.super.get(idx);
				if (value == range1.end - 1) { // single value
					LRangeList.super.remove(idx);
					return true;
				}
				range1.start++;
				return true;
			}
			idx= -(idx + 1); // value > range1.start && value < range2.start
			if (idx > 0) {
				final LRange range1= LRangeList.super.get(idx - 1);
				if (value >= range1.end) {
					return false;
				}
				if (value == range1.end - 1) {
					range1.end--;
					return true;
				}
				LRangeList.super.add(idx, new LRange(value + 1, range1.end));
				range1.end= value;
				return true;
			}
			return false;
		}
		
		public void clear() {
			LRangeList.this.clear();
		}
		
		public long first() {
			return LRangeList.super.get(0).start;
		}
		
		public long last() {
			return LRangeList.super.get(LRangeList.super.size() - 1).end - 1;
		}
		
		public LRange getRangeOf(final long value) {
			int idx= indexOfStart(value);
			if (idx >= 0) { // value == range1.start
				return LRangeList.super.get(idx);
			}
			idx= -(idx + 1); // value > range1.start && value < range2.start
			if (idx > 0) {
				final LRange range1= LRangeList.super.get(idx - 1);
				if (value < range1.end) {
					return range1;
				}
			}
			return null;
		}
		
		
		private List<LRange> getRangeList() {
			return LRangeList.this;
		}
		
		@Override
		public int hashCode() {
			return LRangeList.this.hashCode() ^ 345;
		}
		
		@Override
		public boolean equals(final Object obj) {
			return ((this == obj
					|| (obj instanceof Values 
							&& LRangeList.this.equals(((Values) obj).getRangeList())) ));
		}
		
	}
	
	/**
	 * Provides direct access to the single values of this list.
	 * 
	 * @return the values of the list
	 */
	public Values values() {
		return this.values;
	}
	
	
	@Deprecated // not recommend
	public static Collection<Long> listValues(final Collection<LRange> positions) {
		final ArrayList<Long> list= new ArrayList<>();
		for (final Iterator<LRange> iter= positions.iterator(); iter.hasNext(); ) {
			final LRange lRange= iter.next();
			final long sum= list.size() + lRange.size();
			if (sum > 0xffffff) {
				throw new IndexOutOfBoundsException("" + sum); //$NON-NLS-1$ // TODO implement ranges
			}
			list.ensureCapacity((int) sum);
			for (long position= lRange.start; position < lRange.end; position++) {
				list.add(position);
			}
		}
		return list;
	}
	
}
