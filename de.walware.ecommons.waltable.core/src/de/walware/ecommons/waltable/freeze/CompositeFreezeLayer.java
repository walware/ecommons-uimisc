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

package de.walware.ecommons.waltable.freeze;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;
import static de.walware.ecommons.waltable.painter.cell.GraphicsUtils.safe;

import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import de.walware.ecommons.waltable.command.ILayerCommand;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.PositionCoordinate;
import de.walware.ecommons.waltable.freeze.config.DefaultFreezeGridBindings;
import de.walware.ecommons.waltable.grid.ClientAreaResizeCommand;
import de.walware.ecommons.waltable.grid.layer.DimensionallyDependentLayer;
import de.walware.ecommons.waltable.layer.AbstractLayer;
import de.walware.ecommons.waltable.layer.CompositeLayer;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.painter.layer.ILayerPainter;
import de.walware.ecommons.waltable.persistence.IPersistable;
import de.walware.ecommons.waltable.selection.SelectionLayer;
import de.walware.ecommons.waltable.style.DisplayMode;
import de.walware.ecommons.waltable.util.GUIHelper;
import de.walware.ecommons.waltable.viewport.ViewportLayer;
import de.walware.ecommons.waltable.viewport.ViewportSelectDimPositionsCommandHandler;


public class CompositeFreezeLayer extends CompositeLayer {
	
	
	private final FreezeLayer freezeLayer;
	private final ViewportLayer viewportLayer;
	private final SelectionLayer selectionLayer;
	
	
	public CompositeFreezeLayer(final FreezeLayer freezeLayer, final ViewportLayer viewportLayer, final SelectionLayer selectionLayer) {
		this(freezeLayer, viewportLayer, selectionLayer, true);
	}
	
	public CompositeFreezeLayer(final FreezeLayer freezeLayer, final ViewportLayer viewportLayer, final SelectionLayer selectionLayer,
			final boolean useDefaultConfiguration) {
		super(2, 2);
		this.freezeLayer= freezeLayer;
		this.viewportLayer= viewportLayer;
		this.selectionLayer= selectionLayer;
		
		setChildLayer("FROZEN_REGION", freezeLayer, 0, 0); //$NON-NLS-1$
		setChildLayer("FROZEN_ROW_REGION", new DimensionallyDependentLayer(viewportLayer.getScrollableLayer(), viewportLayer, freezeLayer), 1, 0); //$NON-NLS-1$
		setChildLayer("FROZEN_COLUMN_REGION", new DimensionallyDependentLayer(viewportLayer.getScrollableLayer(), freezeLayer, viewportLayer), 0, 1); //$NON-NLS-1$
		setChildLayer("NONFROZEN_REGION", viewportLayer, 1, 1); //$NON-NLS-1$
		
		registerCommandHandlers();
		
		if (useDefaultConfiguration) {
			addConfiguration(new DefaultFreezeGridBindings());
		}
	}
	
	
	@Override
	protected ILayerPainter createPainter() {
		return new FreezableLayerPainter();
	}
	
	
	public boolean isFrozen() {
		return this.freezeLayer.isFrozen();
	}
	
	@Override
	public ILayerPainter getLayerPainter() {
		return this.layerPainter;
	}
	
	@Override
	protected void registerCommandHandlers() {
		registerCommandHandler(new FreezeCommandHandler(this.freezeLayer, this.viewportLayer, this.selectionLayer));
		
		final AbstractLayer frozenRowLayer= (AbstractLayer) getChildLayerByLayoutCoordinate(1, 0);
		frozenRowLayer.registerCommandHandler(new ViewportSelectDimPositionsCommandHandler(
				frozenRowLayer, VERTICAL ));
		
		final AbstractLayer frozenColumnLayer= (AbstractLayer) getChildLayerByLayoutCoordinate(0, 1);
		frozenColumnLayer.registerCommandHandler(new ViewportSelectDimPositionsCommandHandler(
				frozenRowLayer, HORIZONTAL ));
	}
	
	
	@Override
	public boolean doCommand(final ILayerCommand command) {
		//if this layer should handle a ClientAreaResizeCommand we have to ensure that
		//it is only called on the ViewportLayer, as otherwise an undefined behaviour
		//could occur because the ViewportLayer isn't informed about potential refreshes
		if (command instanceof ClientAreaResizeCommand) {
			this.viewportLayer.doCommand(command);
		}
		return super.doCommand(command);
	}
	
	
	// Persistence
	
