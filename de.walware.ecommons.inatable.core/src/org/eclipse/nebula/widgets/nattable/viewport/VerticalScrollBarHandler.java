/*******************************************************************************
 * Copyright (c) 2012-2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// ~Direction
package org.eclipse.nebula.widgets.nattable.viewport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

import org.eclipse.nebula.widgets.nattable.coordinate.Direction;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;


/**
 * Listener for the Vertical scroll bar events.
 */
public class VerticalScrollBarHandler extends ScrollBarHandlerTemplate implements Listener {

	public VerticalScrollBarHandler(ViewportLayer viewportLayer, ScrollBar scrollBar) {
		super(viewportLayer, scrollBar);
	}

	/**
	 * In a normal scenario scroll by the height of the viewport. If the row
	 * being scrolled is wider than above, use the row height
	 */
	@Override
	int pageScrollDistance() {
		int heightOfRowBeingScrolled = scrollableLayer.getRowHeightByPosition(getScrollablePosition());
		int viewportHeight = viewportLayer.getClientAreaHeight();
		return (heightOfRowBeingScrolled > viewportHeight) ? heightOfRowBeingScrolled : viewportHeight;
	}

	@Override
	int getSpanByPosition(int scrollablePosition) {
		return scrollableLayer.getRowHeightByPosition(scrollablePosition);
	}

	/**
	 * Convert Viewport 0 pos -> Scrollable 0 pos
	 * 
	 * @return
	 */
	@Override
	int getScrollablePosition() {
		return LayerUtil.convertRowPosition(viewportLayer, 0, scrollableLayer);
	}

	@Override
	int getStartPixelOfPosition(int position) {
		return scrollableLayer.getStartYOfRowPosition(position);
	}

	@Override
	int getPositionByPixel(int pixelValue) {
		return scrollableLayer.getRowPositionByY(pixelValue);
	}

	@Override
	int getViewportPixelOffset() {
		int row = viewportLayer.getMinimumOriginRowPosition();
		return (row < scrollableLayer.getRowCount()) ? scrollableLayer.getStartYOfRowPosition(row) : scrollableLayer.getHeight();
	}

	@Override
	void setViewportOrigin(int position) {
		viewportLayer.invalidateVerticalStructure();
		viewportLayer.setOriginRowPosition(position);
		scrollBar.setIncrement(viewportLayer.getRowHeightByPosition(0));
	}

	@Override
	Direction scrollDirectionForEventDetail(int eventDetail) {
		return (eventDetail == SWT.PAGE_UP || eventDetail == SWT.ARROW_UP) ?
				Direction.UP : Direction.DOWN;
	}

	@Override
	boolean keepScrolling() {
		return !viewportLayer.isLastRowCompletelyDisplayed();
	}
	
	@Override
	int getViewportWindowSpan() {
		return viewportLayer.getClientAreaHeight();
	}

	@Override
	int getScrollableLayerSpan() {
		return scrollableLayer.getHeight();
	}
}
