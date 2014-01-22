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
package org.eclipse.nebula.widgets.nattable.layer;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;
import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;
import static org.eclipse.nebula.widgets.nattable.painter.cell.GraphicsUtils.safe;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.layer.cell.AggregrateConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCellDim;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.layer.ILayerPainter;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.util.IClientAreaProvider;


/**
 * A composite layer is a layer that is made up of a number of underlying child layers. This class assumes that the child
 * layers are laid out in a regular grid pattern where the child layers in each composite row all have the same number of
 * rows and the same height, and the child layers in each composite column each have the same number of columns and
 * the same width.
 */
public class CompositeLayer extends DimBasedLayer {
	
	protected final int layoutXCount;
	
	protected final int layoutYCount;
	
	private final Map<ILayer, String> childLayerToRegionNameMap = new HashMap<ILayer, String>();
	
	private final Map<String, IConfigLabelAccumulator> regionNameToConfigLabelAccumulatorMap = new HashMap<String, IConfigLabelAccumulator>();
	
	private final Map<ILayer, LayoutCoordinate> childLayerToLayoutCoordinateMap = new HashMap<ILayer, LayoutCoordinate>();
	
	/** Data struct. for child Layers */
	private final ILayer[][] childLayerLayout;
	
	
	public CompositeLayer(int layoutXCount, int layoutYCount) {
		this.layoutXCount = layoutXCount;
		this.layoutYCount = layoutYCount;
		this.childLayerLayout = new ILayer[layoutXCount][layoutYCount];
		
		updateDims();
	}
	
	
	@Override
	protected void updateDims() {
		if (this.childLayerLayout == null) {
			return;
		}
		
		for (final Orientation orientation : Orientation.values()) {
			setDim((ignoreRef(orientation)) ?
					new CompositeDim.IgnoreRef(this, orientation) :
					new CompositeDim(this, orientation) );
		}
	}
	
	protected boolean ignoreRef(final Orientation orientation) {
		return false;
	}
	
	final CompositeDim get(final Orientation orientation) {
		return (CompositeDim) getDim(orientation);
	}
	
	
	@Override
	protected ILayerPainter createPainter() {
		return new CompositeLayerPainter();
	}
	
	
	// Dispose
	
	@Override
	public void dispose() {
		for (int layoutX = 0; layoutX < layoutXCount; layoutX++) {
			for (int layoutY = 0; layoutY < layoutYCount; layoutY++) {
				ILayer childLayer = childLayerLayout[layoutX][layoutY];
				if (childLayer != null) {
					childLayer.dispose();
				}
			}
		}
	}
	
	// Persistence
	
	@Override
	public void saveState(String prefix, Properties properties) {
		for (int layoutX = 0; layoutX < layoutXCount; layoutX++) {
			for (int layoutY = 0; layoutY < layoutYCount; layoutY++) {
				ILayer childLayer = childLayerLayout[layoutX][layoutY];
				if (childLayer != null) {
					String regionName = childLayerToRegionNameMap.get(childLayer);
					childLayer.saveState(prefix + "." + regionName, properties); //$NON-NLS-1$
				}
			}
		}
		super.saveState(prefix, properties);
	}
	
	@Override
	public void loadState(String prefix, Properties properties) {
		for (int layoutX = 0; layoutX < layoutXCount; layoutX++) {
			for (int layoutY = 0; layoutY < layoutYCount; layoutY++) {
				ILayer childLayer = childLayerLayout[layoutX][layoutY];
				if (childLayer != null) {
					String regionName = childLayerToRegionNameMap.get(childLayer);
					childLayer.loadState(prefix + "." + regionName, properties); //$NON-NLS-1$
				}
			}
		}
		super.loadState(prefix, properties);
	}
	
	// Configuration
	
	@Override
	public void configure(ConfigRegistry configRegistry, UiBindingRegistry uiBindingRegistry) {
		for (int layoutX = 0; layoutX < layoutXCount; layoutX++) {
			for (int layoutY = 0; layoutY < layoutYCount; layoutY++) {
				childLayerLayout[layoutX][layoutY].configure(configRegistry, uiBindingRegistry);
			}
		}
		
		super.configure(configRegistry, uiBindingRegistry);
	}
	
	/**
	 * {@inheritDoc}
	 * Handle commands
	 */
	@Override
	public boolean doCommand(ILayerCommand command) {
		if (super.doCommand(command)) {
			return true;
		}
		return doCommandOnChildLayers(command);
	}
	
	protected boolean doCommandOnChildLayers(ILayerCommand command) {
		for (int layoutX = 0; layoutX < layoutXCount; layoutX++) {
			for (int layoutY = 0; layoutY < layoutYCount; layoutY++) {
				if (childLayerLayout[layoutX][layoutY].doCommand(command.cloneCommand())) {
					return true;
				}
			}
		}
		return false;
	}
	
