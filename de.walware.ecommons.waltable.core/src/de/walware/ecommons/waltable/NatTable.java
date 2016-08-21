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
// -cleanup, ~
package de.walware.ecommons.waltable;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;
import static de.walware.ecommons.waltable.painter.cell.GraphicsUtils.safe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

import de.walware.ecommons.waltable.command.DisposeResourcesCommand;
import de.walware.ecommons.waltable.command.ILayerCommand;
import de.walware.ecommons.waltable.command.ILayerCommandHandler;
import de.walware.ecommons.waltable.command.StructuralRefreshCommand;
import de.walware.ecommons.waltable.config.ConfigRegistry;
import de.walware.ecommons.waltable.config.DefaultNatTableStyleConfiguration;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.config.IConfiguration;
import de.walware.ecommons.waltable.conflation.EventConflaterChain;
import de.walware.ecommons.waltable.conflation.IEventConflater;
import de.walware.ecommons.waltable.conflation.VisualChangeEventConflater;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.coordinate.PixelOutOfBoundsException;
import de.walware.ecommons.waltable.edit.ActiveCellEditorRegistry;
import de.walware.ecommons.waltable.grid.ClientAreaResizeCommand;
import de.walware.ecommons.waltable.grid.InitializeGridCommand;
import de.walware.ecommons.waltable.internal.LayerListenerList;
import de.walware.ecommons.waltable.internal.WaLTablePlugin;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.ILayerListener;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.layer.event.ILayerEvent;
import de.walware.ecommons.waltable.layer.event.IVisualChangeEvent;
import de.walware.ecommons.waltable.painter.IOverlayPainter;
import de.walware.ecommons.waltable.painter.layer.ILayerPainter;
import de.walware.ecommons.waltable.painter.layer.NatLayerPainter;
import de.walware.ecommons.waltable.persistence.IPersistable;
import de.walware.ecommons.waltable.selection.CellSelectionEvent;
import de.walware.ecommons.waltable.swt.SWTUtil;
import de.walware.ecommons.waltable.ui.IClientAreaProvider;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;
import de.walware.ecommons.waltable.ui.mode.ConfigurableModeEventHandler;
import de.walware.ecommons.waltable.ui.mode.Mode;
import de.walware.ecommons.waltable.ui.mode.ModeSupport;
import de.walware.ecommons.waltable.util.GUIHelper;
import de.walware.ecommons.waltable.viewport.RecalculateScrollBarsCommand;


public class NatTable extends Canvas implements ILayer, PaintListener, ILayerListener, IPersistable {
	
	public static final int DEFAULT_STYLE_OPTIONS= SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED  | SWT.V_SCROLL | SWT.H_SCROLL;
	
	
	private final NatTableDim hDim;
	private final NatTableDim vDim;
	
	private IClientAreaProvider clientAreaProvider= new IClientAreaProvider() {
		@Override
		public LRectangle getClientArea() {
			if (!isDisposed()) {
				return SWTUtil.toNatTable(NatTable.this.getClientArea());
			} else {
				return new LRectangle(0, 0, 0, 0);
			}
		}
		
	};
	
	
	private UiBindingRegistry uiBindingRegistry;
	
	private ModeSupport modeSupport;
	
	private final EventConflaterChain conflaterChain= new EventConflaterChain();
	
	private final List<IOverlayPainter> overlayPainters= new ArrayList<>();
	
	private final List<IPersistable> persistables= new LinkedList<>();
	
	private ILayer underlyingLayer;
	
	private IConfigRegistry configRegistry;
	
	protected final Collection<IConfiguration> configurations= new LinkedList<>();
	
	protected String id= GUIHelper.getSequenceNumber();
	
	private ILayerPainter layerPainter= new NatLayerPainter(this);
	
	private final boolean autoconfigure;
	
	
	public NatTable(final Composite parent, final ILayer layer) {
		this(parent, DEFAULT_STYLE_OPTIONS, layer);
	}
	