	@Override
	public void saveState(final String prefix, final Properties properties) {
		PositionCoordinate coord= this.freezeLayer.getTopLeftPosition();
		properties.setProperty(prefix + FreezeLayer.PERSISTENCE_TOP_LEFT_POSITION, 
				coord.columnPosition + IPersistable.VALUE_SEPARATOR + coord.rowPosition);
		
		coord= this.freezeLayer.getBottomRightPosition();
		properties.setProperty(prefix + FreezeLayer.PERSISTENCE_BOTTOM_RIGHT_POSITION, 
				coord.columnPosition + IPersistable.VALUE_SEPARATOR + coord.rowPosition);
		
		super.saveState(prefix, properties);
	}
	
	@Override
	public void loadState(final String prefix, final Properties properties) {
		String property= properties.getProperty(prefix + FreezeLayer.PERSISTENCE_TOP_LEFT_POSITION);
		PositionCoordinate topLeftPosition= null;
		if (property != null) {
			final StringTokenizer tok= new StringTokenizer(property, IPersistable.VALUE_SEPARATOR);
			final String columnPosition= tok.nextToken();
			final String rowPosition= tok.nextToken();
			topLeftPosition= new PositionCoordinate(this.freezeLayer, 
					Long.valueOf(columnPosition), Long.valueOf(rowPosition));
		}
		
		property= properties.getProperty(prefix + FreezeLayer.PERSISTENCE_BOTTOM_RIGHT_POSITION);
		PositionCoordinate bottomRightPosition= null;
		if (property != null) {
			final StringTokenizer tok= new StringTokenizer(property, IPersistable.VALUE_SEPARATOR);
			final String columnPosition= tok.nextToken();
			final String rowPosition= tok.nextToken();
			bottomRightPosition= new PositionCoordinate(this.freezeLayer, 
					Long.valueOf(columnPosition), Long.valueOf(rowPosition));
		}
		
		//only restore a freeze state if there is one persisted
		if (topLeftPosition != null && bottomRightPosition != null) {
			if (topLeftPosition.columnPosition == -1 && topLeftPosition.rowPosition == -1
					&& bottomRightPosition.columnPosition == -1 && bottomRightPosition.rowPosition == -1) {
				FreezeHelper.unfreeze(this.freezeLayer, this.viewportLayer);
			} else {
				FreezeHelper.freeze(this.freezeLayer, this.viewportLayer, topLeftPosition, bottomRightPosition);
			}
		}
		
		super.loadState(prefix, properties);
	}
	
	
	class FreezableLayerPainter extends CompositeLayerPainter {
		
		public FreezableLayerPainter() {
		}
		
		@Override
		public void paintLayer(final ILayer natLayer, final GC gc, final int xOffset, final int yOffset, final org.eclipse.swt.graphics.Rectangle rectangle, final IConfigRegistry configRegistry) {
			super.paintLayer(natLayer, gc, xOffset, yOffset, rectangle, configRegistry);
			
			Color separatorColor= configRegistry.getConfigAttribute(IFreezeConfigAttributes.SEPARATOR_COLOR, DisplayMode.NORMAL);
			if (separatorColor == null) {
				separatorColor= GUIHelper.COLOR_BLUE;
			}
			
			gc.setClipping(rectangle);
			final Color oldFg= gc.getForeground();
			gc.setForeground(separatorColor);
			final long freezeWidth= CompositeFreezeLayer.this.freezeLayer.getWidth() - 1;
			if (freezeWidth > 0) {
				gc.drawLine(safe(xOffset + freezeWidth), yOffset, safe(xOffset + freezeWidth), safe(yOffset + getHeight() - 1));
			}
			final long freezeHeight= CompositeFreezeLayer.this.freezeLayer.getHeight() - 1;
			if (freezeHeight > 0) {
				gc.drawLine(xOffset, safe(yOffset + freezeHeight), safe(xOffset + getWidth() - 1), safe(yOffset + freezeHeight));
			}
			gc.setForeground(oldFg);
		}
		
	}
	
}
