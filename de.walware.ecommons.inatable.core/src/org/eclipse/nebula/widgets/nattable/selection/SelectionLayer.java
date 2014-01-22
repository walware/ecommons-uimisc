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
// ~Selection

package org.eclipse.nebula.widgets.nattable.selection;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;
import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.Direction;
import org.eclipse.nebula.widgets.nattable.coordinate.IValueIterator;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.coordinate.RangeList;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.edit.command.EditSelectionCommandHandler;
import org.eclipse.nebula.widgets.nattable.grid.command.InitializeAutoResizeColumnsCommandHandler;
import org.eclipse.nebula.widgets.nattable.grid.command.InitializeAutoResizeRowsCommandHandler;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.TransformIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCell;
import org.eclipse.nebula.widgets.nattable.painter.layer.ILayerPainter;
import org.eclipse.nebula.widgets.nattable.search.command.SearchGridCellsCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.command.ClearAllSelectionsCommand;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectAllCommand;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultSelectionLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.selection.event.CellSelectionEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.SelectionLayerStructuralChangeEventHandler;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.SelectionStyleLabels;


/**
 * Enables selection of column, rows, cells etc. on the table.
 * Also responds to UI bindings by changing the current selection.
 * Internally it uses the {@link ISelectionModel} to track the selection state.<br/>
 *
 * @see DefaultSelectionLayerConfiguration
 * @see Direction
 */
public class SelectionLayer extends TransformIndexLayer {

	public static final int MOVE_ALL = -1;
	public static final int NO_SELECTION = -1;

	protected ISelectionModel selectionModel;
	protected IUniqueIndexLayer underlyingLayer;
	protected final PositionCoordinate lastSelectedCell;
	protected final PositionCoordinate selectionAnchor;
	protected Rectangle lastSelectedRegion;

	private final SelectRowCommandHandler selectRowCommandHandler;
	private final SelectCellCommandHandler selectCellCommandHandler;
	private final SelectColumnCommandHandler selectColumnCommandHandler;
	
	public SelectionLayer(IUniqueIndexLayer underlyingLayer) {
		this(underlyingLayer, null, true);
	}

	public SelectionLayer(IUniqueIndexLayer underlyingLayer, boolean useDefaultConfiguration) {
		this(underlyingLayer, null, useDefaultConfiguration);
	}

	public SelectionLayer(IUniqueIndexLayer underlyingLayer, ISelectionModel selectionModel, boolean useDefaultConfiguration) {
		this(underlyingLayer, selectionModel, useDefaultConfiguration, true);
	}
	
	public SelectionLayer(IUniqueIndexLayer underlyingLayer, ISelectionModel selectionModel, boolean useDefaultConfiguration, boolean registerDefaultEventHandler) {
		super(underlyingLayer);
		this.underlyingLayer = underlyingLayer;

		setLayerPainter(new SelectionLayerPainter());

		this.selectionModel = selectionModel != null ? selectionModel : new SelectionModel(this);

		lastSelectedCell = new PositionCoordinate(this, NO_SELECTION, NO_SELECTION);
		selectionAnchor = new PositionCoordinate(this, NO_SELECTION, NO_SELECTION);

		selectRowCommandHandler = new SelectRowCommandHandler(this);
		selectCellCommandHandler = new SelectCellCommandHandler(this);
		selectColumnCommandHandler = new SelectColumnCommandHandler(this);

		registerCommandHandlers();

		if(registerDefaultEventHandler){
			registerEventHandler(new SelectionLayerStructuralChangeEventHandler(this, this.selectionModel));
		}
		if (useDefaultConfiguration) {
			addConfiguration(new DefaultSelectionLayerConfiguration());
		}
	}

	public ISelectionModel getSelectionModel() {
		return selectionModel;
	}

	public void setSelectionModel(ISelectionModel selectionModel) {
		this.selectionModel = selectionModel;
	}
	
	@Override
	public ILayerPainter getLayerPainter() {
		return layerPainter;
	}

	protected void addSelection(Rectangle selection) {
		if (selection != lastSelectedRegion) {
			selectionAnchor.columnPosition = lastSelectedCell.columnPosition;
			selectionAnchor.rowPosition = lastSelectedCell.rowPosition;

			lastSelectedRegion = selection;
		}

		selectionModel.addSelection(selection);
	}

	public void clear() {
		clearSelections();
		
		fireLayerEvent(new CellSelectionEvent(this, -1, -1, false));
	}
	
	protected void resetLastSelection() {
		lastSelectedCell.columnPosition = NO_SELECTION;
		lastSelectedCell.rowPosition = NO_SELECTION;
		lastSelectedRegion = null;
	}
	
