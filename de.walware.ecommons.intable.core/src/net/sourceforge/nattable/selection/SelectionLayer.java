package net.sourceforge.nattable.selection;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sourceforge.nattable.command.ILayerCommand;
import net.sourceforge.nattable.coordinate.IRelative.Direction;
import net.sourceforge.nattable.coordinate.PositionCoordinate;
import net.sourceforge.nattable.coordinate.Range;
import net.sourceforge.nattable.copy.command.CopyDataCommandHandler;
import net.sourceforge.nattable.edit.command.EditSelectionCommandHandler;
import net.sourceforge.nattable.grid.command.InitializeAutoResizeColumnsCommandHandler;
import net.sourceforge.nattable.grid.command.InitializeAutoResizeRowsCommandHandler;
import net.sourceforge.nattable.hideshow.command.ColumnHideCommand;
import net.sourceforge.nattable.hideshow.command.MultiColumnHideCommand;
import net.sourceforge.nattable.layer.AbstractLayerTransform;
import net.sourceforge.nattable.layer.IUniqueIndexLayer;
import net.sourceforge.nattable.layer.LabelStack;
import net.sourceforge.nattable.painter.layer.ILayerPainter;
import net.sourceforge.nattable.resize.command.ColumnResizeCommand;
import net.sourceforge.nattable.resize.command.MultiColumnResizeCommand;
import net.sourceforge.nattable.resize.command.MultiRowResizeCommand;
import net.sourceforge.nattable.resize.command.RowResizeCommand;
import net.sourceforge.nattable.search.command.SearchGridCellsCommandHandler;
import net.sourceforge.nattable.selection.command.ClearAllSelectionsCommand;
import net.sourceforge.nattable.selection.command.ISelectionCommand.SelectionFlag;
import net.sourceforge.nattable.selection.command.SelectAllCommand;
import net.sourceforge.nattable.selection.config.DefaultSelectionLayerConfiguration;
import net.sourceforge.nattable.selection.event.CellSelectionEvent;
import net.sourceforge.nattable.selection.event.ColumnSelectionEvent;
import net.sourceforge.nattable.selection.event.SelectionLayerStructuralChangeEventHandler;
import net.sourceforge.nattable.style.DisplayMode;
import net.sourceforge.nattable.style.SelectionStyleLabels;

import org.eclipse.swt.graphics.Rectangle;

/**
 * Enables selection of column, rows, cells etc. on the table.
 * Also responds to UI bindings by changing the current selection.
 * Internally it uses the {@link ISelectionModel} to track the selection state.<br/>
 *
 * @see DefaultSelectionLayerConfiguration
 * @see Direction
 */
public class SelectionLayer extends AbstractLayerTransform implements IUniqueIndexLayer {

	public static final int MOVE_ALL = -1;
	private static final int NO_SELECTION = -1;


	protected ISelectionModel selectionModel;
	protected IUniqueIndexLayer underlyingLayer;
	protected final PositionCoordinate lastSelectedCell;
	protected final PositionCoordinate selectionAnchor;
	protected Rectangle lastSelectedRegion;

	private final SelectRowsCommandHandler selectRowCommandHandler;
	private final SelectCellCommandHandler selectCellCommandHandler;
	private final SelectColumnsCommandHandler selectColumnCommandHandler;

	public SelectionLayer(IUniqueIndexLayer underlyingLayer) {
		this(underlyingLayer, new SelectionModel(), true);
	}

	public SelectionLayer(IUniqueIndexLayer underlyingLayer, boolean useDefaultConfiguration) {
		this(underlyingLayer, new SelectionModel(), useDefaultConfiguration);
	}
	
	public SelectionLayer(IUniqueIndexLayer underlyingLayer, ISelectionModel selectionModel, boolean useDefaultConfiguration) {
		super(underlyingLayer);
		this.underlyingLayer = underlyingLayer;

		setLayerPainter(new SelectionLayerPainter());

		this.selectionModel = selectionModel;

		lastSelectedCell = new PositionCoordinate(this, NO_SELECTION, NO_SELECTION);
		selectionAnchor = new PositionCoordinate(this, 0, 0);

		selectRowCommandHandler = new SelectRowsCommandHandler(this);
		selectCellCommandHandler = new SelectCellCommandHandler(this);
		selectColumnCommandHandler = new SelectColumnsCommandHandler(this);

		registerCommandHandlers();

		registerEventHandler(new SelectionLayerStructuralChangeEventHandler(this, selectionModel));

		if (useDefaultConfiguration) {
			addConfiguration(new DefaultSelectionLayerConfiguration());
		}
	}

	public void setSelectionModel(ISelectionModel selectionModel) {
		this.selectionModel = selectionModel;
	}
	
	@Override
	public ILayerPainter getLayerPainter() {
		return layerPainter;
	}

	public int getColumnPositionByIndex(int columnIndex) {
		return underlyingLayer.getColumnPositionByIndex(columnIndex);
	}

