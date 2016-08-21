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
package de.walware.ecommons.waltable.edit.editor;

/**
 * Enumeration for selection mode within a TextCellEditor.
 */
public enum EditorSelectionEnum {

	/**
	 * Select the whole text contained in the text editor control.
	 */
	ALL,
	/**
	 * In fact selects nothing, simply sets the cursor at the beginning
	 * of the contained text in the text editor control.
	 */
	START,
	/**
	 * In fact selects nothing, simply sets the cursor at the end
	 * of the contained text in the text editor control.
	 */
	END
	
}
