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
// ~
package de.walware.ecommons.waltable.sort;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;

import de.walware.ecommons.waltable.layer.ForwardLayer;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.layer.cell.ForwardLayerCell;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.layer.cell.ILayerCellDim;
import de.walware.ecommons.waltable.persistence.IPersistable;
import de.walware.ecommons.waltable.sort.config.DefaultSortConfiguration;


/**
 * Enables sorting of the data. Uses an {@link ISortModel} to do/track the sorting.
 * @param <T> Type of the Beans in the backing data source.
 *
 * @see DefaultSortConfiguration
 * @see SortStatePersistor
 */
public class SortHeaderLayer<T> extends ForwardLayer implements IPersistable {
	
	
	/** Handles the actual sorting of underlying data */
	private final ISortModel sortModel;
	
	
	public SortHeaderLayer(final ILayer underlyingLayer, final ISortModel sortModel) {
		this(underlyingLayer, sortModel, true);
	}
	
	public SortHeaderLayer(final ILayer underlyingLayer, final ISortModel sortModel, final boolean useDefaultConfiguration) {
		super(underlyingLayer);
		this.sortModel= sortModel;
		
		registerPersistable(new SortStatePersistor<T>(sortModel));
		
		if (useDefaultConfiguration) {
			addConfiguration(new DefaultSortConfiguration());
		}
	}
	
	
	/**
	 * @return The ISortModel that is used to handle the sorting of the underlying data.
	 */
	public ISortModel getSortModel() {
		return this.sortModel;
	}
	
	
	@Override
	protected ILayerCell createCell(final ILayerCellDim hDim, final ILayerCellDim vDim,
			final ILayerCell underlyingCell) {
		return new ForwardLayerCell(this, hDim, vDim, underlyingCell) {
			
			@Override
			public LabelStack getConfigLabels() {
				final LabelStack configLabels= super.getConfigLabels();
				
				final long id= getDim(HORIZONTAL).getId();
				if (SortHeaderLayer.this.sortModel.isSorted(id)) {
					final String sortConfig= DefaultSortConfiguration.SORT_SEQ_CONFIG_TYPE + SortHeaderLayer.this.sortModel.getSortOrder(id);
					configLabels.addLabelOnTop(sortConfig);
					
					final SortDirection sortDirection= SortHeaderLayer.this.sortModel.getSortDirection(id);
					switch (sortDirection) {
					case ASC:
						configLabels.addLabelOnTop(DefaultSortConfiguration.SORT_UP_CONFIG_TYPE);
						break;
					case DESC:
						configLabels.addLabelOnTop(DefaultSortConfiguration.SORT_DOWN_CONFIG_TYPE);
						break;
					}
				}
				
				return configLabels;
			}
			
		};
	}
	
}
