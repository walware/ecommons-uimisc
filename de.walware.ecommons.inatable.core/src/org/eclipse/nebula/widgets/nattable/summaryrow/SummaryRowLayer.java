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
// -depend
package org.eclipse.nebula.widgets.nattable.summaryrow;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;
import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCellDim;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.IVisualChangeEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.RowUpdateEvent;
import org.eclipse.nebula.widgets.nattable.resize.command.RowResizeCommand;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.summaryrow.command.CalculateSummaryRowValuesCommand;
import org.eclipse.nebula.widgets.nattable.util.ArrayUtil;


/**
 * Adds a summary row at the end. Uses {@link ISummaryProvider} to calculate the summaries for all columns.
 * <p>This layer also adds the following labels:
 * <ol>
 *  <li>{@link SummaryRowLayer#DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX} + column index</li>
 * 	<li>{@link SummaryRowLayer#DEFAULT_SUMMARY_ROW_CONFIG_LABEL} to all cells in the row</li>
 * </ol>
 *
 * Example: column with index 1 will have the DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX + 1 label applied.
 * Styling and {@link ISummaryProvider} can be hooked up to these labels.
 *
 * @see DefaultSummaryRowConfiguration
 */
public class SummaryRowLayer extends AbstractLayerTransform {

	/**
	 * Label that gets attached to the LabelStack for every cell in the summary row.
	 */
	public static final String DEFAULT_SUMMARY_ROW_CONFIG_LABEL = "SummaryRow"; //$NON-NLS-1$
	/**
	 * Prefix of the labels that get attached to cells in the summary row. 
	 * The complete label will consist of this prefix and the column index at the end of
	 * the label. This way every cell in the summary row can be accessed directly via
	 * label mechanism.
	 */
	public static final String DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX = "SummaryColumn_"; //$NON-NLS-1$

	private final IConfigRegistry configRegistry;
	private int summaryRowHeight = DataLayer.DEFAULT_ROW_HEIGHT;

	/** 
	 * Cache that contains the calculated summary value.
	 * Introduced for performance reasons since the calculation could be CPU intensive. 
	 */
	protected Map<Long, Object> summaryCache = new HashMap<Long, Object>();
	/** 
	 * Use a cache-copy which does not get cleared, as using an Entry type object with stale flag per 
	 * instance would require traversal of full set of entries in <code>clearSummaryCache()</code>
	 */
	protected Map<Long, Object> summaryCacheIncludingStaleValues = new HashMap<Long, Object>();
	
	/**
	 * Creates a SummaryRowLayer on top of the given underlying layer.
	 * <p>
	 * Note: This constructor will create the SummaryRowLayer by using the default configuration.
	 * 		 The default configuration doesn't fit the needs so you usually will use your custom
	 * 		 summary row configuration.
	 * 
	 * @param underlyingDataLayer The underlying layer on which the SummaryRowLayer should be build.
	 * @param configRegistry The ConfigRegistry for retrieving the ISummaryProvider per column.
	 * 
	 * @see DefaultSummaryRowConfiguration
	 */
	public SummaryRowLayer(IUniqueIndexLayer underlyingDataLayer, IConfigRegistry configRegistry) {
		this(underlyingDataLayer, configRegistry, true);
	}

	/**
	 * Creates a SummaryRowLayer on top of the given underlying layer.
	 * <p>
	 * Note: This constructor will create the SummaryRowLayer by using the default configuration
	 * 		 if the autoConfig parameter is set to <code>true</code>.
	 * 		 The default configuration doesn't fit the needs so you usually will use your custom
	 * 		 summary row configuration. When using a custom configuration you should use this
	 * 		 constructor setting autoConfig to <code>false</code>. Otherwise you might get strange
	 * 		 behaviour as the default configuration will be set additionally to your configuration.
	 * 
	 * @param underlyingDataLayer The underlying layer on which the SummaryRowLayer should be build.
	 * @param configRegistry The ConfigRegistry for retrieving the ISummaryProvider per column.
	 * @param autoConfigure <code>true</code> to use the DefaultSummaryRowConfiguration,
	 * 			<code>false</code> if a custom configuration will be set after the creation.
	 * 
	 * @see DefaultSummaryRowConfiguration
	 */
	public SummaryRowLayer(IUniqueIndexLayer underlyingDataLayer, IConfigRegistry configRegistry, boolean autoConfigure) {
		super(underlyingDataLayer);
		this.configRegistry = configRegistry;
		if(autoConfigure){
			addConfiguration(new DefaultSummaryRowConfiguration());
		}
	}

