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
package de.walware.ecommons.waltable.data.convert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.walware.ecommons.waltable.Messages;
import de.walware.ecommons.waltable.internal.WaLTablePlugin;


/**
 * Converts a java.util.Date object to a given format and vice versa
 */
public class DefaultDateDisplayConverter extends DisplayConverter {

	private SimpleDateFormat dateFormat;

	/**
	 * Convert {@link Date} to {@link String} using the default format from {@link SimpleDateFormat}
	 */
	public DefaultDateDisplayConverter() {
		this(null, null);
	}
	
	public DefaultDateDisplayConverter(final TimeZone timeZone) {
		this(null, timeZone);
	}

	/**
	 * @param dateFormat as specified in {@link SimpleDateFormat}
	 */
	public DefaultDateDisplayConverter(final String dateFormat) {
		this(dateFormat, null);
	}
	
	public DefaultDateDisplayConverter(final String dateFormat, final TimeZone timeZone) {
		if (dateFormat != null) {
			this.dateFormat= new SimpleDateFormat(dateFormat);
		} else {
			this.dateFormat= new SimpleDateFormat();
		}
		
		if (timeZone != null) {
			this.dateFormat.setTimeZone(timeZone);
		}
	}

	@Override
	public Object canonicalToDisplayValue(final Object canonicalValue) {
		try {
			if (canonicalValue != null) {
				return this.dateFormat.format(canonicalValue);
			}
		} catch (final IllegalArgumentException e) {
			WaLTablePlugin.log(new Status(IStatus.WARNING, WaLTablePlugin.PLUGIN_ID,
					"Invalid date value", e ));
		}
		return ""; //$NON-NLS-1$
	}

	@Override
	public Object displayToCanonicalValue(final Object displayValue) {
		try {
			return this.dateFormat.parse(displayValue.toString());
		} catch (final Exception e) {
			throw new ConversionFailedException(
					Messages.getString("DefaultDateDisplayConverter.failure", //$NON-NLS-1$
							new Object[] {displayValue, this.dateFormat.toPattern()}), e);
		}
	}

}
