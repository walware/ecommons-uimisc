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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;


public class ObjectUtils {

	public static long[] asLongArray(Collection<Long> collection) {
		long[] copy = new long[collection.size()];

		int index = 0;
		for (Long value : collection) {
			copy[index] = value.longValue();
			index++;
		}

		return copy;
	}

	/**
	 * Returns an unmodifiable ordered collection.
	 * @param <T>
	 * @param iterator
	 * @return
	 */
	public static <T>Collection<T> asOrderedCollection(Iterator<T> iterator, Comparator<T> comparator) {
		Collection<T> collection = new TreeSet<T>(comparator);
		return addToCollection(iterator, collection);
	}

	private static <T> Collection<T> addToCollection(Iterator<T> iterator, Collection<T> collection) {
		while (iterator.hasNext()) {
			T object = iterator.next();
			collection.add(object);
		}
		return Collections.unmodifiableCollection(collection);
	}

	public static <T>String toString(Collection<T> collection){
		if (collection == null) {
			return "NULL"; //$NON-NLS-1$
		}
		String out = "[ "; //$NON-NLS-1$
		long count = 1;
		for (T object : collection) {
			if(object == null) continue;
			out = out + object.toString();
			if(collection.size() != count){
				out = out + ";\n"; //$NON-NLS-1$
			}
			count++;
		}
		out = out + " ]"; //$NON-NLS-1$
		return out;
	}

	public static <T>String toString(T[] array){
		return toString(Arrays.asList(array));
	}

	/**
	 * @return TRUE is collection is null or contains no elements
	 */
	public static <T> boolean isEmpty(Collection<T> collection) {
		return collection == null || collection.size() == 0;
	}


	private static final Random RANDOM = new Random();

	/**
	 * @return a random Date
	 */
	public static Date getRandomDate() {
		return new Date(RANDOM.nextLong());
	}

	/**
	 * @return 4 digit random Long number
	 */
	public static long getRandomNumber() {
		return RANDOM.nextInt(10000);
	}
	
	private static final ThreadGroup THREAD_GROUP = new ThreadGroup("NatTable"); //$NON-NLS-1$
	
	public static ThreadGroup getNatTableThreadGroup() {
		return THREAD_GROUP;
	}

	public static <T> T getLastElement(List<T> list) {
		return list.get(list.size() - 1);
	}

	public static <T> T getFirstElement(List<T> list) {
		return list.get(0);
	}
}
