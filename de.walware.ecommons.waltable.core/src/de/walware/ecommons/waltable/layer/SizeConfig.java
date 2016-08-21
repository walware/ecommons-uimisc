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
package de.walware.ecommons.waltable.layer;

import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;

import de.walware.ecommons.waltable.coordinate.PositionOutOfBoundsException;
import de.walware.ecommons.waltable.persistence.IPersistable;


/**
 * This class stores the size configuration of rows/columns within the NatTable.
 * 
 * Mixed mode (fixed/percentage sizing):<br>
 * The mixed mode is only working if percentage sizing is enabled globally, and 
 * the fixed sized positions are marked separately.
 */
public class SizeConfig implements IPersistable {
	
	
	public static final String PERSISTENCE_KEY_DEFAULT_SIZE= ".defaultSize"; //$NON-NLS-1$
	public static final String PERSISTENCE_KEY_DEFAULT_SIZES= ".defaultSizes"; //$NON-NLS-1$
	public static final String PERSISTENCE_KEY_SIZES= ".sizes"; //$NON-NLS-1$
	public static final String PERSISTENCE_KEY_RESIZABLE_BY_DEFAULT= ".resizableByDefault"; //$NON-NLS-1$
	public static final String PERSISTENCE_KEY_RESIZABLE_INDEXES= ".resizableIndexes"; //$NON-NLS-1$
	
	
	/**
	 * The global default size of this {@link SizeConfig}.
	 */
	private int defaultSize;
	
	/**
	 * Map that contains default sizes per column.
	 */
	private final Map<Long, Integer> defaultSizeMap= new TreeMap<>();
	
	/**
	 * Map that contains sizes per column.
	 */
	private final Map<Long, Integer> sizeMap= new TreeMap<>();
	
	/**
	 * Map that contains the resizable information per row/column.
	 */
	private final Map<Long, Boolean> resizablesMap= new TreeMap<>();
	
	/**
	 * The global resizable information of this {@link SizeConfig}.
	 */
	private boolean resizableByDefault= true;
	
	
	/**
	 * Create a new {@link SizeConfig} with the given default size.
	 * @param defaultSize The default size to use.
	 */
	public SizeConfig(final int defaultSize) {
		this.defaultSize= defaultSize;
	}
	
	// Persistence
	
	@Override
	public void saveState(final String prefix, final Properties properties) {
		properties.put(prefix + PERSISTENCE_KEY_DEFAULT_SIZE, String.valueOf(this.defaultSize));
		saveMap(this.defaultSizeMap, prefix + PERSISTENCE_KEY_DEFAULT_SIZES, properties);
		saveMap(this.sizeMap, prefix + PERSISTENCE_KEY_SIZES, properties);
		properties.put(prefix + PERSISTENCE_KEY_RESIZABLE_BY_DEFAULT, String.valueOf(this.resizableByDefault));
		saveMap(this.resizablesMap, prefix + PERSISTENCE_KEY_RESIZABLE_INDEXES, properties);
	}
	
	private void saveMap(final Map<Long, ?> map, final String key, final Properties properties) {
		if (map.size() > 0) {
			final StringBuilder strBuilder= new StringBuilder();
			for (final Long index : map.keySet()) {
				strBuilder.append(index);
				strBuilder.append(':');
				strBuilder.append(map.get(index));
				strBuilder.append(',');
			}
			properties.setProperty(key, strBuilder.toString());
		}
	}
	
	@Override
	public void loadState(final String prefix, final Properties properties) {
		//ensure to cleanup the current states prior loading new ones
		this.defaultSizeMap.clear();
		this.sizeMap.clear();
		this.resizablesMap.clear();
		
		final String persistedDefaultSize= properties.getProperty(prefix + PERSISTENCE_KEY_DEFAULT_SIZE);
		if (persistedDefaultSize != null && !persistedDefaultSize.isEmpty()) {
			this.defaultSize= Integer.valueOf(persistedDefaultSize).intValue();
		}
		
		final String persistedResizableDefault= properties.getProperty(prefix + PERSISTENCE_KEY_RESIZABLE_BY_DEFAULT);
		if (persistedResizableDefault != null && !persistedResizableDefault.isEmpty()) {
			this.resizableByDefault= Boolean.valueOf(persistedResizableDefault).booleanValue();
		}
		
		loadBooleanMap(prefix + PERSISTENCE_KEY_RESIZABLE_INDEXES, properties, this.resizablesMap);
		loadIntegerMap(prefix + PERSISTENCE_KEY_DEFAULT_SIZES, properties, this.defaultSizeMap);
		loadIntegerMap(prefix + PERSISTENCE_KEY_SIZES, properties, this.sizeMap);
	}
	
	private void loadIntegerMap(final String key, final Properties properties, final Map<Long, Integer> map) {
		final String property= properties.getProperty(key);
		if (property != null) {
			map.clear();
			
			final StringTokenizer tok= new StringTokenizer(property, ","); //$NON-NLS-1$
			while (tok.hasMoreTokens()) {
				final String token= tok.nextToken();
				final int separatorIndex= token.indexOf(':');
				map.put(Long.valueOf(token.substring(0, separatorIndex)), Integer.valueOf(token.substring(separatorIndex + 1)));
			}
		}
	}
	
