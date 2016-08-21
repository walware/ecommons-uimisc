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
package de.walware.ecommons.waltable.grid.layer;

import de.walware.ecommons.waltable.command.ILayerCommand;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.export.ExportCommandHandler;
import de.walware.ecommons.waltable.grid.ClientAreaResizeCommand;
import de.walware.ecommons.waltable.grid.GridRegion;
import de.walware.ecommons.waltable.grid.layer.config.DefaultGridLayerConfiguration;
import de.walware.ecommons.waltable.layer.CompositeLayer;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.print.PrintCommandHandler;
import de.walware.ecommons.waltable.selection.SelectCellCommand;
import de.walware.ecommons.waltable.swt.SWTUtil;

/**
 * Top level layer. It is composed of the smaller child layers: RowHeader,
 * ColumnHeader, Corner and Body It does not have its own coordinate system
 * unlike the other layers. It simply delegates most functions to its child
 * layers.
 */
public class GridLayer extends CompositeLayer {

	public GridLayer(final ILayer bodyLayer, final ILayer columnHeaderLayer, final ILayer rowHeaderLayer, final ILayer cornerLayer) {
		this(bodyLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer, true);
	}
	
	public GridLayer(final ILayer bodyLayer, final ILayer columnHeaderLayer, final ILayer rowHeaderLayer, final ILayer cornerLayer, final boolean useDefaultConfiguration) {
		super(2, 2);
		
		setBodyLayer(bodyLayer);
		setColumnHeaderLayer(columnHeaderLayer);
		setRowHeaderLayer(rowHeaderLayer);
		setCornerLayer(cornerLayer);
		
		init(useDefaultConfiguration);
	}

	protected GridLayer(final boolean useDefaultConfiguration) {
		super(2, 2);
		init(useDefaultConfiguration);
	}

	protected void init(final boolean useDefaultConfiguration) {
		registerCommandHandlers();
		
		if (useDefaultConfiguration) {
			addConfiguration(new DefaultGridLayerConfiguration(this));
		}
	}

	@Override
	protected void registerCommandHandlers() {
		registerCommandHandler(new PrintCommandHandler(this));
		registerCommandHandler(new ExportCommandHandler(this));
//		registerCommandHandler(new AutoResizeColumnCommandHandler(this));
//		registerCommandHandler(new AutoResizeRowCommandHandler(this));
	}
	
	/**
	 * How the GridLayer processes commands is very important. <strong>Do not
	 * change this unless you know what you are doing and understand the full
	 * ramifications of your change. Otherwise your grid will not behave
	 * correctly!</strong>
	 * 
	 * The Body is always given the first chance to process a command. There are
	 * two reasons for this: (1) most commands (80%) are destined for the body
	 * anyways so it's faster to check there first (2) the other layers all
	 * transitively depend on the body so it's not wise to ask them to do stuff
	 * until after the body has done it. This is especially true of grid
	 * initialization where the body must be initialized before any of its
	 * dependent layers.
	 * 
	 * Because of this, if you want to intercept well-known commands to
	 * implement custom behavior (for example, you want to intercept the
	 * {@link SelectCellCommand}) then <strong>you must inject your special
	 * layer into the body. </strong> An injected column or row header will
	 * never see the command because it will be consumed first by the body. In
	 * practice, it's a good idea to implement all your command-handling logic
	 * in the body.
	 **/
	@Override
	protected boolean doCommandOnChildLayers(final ILayerCommand command) {
		if (getBodyLayer().doCommand(command)) {
			return true;
		}
		else if (getColumnHeaderLayer().doCommand(command)) {
			return true;
		}
		else if (getRowHeaderLayer().doCommand(command)) {
			return true;
		}
		else {
			return getCornerLayer().doCommand(command);
		}
	}
	
	// Sub-layer accessors
	
	public ILayer getCornerLayer() {
		return getChildLayerByLayoutCoordinate(0, 0);
	}
	
	public void setCornerLayer(final ILayer cornerLayer) {
		setChildLayer(GridRegion.CORNER, cornerLayer, 0, 0);
	}

	public ILayer getColumnHeaderLayer() {
		return getChildLayerByLayoutCoordinate(1, 0);
	}
	
	public void setColumnHeaderLayer(final ILayer columnHeaderLayer) {
		setChildLayer(GridRegion.COLUMN_HEADER, columnHeaderLayer, 1, 0);
	}
	
	public ILayer getRowHeaderLayer() {
		return getChildLayerByLayoutCoordinate(0, 1);
	}
	
	public void setRowHeaderLayer(final ILayer rowHeaderLayer) {
		setChildLayer(GridRegion.ROW_HEADER, rowHeaderLayer, 0, 1);
	}
	
	public ILayer getBodyLayer() {
		return getChildLayerByLayoutCoordinate(1, 1);
	}
	
	public void setBodyLayer(final ILayer bodyLayer) {
		setChildLayer(GridRegion.BODY, bodyLayer, 1, 1);
		
		//update the command handlers for auto resize because of the connection to the body layer stack
//		unregisterCommandHandler(AutoResizePositionsCommand.class);
//		unregisterCommandHandler(AutoResizeRowsCommand.class);
		
//		registerCommandHandler(new AutoResizeColumnCommandHandler(this));
//		registerCommandHandler(new AutoResizeRowCommandHandler(this));		
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()
				+ "[corner=" + getCornerLayer() //$NON-NLS-1$
				+ " columnHeader=" + getColumnHeaderLayer() //$NON-NLS-1$
				+ " rowHeader=" + getRowHeaderLayer() //$NON-NLS-1$
				+ " bodyLayer=" + getBodyLayer() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	
	@Override
	public boolean doCommand(final ILayerCommand command) {
		if (command instanceof ClientAreaResizeCommand && command.convertToTargetLayer(this)) {
			final ClientAreaResizeCommand clientAreaResizeCommand= (ClientAreaResizeCommand) command;
			final LRectangle possibleArea= SWTUtil.toNatTable(clientAreaResizeCommand.getScrollable().getClientArea());
			
			//remove the column header height and the row header width from the client area to 
			//ensure that only the body region is used for percentage calculation
			final LRectangle rowLayerArea= getRowHeaderLayer().getClientAreaProvider().getClientArea();
			final LRectangle columnLayerArea= getColumnHeaderLayer().getClientAreaProvider().getClientArea();
			possibleArea.width= possibleArea.width - rowLayerArea.width;
			possibleArea.height= possibleArea.height - columnLayerArea.height;
			
			clientAreaResizeCommand.setCalcArea(possibleArea);
		}
		return super.doCommand(command);
	}

}