package net.sourceforge.nattable.coordinate;

import net.sourceforge.nattable.layer.ILayer;


public final class RowPositionCoordinate {
	
	
	private final ILayer layer;
	
	public int rowPosition;
	
	
	public RowPositionCoordinate(ILayer layer, int rowPosition) {
		this.layer = layer;
		this.rowPosition = rowPosition;
	}
	
	
	public ILayer getLayer() {
		return layer;
	}
	
	public int getRowPosition() {
		return rowPosition;
	}
	
	
	@Override
	public int hashCode() {
		return 125315 + layer.hashCode() + rowPosition * 17;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof RowPositionCoordinate)) {
			return false;
		}
		final RowPositionCoordinate other = (RowPositionCoordinate) obj;
		return (layer == other.layer
				&& rowPosition == other.rowPosition);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + layer + ":" + rowPosition + "]";
	}
	
}
