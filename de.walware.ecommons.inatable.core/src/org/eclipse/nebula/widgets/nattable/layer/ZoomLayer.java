/*******************************************************************************
 * Copyright (c) 2012-2015 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.layer;

public class ZoomLayer extends AbstractLayerTransform {

	private float zoomFactor = 1;
	
	public ZoomLayer(ILayer underlyingILayer) {
		super(underlyingILayer);
	}
	
	public float getZoomFactor() {
		return zoomFactor;
	}
	
	public void setZoomFactor(float zoomFactor) {
		this.zoomFactor = zoomFactor;
	}
	
	// Horizontal features

	// Width
	
	@Override
	public long getWidth() {
		return (int) (zoomFactor * super.getWidth());
	}
	
	@Override
	public long getPreferredWidth() {
		return (int) (zoomFactor * super.getPreferredWidth());
	}
	
	@Override
	public int getColumnWidthByPosition(long columnPosition) {
		return (int) (zoomFactor * super.getColumnWidthByPosition(columnPosition));
	}
	
	// X
	
	@Override
	public long getColumnPositionByX(long x) {
		return super.getColumnPositionByX((int) (x / zoomFactor));
	}
	
	@Override
	public long getStartXOfColumnPosition(long columnPosition) {
		return (int) (zoomFactor * super.getStartXOfColumnPosition(columnPosition));
	}
	
	// Vertical features
	
	// Height
	
	@Override
	public long getHeight() {
		return (int) (zoomFactor * super.getHeight());
	}
	
	@Override
	public long getPreferredHeight() {
		return (int) (zoomFactor * super.getPreferredHeight());
	}
	
	@Override
	public int getRowHeightByPosition(long rowPosition) {
		return (int) (zoomFactor * super.getRowHeightByPosition(rowPosition));
	}
	
	// Y
	
	@Override
	public long getRowPositionByY(long y) {
		return super.getRowPositionByY((int) (y / zoomFactor));
	}
	
	@Override
	public long getStartYOfRowPosition(long rowPosition) {
		return (int) (zoomFactor * super.getStartYOfRowPosition(rowPosition));
	}
	
	@Override
	public LabelStack getRegionLabelsByXY(long x, long y) {
		return super.getRegionLabelsByXY((int) (x / zoomFactor), (int) (y / zoomFactor));
	}
	
}
