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
// ~(ListenerList)

package de.walware.ecommons.waltable.layer;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.walware.ecommons.waltable.command.ILayerCommand;
import de.walware.ecommons.waltable.command.ILayerCommandHandler;
import de.walware.ecommons.waltable.config.ConfigRegistry;
import de.walware.ecommons.waltable.config.IConfiguration;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.coordinate.PixelOutOfBoundsException;
import de.walware.ecommons.waltable.internal.LayerListenerList;
import de.walware.ecommons.waltable.layer.event.ILayerEvent;
import de.walware.ecommons.waltable.layer.event.ILayerEventHandler;
import de.walware.ecommons.waltable.painter.layer.GridLineCellLayerPainter;
import de.walware.ecommons.waltable.painter.layer.ILayerPainter;
import de.walware.ecommons.waltable.persistence.IPersistable;
import de.walware.ecommons.waltable.ui.IClientAreaProvider;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;


/**
 * Base layer implementation with common methods for managing listeners and caching, etc.
 */
public abstract class AbstractLayer implements ILayer {
	
	
	private ILayerDim hDim;
	private ILayerDim vDim;
	
	protected ILayerPainter layerPainter;
	private IClientAreaProvider clientAreaProvider= IClientAreaProvider.DEFAULT;
	
	private final Map<Class<? extends ILayerCommand>, ILayerCommandHandler<? extends ILayerCommand>> commandHandlers= new LinkedHashMap<>();
	private final Map<Class<? extends ILayerEvent>, ILayerEventHandler<? extends ILayerEvent>> eventHandlers= new HashMap<>();
	
	private final List<IPersistable> persistables= new LinkedList<>();
	private final LayerListenerList listeners= new LayerListenerList();
	private final Collection<IConfiguration> configurations= new ArrayList<>();
	
	
	protected AbstractLayer() {
		initDims();
		
		this.layerPainter= createPainter();
	}
	
	
	// Dims
	
	/**
	 * Updates the layer dimensions.
	 * 
	 * Override this method to set custom layer dimension implementations.
	 */
	protected abstract void initDims();
	
	
	/**
	 * Sets the layer dimension of this layer for the orientation of the given dimension.
	 * 
	 * This method use usually called in {@link #initDims()}.
	 * 
	 * @param dim the layer dimension
	 */
	protected void setDim(/*@NonNull*/ final ILayerDim dim) {
		if (dim == null) {
			throw new NullPointerException("dim"); //$NON-NLS-1$
		}
		
		if (dim.getOrientation() == HORIZONTAL) {
			this.hDim= dim;
		}
		else {
			this.vDim= dim;
		}
	}
	
	@Override
	public ILayerDim getDim(final Orientation orientation) {
		if (orientation == null) {
			throw new NullPointerException("orientation"); //$NON-NLS-1$
		}
		
		return (orientation == HORIZONTAL) ? this.hDim : this.vDim;
	}
	
	
	protected ILayerPainter createPainter() {
		return null;
	}
	
	
	// Dispose
	
	@Override
	public void dispose() {
	}
	
	
	// Regions
	
	@Override
	public LabelStack getRegionLabelsByXY(final long x, final long y) {
		return new LabelStack();
	}
	
	// Persistence
	
	@Override
	public void saveState(final String prefix, final Properties properties) {
		for (final IPersistable persistable : this.persistables) {
			persistable.saveState(prefix, properties);
		}
	}
	
	@Override
	public void loadState(final String prefix, final Properties properties) {
		for (final IPersistable persistable : this.persistables) {
			persistable.loadState(prefix, properties);
		}
	}
	  
	@Override
	public void registerPersistable(final IPersistable persistable){
		this.persistables.add(persistable);
	}

	@Override
	public void unregisterPersistable(final IPersistable persistable){
		this.persistables.remove(persistable);
	}
	
	// Configuration
	
	public void addConfiguration(final IConfiguration configuration) {
		this.configurations.add(configuration);
	}

	public void clearConfiguration() {
		this.configurations.clear();
	}
	
	@Override
	public void configure(final ConfigRegistry configRegistry, final UiBindingRegistry uiBindingRegistry) {
		for (final IConfiguration configuration : this.configurations) {
			configuration.configureLayer(this);
			configuration.configureRegistry(configRegistry);
			configuration.configureUiBindings(uiBindingRegistry);
		}
	}
	
