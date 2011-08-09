package net.sourceforge.nattable.layer.event;

import net.sourceforge.nattable.coordinate.Range;

public class StructuralDiff {
	
	public enum DiffTypeEnum {
		ADD, CHANGE, DELETE;
	}
	
	
	private final DiffTypeEnum diffType;
	
	private final Range beforePositionRange;
	
	private final Range afterPositionRange;
	
	
	public StructuralDiff(DiffTypeEnum diffType, Range beforePositionRange, Range afterPositionRange) {
		if (diffType == null
				|| beforePositionRange == null || afterPositionRange == null) {
			throw new NullPointerException();
		}
		this.diffType = diffType;
		this.beforePositionRange = beforePositionRange;
		this.afterPositionRange = afterPositionRange;
	}
	
	public DiffTypeEnum getDiffType() {
		return diffType;
	}
	
	public Range getBeforePositionRange() {
		return beforePositionRange;
	}
	
	public Range getAfterPositionRange() {
		return afterPositionRange;
	}
	
	
	@Override
	public int hashCode() {
		return ((((diffType.hashCode()
				* 13) + beforePositionRange.hashCode())
				* 14) + afterPositionRange.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof StructuralDiff)) {
			return false;
		}
		final StructuralDiff other = (StructuralDiff) obj;
		return (diffType == other.diffType
				&& beforePositionRange == other.beforePositionRange
				&& afterPositionRange == other.afterPositionRange);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()
			+ " " + diffType + " ("
			+ " before = " + beforePositionRange + ", "
			+ " after = " + afterPositionRange + ")";
	}
	
}
