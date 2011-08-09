package net.sourceforge.nattable.layer.cell;

import java.io.Serializable;
import java.util.List;

import net.sourceforge.nattable.data.IRowDataProvider;
import net.sourceforge.nattable.layer.LabelStack;

/*
 * Allows application of config labels to cell(s) containing a specified data value.<br/>
 * Internally the class generated a 'key' using a combination of the cell value and its column position.
 * The registered labels are tracked using this key. 
 * 
 * Note: First Map's key is displayMode, inner Map's key is fieldName, the inner Map's value is cellValue
 */
public class CellOverrideLabelAccumulator<T> extends AbstractOverrider {
	private IRowDataProvider<T> dataProvider;

	public CellOverrideLabelAccumulator(IRowDataProvider<T> dataProvider) {
		this.dataProvider = dataProvider;
	}

	public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
		List<String> cellLabels = getConfigLabels(dataProvider.getDataValue(columnPosition, rowPosition), columnPosition);
		if (cellLabels == null) {
			return;
		}
		for (String configLabel : cellLabels) {
			configLabels.addLabel(configLabel);
		}
	}

	protected List<String> getConfigLabels(Object value, int col) {
		CellValueOverrideKey key = new CellValueOverrideKey(value, col);
		return getOverrides(key);
	}

	/**
	 * Register a config label on the cell
	 * @param cellValue data value of the cell. This is the backing data value, not the display value.
	 * @param col column index of the cell
	 * @param configLabel to apply. Styles for the cell have to be registered against this label.
	 */
	public void registerOverride(Object cellValue, int col, String configLabel) {
		registerOverrides(new CellValueOverrideKey(cellValue, col), configLabel);
	}
}

/**
 * Class used as a key for storing cell labels in an internal map.
 */
class CellValueOverrideKey implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final Object cellValue;
	private final int col;
	
	CellValueOverrideKey(Object cellValue, int col) {
		if (cellValue != null) {
			throw new NullPointerException();
		}
		this.cellValue = cellValue;
		this.col = col;
	}
	
	
	@Override
	public int hashCode() {
		return cellValue.hashCode() + (col * 19);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CellValueOverrideKey)) {
			return false;
		}
		final CellValueOverrideKey other = ((CellValueOverrideKey) obj);
		return (cellValue.equals(other.cellValue) && col == other.col);
	}
	
}
