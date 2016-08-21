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
//
package de.walware.ecommons.waltable.util;

import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class GUIHelper {

	private static final String KEY_PREFIX= GUIHelper.class.getCanonicalName() + "."; //$NON-NLS-1$

	// Color

	public static final Color COLOR_GRAY= Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
	public static final Color COLOR_WHITE= Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	public static final Color COLOR_DARK_GRAY= Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
	public static final Color COLOR_BLACK= Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	public static final Color COLOR_BLUE= Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
	public static final Color COLOR_RED= Display.getDefault().getSystemColor(SWT.COLOR_RED);
	public static final Color COLOR_YELLOW= Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
	public static final Color COLOR_GREEN= Display.getDefault().getSystemColor(SWT.COLOR_GREEN);

	public static final Color COLOR_LIST_BACKGROUND= Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
	public static final Color COLOR_LIST_FOREGROUND= Display.getDefault().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
	public static final Color COLOR_LIST_SELECTION= Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION);
	public static final Color COLOR_LIST_SELECTION_TEXT= Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT);

	public static final Color COLOR_WIDGET_BACKGROUND= Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	public static final Color COLOR_WIDGET_FOREGROUND= Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
	public static final Color COLOR_TITLE_INACTIVE_BACKGROUND= Display.getDefault().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND);
	public static final Color COLOR_WIDGET_BORDER= Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BORDER);
	public static final Color COLOR_WIDGET_DARK_SHADOW= Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW);
	public static final Color COLOR_WIDGET_LIGHT_SHADOW= Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
	public static final Color COLOR_WIDGET_NORMAL_SHADOW= Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
	public static final Color COLOR_WIDGET_HIGHLIGHT_SHADOW= Display.getDefault().getSystemColor( SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);

	public static Color getColor(final RGB rgb) {
		return getColor(rgb.red, rgb.green, rgb.blue);
	}

	public static Color getColor(final int red, final int green, final int blue) {
		final String key= getColorKey(red, green, blue);
		if (JFaceResources.getColorRegistry().hasValueFor(key)) {
			return JFaceResources.getColorRegistry().get(key);
		} else {
			JFaceResources.getColorRegistry().put(key, new RGB(red, green, blue));
			return getColor(key);
		}
	}

	public static Color getColor(final String key) {
		return JFaceResources.getColorRegistry().get(key);
	}

	private static String getColorKey(final int red, final int green, final int blue) {
		return KEY_PREFIX + "_COLOR_" + red + "_" + green + "_" + blue; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	// Font

	public static final Font DEFAULT_FONT= Display.getDefault().getSystemFont();

	public static final int DEFAULT_RESIZE_HANDLE_SIZE= 4;
	public static final int DEFAULT_MIN_DISPLAY_SIZE= 5;
	public static final int DEFAULT_ANTIALIAS= SWT.DEFAULT;;
	public static final int DEFAULT_TEXT_ANTIALIAS= SWT.DEFAULT;;

	public static Font getFont(final FontData...fontDatas) {
		final StringBuilder keyBuilder= new StringBuilder();
		for (final FontData fontData : fontDatas) {
			keyBuilder.append(fontData.toString());
		}
		final String key= keyBuilder.toString();

		if (JFaceResources.getFontRegistry().hasValueFor(key)) {
			return JFaceResources.getFont(key);
		} else {
			JFaceResources.getFontRegistry().put(key, fontDatas);
			return JFaceResources.getFont(key);
		}
	}

	public static Font getFont(final String key) {
		return JFaceResources.getFont(key);
	}

	// Image

	private static final String[] IMAGE_DIRS= new String[] { "de/walware/ecommons/waltable/images/", "" }; //$NON-NLS-1$ //$NON-NLS-2$
	private static final String[] IMAGE_EXTENSIONS= new String[] { ".png", ".gif" }; //$NON-NLS-1$ //$NON-NLS-2$

	public static Image getImage(final String key) {
		Image image= JFaceResources.getImage(key);
		if (image == null) {
			final URL imageUrl= getImageUrl(key);
			if (imageUrl != null) {
				final ImageDescriptor imageDescriptor= ImageDescriptor.createFromURL(imageUrl);
				JFaceResources.getImageRegistry().put(key, imageDescriptor.createImage());
				image= JFaceResources.getImage(key);
			}
		}
		return image;
	}

	public static Image getImage(final ImageData data) {
		if( JFaceResources.getImage(data.toString()) == null ) {
			JFaceResources.getImageRegistry().put(data.toString(), ImageDescriptor.createFromImageData(data));
		}
		return JFaceResources.getImage(data.toString());
	}
	
	private static URL getImageUrl(final String imageName) {
		for (final String dir : IMAGE_DIRS) {
			for (final String ext : IMAGE_EXTENSIONS) {
				final URL url= GUIHelper.class.getClassLoader().getResource(dir + imageName + ext);
				if (url != null) {
					return url;
				}
			}
		}

		return null;
	}

	public static ImageDescriptor getImageDescriptor(final String key) {
		ImageDescriptor imageDescriptor= null;
		final URL imageUrl= getImageUrl(key);
		if (imageUrl != null) {
			imageDescriptor= ImageDescriptor.createFromURL(imageUrl);
		}
		return imageDescriptor;
	}
	
	// Sequence

	private static final AtomicLong atomicLong= new AtomicLong(0);

	public static String getSequenceNumber() {
		final long id= atomicLong.addAndGet(1);
		return String.valueOf(id);
	}
	
	
	/**
	 * Blend the two colour values returning a value that is halfway between
	 * them.
	 *
	 * @param val1
	 *            the first value
	 * @param val2
	 *            the second value
	 * @return the blended colour
	 */
	public static RGB blend(final RGB val1, final RGB val2) {
		return new RGB((val1.red + val2.red) / 2,
				(val1.green + val2.green) / 2,
				(val1.blue + val2.blue) / 2 ); 
	}

}
