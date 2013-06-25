/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// ~
package org.eclipse.nebula.widgets.nattable.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.coordinate.Direction;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel.ColumnGroup;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;


public class ColumnGroupUtils {

	public static Direction getMoveDirection(long fromColumnPosition, long toColumnPosition) {
		if (fromColumnPosition > toColumnPosition){
			return Direction.LEFT;
		} else if(fromColumnPosition < toColumnPosition){
			return Direction.RIGHT;
		} else {
			return null;
		}
	}

	public static boolean isInTheSameGroup(long fromColumnIndex, long toColumnIndex, ColumnGroupModel model) {
		ColumnGroup fromColumnGroup = model.getColumnGroupByIndex(fromColumnIndex);
		ColumnGroup toColumnGroup = model.getColumnGroupByIndex(toColumnIndex);

		return fromColumnGroup != null
			   	&& toColumnGroup != null
			   	&& fromColumnGroup == toColumnGroup;
	}
	
	/**
	 * Checks whether <code>columnIndex</code> is either a defined static column
	 * or (if not) the first visible column in the group containing group. This
	 * method provides downward compatibility for all group definitions without
	 * static columns. When no static columns are defined the first visible 
	 * column will be used. 
	 * 
	 * @param columnIndex
	 * @param layer
	 * @param underlyingLayer
	 * @param model
	 * 
	 * @return <code>TRUE</code> if the given <code>columnIndex</code> is either
	 * a defined static column or (if not) the first visible column the it's
	 * group
	 */
	public static boolean isStaticOrFirstVisibleColumn(long columnIndex, ILayer layer, IUniqueIndexLayer underlyingLayer, ColumnGroupModel model) {
		ColumnGroup columnGroup = model.getColumnGroupByIndex(columnIndex);

		if (columnGroup.getStaticColumnIndexes().size() == 0) {
			return isFirstVisibleColumnIndexInGroup(columnIndex, layer, underlyingLayer, model);
		}
		else {
			return model.isStaticColumn(columnIndex);
		}
	}

	public static boolean isFirstVisibleColumnIndexInGroup(long columnIndex, ILayer layer, IUniqueIndexLayer underlyingLayer, ColumnGroupModel model){
		if (isColumnIndexHiddenInUnderLyingLayer(columnIndex, layer, underlyingLayer)) {
			return false;
		}

		long columnPosition = underlyingLayer.getColumnPositionByIndex(columnIndex);
		List<Long> columnIndexesInGroup = model.getColumnGroupByIndex(columnIndex).getMembers();
		List<Long> previousVisibleColumnIndexes = new ArrayList<Long>();

		//All other indexes in the column group which are visible and
		//are positioned before me
		for (Long currentIndex : columnIndexesInGroup) {
			long currentPosition = underlyingLayer.getColumnPositionByIndex(currentIndex.longValue());
			if(!isColumnIndexHiddenInUnderLyingLayer(currentIndex.longValue(), layer, underlyingLayer)
					&& currentPosition < columnPosition){
				previousVisibleColumnIndexes.add(currentIndex);
			}
		}

		return previousVisibleColumnIndexes.isEmpty();
	}

	public static boolean isLastVisibleColumnIndexInGroup(long columnIndex, ILayer layer, IUniqueIndexLayer underlyingLayer, ColumnGroupModel model) {
		if (isColumnIndexHiddenInUnderLyingLayer(columnIndex, layer, underlyingLayer)) {
			return false;
		}

		List<Long> visibleIndexesToTheRight = getVisibleIndexesToTheRight(columnIndex, layer, underlyingLayer, model);
		return visibleIndexesToTheRight.size() == 1 && visibleIndexesToTheRight.get(0).longValue() == columnIndex;
	}