	// Cell features
	
	@Override
	public ILayerCell getCellByPosition(long columnPosition, long rowPosition) {
		Point layoutCoordinate = getLayoutXYByPosition(columnPosition, rowPosition);
		
		if (layoutCoordinate == null) {
			return null;
		}
		
		ILayer childLayer = childLayerLayout[layoutCoordinate.x][layoutCoordinate.y];
		long childColumnPosition = columnPosition - get(HORIZONTAL).getLayoutPosition(layoutCoordinate.x);
		long childRowPosition = rowPosition - get(VERTICAL).getLayoutPosition(layoutCoordinate.y);
		
		ILayerCell cell = childLayer.getCellByPosition(childColumnPosition, childRowPosition);
		
		if (cell != null) {
			final LayerCellDim hDim = transformCellDim(cell.getDim(HORIZONTAL), columnPosition);
			final LayerCellDim vDim = transformCellDim(cell.getDim(VERTICAL), rowPosition);
			cell = new LayerCell(this, hDim, vDim, cell.getDisplayMode());
		}
		
		return cell;
	}
	
	protected LayerCellDim transformCellDim(final LayerCellDim underlyingDim, final long position) {
		final long originPosition = (underlyingDim.getPosition() == underlyingDim.getOriginPosition()) ?
				position :
				getDim(underlyingDim.getOrientation()).underlyingToLocalPosition(
						position, underlyingDim.getOriginPosition() );
		return new LayerCellDim(underlyingDim.getOrientation(), underlyingDim.getIndex(),
				position, originPosition, underlyingDim.getPositionSpan() );
	}
	
	@Override
	public Rectangle getBoundsByPosition(long compositeColumnPosition, long compositeRowPosition) {
		Point layoutCoordinate = getLayoutXYByPosition(compositeColumnPosition, compositeRowPosition);
		
		if (layoutCoordinate == null) {
			return null;
		}
		
		ILayer childLayer = childLayerLayout[layoutCoordinate.x][layoutCoordinate.y];
		long childColumnPosition = compositeColumnPosition - get(HORIZONTAL).getLayoutPosition(layoutCoordinate.x);
		long childRowPosition = compositeRowPosition - get(VERTICAL).getLayoutPosition(layoutCoordinate.y);
		
		final Rectangle bounds = childLayer.getBoundsByPosition(childColumnPosition, childRowPosition);
		
		if (bounds != null) {
			bounds.x += get(HORIZONTAL).getLayoutStart(layoutCoordinate.x);
			bounds.y += get(VERTICAL).getLayoutStart(layoutCoordinate.y);
		}
		
		return bounds;
	}
	
	@Override
	public LabelStack getConfigLabelsByPosition(long compositeColumnPosition, long compositeRowPosition) {
		Point layoutCoordinate = getLayoutXYByPosition(compositeColumnPosition, compositeRowPosition);
		if (layoutCoordinate == null) {
			return new LabelStack();
		}
		ILayer childLayer = childLayerLayout[layoutCoordinate.x][layoutCoordinate.y];
		
		long childColumnPosition = compositeColumnPosition - get(HORIZONTAL).getLayoutPosition(layoutCoordinate.x);
		long childRowPosition = compositeRowPosition - get(VERTICAL).getLayoutPosition(layoutCoordinate.y);
		LabelStack configLabels = childLayer.getConfigLabelsByPosition(childColumnPosition, childRowPosition);
		
		String regionName = childLayerToRegionNameMap.get(childLayer);
		IConfigLabelAccumulator configLabelAccumulator = regionNameToConfigLabelAccumulatorMap.get(regionName);
		if (configLabelAccumulator != null) {
			configLabelAccumulator.accumulateConfigLabels(configLabels, childColumnPosition, childRowPosition);
		}
		configLabels.addLabel(regionName);
		
		return configLabels;
	}
	
	public Object getDataValueByPosition(long compositeColumnPosition, long compositeRowPosition) {
		Point layoutCoordinate = getLayoutXYByPosition(compositeColumnPosition, compositeRowPosition);
		if (layoutCoordinate == null) {
			return null;
		}
		
		ILayer childLayer = childLayerLayout[layoutCoordinate.x][layoutCoordinate.y];
		return childLayer.getDataValueByPosition(
				compositeColumnPosition - get(HORIZONTAL).getLayoutPosition(layoutCoordinate.x),
				compositeRowPosition - get(VERTICAL).getLayoutPosition(layoutCoordinate.y) );
	}
	
