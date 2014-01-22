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
// -cleanup
package org.eclipse.nebula.widgets.nattable.layer;

import java.util.Collection;
import java.util.Properties;

import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.command.ILayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.IVisualChangeEvent;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.layer.ILayerPainter;
import org.eclipse.nebula.widgets.nattable.persistence.IPersistable;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.util.IClientAreaProvider;


/**
 * <p>
 * A Layer is a rectangular region of grid cells. A layer has methods to access its columns, rows, width and height. A
 * layer can be stacked on top of another layer in order to expose a transformed view of its underlying layer's grid
 * cell structure.
 * </p>
 * <p>
 * Columns and rows in a layer are referenced either by <b>position</b> or <b>index</b>. The position of a column/row in
 * a layer corresponds to the physical location of the column/row in the layer. The index of a column/row in a layer
 * corresponds to the location of the column/row in the lowest level layer in the layer stack. These concepts are
 * illustrated by the following example:
 * </p>
 * <pre>
 * Hide Layer C
 * 0 1 2 3 4 &lt;- column positions
 * 1 0 3 4 5 &lt;- column indexes
 *
 * Reorder Layer B
 * 0 1 2 3 4 5 &lt;- column positions
 * 2 1 0 3 4 5 &lt;- column indexes
 *
 * Data Layer A
 * 0 1 2 3 4 5 &lt;- column positions
 * 0 1 2 3 4 5 &lt;- column indexes
 * </pre>
 * <p>
 * In the above example, Hide Layer C is stacked on top of Reorder Layer B, which is in turn stacked on top of Data
 * Layer A. The positions in Data Layer A are the same as its indexes, because it is the lowest level layer in the
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
	
	@Deprecated
	long getPreferredColumnCount();
	
	/**
	 * Gets the underlying non-transformed column index for the given column position.
	 * @param columnPosition a column position relative to this coordinate model
	 * @return an underlying non-transformed column index, or -1 if the given column position does not exist within this
	 * coordinate system
	 */
	long getColumnIndexByPosition(long columnPosition);
	
	/**
	 * Convert a column position to the coordinates of the underlying layer.
	 * This is possible since each layer is aware of its underlying layer.
	 * @param localColumnPosition column position in local (the layer's own) coordinates
	 * @return column position in the underlying layer's coordinates
	 */
	long localToUnderlyingColumnPosition(long localColumnPosition);
	
	long underlyingToLocalColumnPosition(ILayer sourceUnderlyingLayer, long underlyingColumnPosition);
	
	Collection<Range> underlyingToLocalColumnPositions(ILayer sourceUnderlyingLayer, Collection<Range> underlyingColumnPositionRanges);
	
	// Width
	
	/**
	 * Returns the total width in pixels of this layer.
	 * 
	 * @return the width of this layer
	 */
	long getWidth();
	
	long getPreferredWidth();
	
	/**
	 * Returns the width in pixels of the given column.
	 * 
	 * The width of invisible and non-existing columns is 0.
	 * 
	 * @param columnPosition the column position in this layer
	 * 
	 * @return the width of the column
	 */
	int getColumnWidthByPosition(long columnPosition);
	
	// Column resize
	
	boolean isColumnPositionResizable(long columnPosition);
	
	// X
	
	/**
	 * Returns the column position that contains the given x coordinate.
	 * @param x a horizontal pixel location relative to the pixel boundary of this layer
	 * @return a column position relative to the associated coordinate system, or -1 if there is no column that contains x
	 */
	long getColumnPositionByX(long x);
	
	/**
	 * Returns the x offset in pixels of the given column.
	 * 
	 * @param columnPosition the column position in this layer
	 * @return the x offset of the column, or -1
	 */
	long getStartXOfColumnPosition(long columnPosition);
	
	// Underlying
	
	Collection<ILayer> getUnderlyingLayersByColumnPosition(long columnPosition);
	
	// Vertical features
	
	// Rows
	
	/**
	 * @return the number of rows in this coordinate model
	 */
	long getRowCount();
	
	@Deprecated
	long getPreferredRowCount();
	
	/**
	 * Gets the underlying non-transformed row index for the given row position.
	 * @param rowPosition a row position relative to this coordinate model
	 * @return an underlying non-transformed row index, or -1 if the given row position does not exist within this
	 * coordinate system
	 */
	long getRowIndexByPosition(long rowPosition);
	
	long localToUnderlyingRowPosition(long localRowPosition);
	
	long underlyingToLocalRowPosition(ILayer sourceUnderlyingLayer, long underlyingRowPosition);
	
	Collection<Range> underlyingToLocalRowPositions(ILayer sourceUnderlyingLayer, Collection<Range> underlyingRowPositionRanges);
	
	// Height
	
	/**
	 * Returns the total height in pixels of this layer.
	 * 
	 * @return the height of this layer
	 */
	long getHeight();
	
	long getPreferredHeight();
	
	/**
	 * Returns the height in pixels of the given row.
	 * 
	 * The height of invisible and non-existing rows is 0.
	 * 
	 * @param rowPosition the row position in this layer
	 * 
	 * @return the height of the row
	 */
	int getRowHeightByPosition(long rowPosition);
	
	// Row resize
	
	boolean isRowPositionResizable(long rowPosition);
	
	// Y
	
	/**
	 * Returns the row position that contains the given y coordinate.
	 * @param y a vertical pixel location relative to the pixel boundary of this layer
	 * @return a row position relative to the associated coordinate system, or -1 if there is no row that contains y
	 */
	long getRowPositionByY(long y);
	
	/**
	 * Returns the y offset in pixels of the given row.
	 * 
	 * @param rowPosition the row position in this layer
	 * 
	 * @return the y offset of the row, or -1
	 */
	long getStartYOfRowPosition(long rowPosition);
	
	// Underlying
	
	Collection<ILayer> getUnderlyingLayersByRowPosition(long rowPosition);
	
	// Cell features
	
	ILayerCell getCellByPosition(long columnPosition, long rowPosition);
	
	/**
	 * Calculates the bounds in pixel for the given cell position.
	 * 
	 * @param columnPosition the column position of the cell
	 * @param rowPosition the row position of the cell
	 * 
	 * @return the bounds, or <code>null</code> if there are no valid bounds
	 */
	Rectangle getBoundsByPosition(long columnPosition, long rowPosition);
	
	LabelStack getConfigLabelsByPosition(long columnPosition, long rowPosition);
	
	Object getDataValueByPosition(long columnPosition, long rowPosition);
	
	ILayer getUnderlyingLayerByPosition(long columnPosition, long rowPosition);
	
	ICellPainter getCellPainter(long columnPosition, long rowPosition,
			ILayerCell cell, IConfigRegistry configRegistry);
	
}