	protected void clearSelections() {
		selectionModel.clearSelection();
		resetLastSelection();
		selectionAnchor.columnPosition = -1;
		selectionAnchor.rowPosition = -1;
	}

	protected void clearSelection(long columnPosition, long rowPosition) {
		selectionModel.clearSelection(columnPosition, rowPosition);
		resetLastSelection();
	}

	protected void clearSelection(Rectangle selection) {
		selectionModel.clearSelection(selection);
		resetLastSelection();
	}

	public void selectAll() {
		Rectangle selection = new Rectangle(0, 0, getColumnCount(), getRowCount());
		if(lastSelectedCell.columnPosition == SelectionLayer.NO_SELECTION || lastSelectedCell.rowPosition == SelectionLayer.NO_SELECTION){
			lastSelectedCell.rowPosition = 0;
			lastSelectedCell.columnPosition = 0;
		}
		addSelection(selection);
		fireCellSelectionEvent(lastSelectedCell.columnPosition, lastSelectedCell.rowPosition, false);
	}


	// Cell features

	public boolean isCellPositionSelected(final long columnPosition, final long rowPosition) {
		final ILayerCell cell = getCellByPosition(columnPosition, rowPosition);
		return (cell != null && cell.getDisplayMode() == DisplayMode.SELECT);
	}

	public void setSelectedCell(long columnPosition, long rowPosition) {
		selectCell(columnPosition, rowPosition, 0);
	}

