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
// ~Selection

package de.walware.ecommons.waltable.selection;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.walware.ecommons.waltable.command.ILayerCommand;
import de.walware.ecommons.waltable.coordinate.Direction;
import de.walware.ecommons.waltable.coordinate.ILValueIterator;
import de.walware.ecommons.waltable.coordinate.LRangeList;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.coordinate.PositionCoordinate;
import de.walware.ecommons.waltable.coordinate.PositionOutOfBoundsException;
import de.walware.ecommons.waltable.edit.EditSelectionCommandHandler;
import de.walware.ecommons.waltable.layer.ForwardLayer;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.layer.cell.ForwardLayerCell;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.layer.cell.ILayerCellDim;
import de.walware.ecommons.waltable.painter.layer.ILayerPainter;
import de.walware.ecommons.waltable.selection.config.DefaultSelectionLayerConfiguration;
import de.walware.ecommons.waltable.style.DisplayMode;
import de.walware.ecommons.waltable.style.SelectionStyleLabels;


/**
 * Enables selection of column, rows, cells etc. on the table.
 * Also responds to UI bindings by changing the current selection.
 * Internally it uses the {@link ISelectionModel} to track the selection state.<br/>
 *
 * @see DefaultSelectionLayerConfiguration
 * @see Direction
 */
public class SelectionLayer extends ForwardLayer {
	
	
	public static final int MOVE_ALL= -1;
	public static final long NO_SELECTION= Long.MIN_VALUE;
	
	
	protected ISelectionModel selectionModel;
	protected ILayer underlyingLayer;
	protected final PositionCoordinate lastSelectedCell;
	protected final PositionCoordinate selectionAnchor;
	protected LRectangle lastSelectedRegion;
	
	private final SelectCellCommandHandler selectCellCommandHandler;
	private final SelectDimPositionsCommandHandler selectDimPositionsCommandHandler;
	
	
	public SelectionLayer(final ILayer underlyingLayer) {
		this(underlyingLayer, null, true);
	}
	
	public SelectionLayer(final ILayer underlyingLayer, final boolean useDefaultConfiguration) {
		this(underlyingLayer, null, useDefaultConfiguration);
	}
	
	
	public SelectionLayer(final ILayer underlyingLayer, final ISelectionModel selectionModel, final boolean useDefaultConfiguration) {
		this(underlyingLayer, selectionModel, useDefaultConfiguration, true);
	}
	
	public SelectionLayer(final ILayer underlyingLayer, final ISelectionModel selectionModel, final boolean useDefaultConfiguration, final boolean registerDefaultEventHandler) {
		super(underlyingLayer);
		this.underlyingLayer= underlyingLayer;
		
		setLayerPainter(new SelectionLayerPainter());
		
		this.selectionModel= selectionModel != null ? selectionModel : new SelectionModel(this);
		
		this.lastSelectedCell= new PositionCoordinate(this, NO_SELECTION, NO_SELECTION);
		this.selectionAnchor= new PositionCoordinate(this, NO_SELECTION, NO_SELECTION);
		
		this.selectCellCommandHandler= new SelectCellCommandHandler(this);
		this.selectDimPositionsCommandHandler= new SelectDimPositionsCommandHandler(this);
		
		registerCommandHandlers();
		
		if(registerDefaultEventHandler){
			registerEventHandler(new SelectionLayerStructuralChangeEventHandler(this, this.selectionModel));
		}
		if (useDefaultConfiguration) {
			addConfiguration(new DefaultSelectionLayerConfiguration());
		}
	}
	
	
	public ISelectionModel getSelectionModel() {
		return this.selectionModel;
	}
	
	public void setSelectionModel(final ISelectionModel selectionModel) {
		this.selectionModel= selectionModel;
	}
	
	@Override
	public ILayerPainter getLayerPainter() {
		return this.layerPainter;
	}
	
