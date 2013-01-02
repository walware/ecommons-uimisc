/*******************************************************************************
 * Copyright (c) 2012-2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// -depend
package org.eclipse.nebula.widgets.nattable.layer.event;

import org.eclipse.nebula.widgets.nattable.coordinate.Range;


public class StructuralDiff {
	
	public enum DiffTypeEnum {
		ADD, CHANGE, DELETE;
	}
	
	
	private final DiffTypeEnum diffType;
	
	private final Range beforePositionRange;
	
	private final Range afterPositionRange;
	
	
	public StructuralDiff(DiffTypeEnum diffType, Range beforePositionRange, Range afterPositionRange) {
		if (diffType == null) {
			throw new NullPointerException("diffType");
		}
		if (beforePositionRange == null) {
			throw new NullPointerException("beforePositionRange");
		}
		if (afterPositionRange == null) {
			throw new NullPointerException("afterPositionRange");
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
				&& afterPositionRange == other.afterPositionRange );
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()
			+ " " + diffType + " ("
			+ " before = " + beforePositionRange + ", "
			+ " after = " + afterPositionRange + ")";
	}
	
}
