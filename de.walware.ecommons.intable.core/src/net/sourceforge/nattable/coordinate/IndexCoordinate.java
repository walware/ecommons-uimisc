package net.sourceforge.nattable.coordinate;


public final class IndexCoordinate {
	
	public final int columnIndex;
	
	public final int rowIndex;
	
	
	public IndexCoordinate(int columnIndex, int rowIndex) {
		this.columnIndex = columnIndex;
		this.rowIndex = rowIndex;
	}
	
	
	public int getColumnIndex() {
		return columnIndex;
	}
	
	public int getRowIndex() {
		return rowIndex;
	}
	
	
	@Override
	public int hashCode() {
		return columnIndex + (rowIndex << 16) * rowIndex;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof IndexCoordinate == false)) {
			return false;
		}
		
		IndexCoordinate that = (IndexCoordinate) obj;
		return this.getColumnIndex() == that.getColumnIndex()
			&& this.getRowIndex() == that.getRowIndex();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + columnIndex + "," + rowIndex + "]";
	}
	
}