	protected void addSelection(final LRectangle selection) {
		if (selection != this.lastSelectedRegion) {
			this.selectionAnchor.columnPosition= this.lastSelectedCell.columnPosition;
			this.selectionAnchor.rowPosition= this.lastSelectedCell.rowPosition;
			
			this.lastSelectedRegion= selection;
		}
		
		this.selectionModel.addSelection(selection);
	}
	
	public void clear() {
		clearSelections();
		
		fireLayerEvent(new CellSelectionEvent(this, NO_SELECTION, NO_SELECTION, false));
	}
	
	protected void resetLastSelection() {
		this.lastSelectedCell.columnPosition= NO_SELECTION;
		this.lastSelectedCell.rowPosition= NO_SELECTION;
		this.lastSelectedRegion= null;
	}
	
	protected void clearSelections() {
		this.selectionModel.clearSelection();
		resetLastSelection();
		this.selectionAnchor.columnPosition= -1;
		this.selectionAnchor.rowPosition= -1;
	}
	
	protected void clearSelection(final long columnPosition, final long rowPosition) {
		this.selectionModel.clearSelection(columnPosition, rowPosition);
		resetLastSelection();
	}
	
	protected void clearSelection(final LRectangle selection) {
		this.selectionModel.clearSelection(selection);
		resetLastSelection();
	}
	
	public void selectAll() {
		final LRectangle selection= new LRectangle(0, 0, getColumnCount(), getRowCount());
		if(this.lastSelectedCell.columnPosition == SelectionLayer.NO_SELECTION || this.lastSelectedCell.rowPosition == SelectionLayer.NO_SELECTION){
			this.lastSelectedCell.rowPosition= 0;
			this.lastSelectedCell.columnPosition= 0;
		}
		addSelection(selection);
		fireCellSelectionEvent(this.lastSelectedCell.columnPosition, this.lastSelectedCell.rowPosition, false);
	}
	
	
	// Cell features
	
	public boolean isCellPositionSelected(final long columnPosition, final long rowPosition) {
		final ILayerCell cell= getCellByPosition(columnPosition, rowPosition);
		return (cell != null && cell.getDisplayMode() == DisplayMode.SELECT);
	}
	
	public void setSelectedCell(final long columnPosition, final long rowPosition) {
		selectCell(columnPosition, rowPosition, 0);
	}
	
	public List<PositionCoordinate> getSelectedCellPositions() {
		final LRangeList selectedColumnPositions= getSelectedColumnPositions();
		final LRangeList selectedRowPositions= getSelectedRowPositions();
		
		final List<PositionCoordinate> selectedCells= new ArrayList<>();
		
		for (final ILValueIterator columnIter= selectedColumnPositions.values().iterator(); columnIter.hasNext(); ) {
			final long columnPosition= columnIter.nextValue();
			for (final ILValueIterator rowIter= selectedRowPositions.values().iterator(); rowIter.hasNext(); ) {
				final long rowPosition= rowIter.nextValue();
				if (isCellPositionSelected(columnPosition, rowPosition)) {
					selectedCells.add(new PositionCoordinate(this, columnPosition, rowPosition));
				}
			}
		}
		
		return selectedCells;
	}
	
	/**
	 * Retrieves the ILayerCells out of the SelectionLayer that are currently marked as selected in
	 * the SelectionModel. Takes spanning into account.
	 * @return The selected ILayerCells
	 */
	public Collection<ILayerCell> getSelectedCells() {
		final Set<ILayerCell> selectedCells= new HashSet<>();
		
		final List<PositionCoordinate> selectedCoords= getSelectedCellPositions();
		for (final PositionCoordinate coord : selectedCoords) {
			selectedCells.add(getCellByPosition(coord.columnPosition, coord.rowPosition));
		}
		
		return selectedCells;
	}
	
	/**
	 * Calculates the selected cells - taking into account Shift and Ctrl key presses.
	 */
	public void selectCell(final long columnPosition, final long rowPosition, final int selectionFlags) {
		this.selectCellCommandHandler.selectCell(columnPosition, rowPosition, selectionFlags, false);
	}
	