	@Override
	public ICellPainter getCellPainter(long compositeColumnPosition, long compositeRowPosition, ILayerCell cell, IConfigRegistry configRegistry) {
		Point layoutCoordinate = getLayoutXYByPosition(compositeColumnPosition, compositeRowPosition);
		if (layoutCoordinate == null) {
			return null;
		}
		
		ILayer childLayer = childLayerLayout[layoutCoordinate.x][layoutCoordinate.y];
		return childLayer.getCellPainter(
				compositeColumnPosition - get(HORIZONTAL).getLayoutPosition(layoutCoordinate.x),
				compositeRowPosition - get(VERTICAL).getLayoutPosition(layoutCoordinate.y),
				cell,
				configRegistry);
	}
	
	// Child layer stuff
	
	public void setChildLayer(String regionName, ILayer childLayer, final int layoutX, final int layoutY) {
		if (childLayer == null) {
			throw new IllegalArgumentException("Cannot set null child layer"); //$NON-NLS-1$
		}
		
		childLayerToRegionNameMap.put(childLayer, regionName);
		
		childLayer.addLayerListener(this);
		childLayerToLayoutCoordinateMap.put(childLayer, new LayoutCoordinate(layoutX, layoutY));
		childLayerLayout[layoutX][layoutY] = childLayer;
		
		get(HORIZONTAL).updateChild(layoutX, layoutY, childLayer);
		get(VERTICAL).updateChild(layoutY, layoutX, childLayer);
		
		childLayer.setClientAreaProvider(new IClientAreaProvider() {
			public Rectangle getClientArea() {
				return getChildClientArea(layoutX, layoutY);
			}
		});
	}

	public IConfigLabelAccumulator getConfigLabelAccumulatorByRegionName(String regionName) {
		return regionNameToConfigLabelAccumulatorMap.get(regionName);
	}
	
	/**
	 * Sets the IConfigLabelAccumulator for the given named region. Replaces any existing IConfigLabelAccumulator.
	 */
	public void setConfigLabelAccumulatorForRegion(String regionName, IConfigLabelAccumulator configLabelAccumulator) {
		regionNameToConfigLabelAccumulatorMap.put(regionName, configLabelAccumulator);
	}
	
	/**
	 *  Adds the configLabelAccumulator to the existing label accumulators.
	 */
	public void addConfigLabelAccumulatorForRegion(String regionName, IConfigLabelAccumulator configLabelAccumulator) {
		IConfigLabelAccumulator existingConfigLabelAccumulator = regionNameToConfigLabelAccumulatorMap.get(regionName);
		AggregrateConfigLabelAccumulator aggregateAccumulator;
		if (existingConfigLabelAccumulator instanceof AggregrateConfigLabelAccumulator) {
			aggregateAccumulator = (AggregrateConfigLabelAccumulator) existingConfigLabelAccumulator;
		} else {
			aggregateAccumulator = new AggregrateConfigLabelAccumulator();
			aggregateAccumulator.add(existingConfigLabelAccumulator);
			regionNameToConfigLabelAccumulatorMap.put(regionName, aggregateAccumulator);
		}
		aggregateAccumulator.add(configLabelAccumulator);
	}
	
	private Rectangle getChildClientArea(final int layoutX, final int layoutY) {
		final ILayer childLayer = childLayerLayout[layoutX][layoutY];
		
		final Rectangle compositeClientArea = getClientAreaProvider().getClientArea();
		
		final Rectangle childClientArea = new Rectangle(
				compositeClientArea.x + get(HORIZONTAL).getLayoutStart(layoutX),
				compositeClientArea.y + get(VERTICAL).getLayoutStart(layoutY),
				childLayer.getPreferredWidth(),
				childLayer.getPreferredHeight());
		
		final Rectangle intersection = compositeClientArea.intersection(childClientArea);
		
		return intersection;
	}
	
	/**
	 * @param layoutX col position in the CompositeLayer
	 * @param layoutY row position in the CompositeLayer
	 * @return child layer according to the Composite Layer Layout
	 */
	public ILayer getChildLayerByLayoutCoordinate(int layoutX, int layoutY) {
		if (layoutX < 0 || layoutX >= layoutXCount || layoutY < 0 || layoutY >= layoutYCount) {
			return null;
		} else {
			return childLayerLayout[layoutX][layoutY];
		}
	}
	
	/**
	 * @param x pixel position
	 * @param y pixel position
	 * @return Region which the given position is in
	 */
	@Override
	public LabelStack getRegionLabelsByXY(long x, long y) {
		Point layoutCoordinate = getLayoutXYByPixelXY(x, y);
		if (layoutCoordinate == null) {
			return null;
		}
		
		ILayer childLayer = childLayerLayout[layoutCoordinate.x][layoutCoordinate.y];
		long childX = x - get(HORIZONTAL).getLayoutStart(layoutCoordinate.x);
		long childY = y - get(VERTICAL).getLayoutStart(layoutCoordinate.y);
		LabelStack regionLabels = childLayer.getRegionLabelsByXY(childX, childY);
		
		String regionName = childLayerToRegionNameMap.get(childLayer);
		regionLabels.addLabel(regionName);
		
		return regionLabels;
	}
	
