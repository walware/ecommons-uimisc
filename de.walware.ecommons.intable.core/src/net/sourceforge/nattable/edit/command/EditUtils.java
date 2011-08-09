package net.sourceforge.nattable.edit.command;

import java.util.HashSet;
import java.util.List;

import net.sourceforge.nattable.config.CellConfigAttributes;
import net.sourceforge.nattable.config.IConfigRegistry;
import net.sourceforge.nattable.config.IEditableRule;
import net.sourceforge.nattable.coordinate.PositionCoordinate;
import net.sourceforge.nattable.data.convert.IDisplayConverter;
import net.sourceforge.nattable.edit.EditConfigAttributes;
import net.sourceforge.nattable.edit.editor.ICellEditor;
import net.sourceforge.nattable.layer.LabelStack;
import net.sourceforge.nattable.layer.cell.LayerCell;
import net.sourceforge.nattable.selection.SelectionLayer;
import net.sourceforge.nattable.style.DisplayMode;

public class EditUtils {

	public static LayerCell getLastSelectedCell(SelectionLayer selectionLayer) {
		PositionCoordinate selectionAnchor = selectionLayer.getSelectionAnchor();
		return selectionLayer.getCellByPosition(selectionAnchor.columnPosition, selectionAnchor.rowPosition);
	}

	public static ICellEditor lastSelectedCellEditor(SelectionLayer selectionLayer, IConfigRegistry configRegistry) {
		final List<String> lastSelectedCellLabelsArray = EditUtils.getLastSelectedCell(selectionLayer).getConfigLabels().getLabels();
		return configRegistry.getConfigAttribute(EditConfigAttributes.CELL_EDITOR, DisplayMode.EDIT, lastSelectedCellLabelsArray);
	}

	public static boolean allCellsEditable(SelectionLayer selectionLayer, IConfigRegistry configRegistry) {
		PositionCoordinate[] selectedCells = selectionLayer.getSelectedCells();
		for (PositionCoordinate cell : selectedCells) {
			LabelStack labelStack = selectionLayer.getConfigLabelsByPosition(cell.columnPosition, cell.rowPosition);
			IEditableRule editableRule = configRegistry.getConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, DisplayMode.EDIT, labelStack.getLabels());
			int columnIndex = selectionLayer.getColumnIndexByPosition(cell.columnPosition);
			int rowIndex = selectionLayer.getRowIndexByPosition(cell.rowPosition);
			
			if (!editableRule.isEditable(columnIndex, rowIndex)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isCellEditable(SelectionLayer selectionLayer, IConfigRegistry configRegistry, PositionCoordinate cell){
		LabelStack labelStack = selectionLayer.getConfigLabelsByPosition(cell.columnPosition, cell.rowPosition);
		int columnIndex = selectionLayer.getColumnIndexByPosition(cell.columnPosition);
		int rowIndex = selectionLayer.getRowIndexByPosition(cell.rowPosition);
		
		IEditableRule editableRule = configRegistry.getConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, DisplayMode.EDIT, labelStack.getLabels());
		if (editableRule == null)
			return false;
		
		if (!editableRule.isEditable(columnIndex, rowIndex)) {
			return false;
		}
		return true;
	}

	public static boolean isEditorSame(SelectionLayer selectionLayer, IConfigRegistry configRegistry, ICellEditor lastSelectedCellEditor) {
		PositionCoordinate[] selectedCells = selectionLayer.getSelectedCells();

		boolean isAllSelectedCellsHaveSameEditor = true;
		for (PositionCoordinate selectedCell : selectedCells) {
			LabelStack labelStack = selectionLayer.getConfigLabelsByPosition(selectedCell.columnPosition, selectedCell.rowPosition);
			ICellEditor cellEditor = configRegistry.getConfigAttribute(EditConfigAttributes.CELL_EDITOR, DisplayMode.EDIT, labelStack.getLabels());
			if (cellEditor != lastSelectedCellEditor) {
				isAllSelectedCellsHaveSameEditor = false;
			}
		}
		return isAllSelectedCellsHaveSameEditor;
	}
	
	public static boolean isConverterSame(SelectionLayer selectionLayer, IConfigRegistry configRegistry, ICellEditor lastSelectedCellEditor){
		PositionCoordinate[] selectedCells = selectionLayer.getSelectedCells();
		HashSet<Class> converterSet = new HashSet<Class>();
		
		for (PositionCoordinate selectedCell : selectedCells) {
			LabelStack labelStack = selectionLayer.getConfigLabelsByPosition(selectedCell.columnPosition, selectedCell.rowPosition);
			IDisplayConverter dataTypeConverter = configRegistry.getConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, DisplayMode.EDIT, labelStack.getLabels());
			converterSet.add(dataTypeConverter.getClass());
			if (converterSet.size() > 1)
				return false;	
		}
		return true;
	}
	
}
