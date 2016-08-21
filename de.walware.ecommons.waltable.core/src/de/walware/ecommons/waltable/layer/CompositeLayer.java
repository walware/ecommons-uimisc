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
// ~
package de.walware.ecommons.waltable.layer;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;
import static de.walware.ecommons.waltable.painter.cell.GraphicsUtils.safe;

import java.util.Properties;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import de.walware.ecommons.waltable.command.ILayerCommand;
import de.walware.ecommons.waltable.config.ConfigRegistry;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.coordinate.PositionOutOfBoundsException;
import de.walware.ecommons.waltable.layer.cell.AggregrateConfigLabelAccumulator;
import de.walware.ecommons.waltable.layer.cell.ForwardLayerCell;
import de.walware.ecommons.waltable.layer.cell.IConfigLabelAccumulator;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.layer.cell.ILayerCellDim;
import de.walware.ecommons.waltable.layer.cell.LayerCellDim;
import de.walware.ecommons.waltable.painter.layer.ILayerPainter;
import de.walware.ecommons.waltable.ui.IClientAreaProvider;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;


/**
 * A composite layer is a layer that is made up of a number of underlying child layers. This class assumes that the child
 * layers are laid out in a regular grid pattern where the child layers in each composite row all have the same number of
 * rows and the same height, and the child layers in each composite column each have the same number of columns and
 * the same width.
 */
public class CompositeLayer extends AbstractLayer {
	
	
	protected static final class Child {
		
		public final int layoutX;
		public final int layoutY;
		
		public final String label;
		
		public final ILayer layer;
		
		private IConfigLabelAccumulator configLabelAccumulator;
		
		
		private Child(final int x, final int y, final String label, final ILayer layer) {
			this.layoutX= x;
			this.layoutY= y;
			this.label= label;
			this.layer= layer;
		}
		
	}
	
	
	protected final int layoutXCount;
	
	protected final int layoutYCount;
	
	/** Data struct. for child Layers */
	private final Child[][] childLayout;
	
	
	public CompositeLayer(final int layoutXCount, final int layoutYCount) {
		this.layoutXCount= layoutXCount;
		this.layoutYCount= layoutYCount;
		this.childLayout= new Child[layoutXCount][layoutYCount];
		
		initDims();
	}
	
	
	@Override
	protected void initDims() {
		if (this.childLayout == null) {
			return;
		}
		
		for (final Orientation orientation : Orientation.values()) {
			setDim((ignoreRef(orientation)) ?
					new CompositeLayerDim.IgnoreRef(this, orientation) :
					new CompositeLayerDim(this, orientation) );
		}
	}
	
	protected boolean ignoreRef(final Orientation orientation) {
		return false;
	}
	
	final CompositeLayerDim get(final Orientation orientation) {
		return (CompositeLayerDim) getDim(orientation);
	}
	
	
	@Override
	protected ILayerPainter createPainter() {
		return new CompositeLayerPainter();
	}
	
	
	// Dispose
	
	@Override
	public void dispose() {
		for (int layoutX= 0; layoutX < this.layoutXCount; layoutX++) {
			for (int layoutY= 0; layoutY < this.layoutYCount; layoutY++) {
				final Child child= this.childLayout[layoutX][layoutY];
				if (child != null) {
					child.layer.dispose();
				}
			}
		}
	}
	
	// Persistence
	
	@Override
	public void saveState(final String prefix, final Properties properties) {
		for (int layoutX= 0; layoutX < this.layoutXCount; layoutX++) {
			for (int layoutY= 0; layoutY < this.layoutYCount; layoutY++) {
				final Child child= this.childLayout[layoutX][layoutY];
				if (child != null) {
					child.layer.saveState(prefix + "." + child.label, properties); //$NON-NLS-1$
				}
			}
		}
		super.saveState(prefix, properties);
	}
	
