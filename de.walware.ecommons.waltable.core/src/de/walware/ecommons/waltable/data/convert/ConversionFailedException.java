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
package de.walware.ecommons.waltable.data.convert;

import de.walware.ecommons.waltable.edit.editor.AbstractCellEditor;


/**
 * Exception for handling conversion failures.
 * 
 * As the API should not be modified for the handling of this exception,
 * it is a RuntimeException.
 * To make use of this exception it can be thrown on conversion errors within
 * {@link IDisplayConverter#displayToCanonicalValue(Object)}. 
 * The handling of this exception is done within {@link AbstractCellEditor}
 * where the message is stored and showed within a dialog on trying to commit.
 */
public class ConversionFailedException extends RuntimeException {

	private static final long serialVersionUID= -755775784924211402L;

	public ConversionFailedException(final String message) {
		super(message);
	}
	
	public ConversionFailedException(final String message, final Throwable t) {
		super(message, t);
	}
}