	/**
	 * Calculates the summary for the column using the {@link ISummaryProvider} from the {@link IConfigRegistry}.
	 * In order to prevent the table from freezing (for large data sets), the summary is calculated in a separate 
	 * Thread. While summary is being calculated {@link ISummaryProvider#DEFAULT_SUMMARY_VALUE} is returned.
	 * <p>
	 * NOTE: Since this is a {@link IUniqueIndexLayer} sitting close to the {@link DataLayer}, 
	 * 		 columnPosition == columnIndex
	 */
	@Override
	public Object getDataValueByPosition(final long columnPosition, final long rowPosition) {
		if (isSummaryRowPosition(rowPosition)) {
			final Object potentiallyStaleSummaryValue = getPotentiallyStaleSummaryFromCache(columnPosition);
			if (potentiallyStaleSummaryValue == null || !hasNonStaleSummaryFor(columnPosition)) {
				calculateNewSummaryValue(potentiallyStaleSummaryValue, columnPosition, true);
			}
			
			if (potentiallyStaleSummaryValue != null) {
				return potentiallyStaleSummaryValue;
			}
			
			return null;
		}
		return super.getDataValueByPosition(columnPosition, rowPosition);
	}

	private void calculateNewSummaryValue(
			final Object potentiallyStaleSummaryValue, 
			final long columnPosition,
			boolean calculateInBackground) {
		// Get the summary provider from the configuration registry
		LabelStack labelStack = getConfigLabelsByPosition(columnPosition, getSummaryRowPosition());
		String[] configLabels = labelStack.getLabels().toArray(ArrayUtil.STRING_TYPE_ARRAY);
		
		final ISummaryProvider summaryProvider = configRegistry.getConfigAttribute(
				SummaryRowConfigAttributes.SUMMARY_PROVIDER, DisplayMode.NORMAL, configLabels);
		
		// If there is no Summary provider - skip processing
		if(summaryProvider == ISummaryProvider.NONE){
			return;
		}

		if (calculateInBackground) {
			// Start thread to calculate summary
			new Thread() {
				@Override
				public void run() {
					Object summaryValue = calculateColumnSummary(columnPosition, summaryProvider);
					addToCache(columnPosition, summaryValue);
					if (!((potentiallyStaleSummaryValue != null) ? potentiallyStaleSummaryValue.equals(summaryValue) : null == summaryValue)) {
						fireLayerEvent(new RowUpdateEvent(SummaryRowLayer.this, getSummaryRowPosition()));
					}
				}
			}.start();
		}
		else {
			//calculate in same thread to make printing and exporting work
			//Note: this could cause a performance leak and should be used carefully
			Object summaryValue = calculateColumnSummary(columnPosition, summaryProvider);
			addToCache(columnPosition, summaryValue);
		}
	}

	private Object calculateColumnSummary(long columnIndex, ISummaryProvider summaryProvider) {
		Object summaryValue = null;
		if (summaryProvider != null) {
			summaryValue = summaryProvider.summarize(columnIndex);
		}
		return summaryValue;
	}
	
	public Object getSummaryFromCache(Long columnIndex) {
		return summaryCache.get(columnIndex);
	}

	public Object getPotentiallyStaleSummaryFromCache(Long columnIndex) {
		return summaryCacheIncludingStaleValues.get(columnIndex);
	}
	
