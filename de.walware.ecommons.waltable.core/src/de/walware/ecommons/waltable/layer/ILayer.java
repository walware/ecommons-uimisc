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
// -cleanup
package de.walware.ecommons.waltable.layer;

import java.util.Properties;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.command.ILayerCommand;
import de.walware.ecommons.waltable.command.ILayerCommandHandler;
import de.walware.ecommons.waltable.config.ConfigRegistry;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.layer.event.ILayerEvent;
import de.walware.ecommons.waltable.layer.event.IVisualChangeEvent;
import de.walware.ecommons.waltable.painter.layer.ILayerPainter;
import de.walware.ecommons.waltable.persistence.IPersistable;
import de.walware.ecommons.waltable.ui.IClientAreaProvider;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;


/**
 * <p>
 * A Layer is a rectangular region of grid cells. A layer has methods to access its columns, rows, width and height. A
 * layer can be stacked on top of another layer in order to expose a transformed view of its underlying layer's grid
 * cell structure.
 * </p>
 * <p>
 * Columns and rows in a layer are referenced either by <b>position</b> or <b>id</b>. The position of a column/row in
 * a layer corresponds to the physical location of the column/row in the layer. The id of a column/row in a layer
 * corresponds to the location of the column/row in the lowest level layer in the layer stack. These concepts are
 * illustrated by the following example:
 * </p>
 * <pre>
 * Hide Layer C
 * 0 1 2 3 4 &lt;- column positions
 * 1 0 3 4 5 &lt;- column ids
 *
 * Reorder Layer B
 * 0 1 2 3 4 5 &lt;- column positions
 * 2 1 0 3 4 5 &lt;- column ids
 *
 * Data Layer A
 * 0 1 2 3 4 5 &lt;- column positions
 * 0 1 2 3 4 5 &lt;- column ids
 * </pre>
 * <p>
 * In the above example, Hide Layer C is stacked on top of Reorder Layer B, which is in turn stacked on top of Data
 * Layer A. The positions in Data Layer A are the same as its ids, because it is the lowest level layer in the
 * stack. Reorder Layer B reorders column 0 of its underlying layer (Data Layer A) after column 2 of its underlying
 * layer. Hide Layer C hides the first column of its underlying layer (Reorder Layer B).
 * </p>
 * <p>
 * Layers can also be laterally composed into larger layers. For instance, the standard grid layer is composed of a
 * body layer, column header layer, row header layer, and corner layer:
 * </p>
 * <table border=1>
 *   <caption></caption>
 *   <tr><td>corner</td><td>column header</td></tr>
 *   <tr><td>row header</td><td>body</td></tr>
 * </table>
 * 
 * @see CompositeLayer
 */
public interface ILayer extends ILayerListener, IPersistable {
	
	
	// Dispose
	
	void dispose();
	
	
	/**
	 * Returns the layer dimension of this layer for the given orientation
	 * 
	 * @param orientation the orientation
	 * 
	 * @return the layer dimension
	 */
	ILayerDim getDim(/*@NonNull*/ Orientation orientation);
	
	
	// Persistence
	
	/**
	 * Persistables registered with a layer will have a chance to write their data out to the
	 * {@link Properties} instance when the layer is persisted.
	 * @param persistable the persistable to be registered
	 */
	void registerPersistable(IPersistable persistable);
	
	void unregisterPersistable(IPersistable persistable);
	
	// Configuration
	
	/**
	 * Every layer gets this call back, starting at the top of the stack. This is triggered
	 * by the {@link NatTable#configure()} method. This is an opportunity to add
	 * any key/mouse bindings and other general configuration.
	 *
	 * @param configRegistry instance owned by {@link NatTable}
	 * @param uiBindingRegistry instance owned by {@link NatTable}
	 */
	void configure(ConfigRegistry configRegistry, UiBindingRegistry uiBindingRegistry);
	
	// Region
	
	/**
	 * Layer can apply its own labels to any cell it wishes.
	 * @param x the x pixel coordinate
	 * @param y the y pixel coordinate
	 * @return a LabelStack containing the region labels for the cell at the given pixel position
	 */
	LabelStack getRegionLabelsByXY(long x, long y);
	
	// Commands
	
	/**
	 * Opportunity to respond to a command as it flows down the stack. If the layer
	 * is not interested in the command it should allow the command to keep traveling
	 * down the stack.
	 *
	 * Note: Before the layer can process a command it <i>must</i> convert the
	 * command to its local co-ordinates using {@link ILayerCommand#convertToTargetLayer(ILayer)}
	 *
	 * @param command the command to perform
	 * @return true if the command has been handled, false otherwise
	 */
	boolean doCommand(ILayerCommand command);
	
	void registerCommandHandler(ILayerCommandHandler<?> commandHandler);
	
	void unregisterCommandHandler(Class<? extends ILayerCommand> commandClass);
	
	// Events
	
	/**
	 * Events can be fired to notify other components of the grid.
	 * Events travel <i>up</i> the layer stack and may cause a repaint.
	 * <p>
	 * Example: When the contents of the grid change {@link IVisualChangeEvent} can be
	 * fired to notify other layers to refresh their caches etc.
	 * 
	 * @param event the event to fire
	 */
	void fireLayerEvent(ILayerEvent event);
	
	void addLayerListener(ILayerListener listener);
	
	void removeLayerListener(ILayerListener listener);
	
	ILayerPainter getLayerPainter();
	
	// Client area
	
	IClientAreaProvider getClientAreaProvider();
	
	void setClientAreaProvider(IClientAreaProvider clientAreaProvider);
	
	// Horizontal features
	
	// Columns
	
	/**
	 * @return the number of columns in this coordinate model
	 */
	long getColumnCount();
	
	/**
	 * Returns the total width in pixels of this layer.
	 * 
	 * @return the width of this layer
	 */
	long getWidth();
	
	long getColumnPositionByX(long x);
	
	// X
	
	// Vertical features
	
	// Rows
	
	/**
	 * @return the number of rows in this coordinate model
	 */
	long getRowCount();
	
	/**
	 * Returns the total height in pixels of this layer.
	 * 
	 * @return the height of this layer
	 */
	long getHeight();
	
	long getRowPositionByY(long y);
	
	// Y
	
	// Cell features
	
	ILayerCell getCellByPosition(long columnPosition, long rowPosition);
	
	ILayer getUnderlyingLayerByPosition(long columnPosition, long rowPosition);
	
}
