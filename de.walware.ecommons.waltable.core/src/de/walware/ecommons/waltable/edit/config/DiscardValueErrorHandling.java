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
package de.walware.ecommons.waltable.edit.config;

import de.walware.ecommons.waltable.edit.editor.AbstractEditErrorHandler;
import de.walware.ecommons.waltable.edit.editor.ICellEditor;
import de.walware.ecommons.waltable.edit.editor.IEditErrorHandler;

/**
 * Strategy class for conversion/validation failures.
 * If the entered value is not valid, it is simply discarded.
 * Only handles errors on commit. 
 */
public class DiscardValueErrorHandling extends AbstractEditErrorHandler {
	
	/**
	 * Create a new {@link DiscardValueErrorHandling} with no underlying {@link IEditErrorHandler}
	 */
	public DiscardValueErrorHandling() {
		super(null);
	}
	
	/**
	 * Create a new {@link DiscardValueErrorHandling} using the given {@link IEditErrorHandler} as
	 * the underlying to allow chaining of error handling.
	 * @param underlyingErrorHandler The underlying {@link IEditErrorHandler}
	 */
	public DiscardValueErrorHandling(final IEditErrorHandler underlyingErrorHandler) {
		super(underlyingErrorHandler);
	}
	
	/**
	 * {@inheritDoc}
	 * After the error is handled by its underlying {@link IEditErrorHandler},
	 * the {@link ICellEditor} will be closed, discarding the value.
	 */
	@Override
	public void displayError(final ICellEditor cellEditor, final Exception e) {
		super.displayError(cellEditor, e);
		cellEditor.close();
	}
	
}