	private void loadBooleanMap(final String key, final Properties properties, final Map<Long, Boolean> map) {
		final String property= properties.getProperty(key);
		if (property != null) {
			final StringTokenizer tok= new StringTokenizer(property, ","); //$NON-NLS-1$
			while (tok.hasMoreTokens()) {
				final String token= tok.nextToken();
				final int separatorIndex= token.indexOf(':');
				map.put(Long.valueOf(token.substring(0, separatorIndex)), Boolean.valueOf(token.substring(separatorIndex + 1)));
			}
		}
	}
	
	// Default size
	
	public void setDefaultSize(final int size) {
		if (size < 0) {
			throw new IllegalArgumentException("size < 0"); //$NON-NLS-1$
		}
		this.defaultSize= size;
	}
	
	public void setDefaultSize(final long position, final int size) {
		if (this.defaultSize < 0) {
			throw new IllegalArgumentException("size < 0"); //$NON-NLS-1$
		}
		this.defaultSizeMap.put(Long.valueOf(position), Integer.valueOf(size));
	}
	
	private int getDefaultSize(final long position) {
		final Integer size= this.defaultSizeMap.get(Long.valueOf(position));
		if (size != null) {
			return size.intValue();
		} else {
			return this.defaultSize;
		}
	}
	
	// Size
	
	public long getAggregateSize(final long position) {
		if (position < 0) {
			throw PositionOutOfBoundsException.position(position);
		}
		else if (position == 0) {
			return 0;
		}
		else if (isAllPositionsSameSize()) {
			//if percentage sizing is used, the sizes in defaultSize are used as percentage values
			//and not as pixel values, therefore another value needs to be considered
			return position * this.defaultSize;
		} else {
			long resizeAggregate= 0;
			long resizedColumns= 0;
			
			final Map<Long, Integer> mapToUse= this.sizeMap;
			
			for (final Long resizedPosition : mapToUse.keySet()) {
				if (resizedPosition.longValue() < position) {
					resizedColumns++;
					resizeAggregate+= mapToUse.get(resizedPosition);
				} else {
					break;
				}
			}
			
			return (position * this.defaultSize) + (resizeAggregate - (resizedColumns * this.defaultSize));
		}
	}
	
	public int getSize(final long position) {
		final Integer size= this.sizeMap.get(Long.valueOf(position));
		if (size != null) {
			return size.intValue();
		}
		else {
			return getDefaultSize(position);
		}
	}
	
	/**
	 * Sets the given size for the given position. This method can be called manually for configuration
	 * via {@link DataLayer} and will be called on resizing within the rendered UI. This is why there
	 * is a check for percentage configuration. If this {@link SizeConfig} is configured to not use
	 * percentage sizing, the size is taken as is. If percentage sizing is enabled, the given size
	 * will be calculated to percentage value based on the already known pixel values.
	 * <p>
	 * If you want to use percentage sizing you should use {@link SizeConfig#setPercentage(int, int)}
	 * for manual size configuration to avoid unnecessary calculations.
	 * 
	 * @param position The position for which the size should be set.
	 * @param size The size in pixels to set for the given position.
	 */
	public void setSize(final long position, final int size) {
		if (size < 0) {
			throw new IllegalArgumentException("size < 0"); //$NON-NLS-1$
		}
		if (isPositionResizable(position)) {
			//check whether the given value should be remembered as is or if it needs to be calculated
			this.sizeMap.put(Long.valueOf(position), Integer.valueOf(size));
		}
	}
	
	// Resizable
	
	/**
	 * @return The global resizable information of this {@link SizeConfig}.
	 */
	public boolean isResizableByDefault() {
		return this.resizableByDefault;
	}
	
	/**
	 * Checks if there is a special resizable configuration for the given position. If not the
	 * global resizable information is returned.
	 * @param position The position of the row/column for which the resizable information is requested.
	 * @return <code>true</code> if the given row/column position is resizable,
	 * 			<code>false</code> if not.
	 */
	public boolean isPositionResizable(final long position) {
		final Boolean resizable= this.resizablesMap.get(Long.valueOf(position));
		if (resizable != null) {
			return resizable.booleanValue();
		}
		return this.resizableByDefault;
	}
	
	/**
	 * Sets the resizable configuration for the given row/column position.
	 * @param position The position of the row/column for which the resizable configuration should be set.
	 * @param resizable <code>true</code> if the given row/column position should be resizable,
	 * 			<code>false</code> if not.
	 */
	public void setPositionResizable(final long position, final boolean resizable) {
		this.resizablesMap.put(position, resizable);
	}
	
	/**
	 * Sets the global resizable configuration.
	 * Will reset all special resizable configurations.
	 * @param resizableByDefault <code>true</code> if all rows/columns should be resizable,
	 * 			<code>false</code> if no row/column should be resizable.
	 */
	public void setResizableByDefault(final boolean resizableByDefault) {
		this.resizablesMap.clear();
		this.resizableByDefault= resizableByDefault;
	}
	
	// All positions same size
	
	public boolean isAllPositionsSameSize() {
		return this.defaultSizeMap.size() == 0 && this.sizeMap.size() == 0;
	}
	
}