	public ILayer getUnderlyingLayerByPosition(long columnPosition, long rowPosition) {
		Point layoutCoordinate = getLayoutXYByPosition(columnPosition, rowPosition);
		return childLayerLayout[layoutCoordinate.x][layoutCoordinate.y];
	}
	
	// Layout coordinate accessors
	
	protected final Point getLayoutXYByChildLayer(ILayer childLayer) {
		for (int layoutX = 0; layoutX < layoutXCount; layoutX++) {
			for (int layoutY = 0; layoutY < layoutYCount; layoutY++) {
				if (childLayerLayout[layoutX][layoutY] == childLayer) {
					return new Point(layoutX, layoutY);
				}
			}
		}
		return null;
	}
	
	protected final Point getLayoutXYByPixelXY(long x, long y) {
		int layoutX = get(HORIZONTAL).getLayoutByPixel(x);
		if (layoutX < 0) {
			return null;
		}
		int layoutY = get(VERTICAL).getLayoutByPixel(y);
		if (layoutY < 0) {
			return null;
		}
		return new Point(layoutX, layoutY);
	}
	
	protected final Point getLayoutXYByPosition(long compositeColumnPosition, long compositeRowPosition) {
		int layoutX = get(HORIZONTAL).getLayoutByPosition(compositeColumnPosition);
		if (layoutX < 0) {
			return null;
		}
		int layoutY = get(VERTICAL).getLayoutByPosition(compositeRowPosition);
		if (layoutY < 0) {
			return null;
		}
		return new Point(layoutX, layoutY);
	}
	
	
	protected class CompositeLayerPainter implements ILayerPainter {
		
		public void paintLayer(ILayer natLayer, GC gc, int xOffset, int yOffset, org.eclipse.swt.graphics.Rectangle rectangle, IConfigRegistry configuration) {
			int x = xOffset;
			for (int layoutX = 0; layoutX < layoutXCount; layoutX++) {
				int y = yOffset;
				for (int layoutY = 0; layoutY < layoutYCount; layoutY++) {
					ILayer childLayer = childLayerLayout[layoutX][layoutY];
					
					org.eclipse.swt.graphics.Rectangle childLayerRectangle = safe(new Rectangle(x, y, childLayer.getWidth(), childLayer.getHeight()));
					
					childLayerRectangle = rectangle.intersection(childLayerRectangle);
					
					org.eclipse.swt.graphics.Rectangle originalClipping = gc.getClipping();
					gc.setClipping(childLayerRectangle);
					
					childLayer.getLayerPainter().paintLayer(natLayer, gc, x, y, childLayerRectangle, configuration);
					
					gc.setClipping(originalClipping);
					y += childLayer.getHeight();
				}
				
				x += childLayerLayout[layoutX][0].getWidth();
			}
		}
		
		public Rectangle adjustCellBounds(long columnPosition, long rowPosition, Rectangle cellBounds) {
			Point layoutCoordinate = getLayoutXYByPosition(columnPosition, rowPosition);
			ILayer childLayer = childLayerLayout[layoutCoordinate.x][layoutCoordinate.y];
			
			if (childLayer == null) {
				return null;
			}
			
			long widthOffset = get(HORIZONTAL).getLayoutStart(layoutCoordinate.x);
			long heightOffset = get(VERTICAL).getLayoutStart(layoutCoordinate.y);
			
//			Rectangle bounds = new Rectangle(cellBounds.x - widthOffset, cellBounds.y - heightOffset, cellBounds.width, cellBounds.height);
			cellBounds.x -= widthOffset;
			cellBounds.y -= heightOffset;
			
			ILayerPainter childLayerPainter = childLayer.getLayerPainter();
			long childColumnPosition = columnPosition - get(HORIZONTAL).getLayoutPosition(layoutCoordinate.x);
			long childRowPosition = rowPosition - get(VERTICAL).getLayoutPosition(layoutCoordinate.y);
			Rectangle adjustedChildCellBounds = childLayerPainter.adjustCellBounds(childColumnPosition, childRowPosition, cellBounds);
//			Rectangle adjustedChildCellBounds = childLayerPainter.adjustCellBounds(childColumnPosition, childRowPosition, bounds);
			
			adjustedChildCellBounds.x += widthOffset;
			adjustedChildCellBounds.y += heightOffset;
			
			return adjustedChildCellBounds;
		}
		
	}
	
}
