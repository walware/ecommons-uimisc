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
package de.walware.ecommons.waltable.edit.config;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.walware.ecommons.waltable.data.convert.ConversionFailedException;
import de.walware.ecommons.waltable.data.validate.ValidationFailedException;
import de.walware.ecommons.waltable.edit.editor.AbstractEditErrorHandler;
import de.walware.ecommons.waltable.edit.editor.ICellEditor;
import de.walware.ecommons.waltable.edit.editor.IEditErrorHandler;
import de.walware.ecommons.waltable.internal.WaLTablePlugin;


/**
 * Error handling strategy that simply writes conversion/validation errors to the log. 
 */
public class LoggingErrorHandling extends AbstractEditErrorHandler {
	
	
	/**
	 * Create a new {@link LoggingErrorHandling} with no underlying {@link IEditErrorHandler}
	 */
	public LoggingErrorHandling() {
		super(null);
	}
	
	/**
	 * Create a new {@link LoggingErrorHandling} using the given {@link IEditErrorHandler} as
	 * the underlying to allow chaining of error handling.
	 * @param underlyingErrorHandler The underlying {@link IEditErrorHandler}
	 */
	public LoggingErrorHandling(final IEditErrorHandler underlyingErrorHandler) {
		super(underlyingErrorHandler);
	}
	
	
	/**
	 * {@inheritDoc}
	 * After the error is handled by its underlying {@link IEditErrorHandler},
	 * the error will be logged as a warning.
	 */
	@Override
	public void displayError(final ICellEditor cellEditor, final Exception e) {
		super.displayError(cellEditor, e);
		//for ConversionFailedException and ValidationFailedException we only want to log the corresponding
		//message. Otherwise we need the whole stack trace to find unexpected exceptions
		if (!(e instanceof ConversionFailedException) && !(e instanceof ValidationFailedException)) {
			WaLTablePlugin.log(
					new Status(IStatus.ERROR, WaLTablePlugin.PLUGIN_ID,
							"An error occurred while converting and validating cell value.", e));
		}
	}
	
}
