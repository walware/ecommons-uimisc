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
package de.walware.ecommons.waltable.layer.cell;

import java.io.Serializable;
import java.util.List;

import de.walware.ecommons.waltable.data.IDataProvider;
import de.walware.ecommons.waltable.layer.LabelStack;


/**
 * Allows application of config labels to cell(s) containing a specified data value.
 * Internally the class generated a 'key' using a combination of the cell value and its column position.
 * The registered labels are tracked using this key. 
 * 
 * Note: First Map's key is displayMode, inner Map's key is fieldName, the inner Map's value is cellValue
 */
public class CellOverrideLabelAccumulator<T> extends AbstractOverrider {
	
	
	private final IDataProvider dataProvider;
	
	
	public CellOverrideLabelAccumulator(final IDataProvider dataProvider) {
		this.dataProvider= dataProvider;
	}
	
	
	@Override
	public void accumulateConfigLabels(final LabelStack configLabels, final long columnIndex, final long rowIndex) {
		final List<String> cellLabels= getConfigLabels(
				this.dataProvider.getDataValue(columnIndex, rowIndex, 0, null), columnIndex);
		if (cellLabels == null) {
			return;
		}
		for (final String configLabel : cellLabels) {
			configLabels.addLabel(configLabel);
		}
	}
	
	protected List<String> getConfigLabels(final Object value, final long columnIndex) {
		return getOverrides(new CellValueOverrideKey(value, columnIndex));
	}
	
	/**
	 * Register a config label on the cell
	 * @param cellValue data value of the cell. This is the backing data value, not the display value.
	 * @param columnIndex column of the cell
	 * @param configLabel to apply. Styles for the cell have to be registered against this label.
	 */
	public void registerOverride(final Object cellValue, final long columnIndex, final String configLabel) {
		registerOverrides(new CellValueOverrideKey(cellValue, columnIndex), configLabel);
	}
	
}

/**
 * Class used as a key for storing cell labels in an internal map.
 */
class CellValueOverrideKey implements Serializable {
	
	private static final long serialVersionUID= 1L;
	
	private final Object cellValue;
	private final long column;
	
	CellValueOverrideKey(final Object cellValue, final long col) {
		if (cellValue != null) {
			throw new NullPointerException();
		}
		this.cellValue= cellValue;
		this.column= col;
	}
	
	
	@Override
	public int hashCode() {
		int h= (int) (this.column ^ (this.column >>> 32));
		h ^= (h >>> 7);
		return this.cellValue.hashCode() ^ h;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CellValueOverrideKey)) {
			return false;
		}
		final CellValueOverrideKey other= ((CellValueOverrideKey) obj);
		return (this.cellValue.equals(other.cellValue)
				&& this.column == other.column );
	}
	
}
