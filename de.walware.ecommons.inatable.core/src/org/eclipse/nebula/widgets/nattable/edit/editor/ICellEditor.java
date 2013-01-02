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
// ~Direction
package org.eclipse.nebula.widgets.nattable.edit.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.coordinate.Direction;
import org.eclipse.nebula.widgets.nattable.data.convert.IDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.ICellEditHandler;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.widget.EditModeEnum;


/**
 * Implementations are responsible for capturing new cell value during cell edit. 
 */
public interface ICellEditor {
	
	/**
	 * Invoked by the action handlers to initialize the editor
	 * @param parent
	 * @param originalCanonicalValue of the cell being edited
	 * @param initialEditValue the initial key press char which triggered editing
	 * @param editMode the edit mode inline or multi
	 * @param editHandler the edit handler to use
	 * @param cell the cell that is edited
	 * @param configRegistry
	 * @return the SWT {@link Control} to be used for capturing the new cell value
	 */
	public Control activateCell(
			Composite parent,
			Object originalCanonicalValue,
			Character initialEditValue,
			EditModeEnum editMode,
			ICellEditHandler editHandler,
			ILayerCell cell,
			IConfigRegistry configRegistry
	);
	
	/**
	 * @param canonicalValue the data value to be set in the backing bean.
	 * Note: This should be converted using the {@link IDisplayConverter} for display.
	 */
	public void setCanonicalValue(Object canonicalValue);
	
	public Object getCanonicalValue();

	public boolean validateCanonicalValue();
	public boolean validateCanonicalValue(IEditErrorHandler conversionErrorHandler, IEditErrorHandler validationErrorHandler);
	
	public boolean commit(Direction direction, boolean closeAfterCommit);

	/**
	 * Close/dispose the contained {@link Control}
	 */
	public void close();

	public boolean isClosed();
}
