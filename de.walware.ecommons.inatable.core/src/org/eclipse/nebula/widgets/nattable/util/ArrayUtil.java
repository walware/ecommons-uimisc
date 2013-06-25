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
package org.eclipse.nebula.widgets.nattable.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArrayUtil {

	public static final String[] STRING_TYPE_ARRAY = new String[] {};
	public static final long[] INT_TYPE_ARRAY = new long[] {};

	public static <T> List<T> asList(T[] array) {
		return new ArrayList<T>(ArrayUtil.asCollection(array));
	}

	public static <T> Collection<T> asCollection(T[] array) {
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}

	public static List<Long> asLongList(long... ints) {
		ArrayList<Long> list = new ArrayList<Long>();
		for (Long integer : ints) {
			list.add(integer);
		}
		return list;
	}

}
