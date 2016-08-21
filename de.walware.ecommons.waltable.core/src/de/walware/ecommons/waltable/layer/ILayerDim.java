/*******************************************************************************
 * Copyright (c) 2013-2016 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package de.walware.ecommons.waltable.layer;

import java.util.Collection;
import java.util.List;

import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.Orientation;


/**
 * This interface for layers allows to write code which is independent of the orientation
 * (horizontal= columns / vertical= rows).
 * 
 * It is recommend that implementation extends {@link AbstractLayerDim}.
 */
public interface ILayerDim {
	
	
	long POSITION_NA= Long.MIN_VALUE;
	
	
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
	
	
	// Id
	
	/**
	 * Returns the unique id for the specified position.
	 * 
	 * @param position the local position
	 * 
	 * @return the id
	 */
	long getPositionId(long refPosition, long position);
	
	/**
	 * Returns the position in this layer dimension for the id.
	 * 
	 * @param id the position id
	 * 
	 * @return the local position
	 */
	long getPositionById(long id);
	
	
	// Position= Columns / Rows
	
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
	 * @param the local position
	 * 
	 * @return the position in the underlying layer
	 */
	long localToUnderlyingPosition(long refPosition, long position);
	
	/**
	 * Converts the specified position in the specified underlying layer to the position in this
	 * layer dimension.
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
	List<LRange> underlyingToLocalPositions(ILayerDim sourceUnderlyingDim,
			Collection<LRange> underlyingPositions);
	
	/**
	 * Returns all underlying dimensions for the specified position.
	 * 
	 * @param position the local position
	 * 
	 * @return the underlying layer dimensions
	 */
	List<ILayerDim> getUnderlyingDimsByPosition(long position);
	
	
	// Pixel= X / Y, Width / Height
	
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
	 * @param position the local position
	 * 
	 * @return the pixel coordinate of the start
	 */
	long getPositionStart(long refPosition, long position);
	long getPositionStart(long position);
	
	/**
	 * Returns the size in pixel of the specified position in this layer dimension.
	 * 
	 * @param position the local position
	 * 
	 * @return the size in pixel
	 */
	int getPositionSize(long refPosition, long position);
	int getPositionSize(long position);
	
	/**
	 * Returns if the specified position is resizable.
	 *  
	 * @param position the local position
	 * 
	 * @return <code>true</code> if the position is resizable, otherwise <code>false</code>
	 */
	boolean isPositionResizable(long position);
	
}