	public NatTable(final Composite parent, final ILayer layer, final boolean autoconfigure) {
		this(parent, DEFAULT_STYLE_OPTIONS, layer, autoconfigure);
	}
	
	public NatTable(final Composite parent, final int style, final ILayer layer) {
		this(parent, style, layer, true);
	}
	
	public NatTable(final Composite parent, final int style, final ILayer layer, final boolean autoconfigure) {
		super(parent, style);
		
		this.hDim= new NatTableDim(this, layer.getDim(HORIZONTAL));
		this.vDim= new NatTableDim(this, layer.getDim(VERTICAL));
		
		// Disable scroll bars by default; if a Viewport is available, it will enable the scroll bars
		disableScrollBar(getHorizontalBar());
		disableScrollBar(getVerticalBar());
		
		initInternalListener();
		
		internalSetLayer(layer);
		
		this.autoconfigure= autoconfigure;
		if (autoconfigure) {
			this.configurations.add(new DefaultNatTableStyleConfiguration());
			configure();
		}
		
		this.conflaterChain.add(getVisualChangeEventConflater());
		this.conflaterChain.start();
		
		addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(final DisposeEvent e) {
				doCommand(new DisposeResourcesCommand());
				NatTable.this.conflaterChain.stop();
				ActiveCellEditorRegistry.unregisterActiveCellEditor();
				layer.dispose();
			}
			
		});
	}
	
	
	@Override
	public NatTableDim getDim(final Orientation orientation) {
		if (orientation == null) {
			throw new NullPointerException("orientation"); //$NON-NLS-1$
		}
		
		return (orientation == HORIZONTAL) ? this.hDim : this.vDim;
	}
	
	
	protected IEventConflater getVisualChangeEventConflater() {
		return new VisualChangeEventConflater(this);
	}
	
	private void disableScrollBar(final ScrollBar scrollBar) {
		if (scrollBar != null) {
			scrollBar.setMinimum(0);
			scrollBar.setMaximum(1);
			scrollBar.setThumb(1);
			scrollBar.setEnabled(false);
		}
	}
	
	public ILayer getLayer() {
		return this.underlyingLayer;
	}
	
	private void internalSetLayer(final ILayer layer) {
		if (layer != null) {
			this.underlyingLayer= layer;
			this.underlyingLayer.setClientAreaProvider(getClientAreaProvider());
			this.underlyingLayer.addLayerListener(this);
		}
	}
	
	/**
	 * Adds a configuration to the table.
	 * <p>
	 * Configurations are processed when the {@link #configure()} method is invoked.
	 * Each configuration object then has a chance to configure the
	 * 	<ol>
	 * 		<li>ILayer</li>
	 * 		<li>ConfigRegistry</li>
	 * 		<li>UiBindingRegistry</li>
	 *  </ol>
	 */
	public void addConfiguration(final IConfiguration configuration) {
		if (this.autoconfigure) {
			throw new IllegalStateException("May only add configurations post construction if autoconfigure is turned off"); //$NON-NLS-1$
		}
		
		this.configurations.add(configuration);
	}
	
	/**
	 * @return {@link IConfigRegistry} used to hold the configuration bindings
	 * 	by Layer, DisplayMode and Config labels.
	 */
	public IConfigRegistry getConfigRegistry() {
		if (this.configRegistry == null) {
			this.configRegistry= new ConfigRegistry();
		}
		return this.configRegistry;
	}
	
	public void setConfigRegistry(final IConfigRegistry configRegistry) {
		if (this.autoconfigure) {
			throw new IllegalStateException("May only set config registry post construction if autoconfigure is turned off"); //$NON-NLS-1$
		}
		
		this.configRegistry= configRegistry;
	}
	
	/**
	 * @return Registry holding all the UIBindings contributed by the underlying layers
	 */
	public UiBindingRegistry getUiBindingRegistry() {
		if (this.uiBindingRegistry == null) {
			this.uiBindingRegistry= new UiBindingRegistry(this);
		}
		return this.uiBindingRegistry;
	}
	
	public void setUiBindingRegistry(final UiBindingRegistry uiBindingRegistry) {
		if (this.autoconfigure) {
			throw new IllegalStateException("May only set UI binding registry post construction if autoconfigure is turned off"); //$NON-NLS-1$
		}
		
		this.uiBindingRegistry= uiBindingRegistry;
	}
	
	public String getID() {
		return this.id;
	}
	
	@Override
	protected void checkSubclass() {
	}
	
	protected void initInternalListener() {
		this.modeSupport= new ModeSupport(this);
		this.modeSupport.registerModeEventHandler(Mode.NORMAL_MODE, new ConfigurableModeEventHandler(this.modeSupport, this));
		this.modeSupport.switchMode(Mode.NORMAL_MODE);
		
		addPaintListener(this);
		
		addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(final FocusEvent arg0) {
				redraw();
			}
			
			@Override
			public void focusGained(final FocusEvent arg0) {
				redraw();
			}
			
		});
		
		addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				doCommand(new ClientAreaResizeCommand(NatTable.this));
			}
		});
	}
	
	@Override
	public boolean forceFocus() {
		return super.forceFocus();
	}
	
	// Painting ///////////////////////////////////////////////////////////////
	
	public List<IOverlayPainter> getOverlayPainters() {
		return this.overlayPainters;
	}
	
	public void addOverlayPainter(final IOverlayPainter overlayPainter) {
		this.overlayPainters.add(overlayPainter);
	}
	
	public void removeOverlayPainter(final IOverlayPainter overlayPainter) {
		this.overlayPainters.remove(overlayPainter);
	}
	
	@Override
	public void paintControl(final PaintEvent event) {
		paintNatTable(event);
	}
	
	private void paintNatTable(final PaintEvent event) {
		final Rectangle eventRectangle= new Rectangle(event.x, event.y, event.width, event.height);
		
		if (!eventRectangle.isEmpty()) {
			getLayerPainter().paintLayer(this, event.gc, 0, 0, eventRectangle, getConfigRegistry());
		}
	}
	
	@Override
	public ILayerPainter getLayerPainter() {
		return this.layerPainter;
	}
	
	public void setLayerPainter(final ILayerPainter layerPainter) {
		this.layerPainter= layerPainter;
	}
	
	/**
	 * Repaint only a specific column in the grid. This method is optimized so that only the specific column is
	 * repainted and nothing else.
	 *
	 * @param columnPosition column of the grid to repaint
	 */
	public void repaintColumn(final long columnPosition) {
		this.hDim.repaintPosition(columnPosition);
	}
	
	/**
	 * Repaint only a specific row in the grid. This method is optimized so that only the specific row is repainted and
	 * nothing else.
	 *
	 * @param rowPosition row of the grid to repaint
	 */
	public void repaintRow(final long rowPosition) {
		this.vDim.repaintPosition(rowPosition);
	}
	
	protected void repaint(final Orientation orientation, final int start, final int size) {
		if (orientation == HORIZONTAL) {
			redraw(start, 0, size, safe(getHeight()), true);
		}
		else {
			redraw(0, start, safe(getWidth()), size, true);
		}
	}
	
	
	public void updateResize() {
		updateResize(true);
	}
	
	/**
	 * Update the table screen by re-calculating everything again. It should not
	 * be called too frequently.
	 *
	 * @param redraw
	 *            true to redraw the table
	 */
	private void updateResize(final boolean redraw) {
		if (isDisposed()) {
			return;
		}
		doCommand(new RecalculateScrollBarsCommand());
		if (redraw) {
			redraw();
		}
	}
	
	/**
	 * Refreshes the entire NatTable as every layer will be refreshed.
	 */
	public void refresh() {
		doCommand(new StructuralRefreshCommand());
	}
	
	@Override
	public void configure(final ConfigRegistry configRegistry, final UiBindingRegistry uiBindingRegistry) {
		throw new UnsupportedOperationException("Cannot use this method to configure NatTable. Use no-argument configure() instead."); //$NON-NLS-1$
	}
	
	/**
	 * Processes all the registered {@link IConfiguration} (s).
	 * All the underlying layers are walked and given a chance to configure.
	 * Note: all desired configuration tweaks must be done <i>before</i> this method is invoked.
	 */
	public void configure() {
		if (this.underlyingLayer == null) {
			throw new IllegalStateException("Layer must be set before configure is called"); //$NON-NLS-1$
		}
		
		if (this.underlyingLayer != null) {
			this.underlyingLayer.configure((ConfigRegistry) getConfigRegistry(), getUiBindingRegistry());
		}
		
		for (final IConfiguration configuration : this.configurations) {
			configuration.configureLayer(this);
			configuration.configureRegistry(getConfigRegistry());
			configuration.configureUiBindings(getUiBindingRegistry());
		}
		
		// Once everything is initialized and properly configured we will
		// now formally initialize the grid
		doCommand(new InitializeGridCommand(this));
	}
	
	// Events /////////////////////////////////////////////////////////////////
	
	@Override
	public void handleLayerEvent(final ILayerEvent event) {
		for (final ILayerListener layerListener : this.listeners.getListeners()) {
			layerListener.handleLayerEvent(event);
		}
		
		if (event instanceof IVisualChangeEvent) {
			this.conflaterChain.addEvent(event);
		}
		
		if (event instanceof CellSelectionEvent) {
			final Event e= new Event();
			e.widget= this;
			try {
				notifyListeners(SWT.Selection, e);
			} catch (final RuntimeException re) {
				WaLTablePlugin.log(new Status(IStatus.ERROR, WaLTablePlugin.PLUGIN_ID,
						"An error occurred when fireing SWT selection event.", re )); //$NON-NLS-1$
			}
		}
	}
	
	
	// ILayer /////////////////////////////////////////////////////////////////
	
	// Persistence
	
	/**
	 * Save the state of the table to the properties object.
	 * {@link ILayer#saveState(String, Properties)} is invoked on all the underlying layers.
	 * This properties object will be populated with the settings of all underlying layers
	 * and any {@link IPersistable} registered with those layers.
	 */
	@Override
	public void saveState(final String prefix, final Properties properties) {
		BusyIndicator.showWhile(null, new Runnable() {
			
			@Override
			public void run() {
				NatTable.this.underlyingLayer.saveState(prefix, properties);
			}
		});
	}
	
	/**
	 * Restore the state of the underlying layers from the values in the properties object.
	 * @see #saveState(String, Properties)
	 */
	@Override
	public void loadState(final String prefix, final Properties properties) {
		BusyIndicator.showWhile(null, new Runnable() {
			
			@Override
			public void run() {
				NatTable.this.underlyingLayer.loadState(prefix, properties);
			}
		});
	}
	
	/**
	 * @see ILayer#registerPersistable(IPersistable)
	 */
	@Override
	public void registerPersistable(final IPersistable persistable) {
		this.persistables.add(persistable);
	}
	
	@Override
	public void unregisterPersistable(final IPersistable persistable) {
		this.persistables.remove(persistable);
	}
	
	// Command
	
	@Override
	public boolean doCommand(final ILayerCommand command) {
		return this.underlyingLayer.doCommand(command);
	}
	
	@Override
	public void registerCommandHandler(final ILayerCommandHandler<?> commandHandler) {
		this.underlyingLayer.registerCommandHandler(commandHandler);
	}
	
	@Override
	public void unregisterCommandHandler(final Class<? extends ILayerCommand> commandClass) {
		this.underlyingLayer.unregisterCommandHandler(commandClass);
	}
	
	// Events
	
	private final LayerListenerList listeners= new LayerListenerList();
	
	@Override
	public void fireLayerEvent(final ILayerEvent event) {
		this.underlyingLayer.fireLayerEvent(event);
	}
	
	@Override
	public void addLayerListener(final ILayerListener listener) {
		this.listeners.add(listener);
	}
	
	@Override
	public void removeLayerListener(final ILayerListener listener) {
		this.listeners.remove(listener);
	}
	
	// Columns/Horizontal
	
	@Override
	public long getColumnCount() {
		return this.hDim.getPositionCount();
	}
	
	@Override
	public long getWidth() {
		return this.hDim.getSize();
	}
	
	@Override
	public long getColumnPositionByX(final long x) {
		try {
			return this.hDim.getPositionByPixel(x);
		}
		catch (final PixelOutOfBoundsException e) {
			return Long.MIN_VALUE;
		}
	}
	
	
	// Rows/Vertical
	
	@Override
	public long getRowCount() {
		return this.vDim.getPositionCount();
	}
	
	@Override
	public long getHeight() {
		return this.vDim.getSize();
	}
	
	@Override
	public long getRowPositionByY(final long y) {
		try {
			return this.vDim.getPositionByPixel(y);
		}
		catch (final PixelOutOfBoundsException e) {
			return Long.MIN_VALUE;
		}
	}
	
	// Y
	
	// Cell features
	
	@Override
	public ILayerCell getCellByPosition(final long columnPosition, final long rowPosition) {
		return this.underlyingLayer.getCellByPosition(columnPosition, rowPosition);
	}
	
	// IRegionResolver
	
	@Override
	public LabelStack getRegionLabelsByXY(final long x, final long y) {
		return this.underlyingLayer.getRegionLabelsByXY(x, y);
	}
	
	@Override
	public ILayer getUnderlyingLayerByPosition(final long columnPosition, final long rowPosition) {
		return this.underlyingLayer;
	}
	
	@Override
	public IClientAreaProvider getClientAreaProvider() {
		return this.clientAreaProvider;
	}
	
	@Override
	public void setClientAreaProvider(final IClientAreaProvider clientAreaProvider) {
		this.clientAreaProvider= clientAreaProvider;
		this.underlyingLayer.setClientAreaProvider(clientAreaProvider);
	}
	
	
	// DND /////////////////////////////////////////////////////////////////
	
	/**
	 * Adds support for dragging items out of this control via a user
	 * drag-and-drop operation.
	 *
	 * @param operations
	 *            a bitwise OR of the supported drag and drop operation types (
	 *            <code>DROP_COPY</code>,<code>DROP_LINK</code>, and
	 *            <code>DROP_MOVE</code>)
	 * @param transferTypes
	 *            the transfer types that are supported by the drag operation
	 * @param listener
	 *            the callback that will be invoked to set the drag data and to
	 *            cleanup after the drag and drop operation finishes
	 * @see org.eclipse.swt.dnd.DND
	 */
	public void addDragSupport(final int operations, final Transfer[] transferTypes, final DragSourceListener listener) {
		final DragSource dragSource= new DragSource(this, operations);
		dragSource.setTransfer(transferTypes);
		dragSource.addDragListener(listener);
	}
	
	/**
	 * Adds support for dropping items into this control via a user drag-and-drop
	 * operation.
	 *
	 * @param operations
	 *            a bitwise OR of the supported drag and drop operation types (
	 *            <code>DROP_COPY</code>,<code>DROP_LINK</code>, and
	 *            <code>DROP_MOVE</code>)
	 * @param transferTypes
	 *            the transfer types that are supported by the drop operation
	 * @param listener
	 *            the callback that will be invoked after the drag and drop
	 *            operation finishes
	 * @see org.eclipse.swt.dnd.DND
	 */
	public void addDropSupport(final int operations, final Transfer[] transferTypes, final DropTargetListener listener) {
		final DropTarget dropTarget= new DropTarget(this, operations);
		dropTarget.setTransfer(transferTypes);
		dropTarget.addDropListener(listener);
	}
	
}