	// Column features

	public boolean hasColumnSelection() {
		return lastSelectedCell.columnPosition != NO_SELECTION;
	}

	public boolean isColumnPositionSelected(int columnPosition) {
		return selectionModel.isColumnPositionSelected(columnPosition);
	}

	public int[] getSelectedColumns() {
		return selectionModel.getSelectedColumns();
	}

	public int[] getFullySelectedColumnPositions(){
		return selectionModel.getFullySelectedColumns(getRowCount());
	}

	public int[] getFullySelectedColumns() {
		return selectionModel.getFullySelectedColumns(getRowCount());
	}

	public boolean isColumnFullySelected(int columnPosition) {
		return selectionModel.isColumnFullySelected(columnPosition, getRowCount());
	}

	public void selectColumn(final int columnPosition, final int rowPosition,
			final Set<SelectionFlag> selectionFlags){
		selectColumnCommandHandler.selectColumn(Collections.singleton(columnPosition), rowPosition,
				selectionFlags);
	}

	// Cell features

	public boolean isCellPositionSelected(int columnPosition, int rowPosition) {
		return selectionModel.isCellPositionSelected(columnPosition, rowPosition);
	}

	public void setSelectedCell(int columnPosition, int rowPosition) {
		selectCell(columnPosition, rowPosition, SelectionFlag.NONE);
	}

	/**
	 * When extending a selected area we need to start adding cells from the last selected cell.
	 * If we are not extending a selection we need to move from the selection <i>anchor</i>.
	 */
	protected PositionCoordinate getCellPositionToMoveFrom(boolean withShiftMask, boolean withControlMask) {
		return (!withShiftMask && !withControlMask)	? getSelectionAnchor() : getLastSelectedCellPosition();
	}

	public PositionCoordinate[] getSelectedCells() {
		int[] selectedColumnPositions = getSelectedColumns();
		Set<Range> selectedRowPositions = getSelectedRows();

		List<PositionCoordinate> selectedCells = new LinkedList<PositionCoordinate>();

		for (int columnPositionIndex = 0; columnPositionIndex < selectedColumnPositions.length; columnPositionIndex++) {
			final int columnPosition = selectedColumnPositions[columnPositionIndex];

			for (Range rowIndexRange : selectedRowPositions) {
				for (int rowPositionIndex = rowIndexRange.start; rowPositionIndex < rowIndexRange.end; rowPositionIndex++) {
					if (selectionModel.isCellPositionSelected(columnPosition, rowPositionIndex)) {
						selectedCells.add(new PositionCoordinate(this, columnPosition, rowPositionIndex));
					}
				}
			}
		}
		return selectedCells.toArray(new PositionCoordinate[0]);
	}

	/**
	 * Calculates the selected cells - taking into account Shift and Ctrl key presses.
	 */
	public void selectCell(final int columnPosition, final int rowPosition,
			final Set<SelectionFlag> selectionFlags) {
		selectCellCommandHandler.selectCell(columnPosition, rowPosition, selectionFlags);
	}

	public PositionCoordinate getSelectionAnchor() {
		return selectionAnchor;
	}

	public PositionCoordinate getLastSelectedCellPosition() {
		if (lastSelectedCell.columnPosition != NO_SELECTION && lastSelectedCell.rowPosition != NO_SELECTION) {
			return lastSelectedCell;
		} else {
			return null;
		}
	}

	// Row features

	public boolean hasRowSelection() {
		return lastSelectedCell.rowPosition != NO_SELECTION;
	}

	public boolean isRowPositionSelected(int rowPosition) {
		return selectionModel.isRowPositionSelected(rowPosition);
	}

	public boolean isRowFullySelected(int rowPosition) {
		return selectionModel.isRowFullySelected(rowPosition, getColumnCount());
	}

	public Set<Range> getSelectedRows() {
		return selectionModel.getSelectedRows();
	}

	public int[] getFullySelectedRowPositions() {
		return selectionModel.getFullySelectedRows(getColumnCount());
	}

	protected ISelectionModel getSelectionModel() {
		return selectionModel;
	}

	public int getSelectedRowCount() {
		return selectionModel.getSelectedRowCount();
	}

	public void selectRow(final int columnPosition, final int rowPosition,
			final Set<SelectionFlag> selectionFlags, boolean moveIntoViewport) {
		selectRowCommandHandler.selectRows(columnPosition, Arrays.asList(Integer.valueOf(rowPosition)),
				selectionFlags, (moveIntoViewport) ? rowPosition : -1);
	}

	// Clear features

	public void clear() {
		clearSelections();
		
		fireLayerEvent(new CellSelectionEvent(this, -1, -1, false));
	}
	
	protected void clearSelections() {
		selectionModel.clearSelection();
		lastSelectedCell.columnPosition = -1;
		lastSelectedCell.rowPosition = -1;
		lastSelectedRegion = new Rectangle(0,0,0,0);
		selectionAnchor.columnPosition = -1;
		selectionAnchor.rowPosition = -1;
	}

