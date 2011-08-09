package net.sourceforge.nattable.coordinate;

import net.sourceforge.nattable.layer.ILayer;


public final class PositionCoordinate {
	
	private final ILayer layer;
	
	public int columnPosition;
	public int rowPosition;
	
	
	public PositionCoordinate(ILayer layer, int columnPosition, int rowPosition) {
		this.layer = layer;
		this.columnPosition = columnPosition;
		this.rowPosition = rowPosition;
	}
	
	public PositionCoordinate(PositionCoordinate coordinate) {
		this.layer = coordinate.layer;
		this.columnPosition = coordinate.columnPosition;
		this.rowPosition = coordinate.rowPosition;
	}
	
	
	public ILayer getLayer() {
		return layer;
	}
	
	public int getColumnPosition() {
		return columnPosition;
	}
	
	public int getRowPosition() {
		return rowPosition;
	}
	
	public void set(int rowPosition, int columnPosition) {
		this.rowPosition = rowPosition;
		this.columnPosition = columnPosition;
	}
	
	@Override
	public int hashCode() {
		return layer.hashCode() + columnPosition * 253
				+ (rowPosition << 16) + rowPosition;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof PositionCoordinate)) {
			return false;
		}
		final PositionCoordinate other = (PositionCoordinate) obj;
		return (this == obj ||
				(this.layer.equals(other.layer)
						&& this.columnPosition == other.columnPosition
						&& this.rowPosition == other.rowPosition ));
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + layer + ":" + columnPosition + "," + rowPosition + "]";
	}
	
}