	@Override
	public void loadState(final String prefix, final Properties properties) {
		for (int layoutX= 0; layoutX < this.layoutXCount; layoutX++) {
			for (int layoutY= 0; layoutY < this.layoutYCount; layoutY++) {
				final Child child= this.childLayout[layoutX][layoutY];
				if (child != null) {
					child.layer.loadState(prefix + "." + child.label, properties); //$NON-NLS-1$
				}
			}
		}
		super.loadState(prefix, properties);
	}
	
	// Configuration
	
	@Override
	public void configure(final ConfigRegistry configRegistry, final UiBindingRegistry uiBindingRegistry) {
		for (int layoutX= 0; layoutX < this.layoutXCount; layoutX++) {
			for (int layoutY= 0; layoutY < this.layoutYCount; layoutY++) {
				this.childLayout[layoutX][layoutY].layer.configure(configRegistry, uiBindingRegistry);
			}
		}
		
		super.configure(configRegistry, uiBindingRegistry);
	}
	
	/**
	 * {@inheritDoc}
	 * Handle commands
	 */
	@Override
	public boolean doCommand(final ILayerCommand command) {
		if (super.doCommand(command)) {
			return true;
		}
		return doCommandOnChildLayers(command);
	}
	
	protected boolean doCommandOnChildLayers(final ILayerCommand command) {
		for (int layoutX= 0; layoutX < this.layoutXCount; layoutX++) {
			for (int layoutY= 0; layoutY < this.layoutYCount; layoutY++) {
				if (this.childLayout[layoutX][layoutY].layer.doCommand(command)) {
					return true;
				}
			}
		}
		return false;
	}
	
	// Cell features
	
	@Override
	public ILayerCell getCellByPosition(final long columnPosition, final long rowPosition) {
		final Child child= getChildByPosition(columnPosition, rowPosition);
		
		if (child == null) {
			throw new PositionOutOfBoundsException(columnPosition + ", " + rowPosition); //$NON-NLS-1$
		}
		
		final long childColumnPosition= columnPosition - get(HORIZONTAL).getLayoutPosition(child.layoutX);
		final long childRowPosition= rowPosition - get(VERTICAL).getLayoutPosition(child.layoutY);
		
		final ILayerCell underlyingCell= child.layer.getCellByPosition(childColumnPosition, childRowPosition);
		
		final ILayerCellDim hDim= transformCellDim(underlyingCell.getDim(HORIZONTAL), columnPosition);
		final ILayerCellDim vDim= transformCellDim(underlyingCell.getDim(VERTICAL), rowPosition);
		
		return new ForwardLayerCell(this, hDim, vDim, underlyingCell) {
			
			@Override
			public LabelStack getConfigLabels() {
				final LabelStack configLabels= super.getConfigLabels();
				
				if (child.configLabelAccumulator != null) {
					child.configLabelAccumulator.accumulateConfigLabels(configLabels, childColumnPosition, childRowPosition);
				}
				
				configLabels.addLabel(child.label);
				
				return configLabels;
			}
			
		};
	}
	
	protected ILayerCellDim transformCellDim(final ILayerCellDim underlyingDim, final long position) {
		final long originPosition= (underlyingDim.getPosition() == underlyingDim.getOriginPosition()) ?
				position :
				get(underlyingDim.getOrientation()).underlyingToLocalPosition(
						position, underlyingDim.getOriginPosition() );
		return new LayerCellDim(underlyingDim.getOrientation(), underlyingDim.getId(),
				position, originPosition, underlyingDim.getPositionSpan() );
	}
	
	// Child layer stuff
	
	public void setChildLayer(final String label, final ILayer childLayer, final int layoutX, final int layoutY) {
		if (childLayer == null) {
			throw new IllegalArgumentException("Cannot set null child layer"); //$NON-NLS-1$
		}
		
		final Child child= new Child(layoutX, layoutY, label, childLayer);
		
		childLayer.addLayerListener(this);
		this.childLayout[layoutX][layoutY]= child;
		
		get(HORIZONTAL).updateChild(layoutX, layoutY, childLayer);
		get(VERTICAL).updateChild(layoutY, layoutX, childLayer);
		
		childLayer.setClientAreaProvider(new IClientAreaProvider() {
			@Override
			public LRectangle getClientArea() {
				return getChildClientArea(child);
			}
		});
	}
	
