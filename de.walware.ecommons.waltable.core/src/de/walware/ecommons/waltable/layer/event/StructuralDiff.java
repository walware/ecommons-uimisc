/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// -depend
package de.walware.ecommons.waltable.layer.event;

import de.walware.ecommons.waltable.coordinate.LRange;


public class StructuralDiff {
	
	public enum DiffTypeEnum {
		ADD, CHANGE, DELETE;
	}
	
	
	private final DiffTypeEnum diffType;
	
	private final LRange beforePositionRange;
	
	private final LRange afterPositionRange;
	
	
	public StructuralDiff(final DiffTypeEnum diffType, final LRange beforePositionRange, final LRange afterPositionRange) {
		if (diffType == null) {
			throw new NullPointerException("diffType");
		}
		if (beforePositionRange == null) {
			throw new NullPointerException("beforePositionRange");
		}
		if (afterPositionRange == null) {
			throw new NullPointerException("afterPositionRange");
		}
		
		this.diffType= diffType;
		this.beforePositionRange= beforePositionRange;
		this.afterPositionRange= afterPositionRange;
	}
	
	public DiffTypeEnum getDiffType() {
		return this.diffType;
	}
	
	public LRange getBeforePositionRange() {
		return this.beforePositionRange;
	}
	
	public LRange getAfterPositionRange() {
		return this.afterPositionRange;
	}
	
	
	@Override
	public int hashCode() {
		return ((((this.diffType.hashCode()
				* 13) + this.beforePositionRange.hashCode())
				* 14) + this.afterPositionRange.hashCode());
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof StructuralDiff)) {
			return false;
		}
		final StructuralDiff other= (StructuralDiff) obj;
		return (this.diffType == other.diffType
				&& this.beforePositionRange == other.beforePositionRange
				&& this.afterPositionRange == other.afterPositionRange );
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()
			+ " " + this.diffType + " ("
			+ " before= " + this.beforePositionRange + ", "
			+ " after= " + this.afterPositionRange + ")";
	}
	
}
