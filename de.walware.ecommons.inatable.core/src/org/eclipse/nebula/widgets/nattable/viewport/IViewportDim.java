/*******************************************************************************
 * Copyright (c) 2013 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.viewport;

import org.eclipse.nebula.widgets.nattable.layer.ILayerDim;


public interface IViewportDim extends ILayerDim {
	
	
	ILayerDim getScrollable();
	
	
	long getMinimumOriginPixel();
	
	long getMinimumOriginPosition();
	
	void setMinimumOriginPosition(long scrollablePosition);
	
	
	long getOriginPixel();
	
	long getOriginPosition();
	
	void setOriginPixel(long scrollablePixel);
	
	void setOriginPosition(long scrollablePosition);
	
	void reset(long scrollablePosition);
	
	
	/**
	 * Scrolls the viewport (if required) so that the specified column/row is visible.
	 * 
	 * @param scrollablePosition column/row position in terms of the Scrollable Layer
	 */
	void movePositionIntoViewport(long scrollablePosition);
	
	void scrollBackwardByStep();
	void scrollForwardByStep();
	
	void scrollBackwardByPosition();
	void scrollForwardByPosition();
	
	void scrollBackwardByPage();
	void scrollForwardByPage();
	
	void scrollBackwardToBound();
	void scrollForwardToBound();
	
}
