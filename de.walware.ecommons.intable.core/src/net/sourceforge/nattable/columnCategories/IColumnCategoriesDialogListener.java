package net.sourceforge.nattable.columnCategories;

import java.util.List;

import net.sourceforge.nattable.coordinate.IRelative.Direction;


public interface IColumnCategoriesDialogListener {

	void itemsSelected(List<Integer> addedColumnIndexes);

	void itemsRemoved(List<Integer> removedColumnPositions);

	void itemsMoved(Direction direction, List<Integer> selectedPositions);

}
