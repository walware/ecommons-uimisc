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
package de.walware.ecommons.waltable.data;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.walware.ecommons.waltable.internal.WaLTablePlugin;


/**
 * Convenience class which uses java reflection to get/set property names
 *  from the row bean. It looks for getter methods for reading and setter
 *  methods for writing according to the Java conventions.
 *
 * @param <R> type of the row object/bean
 */
public class ReflectiveColumnPropertyAccessor<R> implements IColumnPropertyAccessor<R> {
	
	
	private final List<String> propertyNames;

	private Map<String, PropertyDescriptor> propertyDescriptorMap;

	/**
	 * @param propertyNames of the members of the row bean
	 */
	public ReflectiveColumnPropertyAccessor(final String[] propertyNames) {
		this.propertyNames= Arrays.asList(propertyNames);
	}

	@Override
	public long getColumnCount() {
		return this.propertyNames.size();
	}

	@Override
	public Object getDataValue(final R rowObj, final long columnIndex) {
		try {
			final PropertyDescriptor propertyDesc= getPropertyDescriptor(rowObj, columnIndex);
			final Method readMethod= propertyDesc.getReadMethod();
			return readMethod.invoke(rowObj);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setDataValue(final R rowObj, final long columnIndex, final Object newValue) {
		try {
			final PropertyDescriptor propertyDesc= getPropertyDescriptor(rowObj, columnIndex);
			final Method writeMethod= propertyDesc.getWriteMethod();
			if (writeMethod == null) {
				throw new RuntimeException("Setter method not found in backing bean for value at column index: " + columnIndex); //$NON-NLS-1$
			}
			writeMethod.invoke(rowObj, newValue);
		} catch (final IllegalArgumentException ex) {
			WaLTablePlugin.log(new Status(IStatus.WARNING, WaLTablePlugin.PLUGIN_ID,
					"Data type being set does not match the data type of the setter method in the backing bean", ex )); //$NON-NLS-1$
		} catch (final Exception e) {
			WaLTablePlugin.log(new Status(IStatus.ERROR, WaLTablePlugin.PLUGIN_ID,
					"Error while setting data value", e )); //$NON-NLS-1$
			throw new RuntimeException("Error while setting data value"); //$NON-NLS-1$
		}
	};

	@Override
	public String getColumnProperty(final long columnIndex) {
		if (columnIndex >= Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		return this.propertyNames.get((int) columnIndex);
	}

	@Override
	public long getColumnIndex(final String propertyName) {
		return this.propertyNames.indexOf(propertyName);
	}

	private PropertyDescriptor getPropertyDescriptor(final R rowObj, final long columnIndex) throws IntrospectionException {
		if (columnIndex >= Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		if (this.propertyDescriptorMap == null) {
			this.propertyDescriptorMap= new HashMap<>();
			final PropertyDescriptor[] propertyDescriptors= Introspector.getBeanInfo(rowObj.getClass()).getPropertyDescriptors();
			for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				this.propertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
			}
		}

		final String propertyName= this.propertyNames.get((int) columnIndex);
		return this.propertyDescriptorMap.get(propertyName);
	}

}
