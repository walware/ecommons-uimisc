package net.sourceforge.nattable.freeze.command;

import net.sourceforge.nattable.coordinate.PositionCoordinate;
import net.sourceforge.nattable.freeze.FreezeLayer;

class FreezePositionStrategy implements IFreezeCoordinatesProvider {

	private final FreezeLayer freezeLayer;
	
	private final int columnPosition;
	private final int rowPosition;

	FreezePositionStrategy(FreezeLayer freezeLayer, int columnPosition, int rowPosition) {
		this.freezeLayer = freezeLayer;
		this.columnPosition = columnPosition;
		this.rowPosition = rowPosition;
	}

	public PositionCoordinate getTopLeftPosition() {
		return new PositionCoordinate(freezeLayer, 0, 0);
	}
	
	public PositionCoordinate getBottomRightPosition() {
		return new PositionCoordinate(freezeLayer, columnPosition-1, rowPosition-1);
	}
	
}
