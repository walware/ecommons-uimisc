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

package org.eclipse.nebula.widgets.nattable.layer;

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;


/**
 * This interface for layer allows to write code which is independent of the orientation
 * (horizontal = columns / vertical = rows).
 * 
 * @see HorizontalLayerDim
 * @see VerticalLayerDim
 */
public interface ILayerDim {
	
	
	ILayer getLayer();
	
	Orientation getOrientation();
	
	
	// Index
	
	/**
	 * {@link ILayer#getColumnIndexByPosition(int)} /
	 * {@link ILayer#getRowIndexByPosition(int)}
	 */
	int getPositionIndex(int refPosition, int position);
	
	
	// Position = Columns / Rows
	
	/**
	 * {@link ILayer#getColumnCount()} /
	 * {@link ILayer#getRowCount()}
	 */
	int getPositionCount();
	
	/**
	 * {@link ILayer#getPreferredColumnCount()} /
	 * {@link ILayer#getPreferredRowCount()}
	 */
	int getPreferredPositionCount();
	
	
	/**
	 * {@link ILayer#localToUnderlyingColumnPosition(int)} /
	 * {@link ILayer#localToUnderlyingRowPosition(int)}
	 */
	int localToUnderlyingPosition(int refPosition, int position);
	
	/**
	 * {@link ILayer#underlyingToLocalColumnPosition(ILayer, int)} /
	 * {@link ILayer#underlyingToLocalRowPosition(ILayer, int)}
	 */
	int underlyingToLocalPosition(int refPosition, int underlyingPosition);
	
	int underlyingToLocalPosition(ILayer sourceUnderlyingLayer, int underlyingPosition);
	
	/**
	 * {@link ILayer#underlyingToLocalColumnPositions(ILayer, Collection)} /
	 * {@link ILayer#underlyingToLocalRowPositions(ILayer, Collection)}
	 */
	Collection<Range> underlyingToLocalPositions(ILayer sourceUnderlyingLayer,
			Collection<Range> underlyingPositionRanges);
	
	/**
	 * {@link ILayer#getUnderlyingLayersByColumnPosition(int)} /
	 * {@link ILayer#getUnderlyingLayersByRowPosition(int)}
	 */
	Collection<ILayer> getUnderlyingLayersByPosition(int position);
	
	
	// Pixel = X / Y, Width / Height
	
	/**
	 * {@link ILayer#getWidth()} /
	 * {@link ILayer#getHeight()}
	 */
	int getSize();
	
	/**
	 * {@link ILayer#getPreferredWidth()} /
	 * {@link ILayer#getPreferredHeight()}
	 */
	int getPreferredSize();
	
	/**
	 * {@link ILayer#getColumnPositionByX(int)} /
	 * {@link ILayer#getRowPositionByY(int)}
	 */
	int getPositionByPixel(int pixel);
	
	/**
	 * {@link ILayer#getStartXOfColumnPosition(int)} /
	 * {@link ILayer#getStartYOfRowPosition(int)}
	 */
	int getPositionStart(int refPosition, int position);
	
	/**
	 * {@link ILayer#getColumnWidthByPosition(int)} /
	 * {@link ILayer#getRowHeightByPosition(int)}
	 */
	int getPositionSize(int refPosition, int position);
	
	
	// Resize
	
	/**
	 * {@link ILayer#isColumnPositionResizable(int)} /
	 * {@link ILayer#isRowPositionResizable(int)}
	 */
	boolean isPositionResizable(int position);
	
}
