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

package de.walware.ecommons.waltable.layer;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.ecommons.waltable.command.StructuralRefreshCommandHandler;
import de.walware.ecommons.waltable.command.VisualRefreshCommandHandler;
import de.walware.ecommons.waltable.data.IDataProvider;
import de.walware.ecommons.waltable.edit.UpdateDataCommandHandler;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.layer.cell.ILayerCellDim;
import de.walware.ecommons.waltable.layer.cell.LayerCell;
import de.walware.ecommons.waltable.layer.cell.LayerCellDim;
import de.walware.ecommons.waltable.layer.event.StructuralRefreshEvent;
import de.walware.ecommons.waltable.persistence.IPersistable;
import de.walware.ecommons.waltable.resize.ColumnResizeEvent;
import de.walware.ecommons.waltable.resize.MultiColumnResizeCommandHandler;
import de.walware.ecommons.waltable.resize.MultiRowResizeCommandHandler;
import de.walware.ecommons.waltable.resize.PositionResizeCommandHandler;
import de.walware.ecommons.waltable.resize.RowResizeEvent;


/**
 * Wraps the {@link IDataProvider}, and serves as the data source for all
 * other layers. Also, tracks the size of the columns and the rows using
 * {@link SizeConfig} objects. Since this layer sits directly on top of the
 * data source, at this layer index == position.
 */
public class DataLayer extends AbstractLayer implements ILayer {
	
	
	protected class DataLayerCell extends LayerCell {
		
		
		public DataLayerCell(final ILayerCellDim horizontalDim, final ILayerCellDim verticalDim) {
			super(DataLayer.this, horizontalDim, verticalDim);
		}
		
		
		@Override
		public Object getDataValue(final int flags, final IProgressMonitor monitor) {
			return DataLayer.this.dataProvider.getDataValue(getColumnPosition(), getRowPosition(),
					flags, monitor );
		}
		
	}
	
	
	public static final String PERSISTENCE_KEY_ROW_HEIGHT= ".rowHeight"; //$NON-NLS-1$
	public static final String PERSISTENCE_KEY_COLUMN_WIDTH= ".columnWidth"; //$NON-NLS-1$
	
	public static final int DEFAULT_COLUMN_WIDTH= 100;
	public static final int DEFAULT_ROW_HEIGHT= 20;
	
	
	protected IDataProvider dataProvider;
	
	private final long columnIdCat;
	private final SizeConfig columnWidthConfig;
	
	private final long rowIdCat;
	private final SizeConfig rowHeightConfig;
	
	
	public DataLayer(final IDataProvider dataProvider, final long idCat) {
		this(dataProvider,
				idCat, DEFAULT_COLUMN_WIDTH,
				idCat, DEFAULT_ROW_HEIGHT );
	}
	
	public DataLayer(final IDataProvider dataProvider,
			final long columnIdCat, final int defaultColumnWidth,
			final long rowIdCat, final int defaultRowHeight) {
		this(dataProvider,
				columnIdCat, new SizeConfig(defaultColumnWidth),
				rowIdCat, new SizeConfig(defaultRowHeight) );
	}
	
	public DataLayer(final IDataProvider dataProvider,
			final long columnIdCat, final SizeConfig columnWidthConfig,
			final long rowIdCat, final SizeConfig rowHeightConfig) {
		this.columnIdCat= columnIdCat;
		this.columnWidthConfig= columnWidthConfig;
		this.rowIdCat= rowIdCat;
		this.rowHeightConfig= rowHeightConfig;
		initDims();
		
		registerCommandHandlers();
		
		setDataProvider(dataProvider);
	}
	
	
	@Override
	protected void initDims() {
		if (this.columnIdCat == 0 || this.rowIdCat == 0) {
			return;
		}
		
		setDim(new SizeConfigDim<ILayer>(this, HORIZONTAL, this.columnIdCat, this.columnWidthConfig) {
			@Override
			public long getPositionCount() {
				return DataLayer.this.dataProvider.getColumnCount();
			}
		});
		setDim(new SizeConfigDim<ILayer>(this, VERTICAL, this.rowIdCat, this.rowHeightConfig) {
			@Override
			public long getPositionCount() {
				return DataLayer.this.dataProvider.getRowCount();
			}
		});
	}
	
	
	// Persistence
	