	public void selectRegion(final long startColumnPosition, final long startRowPosition, final long regionWidth, final long regionHeight) {
		if (this.lastSelectedRegion == null) {
			this.lastSelectedRegion=  new LRectangle(startColumnPosition, startRowPosition, regionWidth, regionHeight);
		} else {
			this.lastSelectedRegion.x= startColumnPosition;
			this.lastSelectedRegion.y= startRowPosition;
			this.lastSelectedRegion.width= regionWidth;
			this.lastSelectedRegion.height= regionHeight;
		}
		this.selectionModel.addSelection(new LRectangle(this.lastSelectedRegion.x, this.lastSelectedRegion.y, this.lastSelectedRegion.width,	this.lastSelectedRegion.height));
	}
	
	// Selection anchor
	
	public PositionCoordinate getSelectionAnchor() {
		return this.selectionAnchor;
	}
	
	public void setSelectionAnchor(final long columnPosition, final long rowPosition,
			final boolean revealCell) {
		final long previousColumnPosition= this.selectionAnchor.columnPosition;
		final long previousRowPosition= this.selectionAnchor.rowPosition;
		
		if (columnPosition == NO_SELECTION || rowPosition == NO_SELECTION) {
			this.selectionAnchor.columnPosition= NO_SELECTION;
			this.selectionAnchor.rowPosition= NO_SELECTION;
			
			resetLastSelection();
			
			if (previousColumnPosition != NO_SELECTION && previousRowPosition != NO_SELECTION) {
//				?
//				fireLayerEvent(new CellSelectionEvent(this, columnPosition, rowPosition, false));
			}
			return;
		}
		
		if (columnPosition < 0 || columnPosition >= getColumnCount()
				|| rowPosition < 0 || rowPosition >= getRowCount() ) {
			throw new PositionOutOfBoundsException(columnPosition + ", " + rowPosition);
		}
		
		this.selectionAnchor.columnPosition= columnPosition;
		this.selectionAnchor.rowPosition= rowPosition;
		
		resetLastSelection();
		
		fireLayerEvent(new CellSelectionEvent(this, columnPosition, rowPosition, revealCell));
	}

	// Last selected

	public PositionCoordinate getLastSelectedCellPosition() {
		if (this.lastSelectedCell.columnPosition != NO_SELECTION && this.lastSelectedCell.rowPosition != NO_SELECTION) {
			return this.lastSelectedCell;
		} else {
			return this.selectionAnchor;
		}
	}

	// Column features

	public boolean hasColumnSelection() {
		return this.lastSelectedCell.columnPosition != NO_SELECTION;
	}

	public LRangeList getSelectedColumnPositions() {
		return this.selectionModel.getSelectedColumnPositions();
	}

	public boolean isColumnPositionSelected(final long columnPosition) {
		return this.selectionModel.isColumnPositionSelected(columnPosition);
	}

	public LRangeList getFullySelectedColumnPositions() {
		return this.selectionModel.getFullySelectedColumnPositions();
	}

	public boolean isColumnPositionFullySelected(final long columnPosition) {
		return this.selectionModel.isColumnPositionFullySelected(columnPosition);
	}

//	public void selectColumn(final long columnPosition, final long rowPosition, final int selectionFlags) {
//		selectDimPositionsCommandHandler.;
//	}


	// Row features

	public boolean hasRowSelection() {
		return this.lastSelectedCell.rowPosition != NO_SELECTION;
	}

	public long getSelectedRowCount() {
		return this.selectionModel.getSelectedRowCount();
	}

	public LRangeList getSelectedRowPositions() {
		return this.selectionModel.getSelectedRowPositions();
	}

	public boolean isRowPositionSelected(final long rowPosition) {
		return this.selectionModel.isRowPositionSelected(rowPosition);
	}