	public boolean hasNonStaleSummaryFor(Long columnIndex) {
		return summaryCache.containsKey(columnIndex);
	}
	
	protected boolean addToCache(Long columnIndex, Object summaryValue) {
		Object oldSummaryValue = summaryCache.put(columnIndex, summaryValue);
		summaryCacheIncludingStaleValues.put(columnIndex,summaryValue);
		return (!((oldSummaryValue != null) ? oldSummaryValue.equals(summaryValue) : null == summaryValue));
	}

	protected void clearSummaryCache() {
		summaryCache.clear();
	}

	private boolean isSummaryRowPosition(long rowPosition) {
		return rowPosition == getSummaryRowPosition();
	}

	/**
	 * @return The position of the summary row. 
	 * 			In most cases <code>rowCount - 1</code>. 
	 */
	private long getSummaryRowPosition() {
		return getRowCount() - 1;
	}

	@Override
	public boolean doCommand(ILayerCommand command) {
		if (command instanceof RowResizeCommand) {
			RowResizeCommand rowResizeCommand = (RowResizeCommand) command;
			if (isSummaryRowPosition(rowResizeCommand.getRowPosition())) {
				summaryRowHeight = rowResizeCommand.getNewHeight();
				return true;
			}
		}
		else if (command instanceof CalculateSummaryRowValuesCommand) {
			for (int i = 0; i < getColumnCount(); i++) {
				calculateNewSummaryValue(null, i, false);
			}
			return true;
		}
		return super.doCommand(command);
	}

	@Override
	public void handleLayerEvent(ILayerEvent event) {
		if (event instanceof IVisualChangeEvent) {
			clearSummaryCache();
		}
		super.handleLayerEvent(event);
	}

	@Override
	public LabelStack getConfigLabelsByPosition(long columnPosition, long rowPosition) {
		if (isSummaryRowPosition(rowPosition)) {
			//create a new LabelStack that takes the config labels into account
			LabelStack labelStack = new LabelStack();
			if (getConfigLabelAccumulator() != null) {
				getConfigLabelAccumulator().accumulateConfigLabels(labelStack, columnPosition, rowPosition);
			}
			labelStack.addLabelOnTop(DEFAULT_SUMMARY_ROW_CONFIG_LABEL);
			labelStack.addLabelOnTop(DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX + columnPosition);
			return labelStack;
		}
		return super.getConfigLabelsByPosition(columnPosition, rowPosition);
	}

	@Override
	public ILayerCell getCellByPosition(long columnPosition, long rowPosition) {
		if (isSummaryRowPosition(rowPosition)) {
			return new LayerCell(this,
					new LayerCellDim(HORIZONTAL, getColumnIndexByPosition(columnPosition),
							columnPosition ),
					new LayerCellDim(VERTICAL, getRowIndexByPosition(rowPosition),
							rowPosition ) );
		}
		return super.getCellByPosition(columnPosition, rowPosition);
	}

	@Override
	public long getHeight() {
		return super.getHeight() + getRowHeightByPosition(getSummaryRowPosition());
	}
	
	@Override
	public long getPreferredHeight() {
		return super.getPreferredHeight() + getRowHeightByPosition(getSummaryRowPosition());
	}
	
	@Override
	public long getRowCount() {
		return super.getRowCount() + 1;
	}

	@Override
	public long getPreferredRowCount() {
		return getRowCount();
	}

	@Override
	public long getRowIndexByPosition(long rowPosition) {
		if (isSummaryRowPosition(rowPosition)) {
			return rowPosition;
		}
		return super.getRowIndexByPosition(rowPosition);
	}

	@Override
	public long getRowPositionByY(long y) {
		return LayerUtil.getRowPositionByY(this, y);
	}

	@Override
	public int getRowHeightByPosition(long rowPosition) {
		if (isSummaryRowPosition(rowPosition)) {
			return summaryRowHeight;
		}
		return super.getRowHeightByPosition(rowPosition);
	}

}