	/**
	 * Inclusive of the columnIndex passed as the parameter.
	 */
	public static List<Long> getVisibleIndexesToTheRight(long columnIndex, ILayer layer, IUniqueIndexLayer underlyingLayer, ColumnGroupModel model){
		ColumnGroup columnGroup = model.getColumnGroupByIndex(columnIndex);
		
		if(columnGroup.isCollapsed()){
			return Collections.emptyList();
		}

		List<Long> columnIndexesInGroup = columnGroup.getMembers();
		long columnPosition = underlyingLayer.getColumnPositionByIndex(columnIndex);
		List<Long> visibleColumnIndexesOnRight = new ArrayList<Long>();

		for (Long currentIndex : columnIndexesInGroup) {
			long currentPosition = underlyingLayer.getColumnPositionByIndex(currentIndex.longValue());
			if(!isColumnIndexHiddenInUnderLyingLayer(currentIndex.longValue(), layer, underlyingLayer)
					&& currentPosition >= columnPosition){
				visibleColumnIndexesOnRight.add(currentIndex);
			}
		}

		return visibleColumnIndexesOnRight;
	}

	public static boolean isColumnIndexHiddenInUnderLyingLayer(long columnIndex, ILayer layer, IUniqueIndexLayer underlyingLayer) {
		return underlyingLayer.getColumnPositionByIndex(columnIndex) == Long.MIN_VALUE;
	}

	public static boolean isColumnPositionHiddenInUnderLyingLayer(long columnPosition, ILayer layer, IUniqueIndexLayer underlyingLayer) {
		if (columnPosition < underlyingLayer.getColumnCount() && columnPosition >= 0) {
			long columnIndex = underlyingLayer.getColumnIndexByPosition(columnPosition);
			return isColumnIndexHiddenInUnderLyingLayer(columnIndex, layer, underlyingLayer);
		}
		return true;
	}

	/**
	 * See ColumnGroupUtilsTest
	 * @return TRUE if the given column is the <i>right</i> most column in a group
	 */
	public static boolean isRightEdgeOfAColumnGroup(ILayer natLayer, long columnPosition, long columnIndex, ColumnGroupModel model) {
		long nextColumnPosition = columnPosition + 1;

		if (nextColumnPosition < natLayer.getColumnCount()) {
			long nextColumnIndex = natLayer.getColumnIndexByPosition(nextColumnPosition);
			if ((model.isPartOfAGroup(columnIndex) && !model.isPartOfAGroup(nextColumnIndex))) {
				return true;
			}
			if ((model.isPartOfAGroup(columnIndex) && model.isPartOfAGroup(nextColumnIndex))
					&& !ColumnGroupUtils.isInTheSameGroup(columnIndex, nextColumnIndex, model)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * See ColumnGroupUtilsTest
	 * @return TRUE if the given column is the <i>left</i> most column in a group
	 */
	public static boolean isLeftEdgeOfAColumnGroup(ILayer natLayer, long columnPosition, long columnIndex, ColumnGroupModel model) {
		long previousColumnPosition = columnPosition - 1;

		// First column && in a group
		if(columnPosition == 0 && model.isPartOfAGroup(columnIndex)){
			return true;
		}

		if (previousColumnPosition >= 0) {
			long previousColumnIndex = natLayer.getColumnIndexByPosition(previousColumnPosition);
			if ((model.isPartOfAGroup(columnIndex) && !model.isPartOfAGroup(previousColumnIndex))) {
				return true;
			}
			if ((model.isPartOfAGroup(columnIndex) && model.isPartOfAGroup(previousColumnIndex))
					&& !ColumnGroupUtils.isInTheSameGroup(columnIndex, previousColumnIndex, model)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return TRUE if there is a column group boundary between startX and endX
	 */
	public static boolean isBetweenTwoGroups(ILayer natLayer, long startX, long endX, ColumnGroupModel model) {
		return !ColumnGroupUtils.isInTheSameGroup(
				natLayer.getColumnIndexByPosition(natLayer.getColumnPositionByX(startX)),
				natLayer.getColumnIndexByPosition(natLayer.getColumnPositionByX(endX)),
				model);
	}
	
}
