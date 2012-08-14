/*******************************************************************************
 * Copyright (c) 2009-2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.walware.ecommons.ICommonStatusConstants;
import de.walware.ecommons.IDisposable;
import de.walware.ecommons.ui.ColorManager;
import de.walware.ecommons.ui.SharedUIResources;
import de.walware.ecommons.ui.util.ImageRegistryUtil;
import de.walware.ecommons.ui.util.UIAccess;


public class UIMiscellanyPlugin extends AbstractUIPlugin {
	
	
	public static final String PLUGIN_ID = "de.walware.ecommons.uimisc"; //$NON-NLS-1$
	
	
	/** The shared instance */
	private static UIMiscellanyPlugin gPlugin;
	
	/**
	 * Returns the shared plug-in instance
	 *
	 * @return the shared instance
	 */
	public static UIMiscellanyPlugin getDefault() {
		return gPlugin;
	}
	
	
	private boolean fStarted;
	
	private final List<IDisposable> fDisposables = new ArrayList<IDisposable>();
	
	private ColorManager fColorManager;
	private ImageRegistry fImageRegistry;
	
	
	/**
	 * The default constructor
	 */
	public UIMiscellanyPlugin() {
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		gPlugin = this;
		
		fStarted = true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			final ColorManager colorManager;
			final ImageRegistry imageRegistry;
			synchronized (this) {
				fStarted = false;
				
				colorManager = fColorManager;
				fColorManager = null;
				imageRegistry = fImageRegistry;
				fImageRegistry = null;
			}
			
			final Display display = UIAccess.getDisplay();
			if (display != null && !display.isDisposed()) {
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						
						if (colorManager != null) {
							try {
								colorManager.dispose();
							}
							catch (final Exception e) {}
						}
						if (imageRegistry != null) {
							try {
								imageRegistry.dispose();
							}
							catch (final Exception e) {}
						}
					}
				});
			}
			for (final IDisposable listener : fDisposables) {
				try {
					listener.dispose();
				}
				catch (final Throwable e) {
					getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, ICommonStatusConstants.INTERNAL_PLUGGED_IN, "Error occured when dispose module", e)); 
				}
			}
			fDisposables.clear();
		}
		finally {
			gPlugin = null;
			super.stop(context);
		}
	}
	
	
	public void addStoppingListener(final IDisposable listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		synchronized (this) {
			if (!fStarted) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			fDisposables.add(listener);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeImageRegistry(final ImageRegistry reg) {
		if (!fStarted) {
			throw new IllegalStateException("Plug-in is not started.");
		}
		fImageRegistry = reg;
		final ImageRegistryUtil util = new ImageRegistryUtil(this);
		
		util.register(SharedUIResources.OBJ_USER_IMAGE_ID, ImageRegistryUtil.T_OBJ, "user.png"); //$NON-NLS-1$
		util.register(SharedUIResources.OBJ_LINE_MATCH_IMAGE_ID, ImageRegistryUtil.T_OBJ, "line_match.gif"); //$NON-NLS-1$
		
		util.register(SharedUIResources.OVR_DEFAULT_MARKER_IMAGE_ID, ImageRegistryUtil.T_OVR, "default_marker.gif"); //$NON-NLS-1$
		util.register(SharedUIResources.OVR_GREEN_LIGHT_IMAGE_ID, ImageRegistryUtil.T_OVR, "light-green.png"); //$NON-NLS-1$
		util.register(SharedUIResources.OVR_YELLOW_LIGHT_IMAGE_ID, ImageRegistryUtil.T_OVR, "light-yellow.png"); //$NON-NLS-1$
		
		util.register(SharedUIResources.LOCTOOL_FILTER_IMAGE_ID, ImageRegistryUtil.T_LOCTOOL, "filter_view.gif"); //$NON-NLS-1$
		util.register(SharedUIResources.LOCTOOLD_FILTER_IMAGE_ID, ImageRegistryUtil.T_LOCTOOL_D, "filter_view.gif"); //$NON-NLS-1$
		util.register(SharedUIResources.LOCTOOL_DISABLE_FILTER_IMAGE_ID, ImageRegistryUtil.T_LOCTOOL, "disable-filter.png"); //$NON-NLS-1$
		
		util.register(SharedUIResources.LOCTOOL_SORT_ALPHA_IMAGE_ID, ImageRegistryUtil.T_LOCTOOL, "sort-alpha.png"); //$NON-NLS-1$
		util.register(SharedUIResources.LOCTOOL_SORT_SCORE_IMAGE_ID, ImageRegistryUtil.T_LOCTOOL, "sort-score.png"); //$NON-NLS-1$
		
		util.register(SharedUIResources.LOCTOOL_CASESENSITIVE_IMAGE_ID, ImageRegistryUtil.T_LOCTOOL, "casesensitive.png"); //$NON-NLS-1$
		
		util.register(SharedUIResources.LOCTOOL_EXPANDALL_IMAGE_ID, ImageRegistryUtil.T_LOCTOOL, "expandall.gif"); //$NON-NLS-1$
		util.register(SharedUIResources.LOCTOOL_COLLAPSEALL_IMAGE_ID, ImageRegistryUtil.T_LOCTOOL, "collapseall.gif"); //$NON-NLS-1$
		
		util.register(SharedUIResources.LOCTOOL_SCROLLLOCK_IMAGE_ID, ImageRegistryUtil.T_LOCTOOL, "scrolllock.gif"); //$NON-NLS-1$
		
		util.register(SharedUIResources.LOCTOOL_CLEARSEARCH_IMAGE_ID, ImageRegistryUtil.T_LOCTOOL, "clearsearch.gif"); //$NON-NLS-1$
		util.register(SharedUIResources.LOCTOOLD_CLEARSEARCH_IMAGE_ID, ImageRegistryUtil.T_LOCTOOL_D, "clearsearch.gif"); //$NON-NLS-1$
		
		util.register(SharedUIResources.LOCTOOL_SYNCHRONIZED_IMAGE_ID, ImageRegistryUtil.T_LOCTOOL, "synced.png"); //$NON-NLS-1$
		
		util.register(SharedUIResources.LOCTOOL_FAVORITES_IMAGE_ID, ImageRegistryUtil.T_LOCTOOL, "favorites.png"); //$NON-NLS-1$
		
		util.register(SharedUIResources.LOCTOOL_CHANGE_PAGE_IMAGE_ID, ImageRegistryUtil.T_LOCTOOL, "change_page.png"); //$NON-NLS-1$
		util.register(SharedUIResources.LOCTOOL_PIN_PAGE_IMAGE_ID, ImageRegistryUtil.T_LOCTOOL, "pin_page.png"); //$NON-NLS-1$
		util.register(SharedUIResources.LOCTOOLD_PIN_PAGE_IMAGE_ID, ImageRegistryUtil.T_LOCTOOL, "pin_page.png"); //$NON-NLS-1$
		
		UIAccess.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				final Display display = Display.getCurrent();
				final int[] cross = new int[] { 
						 3,  3,  5,  3,  7,  5,  8,  5, 10,  3, 12,  3, 
						12,  5, 10,  7, 10,  8, 12, 10, 12, 12,
						10, 12,  8, 10,  7, 10,  5, 12,  3, 12,
						 3, 10,  5,  8,  5,  7,  3,  5,
				};
				final int[] right = new int[] { 
						 5,  3,  8,  3, 12,  7, 12,  8,  8, 12,  5, 12,
						 5, 11,  8,  8,  8,  7,  5,  4, 
				};
				final int[] left = new int[right.length];
				final int[] up = new int[right.length];
				final int[] down = new int[right.length];
				for (int i = 0; i < right.length; i = i+2) {
					final int j = i+1;
					final int x = right[i];
					final int y = right[j];
					left[i] = 16-x;
					left[j] = y;
					up[i] = y;
					up[j] = 16-x;
					down[i] = y;
					down[j] = x;
				}
				
				final Color border = display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW);
				final Color background = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
				final Color hotRed = new Color(display, new RGB(252, 160, 160));
				final Color hotYellow = new Color(display, new RGB(252, 232, 160));
				final Color transparent = display.getSystemColor(SWT.COLOR_MAGENTA);
				
				final PaletteData palette = new PaletteData(new RGB[] { transparent.getRGB(), border.getRGB(), background.getRGB(), hotRed.getRGB(), hotYellow.getRGB() });
				final ImageData data = new ImageData(16, 16, 8, palette);
				data.transparentPixel = 0;
				
				{	// Dummy
					final Image image = new Image(display, data);
					
					reg.put(SharedUIResources.PLACEHOLDER_IMAGE_ID, image);
				}
				{	// Close
					final Image image = new Image(display, data);
					image.setBackground(transparent);
					final GC gc = new GC(image);
					gc.setBackground(background);
					gc.fillPolygon(cross);
					gc.setForeground(border);
					gc.drawPolygon(cross);
					gc.dispose();
					
					reg.put(SharedUIResources.LOCTOOL_CLOSETRAY_IMAGE_ID, image);
				}
				{	// Close hot
					final Image image = new Image(display, data);
					image.setBackground(transparent);
					final GC gc = new GC(image);
					gc.setBackground(hotRed);
					gc.fillPolygon(cross);
					gc.setForeground(border);
					gc.drawPolygon(cross);
					gc.dispose();
					
					reg.put(SharedUIResources.LOCTOOL_CLOSETRAY_H_IMAGE_ID, image);
				}
				{	// Left
					final Image image = new Image(display, data);
					image.setBackground(transparent);
					final GC gc = new GC(image);
					gc.setBackground(background);
					gc.fillPolygon(left);
					gc.setForeground(border);
					gc.drawPolygon(left);
					gc.dispose();
					
					reg.put(SharedUIResources.LOCTOOL_LEFT_IMAGE_ID, image);
				}
				{	// Left hot
					final Image image = new Image(display, data);
					image.setBackground(transparent);
					final GC gc = new GC(image);
					gc.setBackground(hotYellow);
					gc.fillPolygon(left);
					gc.setForeground(border);
					gc.drawPolygon(left);
					gc.dispose();
					
					reg.put(SharedUIResources.LOCTOOL_LEFT_H_IMAGE_ID, image);
				}
				{	// Right
					final Image image = new Image(display, data);
					image.setBackground(transparent);
					final GC gc = new GC(image);
					gc.setBackground(background);
					gc.fillPolygon(right);
					gc.setForeground(border);
					gc.drawPolygon(right);
					gc.dispose();
					
					reg.put(SharedUIResources.LOCTOOL_RIGHT_IMAGE_ID, image);
				}
				{	// Right hot
					final Image image = new Image(display, data);
					image.setBackground(transparent);
					final GC gc = new GC(image);
					gc.setBackground(hotYellow);
					gc.fillPolygon(right);
					gc.setForeground(border);
					gc.drawPolygon(right);
					gc.dispose();
					
					reg.put(SharedUIResources.LOCTOOL_RIGHT_H_IMAGE_ID, image);
				}
				{	// Up
					final Image image = new Image(display, data);
					image.setBackground(transparent);
					final GC gc = new GC(image);
					gc.setBackground(background);
					gc.fillPolygon(up);
					gc.setForeground(border);
					gc.drawPolygon(up);
					gc.dispose();
					
					reg.put(SharedUIResources.LOCTOOL_UP_IMAGE_ID, image);
				}
				{	// Up hot
					final Image image = new Image(display, data);
					image.setBackground(transparent);
					final GC gc = new GC(image);
					gc.setBackground(hotYellow);
					gc.fillPolygon(up);
					gc.setForeground(border);
					gc.drawPolygon(up);
					gc.dispose();
					
					reg.put(SharedUIResources.LOCTOOL_UP_H_IMAGE_ID, image);
				}
				{	// Down
					final Image image = new Image(display, data);
					image.setBackground(transparent);
					final GC gc = new GC(image);
					gc.setBackground(background);
					gc.fillPolygon(down);
					gc.setForeground(border);
					gc.drawPolygon(down);
					gc.dispose();
					
					reg.put(SharedUIResources.LOCTOOL_DOWN_IMAGE_ID, image);
				}
				{	// Down hot
					final Image image = new Image(display, data);
					image.setBackground(transparent);
					final GC gc = new GC(image);
					gc.setBackground(hotYellow);
					gc.fillPolygon(down);
					gc.setForeground(border);
					gc.drawPolygon(down);
					gc.dispose();
					
					reg.put(SharedUIResources.LOCTOOL_DOWN_H_IMAGE_ID, image);
				}
				
				hotRed.dispose();
				hotYellow.dispose();
			}
		});
	}
	
	
	public synchronized ColorManager getColorManager() {
		if (fColorManager == null) {
			if (!fStarted) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			fColorManager = new ColorManager();
		}
		return fColorManager;
	}
	
}
