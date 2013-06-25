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
	 * {@link ILayer#getColumnIndexByPosition(long)} /
	 * {@link ILayer#getRowIndexByPosition(long)}
	 */
	long getPositionIndex(long refPosition, long position);
	
	
	// Position = Columns / Rows
	
	/**
	 * {@link ILayer#getColumnCount()} /
	 * {@link ILayer#getRowCount()}
	 */
	long getPositionCount();
	
	/**
	 * {@link ILayer#getPreferredColumnCount()} /
	 * {@link ILayer#getPreferredRowCount()}
	 */
	long getPreferredPositionCount();
	
	
	/**
	 * {@link ILayer#localToUnderlyingColumnPosition(long)} /
	 * {@link ILayer#localToUnderlyingRowPosition(long)}
	 */
	long localToUnderlyingPosition(long refPosition, long position);
	
	/**
	 * {@link ILayer#underlyingToLocalColumnPosition(ILayer, long)} /
	 * {@link ILayer#underlyingToLocalRowPosition(ILayer, long)}
	 */
	long underlyingToLocalPosition(long refPosition, long underlyingPosition);
	
	long underlyingToLocalPosition(ILayer sourceUnderlyingLayer, long underlyingPosition);
	
	/**
	 * {@link ILayer#underlyingToLocalColumnPositions(ILayer, Collection)} /
	 * {@link ILayer#underlyingToLocalRowPositions(ILayer, Collection)}
	 */
	Collection<Range> underlyingToLocalPositions(ILayer sourceUnderlyingLayer,
			Collection<Range> underlyingPositionRanges);
	
	/**
	 * {@link ILayer#getUnderlyingLayersByColumnPosition(long)} /
	 * {@link ILayer#getUnderlyingLayersByRowPosition(long)}
	 */
	Collection<ILayer> getUnderlyingLayersByPosition(long position);
	
	
	// Pixel = X / Y, Width / Height
	
	/**
	 * {@link ILayer#getWidth()} /
	 * {@link ILayer#getHeight()}
	 */
	long getSize();
	
	/**
	 * {@link ILayer#getPreferredWidth()} /
	 * {@link ILayer#getPreferredHeight()}
	 */
	long getPreferredSize();
	
	/**
	 * {@link ILayer#getColumnPositionByX(long)} /
	 * {@link ILayer#getRowPositionByY(long)}
	 */
	long getPositionByPixel(long pixel);
	
	/**
	 * {@link ILayer#getStartXOfColumnPosition(long)} /
	 * {@link ILayer#getStartYOfRowPosition(long)}
	 */
	long getPositionStart(long refPosition, long position);
	
	/**
	 * {@link ILayer#getColumnWidthByPosition(long)} /
	 * {@link ILayer#getRowHeightByPosition(long)}
	 */
	int getPositionSize(long refPosition, long position);
	
	
	// Resize
	
	/**
	 * {@link ILayer#isColumnPositionResizable(long)} /
	 * {@link ILayer#isRowPositionResizable(long)}
	 */
	boolean isPositionResizable(long position);
	
}
