package net.sourceforge.nattable.layer;


public final class LayoutCoordinate {
	
	
	public final int x;
	
	public final int y;
	
	
	public LayoutCoordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	
	public int getColumnPosition() {
		return x;
	}
	
	public int getRowPosition() {
		return y;
	}
	
	
	@Override
	public int hashCode() {
		return x * 17 + (y << 16) + y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof LayoutCoordinate)) {
			return false;
		}
		final LayoutCoordinate other = (LayoutCoordinate) obj;
		return (x == other.x && y == other.y);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + x + "," + y + "]";
	}
	
}
