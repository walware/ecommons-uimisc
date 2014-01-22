/*******************************************************************************
 * Copyright (c) 2012, 2013 Edwin Park and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Edwin Park - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.layer;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;
import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;

import java.util.Collection;
import java.util.Properties;

import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.command.ILayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.InvertedLayerCell;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.layer.ILayerPainter;
import org.eclipse.nebula.widgets.nattable.persistence.IPersistable;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.util.IClientAreaProvider;


public class InvertedLayer implements IUniqueIndexLayer {
	
	
	private final ILayerDim hDim;
	private final ILayerDim vDim;
	
	private IUniqueIndexLayer underlyingLayer;
	
	
	public InvertedLayer(IUniqueIndexLayer underlyingLayer) {
		this.underlyingLayer = underlyingLayer;
		
		this.hDim = new HorizontalLayerDim(this);
		this.vDim = new VerticalLayerDim(this);
	}
	
	
	@Override
	public ILayerDim getDim(final Orientation orientation) {
		if (orientation == null) {
			throw new NullPointerException("orientation"); //$NON-NLS-1$
		}
		
		return (orientation == HORIZONTAL) ? this.hDim : this.vDim;
	}
	
	
	// ILayerListener
	
	public void handleLayerEvent(ILayerEvent event) {
		underlyingLayer.handleLayerEvent(event);
	}
	
	// IPersistable
	
	public void saveState(String prefix, Properties properties) {
		underlyingLayer.saveState(prefix, properties);
	}
	
	public void loadState(String prefix, Properties properties) {
		underlyingLayer.loadState(prefix, properties);
	}
	
	// Dispose
	
	public void dispose() {
		underlyingLayer.dispose();
	}
	
	// Persistence
	
	public void registerPersistable(IPersistable persistable) {
		underlyingLayer.registerPersistable(persistable);
	}
	
	public void unregisterPersistable(IPersistable persistable) {
		underlyingLayer.unregisterPersistable(persistable);
	}
	
	// Configuration
	
	public void configure(ConfigRegistry configRegistry, UiBindingRegistry uiBindingRegistry) {
		underlyingLayer.configure(configRegistry, uiBindingRegistry);
	}
	
	// Region
	
	public LabelStack getRegionLabelsByXY(long x, long y) {
		return underlyingLayer.getRegionLabelsByXY(y, x);
	}
	
	// Commands
	
	public boolean doCommand(ILayerCommand command) {
		return underlyingLayer.doCommand(command);
	}
	
	public void registerCommandHandler(ILayerCommandHandler<?> commandHandler) {
		underlyingLayer.registerCommandHandler(commandHandler);
	}
	
	public void unregisterCommandHandler(Class<? extends ILayerCommand> commandClass) {
		underlyingLayer.unregisterCommandHandler(commandClass);
	}
	
	// Events
	
	public void fireLayerEvent(ILayerEvent event) {
		underlyingLayer.fireLayerEvent(event);
	}
	
	public void addLayerListener(ILayerListener listener) {
		underlyingLayer.addLayerListener(listener);
	}
	
	public void removeLayerListener(ILayerListener listener) {
		underlyingLayer.removeLayerListener(listener);
	}
	
	public ILayerPainter getLayerPainter() {
		return underlyingLayer.getLayerPainter();
	}
	
	// Client area
	
	public IClientAreaProvider getClientAreaProvider() {
		return underlyingLayer.getClientAreaProvider();
	}
	
	public void setClientAreaProvider(final IClientAreaProvider clientAreaProvider) {
		underlyingLayer.setClientAreaProvider(new IClientAreaProvider() {
			public Rectangle getClientArea() {
				return clientAreaProvider.getClientArea().switchOrientation();
			}
		});
	}
	
	// Horizontal features
	
	// Columns
	
	public long getColumnCount() {
		return underlyingLayer.getRowCount();
	}
	
	public long getPreferredColumnCount() {
		return underlyingLayer.getPreferredRowCount();
	}
	
	public long getColumnIndexByPosition(long columnPosition) {
		return underlyingLayer.getRowIndexByPosition(columnPosition);
	}
	
	public long localToUnderlyingColumnPosition(long localColumnPosition) {
		return underlyingLayer.localToUnderlyingRowPosition(localColumnPosition);
	}
	
	public long underlyingToLocalColumnPosition(ILayer sourceUnderlyingLayer, long underlyingColumnPosition) {
		return underlyingLayer.underlyingToLocalRowPosition(sourceUnderlyingLayer, underlyingColumnPosition);
	}
	
	public Collection<Range> underlyingToLocalColumnPositions(ILayer sourceUnderlyingLayer, Collection<Range> underlyingColumnPositionRanges) {
		return underlyingLayer.underlyingToLocalRowPositions(sourceUnderlyingLayer, underlyingColumnPositionRanges);
	}
	
	// Width
	
	public long getWidth() {
		return underlyingLayer.getHeight();
	}
	
	public long getPreferredWidth() {
		return underlyingLayer.getPreferredHeight();
	}
	
	public int getColumnWidthByPosition(long columnPosition) {
		return underlyingLayer.getRowHeightByPosition(columnPosition);
	}
	
	// Column resize
	
	public boolean isColumnPositionResizable(long columnPosition) {
		return underlyingLayer.isRowPositionResizable(columnPosition);
	}
	
	// X
	
	public long getColumnPositionByX(long x) {
		return underlyingLayer.getRowPositionByY(x);
	}
	
	public long getStartXOfColumnPosition(long columnPosition) {
		return underlyingLayer.getStartYOfRowPosition(columnPosition);
	}
	
	// Underlying
	
	public Collection<ILayer> getUnderlyingLayersByColumnPosition(long columnPosition) {
		return underlyingLayer.getUnderlyingLayersByRowPosition(columnPosition);
	}
	
	// Unique index

	public long getColumnPositionByIndex(long columnIndex) {
		return underlyingLayer.getRowPositionByIndex(columnIndex);
	}
	
	// Vertical features
	
	// Rows
	
	public long getRowCount() {
		return underlyingLayer.getColumnCount();
	}
	
	public long getPreferredRowCount() {
		return underlyingLayer.getPreferredColumnCount();
	}
	
	public long getRowIndexByPosition(long rowPosition) {
		return underlyingLayer.getColumnIndexByPosition(rowPosition);
	}
	
	public long localToUnderlyingRowPosition(long localRowPosition) {
		return underlyingLayer.localToUnderlyingColumnPosition(localRowPosition);
	}
	
	public long underlyingToLocalRowPosition(ILayer sourceUnderlyingLayer, long underlyingRowPosition) {
		return underlyingLayer.underlyingToLocalColumnPosition(sourceUnderlyingLayer, underlyingRowPosition);
	}
	
	public Collection<Range> underlyingToLocalRowPositions(ILayer sourceUnderlyingLayer, Collection<Range> underlyingRowPositionRanges) {
		return underlyingLayer.underlyingToLocalColumnPositions(sourceUnderlyingLayer, underlyingRowPositionRanges);
	}
	
	// Height
	
	public long getHeight() {
		return underlyingLayer.getWidth();
	}
	
	public long getPreferredHeight() {
		return underlyingLayer.getPreferredWidth();
	}
	
	public int getRowHeightByPosition(long rowPosition) {
		return underlyingLayer.getColumnWidthByPosition(rowPosition);
	}
	
	// Row resize
	
	public boolean isRowPositionResizable(long rowPosition) {
		return underlyingLayer.isColumnPositionResizable(rowPosition);
	}
	
	// Y
	
	public long getRowPositionByY(long y) {
		return underlyingLayer.getColumnPositionByX(y);
	}
	
	public long getStartYOfRowPosition(long rowPosition) {
		return underlyingLayer.getStartXOfColumnPosition(rowPosition);
	}
	
	// Underlying
	
	public Collection<ILayer> getUnderlyingLayersByRowPosition(long rowPosition) {
		return underlyingLayer.getUnderlyingLayersByColumnPosition(rowPosition);
	}
	
	// Unique index
	
	public long getRowPositionByIndex(long rowIndex) {
		return underlyingLayer.getColumnPositionByIndex(rowIndex);
	}
	
	// Cell features
	
	public ILayerCell getCellByPosition(long columnPosition, long rowPosition) {
		ILayerCell cell = underlyingLayer.getCellByPosition(rowPosition, columnPosition);
		if (cell != null)
			return new InvertedLayerCell(cell);
		else
			return null;
//		return underlyingLayer.getCellByPosition(rowPosition, columnPosition);
	}
	
	public Rectangle getBoundsByPosition(long columnPosition, long rowPosition) {
		return underlyingLayer.getBoundsByPosition(rowPosition, columnPosition).switchOrientation();
	}
	
	public LabelStack getConfigLabelsByPosition(long columnPosition, long rowPosition) {
		return underlyingLayer.getConfigLabelsByPosition(rowPosition, columnPosition);
	}
	
	public Object getDataValueByPosition(long columnPosition, long rowPosition) {
		return underlyingLayer.getDataValueByPosition(rowPosition, columnPosition);
	}
	
	public ILayer getUnderlyingLayerByPosition(long columnPosition, long rowPosition) {
		return underlyingLayer.getUnderlyingLayerByPosition(rowPosition, columnPosition);
	}
	
	public ICellPainter getCellPainter(long columnPosition, long rowPosition, ILayerCell cell, IConfigRegistry configRegistry) {
		return underlyingLayer.getCellPainter(rowPosition, columnPosition, cell, configRegistry);
	}
	
}