	// Commands
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean doCommand(final ILayerCommand command) {
		for (final Class<? extends ILayerCommand> commandClass : this.commandHandlers.keySet()) {
			if (commandClass.isInstance(command)) {
				final ILayerCommandHandler commandHandler= this.commandHandlers.get(commandClass);
				if (commandHandler.doCommand(this, command.cloneCommand())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	// Command handlers
	
	/**
	 * Layers should use this method to register their command handlers
	 * and call it from their constructor. This allows easy overriding if 
	 * required of command handlers 
	 */
	protected void registerCommandHandlers() {
		// No op
	}
	
	@Override
	public void registerCommandHandler(final ILayerCommandHandler<?> commandHandler) {
		this.commandHandlers.put(commandHandler.getCommandClass(), commandHandler);
	}
	
	@Override
	public void unregisterCommandHandler(final Class<? extends ILayerCommand> commandClass) {
		this.commandHandlers.remove(commandClass);
	}
	
	// Events
	
	@Override
	public void addLayerListener(final ILayerListener listener) {
		this.listeners.add(listener);
	}
	
	@Override
	public void removeLayerListener(final ILayerListener listener) {
		this.listeners.remove(listener);
	}
	
	/**
	 * Handle layer event notification. Convert it to your context
	 * and propagate <i>UP</i>.
	 *  
	 * If you override this method you <strong>MUST NOT FORGET</strong> to raise
	 * the event up the layer stack by calling <code>super.fireLayerEvent(event)</code>
	 * - unless you plan to eat the event yourself.
	 **/
	@Override
	@SuppressWarnings("unchecked")
	public void handleLayerEvent(final ILayerEvent event) {
		for (final Class<? extends ILayerEvent> eventClass : this.eventHandlers.keySet()) {
			if (eventClass.isInstance(event)) {
				final ILayerEventHandler eventHandler= this.eventHandlers.get(eventClass);
				eventHandler.handleLayerEvent(event);
			}
		}
		
		// Pass on the event to our parent
		if (event.convertToLocal(this)) {
			fireLayerEvent(event);
		}
	}
	
	public void registerEventHandler(final ILayerEventHandler<?> eventHandler) {
		this.eventHandlers.put(eventHandler.getLayerEventClass(), eventHandler);
	}
	
	public void unregisterEventHandler(final ILayerEventHandler<?> eventHandler) {
		this.eventHandlers.remove(eventHandler.getLayerEventClass());
	}
	
	/**
	 * Pass the event to all the {@link ILayerListener} registered on this layer.
	 * A cloned copy is passed to each listener.
	 */
	@Override
	public void fireLayerEvent(final ILayerEvent event) {
		final ILayerListener[] currentListeners= this.listeners.getListeners();
		final int last= currentListeners.length - 1;
		if (last >= 0) {
			// Fire cloned event to first n-1 listeners; fire original event to last listener
			for (int i= 0; i < last; i++) {
				currentListeners[i].handleLayerEvent(event.cloneEvent());
			}
			currentListeners[last].handleLayerEvent(event);
		}
	}
	
	/**
	 * @return {@link ILayerPainter}. Defaults to {@link GridLineCellLayerPainter}
	 */
	@Override
	public ILayerPainter getLayerPainter() {
		if (this.layerPainter == null) {
			this.layerPainter= new GridLineCellLayerPainter();
		}
		return this.layerPainter;
	}
	
	protected void setLayerPainter(final ILayerPainter layerPainter) {
		this.layerPainter= layerPainter;
	}

	// Client area
	
	@Override
	public IClientAreaProvider getClientAreaProvider() {
		return this.clientAreaProvider;
	}
	
	@Override
	public void setClientAreaProvider(final IClientAreaProvider clientAreaProvider) {
		this.clientAreaProvider= clientAreaProvider;
	}
	
	
	@Override
	public final long getColumnCount() {
		return this.hDim.getPositionCount();
	}
	
	@Override
	public final long getWidth() {
		return this.hDim.getSize();
	}
	
	@Override
	public final long getColumnPositionByX(final long x) {
		try {
			return this.hDim.getPositionByPixel(x);
		}
		catch (final PixelOutOfBoundsException e) {
			return Long.MIN_VALUE;
		}
	}
	
	
	@Override
	public final long getRowCount() {
		return this.vDim.getPositionCount();
	}
	
	@Override
	public final long getHeight() {
		return this.vDim.getSize();
	}
	
	@Override
	public final long getRowPositionByY(final long y) {
		try {
			return this.vDim.getPositionByPixel(y);
		}
		catch (final PixelOutOfBoundsException e) {
			return Long.MIN_VALUE;
		}
	}
	
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
}
