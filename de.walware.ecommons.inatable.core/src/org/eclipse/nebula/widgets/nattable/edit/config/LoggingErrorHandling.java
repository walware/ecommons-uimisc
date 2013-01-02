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
package org.eclipse.nebula.widgets.nattable.edit.config;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.nebula.widgets.nattable.data.convert.ConversionFailedException;
import org.eclipse.nebula.widgets.nattable.data.validate.ValidationFailedException;
import org.eclipse.nebula.widgets.nattable.edit.editor.AbstractEditErrorHandler;
import org.eclipse.nebula.widgets.nattable.edit.editor.ICellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.IEditErrorHandler;
import org.eclipse.nebula.widgets.nattable.internal.NatTablePlugin;


/**
 * Error handling strategy that simply logs if 
 * 
 * @author fipro
 *
 */
public class LoggingErrorHandling extends AbstractEditErrorHandler {
	
	
	public LoggingErrorHandling() {
		super(null);
	}
	
	public LoggingErrorHandling(IEditErrorHandler underlyingErrorHandler) {
		super(underlyingErrorHandler);
	}
	
	
	@Override
	public void displayError(ICellEditor cellEditor, Exception e) {
		super.displayError(cellEditor, e);
		//for ConversionFailedException and ValidationFailedException we only want to log the corresponding
		//message. Otherwise we need the whole stack trace to find unexpected exceptions
		if (!(e instanceof ConversionFailedException) && !(e instanceof ValidationFailedException)) {
			NatTablePlugin.log(
					new Status(IStatus.ERROR, NatTablePlugin.PLUGIN_ID,
							"An error occurred while converting and validating cell value.", e));
		}
	}
	
}
