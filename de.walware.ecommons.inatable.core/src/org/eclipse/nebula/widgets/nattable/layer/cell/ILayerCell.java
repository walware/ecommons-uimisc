/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.layer.cell;

import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;


public interface ILayerCell {
	
	
	int NO_INDEX = -1;
	
	
	ILayer getLayer();
	
	LayerCellDim getDim(Orientation orientation);
	
	
	public int getOriginColumnPosition();
	
	public int getOriginRowPosition();
	
	public int getColumnPosition();
	
	public int getRowPosition();
	
	public int getColumnIndex();
	
	public int getRowIndex();
	
	public int getColumnSpan();
	
	public int getRowSpan();
	
	public boolean isSpannedCell();

	public String getDisplayMode();

	public LabelStack getConfigLabels();

	public Object getDataValue();

	public Rectangle getBounds();

}