	public List<PositionCoordinate> getSelectedCellPositions() {
		final RangeList selectedColumnPositions = getSelectedColumnPositions();
		final RangeList selectedRowPositions = getSelectedRowPositions();
		
		List<PositionCoordinate> selectedCells = new ArrayList<PositionCoordinate>();
		
		for (final IValueIterator columnIter = selectedColumnPositions.values().iterator(); columnIter.hasNext(); ) {
			final long columnPosition = columnIter.nextValue();
			for (final IValueIterator rowIter = selectedRowPositions.values().iterator(); rowIter.hasNext(); ) {
				final long rowPosition = rowIter.nextValue();
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
		Set<ILayerCell> selectedCells = new HashSet<ILayerCell>();

		List<PositionCoordinate> selectedCoords = getSelectedCellPositions();
		for (PositionCoordinate coord : selectedCoords) {
			selectedCells.add(getCellByPosition(coord.columnPosition, coord.rowPosition));
		}

		return selectedCells;
	}
	
	/**
	 * Calculates the selected cells - taking into account Shift and Ctrl key presses.
	 */
	public void selectCell(final long columnPosition, final long rowPosition, final int selectionFlags) {
		selectCellCommandHandler.selectCell(columnPosition, rowPosition, selectionFlags, false);
	}
	
	public void selectRegion(long startColumnPosition, long startRowPosition, long regionWidth, long regionHeight) {
		if (lastSelectedRegion == null) {
			lastSelectedRegion =  new Rectangle(startColumnPosition, startRowPosition, regionWidth, regionHeight);
		} else {
			lastSelectedRegion.x = startColumnPosition;
			lastSelectedRegion.y = startRowPosition;
			lastSelectedRegion.width = regionWidth;
			lastSelectedRegion.height = regionHeight;
		}
		selectionModel.addSelection(new Rectangle(lastSelectedRegion.x, lastSelectedRegion.y, lastSelectedRegion.width,	lastSelectedRegion.height));
	}
	
	// Selection anchor

	public PositionCoordinate getSelectionAnchor() {
		return selectionAnchor;
	}
	
	public void setSelectionAnchor(final long columnPosition, final long rowPosition,
			final boolean revealCell) {
		final long previousColumnPosition = this.selectionAnchor.columnPosition;
		final long previousRowPosition = this.selectionAnchor.rowPosition;
		
		if (columnPosition == NO_SELECTION || rowPosition == NO_SELECTION) {
			this.selectionAnchor.columnPosition = NO_SELECTION;
			this.selectionAnchor.rowPosition = NO_SELECTION;
			
			resetLastSelection();
			
			if (previousColumnPosition != NO_SELECTION && previousRowPosition != NO_SELECTION) {
//				?
//				fireLayerEvent(new CellSelectionEvent(this, columnPosition, rowPosition, false));
			}
			return;
		}
		
		if (columnPosition < 0 || columnPosition >= getColumnCount()
				|| rowPosition < 0 || rowPosition >= getRowCount() ) {
			throw new IndexOutOfBoundsException();
		}
		
		this.selectionAnchor.columnPosition = columnPosition;
		this.selectionAnchor.rowPosition = rowPosition;
		
		resetLastSelection();
		
		fireLayerEvent(new CellSelectionEvent(this, columnPosition, rowPosition, revealCell));
	}

	// Last selected

	public PositionCoordinate getLastSelectedCellPosition() {
		if (lastSelectedCell.columnPosition != NO_SELECTION && lastSelectedCell.rowPosition != NO_SELECTION) {
			return lastSelectedCell;
		} else {
			return selectionAnchor;
		}
	}

	// Column features

	public boolean hasColumnSelection() {
		return lastSelectedCell.columnPosition != NO_SELECTION;
	}

	public RangeList getSelectedColumnPositions() {
		return selectionModel.getSelectedColumnPositions();
	}

	public boolean isColumnPositionSelected(long columnPosition) {
		return selectionModel.isColumnPositionSelected(columnPosition);
	}

	public RangeList getFullySelectedColumnPositions() {
		return selectionModel.getFullySelectedColumnPositions();
	}

	public boolean isColumnPositionFullySelected(long columnPosition) {
		return selectionModel.isColumnPositionFullySelected(columnPosition);
	}

//	public void selectColumn(final long columnPosition, final long rowPosition, final int selectionFlags) {
//		selectColumnCommandHandler.;
//	}


	// Row features

	public boolean hasRowSelection() {
		return lastSelectedCell.rowPosition != NO_SELECTION;
	}

	public long getSelectedRowCount() {
		return selectionModel.getSelectedRowCount();
	}

	public RangeList getSelectedRowPositions() {
		return selectionModel.getSelectedRowPositions();
	}

	public boolean isRowPositionSelected(long rowPosition) {
		return selectionModel.isRowPositionSelected(rowPosition);
	}

	public RangeList getFullySelectedRowPositions() {
		return selectionModel.getFullySelectedRowPositions();
	}

	public boolean isRowPositionFullySelected(long rowPosition) {
		return selectionModel.isRowPositionFullySelected(rowPosition);
	}

//	public void selectRow(final long columnPosition, final long rowPosition, final int selectionFlags,
//			boolean moveIntoViewport) {
//		selectRowCommandHandler.selectRows(columnPosition, Arrays.asList(Long.valueOf(rowPosition)),
//				selectionFlags, (moveIntoViewport) ? rowPosition : -1);
//	}

	// ILayer methods
	
	@Override
	public ILayerCell getCellByPosition(long columnPosition, long rowPosition) {
		ILayerCell cell = super.getCellByPosition(columnPosition, rowPosition);
		if (cell != null && selectionModel.isCellPositionSelected(cell)) {
			cell = new LayerCell(cell.getLayer(), cell.getDim(HORIZONTAL), cell.getDim(VERTICAL),
					DisplayMode.SELECT );
		}
		return cell;
	}
	
	@Override
	public LabelStack getConfigLabelsByPosition(long columnPosition, long rowPosition) {
		LabelStack labelStack = super.getConfigLabelsByPosition(columnPosition, rowPosition);
		
		ILayerCell cell = getCellByPosition(columnPosition, rowPosition);
		if (cell != null) {
			Rectangle cellRectangle = new Rectangle(
							cell.getOriginColumnPosition(),
							cell.getOriginRowPosition(),
							cell.getColumnSpan(),
							cell.getRowSpan());
			
			if (cellRectangle.contains(selectionAnchor.columnPosition, selectionAnchor.rowPosition)) {
				labelStack.addLabel(SelectionStyleLabels.SELECTION_ANCHOR_STYLE);
			}
		}

		return labelStack;
	}

	// Command handling

	@Override
	protected void registerCommandHandlers() {
		// Command handlers also registered by the DefaultSelectionLayerConfiguration
		registerCommandHandler(selectCellCommandHandler);
		registerCommandHandler(selectRowCommandHandler);
		registerCommandHandler(selectColumnCommandHandler);

		registerCommandHandler(new EditSelectionCommandHandler(this));
		registerCommandHandler(new InitializeAutoResizeColumnsCommandHandler(this));
		registerCommandHandler(new InitializeAutoResizeRowsCommandHandler(this));
		registerCommandHandler(new CopyDataCommandHandler(this));
		registerCommandHandler(new SearchGridCellsCommandHandler(this));
	}

	@Override
	public boolean doCommand(ILayerCommand command) {
		if (command instanceof SelectAllCommand && command.convertToTargetLayer(this)) {
			selectAll();
			return true;
		} else if (command instanceof ClearAllSelectionsCommand && command.convertToTargetLayer(this)) {
			clear();
			return true;
		}
		return super.doCommand(command);
	}

	protected void fireCellSelectionEvent(long columnPosition, long rowPosition,
			boolean forcingEntireCellIntoViewport) {
		fireLayerEvent(new CellSelectionEvent(this, columnPosition, rowPosition,
				forcingEntireCellIntoViewport));
	}
	
}