	/**
	 * Adds the configLabelAccumulator to the existing label accumulators.
	 */
	public void addConfigLabelAccumulatorForRegion(final String regionLabel, final IConfigLabelAccumulator configLabelAccumulator) {
		final Child child= getChildByLabel(regionLabel);
		if (child.configLabelAccumulator == null) {
			child.configLabelAccumulator= configLabelAccumulator;
		}
		else {
			final AggregrateConfigLabelAccumulator aggregateAccumulator;
			if (child.configLabelAccumulator instanceof AggregrateConfigLabelAccumulator) {
				aggregateAccumulator= (AggregrateConfigLabelAccumulator) child.configLabelAccumulator;
			}
			else {
				aggregateAccumulator= new AggregrateConfigLabelAccumulator();
				aggregateAccumulator.add(child.configLabelAccumulator);
				child.configLabelAccumulator= aggregateAccumulator;
			}
			aggregateAccumulator.add(configLabelAccumulator);
		}
	}
	
	private LRectangle getChildClientArea(final Child child) {
		final LRectangle compositeClientArea= getClientAreaProvider().getClientArea();
		
		final LRectangle childClientArea= new LRectangle(
				compositeClientArea.x + get(HORIZONTAL).getLayoutStart(child.layoutX),
				compositeClientArea.y + get(VERTICAL).getLayoutStart(child.layoutY),
				child.layer.getDim(HORIZONTAL).getPreferredSize(),
				child.layer.getDim(VERTICAL).getPreferredSize() );
		
		final LRectangle intersection= compositeClientArea.intersection(childClientArea);
		
		return intersection;
	}
	
	/**
	 * @param layoutX col position in the CompositeLayer
	 * @param layoutY row position in the CompositeLayer
	 * @return child layer according to the Composite Layer Layout
	 */
	public ILayer getChildLayerByLayoutCoordinate(final int layoutX, final int layoutY) {
		if (layoutX < 0 || layoutX >= this.layoutXCount || layoutY < 0 || layoutY >= this.layoutYCount) {
			return null;
		}
		else {
			return this.childLayout[layoutX][layoutY].layer;
		}
	}
	
	/**
	 * @param x pixel position
	 * @param y pixel position
	 * @return Region which the given position is in
	 */
	@Override
	public LabelStack getRegionLabelsByXY(final long x, final long y) {
		final Child child= getChildByPixelXY(x, y);
		if (child == null) {
			return null;
		}
		
		final long childX= x - get(HORIZONTAL).getLayoutStart(child.layoutX);
		final long childY= y - get(VERTICAL).getLayoutStart(child.layoutY);
		final LabelStack regionLabels= child.layer.getRegionLabelsByXY(childX, childY);
		
		regionLabels.addLabel(child.label);
		
		return regionLabels;
	}
	
	@Override
	public ILayer getUnderlyingLayerByPosition(final long columnPosition, final long rowPosition) {
		final Child child= getChildByPosition(columnPosition, rowPosition);
		return (child != null) ? child.layer : null;
	}
	
	// Layout coordinate accessors
	
	protected final Child getChildByLabel(final String label) {
		for (int layoutX= 0; layoutX < this.layoutXCount; layoutX++) {
			for (int layoutY= 0; layoutY < this.layoutYCount; layoutY++) {
				if (label.equals(this.childLayout[layoutX][layoutY].label)) {
					return this.childLayout[layoutX][layoutY];
				}
			}
		}
		return null;
	}
	
