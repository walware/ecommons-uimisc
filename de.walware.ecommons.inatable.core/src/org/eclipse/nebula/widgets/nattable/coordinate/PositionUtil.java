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
package org.eclipse.nebula.widgets.nattable.coordinate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class PositionUtil {
	
	
	public static List<Long> concat(List<Long> list, Long add) {
		ArrayList<Long> newList = new ArrayList<Long>(list.size() + 1);
		newList.addAll(list);
		newList.add(add);
		return newList;
	}
	
	/**
	 * Finds contiguous numbers in a group of numbers.
	 * <p>
	 * See ColumnChooserDialogTest#getGroupedByContiguous()
	 */
	public static List<List<Long>> getGroupedByContiguous(Collection<Long> numberCollection) {
		List<Long> numbers = new ArrayList<Long>(numberCollection);
		Collections.sort(numbers);

		List<Long> contiguous = new ArrayList<Long>();
		List<List<Long>> grouped =  new ArrayList<List<Long>>();

		for(int i = 0; i < numbers.size()-1; i++) {
			if(numbers.get(i).longValue()+1 != numbers.get(i+1).longValue()){
				contiguous.add(numbers.get(i));
				grouped.add(contiguous);
				contiguous = new ArrayList<Long>();
			} else {
				contiguous.add(numbers.get(i));
			}
		}
		if (!numbers.isEmpty()) {
			contiguous.add(numbers.get(numbers.size()-1));
		}
		grouped.add(contiguous);
		return grouped;
	}

	/**
	 * <p>Creates {@link Range}s out of list of numbers.
	 * The contiguous numbers are grouped together in Ranges.</p>
	 * 
	 * <p>Example: 0, 1, 2, 4, 5, 6 will return [[Range(0 - 3)][Range(4 - 7)]]</p>
	 * <p>The last number in the Range is not inclusive.</p>
	 */
	public static List<Range> getRanges(Collection<Long> numbers) {
		List<Range> ranges = new ArrayList<Range>();

		if (numbers != null) {
			for (List<Long> number : PositionUtil.getGroupedByContiguous(numbers)) {
				long start = number.get(0).longValue();
				long end = number.get(number.size() - 1).longValue() + 1;

				ranges.add(new Range(start, end));
			}
		}

		return ranges;
	}

}