	@Override
	public void saveState(final String prefix, final Properties properties) {
		super.saveState(prefix, properties);
		this.columnWidthConfig.saveState(prefix + PERSISTENCE_KEY_COLUMN_WIDTH, properties);
		this.rowHeightConfig.saveState(prefix + PERSISTENCE_KEY_ROW_HEIGHT, properties);
	}
	
	@Override
	public void loadState(final String prefix, final Properties properties) {
		super.loadState(prefix, properties);
		this.columnWidthConfig.loadState(prefix + PERSISTENCE_KEY_COLUMN_WIDTH, properties);
		this.rowHeightConfig.loadState(prefix + PERSISTENCE_KEY_ROW_HEIGHT, properties);
		fireLayerEvent(new StructuralRefreshEvent(this));
	}
	
	// Configuration
	
	@Override
	protected void registerCommandHandlers() {
		registerCommandHandler(new PositionResizeCommandHandler(this));
		registerCommandHandler(new MultiColumnResizeCommandHandler(this));
		registerCommandHandler(new MultiRowResizeCommandHandler(this));
		registerCommandHandler(new UpdateDataCommandHandler(this));
		registerCommandHandler(new StructuralRefreshCommandHandler());
		registerCommandHandler(new VisualRefreshCommandHandler());
	}
	
	public IDataProvider getDataProvider() {
		return this.dataProvider;
	}
	
	protected void setDataProvider(final IDataProvider dataProvider) {
		if (this.dataProvider instanceof IPersistable) {
			unregisterPersistable((IPersistable) this.dataProvider);
		}
		
		this.dataProvider= dataProvider;
		
		if (dataProvider instanceof IPersistable) {
			registerPersistable((IPersistable) dataProvider);
		}
	}
	
	
	// Column Width
	
	public void setColumnWidthByPosition(final long columnPosition, final int width) {
		this.columnWidthConfig.setSize(columnPosition, width);
		fireLayerEvent(new ColumnResizeEvent(this, columnPosition));
	}
	
	public void setDefaultColumnWidth(final int width) {
		this.columnWidthConfig.setDefaultSize(width);
	}
	
	public void setDefaultColumnWidthByPosition(final long columnPosition, final int width) {
		this.columnWidthConfig.setDefaultSize(columnPosition, width);
	}
	
	// Column Resize
	
	public void setColumnPositionResizable(final long columnPosition, final boolean resizable) {
		this.columnWidthConfig.setPositionResizable(columnPosition, resizable);
	}
	
	public void setColumnsResizableByDefault(final boolean resizableByDefault) {
		this.columnWidthConfig.setResizableByDefault(resizableByDefault);
	}
	
	// Vertical features
	
	// Row Height
	
	public void setRowHeightByPosition(final long rowPosition, final int height) {
		this.rowHeightConfig.setSize(rowPosition, height);
		fireLayerEvent(new RowResizeEvent(this, rowPosition));
	}
	
	public void setDefaultRowHeight(final int height) {
		this.rowHeightConfig.setDefaultSize(height);
	}
	
	public void setDefaultRowHeightByPosition(final long rowPosition, final int height) {
		this.rowHeightConfig.setDefaultSize(rowPosition, height);
	}
	
	// Row Resize
	
	public void setRowPositionResizable(final long rowPosition, final boolean resizable) {
		this.rowHeightConfig.setPositionResizable(rowPosition, resizable);
	}
	
	public void setRowsResizableByDefault(final boolean resizableByDefault) {
		this.rowHeightConfig.setResizableByDefault(resizableByDefault);
	}
	
	
	// Cell features
	
	@Override
	public ILayerCell getCellByPosition(final long columnPosition, final long rowPosition) {
		final ILayerDim hDim= getDim(HORIZONTAL);
		final ILayerDim vDim= getDim(VERTICAL);
		final long columnId= hDim.getPositionId(columnPosition, columnPosition);
		final long rowId= vDim.getPositionId(rowPosition, rowPosition);
		
		return new DataLayerCell(
				new LayerCellDim(HORIZONTAL, columnId, columnPosition),
				new LayerCellDim(VERTICAL, rowId, rowPosition) );
	}
	
	@Override
	public ILayer getUnderlyingLayerByPosition(final long columnPosition, final long rowPosition) {
		return null;
	}
	
}