	protected void clearSelection(int columnPosition, int rowPosition) {
		selectionModel.removeSelection(columnPosition, rowPosition);
	}

	protected void clearSelection(Rectangle selection) {
		if (lastSelectedRegion != null
				&& lastSelectedRegion.equals(selection)){
			if (selectionAnchor.columnPosition >= 0) {
				lastSelectedRegion = new Rectangle(
						selectionAnchor.columnPosition,
						selectionAnchor.rowPosition, 1, 1);
			}
			else {
				lastSelectedRegion = null;
			}
			lastSelectedCell.columnPosition = -1;
			lastSelectedCell.rowPosition = -1;
		}
		
		selectionModel.removeSelection(selection);
	}

	protected void addSelection(Rectangle selection) {
		if (selection != lastSelectedRegion) {
			selectionAnchor.columnPosition = lastSelectedCell.columnPosition;
			selectionAnchor.rowPosition = lastSelectedCell.rowPosition;

			lastSelectedRegion = selection;
		}

		selectionModel.addSelection(selection);
	}

	public void selectAll() {
		Rectangle selection = new Rectangle(0, 0, getColumnCount(), getRowCount());
		addSelection(selection);
		fireCellSelectionEvent(lastSelectedCell.columnPosition, lastSelectedCell.rowPosition, false);
	}

	// ILayer methods

	public int getRowPositionByIndex(int rowIndex) {
		return underlyingLayer.getRowPositionByIndex(rowIndex);
	}

	@Override
	public String getDisplayModeByPosition(int columnPosition, int rowPosition) {
		if (isCellPositionSelected(columnPosition, rowPosition)) {
			return DisplayMode.SELECT;
		} else {
			return super.getDisplayModeByPosition(columnPosition, rowPosition);
		}
	}

	@Override
	public LabelStack getConfigLabelsByPosition(int columnPosition, int rowPosition) {
		LabelStack labelStack = super.getConfigLabelsByPosition(columnPosition, rowPosition);

		if (selectionAnchor.columnPosition == columnPosition && selectionAnchor.rowPosition == rowPosition) {
			labelStack.addLabel(SelectionStyleLabels.SELECTION_ANCHOR_STYLE);
		}

		return labelStack;
	}

	// Command handling

	private void registerCommandHandlers() {
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
		} else if (command instanceof MultiColumnHideCommand && command.convertToTargetLayer(this)) {
			return hideMultipleColumnPositions((MultiColumnHideCommand)command);
		} else if (command instanceof ColumnHideCommand && command.convertToTargetLayer(this)) {
			return hideColumnPosition((ColumnHideCommand)command);
		} else if (command instanceof ColumnResizeCommand && command.convertToTargetLayer(this)) {
			return handleColumnResizeCommand((ColumnResizeCommand) command);
		} else if (command instanceof RowResizeCommand && command.convertToTargetLayer(this)) {
			return handleRowResizeCommand((RowResizeCommand) command);
		}
		return super.doCommand(command);
	}

	/**
	 * Any selected columns will be hidden. A column is considered selected even if a cell is selected.
	 */
	protected boolean hideMultipleColumnPositions(MultiColumnHideCommand command) {
		for (int columnPosition : command.getColumnPositions()) {
			if (isColumnFullySelected(columnPosition)) {
				Rectangle selection = new Rectangle(columnPosition, 0, 1, getRowCount());
				clearSelection(selection);
			}
		}
		return super.doCommand(command);
	}

	protected boolean hideColumnPosition(ColumnHideCommand command) {
		if (isColumnFullySelected(command.getColumnPosition())) {
			return super.doCommand(new MultiColumnHideCommand(this, getFullySelectedColumnPositions()));
		} else {
			return super.doCommand(command);
		}
	}

	/**
	 * This method will check to see if the column to resize is part of the selection model, if it is, it will create a
	 * new MultiResizeColumnCommand and pass it.
	 * @param command
	 */
	protected boolean handleColumnResizeCommand(ColumnResizeCommand command) {
		if (isColumnFullySelected(command.getColumnPosition())) {
			return super.doCommand(new MultiColumnResizeCommand(this, selectionModel.getFullySelectedColumns(getRowCount()), command.getNewColumnWidth()));
		} else {
			return super.doCommand(command);
		}
	}

	protected boolean handleRowResizeCommand(RowResizeCommand command) {
		if (isRowFullySelected(command.getRowPosition())) {
			return super.doCommand(new MultiRowResizeCommand(this, selectionModel.getFullySelectedRows(getColumnCount()), command.getNewHeight()));
		} else {
			return super.doCommand(command);
		}
	}

	public void fireCellSelectionEvent(int columnPosition, int rowPosition,
			boolean forcingEntireCellIntoViewport) {
		fireLayerEvent(new CellSelectionEvent(this, columnPosition, rowPosition,
				forcingEntireCellIntoViewport));
	}
	
}