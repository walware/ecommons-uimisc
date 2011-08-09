package net.sourceforge.nattable.coordinate;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Represents an Range of numbers.
 * Example a Range of selected rows: 1 - 100
 * Ranges are inclusive of their start value and not inclusive of their end value, i.e. start <= x < end
 */
public class Range {
	
	
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
	
	public int size() {
		return end - start;
	}
	
	
	/**
	 * @return TRUE if the range contains the given row position
	 */
	public boolean contains(int position) {
		return position >= start && position < end;
	}
	
	public boolean overlap(Range range) {
		return this.contains(range.start);
	}
	
	public Set<Integer> getMembers() {
		final Set<Integer> members = new HashSet<Integer>();
		for (int i = start; i < end; i++) {
			members.add(Integer.valueOf(i));
		}
		return members;
	}
	
	
	@Override
	public int hashCode() {
		return start * 17 + (end << 16) + end;
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