	protected final Child getChildByLayer(final ILayer childLayer) {
		for (int layoutX= 0; layoutX < this.layoutXCount; layoutX++) {
			for (int layoutY= 0; layoutY < this.layoutYCount; layoutY++) {
				if (this.childLayout[layoutX][layoutY].layer == childLayer) {
					return this.childLayout[layoutX][layoutY];
				}
			}
		}
		return null;
	}
	
	protected final Child getChildByPixelXY(final long x, final long y) {
		final int layoutX= get(HORIZONTAL).getLayoutByPixel(x);
		if (layoutX < 0) {
			return null;
		}
		final int layoutY= get(VERTICAL).getLayoutByPixel(y);
		if (layoutY < 0) {
			return null;
		}
		return this.childLayout[layoutX][layoutY];
	}
	
	protected final Child getChildByPosition(final long compositeColumnPosition, final long compositeRowPosition) {
		final int layoutX= get(HORIZONTAL).getLayoutByPosition(compositeColumnPosition);
		if (layoutX < 0) {
			return null;
		}
		final int layoutY= get(VERTICAL).getLayoutByPosition(compositeRowPosition);
		if (layoutY < 0) {
			return null;
		}
		return this.childLayout[layoutX][layoutY];
	}
	
	
	protected class CompositeLayerPainter implements ILayerPainter {
		
		@Override
		public void paintLayer(final ILayer natLayer, final GC gc,
				final int xOffset, final int yOffset, final Rectangle pixelRectangle,
				final IConfigRegistry configuration) {
			int x= xOffset;
			for (int layoutX= 0; layoutX < CompositeLayer.this.layoutXCount; layoutX++) {
				int y= yOffset;
				final int width= safe(CompositeLayer.this.childLayout[layoutX][0].layer.getWidth());
				for (int layoutY= 0; layoutY < CompositeLayer.this.layoutYCount; layoutY++) {
					final Child child= CompositeLayer.this.childLayout[layoutX][layoutY];
					
					final Rectangle childRectangle= new Rectangle(x, y, width, safe(child.layer.getHeight()) );
					final Rectangle childPaintRectangle= pixelRectangle.intersection(childRectangle);
					
					if (!childPaintRectangle.isEmpty()) {
						final Rectangle originalClipping= gc.getClipping();
						gc.setClipping(childPaintRectangle);
						
						child.layer.getLayerPainter().paintLayer(natLayer, gc,
								x, y, childPaintRectangle, configuration );
						
						gc.setClipping(originalClipping);
					}
					
					y+= childRectangle.height;
				}
				
				x+= width;
			}
		}
		
		@Override
		public LRectangle adjustCellBounds(final long columnPosition, final long rowPosition, final LRectangle cellBounds) {
			final Child child= getChildByPosition(columnPosition, rowPosition);
			
			final long widthOffset= get(HORIZONTAL).getLayoutStart(child.layoutX);
			final long heightOffset= get(VERTICAL).getLayoutStart(child.layoutY);
			
//			LRectangle bounds= new LRectangle(cellBounds.x - widthOffset, cellBounds.y - heightOffset, cellBounds.width, cellBounds.height);
			cellBounds.x-= widthOffset;
			cellBounds.y-= heightOffset;
			
			final ILayerPainter childLayerPainter= child.layer.getLayerPainter();
			final long childColumnPosition= columnPosition - get(HORIZONTAL).getLayoutPosition(child.layoutX);
			final long childRowPosition= rowPosition - get(VERTICAL).getLayoutPosition(child.layoutY);
			final LRectangle adjustedChildCellBounds= childLayerPainter.adjustCellBounds(childColumnPosition, childRowPosition, cellBounds);
//			LRectangle adjustedChildCellBounds= childLayerPainter.adjustCellBounds(childColumnPosition, childRowPosition, bounds);
			
			adjustedChildCellBounds.x+= widthOffset;
			adjustedChildCellBounds.y+= heightOffset;
			
			return adjustedChildCellBounds;
		}
		
	}
	
}
