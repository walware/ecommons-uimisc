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
package org.eclipse.nebula.widgets.nattable.columnChooser;

import org.eclipse.nebula.widgets.nattable.Messages;



/**
 * Object representation of a NatTable Column.
 * This is used in the Column chooser dialogs as a mechanism of preserving
 * meta data on the columns in the dialog.
 * 
 * @see ColumnChooserUtils
 */
public class ColumnEntry {

	private final String label;
	private final Long index;
	private Long position;

	public ColumnEntry(String label, Long index, Long position) {
		this.label = label;
		this.index = index;
		this.position = position;
	}

	@Override
	public String toString() {
		return label != null ? label : Messages.getString("ColumnEntry.0"); //$NON-NLS-1$
	}

	public Long getPosition() {
		return position;
	}

	public void setPosition(Long position) {
		this.position = position;
	}

	public Long getIndex() {
		return index;
	}

	public String getLabel() {
		return toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ColumnEntry) {
			ColumnEntry that = (ColumnEntry) obj;
			return index.longValue() == that.index.longValue();
		}

		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return index.hashCode();
	}
}
