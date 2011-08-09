package net.sourceforge.nattable.columnChooser;


/**
 * Object representation of a NatTable Column. <br/>
 * This is used in the Column chooser dialogs as a mechanism of preserving
 * meta data on the columns in the dialog.
 * 
 * @see ColumnChooserUtils
 */
public class ColumnEntry {
	
	
	private final String label;
	private final Integer index;
	private Integer position;
	
	
	public ColumnEntry(String label, Integer index, Integer position) {
		this.label = label;
		this.index = index;
		this.position = position;
	}
	
	
	public Integer getPosition() {
		return position;
	}
	
	public void setPosition(Integer position) {
		this.position = position;
	}
	
	public Integer getIndex() {
		return index;
	}
	
	public String getLabel() {
		return (label != null) ? label : "<No Label>";
	}
	
	
	@Override
	public int hashCode() {
		return index.hashCode() * 253;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ColumnEntry)) {
			return false;
		}
		final ColumnEntry other = (ColumnEntry) obj;
		return (index.intValue() == other.index.intValue());
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " (label = " + getLabel() + ")";
	}
	
}
