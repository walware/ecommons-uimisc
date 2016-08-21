/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package de.walware.ecommons.waltable.layer.cell;

import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.style.DisplayMode;


public interface ILayerCell {
	
	
	ILayer getLayer();
	
	ILayerCellDim getDim(Orientation orientation);
	
	
	long getOriginColumnPosition();
	
	long getOriginRowPosition();
	
	long getColumnPosition();
	
	long getRowPosition();
	
	long getColumnSpan();
	
	long getRowSpan();
	
	boolean isSpannedCell();
	
	
	DisplayMode getDisplayMode();
	
	LRectangle getBounds();
	
	LabelStack getConfigLabels();
	
	
	Object getDataValue(int flags);
	
}
