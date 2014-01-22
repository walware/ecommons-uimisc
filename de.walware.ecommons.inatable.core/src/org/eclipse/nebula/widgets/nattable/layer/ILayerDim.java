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
import java.util.List;

import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;


/**
 * This interface for layers allows to write code which is independent of the orientation
 * (horizontal = columns / vertical = rows).
 * 
 * It is recommend that implementation extends {@link AbstractLayerDim}.
 */
public interface ILayerDim {
	
	
	/**
	 * Returns the layer this dimension belongs to.
	 * 
	 * @return the layer
	 */
	ILayer getLayer();
	
	/**
	 * Returns the orientation of this dimension.
	 * 
	 * @return the orientation
	 */
	Orientation getOrientation();
	
	
	// Index
	
	/**
	 * Returns the unique index for the specified position.
	 * 
	 * {@link ILayer#getColumnIndexByPosition(int)} /
	 * {@link ILayer#getRowIndexByPosition(int)}
	 * 
	 * @param position the local position
	 * 
	 * @return the index
	 */
	long getPositionIndex(long refPosition, long position);
	
	
	// Position = Columns / Rows
	
	/**
	 * Returns the number of positions in this layer dimension.
	 * 
	 * {@link ILayer#getColumnCount()} /
	 * {@link ILayer#getRowCount()}
	 * 
	 * @return the count of local positions
	 */
	long getPositionCount();
	
	
	/**
	 * Converts the specified position in this layer dimension to the position in the underlying
	 * layer.
	 * 
	 * {@link ILayer#localToUnderlyingColumnPosition(int)} /
	 * {@link ILayer#localToUnderlyingRowPosition(int)}
	 * 
	 * @param the local position
	 * 
	 * @return the position in the underlying layer
	 */
	long localToUnderlyingPosition(long refPosition, long position);
	
	/**
	 * Converts the specified position in the specified underlying layer to the position in this
	 * layer dimension.
	 * 
	 * {@link ILayer#underlyingToLocalColumnPosition(ILayer, long)} /
	 * {@link ILayer#underlyingToLocalRowPosition(ILayer, long)}
	 * 
	 * @param sourceUnderlyingLayer the underlying layer the position refers to
	 * @param underlyingPosition the position in the underlying layer
	 * 
	 * @return the local position
	 */
	long underlyingToLocalPosition(long refPosition, long underlyingPosition);
	
	/**
	 * Converts the specified position in the specified underlying layer to the position in this
	 * layer dimension.
	 * 
	 * {@link ILayer#underlyingToLocalColumnPosition(ILayer, long)} /
	 * {@link ILayer#underlyingToLocalRowPosition(ILayer, long)}
	 * 
	 * @param sourceUnderlyingDim the underlying layer dimension the position refers to
	 * @param underlyingPosition the position in the underlying layer
	 * 
	 * @return the local position
	 */
	long underlyingToLocalPosition(ILayerDim sourceUnderlyingDim, long underlyingPosition);
	
	/**
	 * Converts the specified positions in the specified underlying layer to the position in this
	 * layer dimension.
	 * 
	 * {@link ILayer#underlyingToLocalColumnPositions(ILayer, Collection)} /
	 * {@link ILayer#underlyingToLocalRowPositions(ILayer, Collection)}
	 * 
	 * @param sourceUnderlyingDim the underlying layer dimension the positions refers to
	 * @param underlyingPositions the positions in the underlying layer
	 * 
	 * @return the local positions
	 */
	List<Range> underlyingToLocalPositions(ILayerDim sourceUnderlyingDim,
			Collection<Range> underlyingPositions);
	
	/**
	 * Returns all underlying dimensions for the specified position.
	 * 
	 * {@link ILayer#getUnderlyingLayersByColumnPosition(int)} /
	 * {@link ILayer#getUnderlyingLayersByRowPosition(int)}
	 * 
	 * @param position the local position
	 * 
	 * @return the underlying layer dimensions
	 */
	List<ILayerDim> getUnderlyingDimsByPosition(long position);
	
	
	// Pixel = X / Y, Width / Height
	
	/**
	 * Returns the size of this layer dimension.
	 * 
	 * {@link ILayer#getWidth()} /
	 * {@link ILayer#getHeight()}
	 * 
	 * @return the size in pixel
	 */
	long getSize();
	
	/**
	 * Returns the preferred size of this layer dimension.
	 * 
	 * {@link ILayer#getPreferredWidth()} /
	 * {@link ILayer#getPreferredHeight()}
	 * 
	 * @return the preferred size in pixel
	 */
	long getPreferredSize();
	
	/**
	 * Returns the position in this layer dimension for the specified pixel coordinate.
	 * 
	 * {@link ILayer#getColumnPositionByX(int)} /
	 * {@link ILayer#getRowPositionByY(int)}
	 * 
	 * @param pixel the pixel coordinate
	 * 
	 * @return the local position
	 */
	long getPositionByPixel(long pixel);
	
	/**
	 * Returns the pixel coordinate of the start of the specified position in this layer dimension.
	 * 
	 * {@link ILayer#getStartXOfColumnPosition(int)} /
	 * {@link ILayer#getStartYOfRowPosition(int)}
	 * 
	 * @param position the local position
	 * 
	 * @return the pixel coordinate of the start
	 */
	long getPositionStart(long refPosition, long position);
	
	/**
	 * Returns the size in pixel of the specified position in this layer dimension.
	 * 
	 * {@link ILayer#getColumnWidthByPosition(int)} /
	 * {@link ILayer#getRowHeightByPosition(int)}
	 * 
	 * @param position the local position
	 * 
	 * @return the size in pixel
	 */
	int getPositionSize(long refPosition, long position);
	
	/**
	 * Returns if the specified position is resizable.
	 *  
	 * {@link ILayer#isColumnPositionResizable(int)} /
	 * {@link ILayer#isRowPositionResizable(int)}
	 * 
	 * @param position the local position
	 * 
	 * @return <code>true</code> if the position is resizable, otherwise <code>false</code>
	 */
	boolean isPositionResizable(long position);
	
}
