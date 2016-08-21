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
package de.walware.ecommons.waltable.layer.cell;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.edit.editor.ICellEditor;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.painter.cell.ICellPainter;
import de.walware.ecommons.waltable.persistence.IPersistable;
import de.walware.ecommons.waltable.style.IStyle;


/**
 * Registers/Adds configuration labels for a given column (by index).
 * Custom {@link ICellEditor}, {@link ICellPainter}, {@link IStyle} can then 
 * be registered in the {@link IConfigRegistry} against these labels.
 * 
 * Also @see {@link RowOverrideLabelAccumulator} 
 */
public class ColumnOverrideLabelAccumulator extends AbstractOverrider implements IPersistable {
	
	
	public static final String PERSISTENCE_KEY= ".columnOverrideLabelAccumulator"; //$NON-NLS-1$
	
	
	private final ILayer layer;
	
	
	public ColumnOverrideLabelAccumulator(final ILayer layer) {
		this.layer= layer;
	}
	
	
	@Override
	public void accumulateConfigLabels(final LabelStack configLabels, final long columnPosition, final long rowPosition) {
		final long columnIndex= this.layer.getDim(HORIZONTAL)
				.getPositionId(columnPosition, columnPosition);
		final List<String> overrides= getOverrides(Long.valueOf(columnIndex));
		if (overrides != null) {
			for (final String configLabel : overrides) {
				configLabels.addLabel(configLabel);
			}
		}
	}

	/**
	 * Register labels to be contributed a column. This label will be applied to
	 * all cells in the column.
	 */
	public void registerColumnOverrides(final long columnIndex, final String... configLabels) {
		super.registerOverrides(Long.valueOf(columnIndex), configLabels);
	}
	
	/**
	 * Register labels to be contributed a column. This label will be applied to
	 * all cells in the column.
	 */
	public void registerColumnOverridesOnTop(final long columnIndex, final String... configLabels) {
		super.registerOverridesOnTop(Long.valueOf(columnIndex), configLabels);
	}
	
	/** 
	 * Save the overrides to a properties file. A line is stored for every column.
	 * 
	 * Example for column 0:
	 * prefix.columnOverrideLabelAccumulator.0= LABEL1,LABEL2
	 */
	@Override
	public void saveState(final String prefix, final Properties properties) {
		final Map<Serializable, List<String>> overrides= getOverrides();

		for (final Map.Entry<Serializable, List<String>> entry : overrides.entrySet()) {
			final StringBuilder strBuilder= new StringBuilder();
			for (final String columnLabel : entry.getValue()) {
				strBuilder.append(columnLabel);
				strBuilder.append(VALUE_SEPARATOR);
			}
			//Strip the last comma
			String propertyValue= strBuilder.toString();
			if(propertyValue.endsWith(VALUE_SEPARATOR)){
				propertyValue= propertyValue.substring(0, propertyValue.length() - 1);
			}
			final String propertyKey= prefix + PERSISTENCE_KEY + DOT + entry.getKey();
			properties.setProperty(propertyKey, propertyValue);
		}
	}

	/**
	 * Load the overrides state from the given properties file.
	 * @see #saveState(String, Properties)
	 */
	@Override
	public void loadState(final String prefix, final Properties properties) {
		final Set<Object> keySet= properties.keySet();
		for (final Object key : keySet) {
			final String keyString= (String) key;
			if(keyString.contains(PERSISTENCE_KEY)){
				final String labelsFromPropertyValue= properties.getProperty(keyString).trim();
				final String columnIndexFromKey= keyString.substring(keyString.lastIndexOf(DOT) + 1);
				registerColumnOverrides(Long.parseLong(columnIndexFromKey), labelsFromPropertyValue.split(VALUE_SEPARATOR));
			}
		}
	}	
}
