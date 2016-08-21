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
// -depend
package de.walware.ecommons.waltable.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.walware.ecommons.waltable.internal.WaLTablePlugin;
import de.walware.ecommons.waltable.persistence.IPersistable;


/**
 * Handles persisting of the sorting state.
 * The sorting state is read from and restored to the {@link ISortModel}.
 *
 * @param <T> Type of the Beans in the backing data source.
 */
public class SortStatePersistor<T> implements IPersistable {
	
	
	public static final String PERSISTENCE_KEY_SORTING_STATE= ".SortHeaderLayer.sortingState"; //$NON-NLS-1$
	private final ISortModel sortModel;

	public SortStatePersistor(final ISortModel sortModel) {
		this.sortModel= sortModel;
	}

	/**
	 * Save the sorting state in the properties file.
	 * <p>
	 * Key:
	 * 	{@link #PERSISTENCE_KEY_SORTING_STATE}
	 * <p>
	 * Format:
	 * 	column index : sort direction : sort order |
	 */
	@Override
	public void saveState(final String prefix, final Properties properties) {
		final StringBuilder buffer= new StringBuilder();
		
		for (final long id : this.sortModel.getSortedColumnIds()) {
			final SortDirection sortDirection= this.sortModel.getSortDirection(id);
			final long sortOrder= this.sortModel.getSortOrder(id);
			
			buffer.append(id);
			buffer.append(":"); //$NON-NLS-1$
			buffer.append(sortDirection.toString());
			buffer.append(":"); //$NON-NLS-1$
			buffer.append(sortOrder);
			buffer.append("|"); //$NON-NLS-1$
		}
		
		if (buffer.length() > 0) {
			properties.put(prefix + PERSISTENCE_KEY_SORTING_STATE, buffer.toString());
		}
	}

	/**
	 * Parses the saved string and restores the state to the {@link ISortModel}.
	 */
	@Override
	public void loadState(final String prefix, final Properties properties) {
		
		/*
		 * restoring the sortState starts with a clean sortModel. This step
		 * is necessary because there could be calls to the sortModel before
		 * which leads to an undefined state afterwards ...
		 */
		this.sortModel.clear();
		
		final Object savedValue= properties.get(prefix + PERSISTENCE_KEY_SORTING_STATE);
		if(savedValue == null){
			return;
		}
		
		try{
			final String savedState= savedValue.toString();
			final String[] sortedColumns= savedState.split("\\|"); //$NON-NLS-1$
			final List<SortState> stateInfo= new ArrayList<>();

			// Parse string
			for (final String token : sortedColumns) {
				stateInfo.add(getSortStateFromString(token));
			}

			// Restore to the model
			Collections.sort(stateInfo, new SortStateComparator());
			for (final SortState state : stateInfo) {
				this.sortModel.sort(state.columnIndex, state.sortDirection, true);
			}
		}
		catch(final Exception ex){
			this.sortModel.clear();
			WaLTablePlugin.log(new Status(IStatus.ERROR, WaLTablePlugin.PLUGIN_ID,
					"Error while restoring sorting state: " + ex.getLocalizedMessage(), ex )); //$NON-NLS-1$
		}
	}

	/**
	 * Parse the string representation to extract the
	 * column index, sort direction and sort order
	 */
	protected SortState getSortStateFromString(final String token) {
		final String[] split= token.split(":"); //$NON-NLS-1$
		final long columnIndex= Long.parseLong(split[0]);
		final SortDirection sortDirection= SortDirection.valueOf(split[1]);
		final int sortOrder= Integer.parseInt(split[2]);

		return new SortState(columnIndex, sortDirection, sortOrder);
	}

	/**
	 * Encapsulation of the sort state of a column
	 */
	protected class SortState {
		public long columnIndex;
		public SortDirection sortDirection;
		public int sortOrder;

		public SortState(final long columnIndex, final SortDirection sortDirection, final int sortOrder) {
			this.columnIndex= columnIndex;
			this.sortDirection= sortDirection;
			this.sortOrder= sortOrder;
		}
	}

	/**
	 * Helper class to order sorting state by the 'sort order'.
	 * The sorting state has be restored in the same sequence
	 * in which the original sort was applied.
	 */
	private class SortStateComparator implements Comparator<SortState> {

		@Override
		public int compare(final SortState state1, final SortState state2) {
			return Long.valueOf(state1.sortOrder).compareTo(Long.valueOf(state2.sortOrder));
		}

	}
	
}