	public LRangeList getFullySelectedRowPositions() {
		return this.selectionModel.getFullySelectedRowPositions();
	}

	public boolean isRowPositionFullySelected(final long rowPosition) {
		return this.selectionModel.isRowPositionFullySelected(rowPosition);
	}

//	public void selectRow(final long columnPosition, final long rowPosition, final int selectionFlags,
//			boolean moveIntoViewport) {
//		selectRowCommandHandler.selectRows(columnPosition, Arrays.asList(Long.valueOf(rowPosition)),
//				selectionFlags, (moveIntoViewport) ? rowPosition : -1);
//	}
	

	public LRangeList getSelectedPositions(final Orientation orientation) {
		return (orientation == HORIZONTAL) ?
				this.selectionModel.getSelectedColumnPositions() :
				this.selectionModel.getSelectedRowPositions();
	}

	public boolean isPositionSelected(final Orientation orientation, final long position) {
		return (orientation == HORIZONTAL) ?
				this.selectionModel.isColumnPositionSelected(position) :
				this.selectionModel.isRowPositionSelected(position);
	}

	public LRangeList getFullySelectedPositions(final Orientation orientation) {
		return (orientation == HORIZONTAL) ?
				this.selectionModel.getFullySelectedColumnPositions() :
				this.selectionModel.getFullySelectedRowPositions();
	}

	public boolean isPositionFullySelected(final Orientation orientation, final long position) {
		return (orientation == HORIZONTAL) ?
				this.selectionModel.isColumnPositionFullySelected(position) :
				this.selectionModel.isRowPositionFullySelected(position);
	}

	// ILayer methods
	
	@Override
	protected ILayerCell createCell(final ILayerCellDim hDim, final ILayerCellDim vDim,
			final ILayerCell underlyingCell) {
		return new ForwardLayerCell(this, hDim, vDim, underlyingCell) {
			
			@Override
			public DisplayMode getDisplayMode() {
				if (SelectionLayer.this.selectionModel.isCellPositionSelected(this)) {
					return DisplayMode.SELECT;
				}
				return super.getDisplayMode();
			}
			
			@Override
			public LabelStack getConfigLabels() {
				final LabelStack configLabels= super.getConfigLabels();
				
				final LRectangle cellRectangle= new LRectangle(
						getOriginColumnPosition(),
						getOriginRowPosition(),
						getColumnSpan(),
						getRowSpan());
				if (cellRectangle.contains(
						SelectionLayer.this.selectionAnchor.columnPosition, SelectionLayer.this.selectionAnchor.rowPosition) ) {
					configLabels.addLabel(SelectionStyleLabels.SELECTION_ANCHOR_STYLE);
				}
				
				return configLabels;
			}
			
		};
	}
	
	// Command handling
	
	@Override
	protected void registerCommandHandlers() {
		// Command handlers also registered by the DefaultSelectionLayerConfiguration
		registerCommandHandler(this.selectCellCommandHandler);
		registerCommandHandler(this.selectDimPositionsCommandHandler);
		
		registerCommandHandler(new EditSelectionCommandHandler(this));
//		registerCommandHandler(new CopyToClipboardCommandHandler(this));
	}
	
	@Override
	public boolean doCommand(final ILayerCommand command) {
		if (command instanceof SelectAllCommand
				&& command.cloneCommand().convertToTargetLayer(this)) {
			selectAll();
			return true;
		}
		else if (command instanceof ClearAllSelectionsCommand
				&& command.cloneCommand().convertToTargetLayer(this)) {
			clear();
			return true;
		}
		return super.doCommand(command);
	}
	
	protected void fireCellSelectionEvent(final long columnPosition, final long rowPosition,
			final boolean forcingEntireCellIntoViewport) {
		fireLayerEvent(new CellSelectionEvent(this, columnPosition, rowPosition,
				forcingEntireCellIntoViewport));
	}
	
}
