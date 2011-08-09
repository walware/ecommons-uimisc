package net.sourceforge.nattable.coordinate;

import net.sourceforge.nattable.layer.ILayer;


public final class ColumnPositionCoordinate {
	
	
	private final ILayer layer;
	
	public int columnPosition;
	
	
	public ColumnPositionCoordinate(ILayer layer, int columnPosition) {
		this.layer = layer;
		this.columnPosition = columnPosition;
	}
	
	
	public ILayer getLayer() {
		return layer;
	}
	
	public int getColumnPosition() {
		return columnPosition;
	}
	
	
	@Override
	public int hashCode() {
		return layer.hashCode() + columnPosition * 253;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ColumnPositionCoordinate)) {
			return false;
		}
		final ColumnPositionCoordinate other = (ColumnPositionCoordinate) obj;
		return (layer == other.layer
				&& columnPosition == other.columnPosition);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + layer + ":" + columnPosition + "]";
	}
	
}
