/*******************************************************************************
 * Copyright (c) 2012-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.internal;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.themes.ColorUtil;


/**
 * An arrow image descriptor, e.g. for drop down buttons.
 * Using the foreground and background colors of the widget makes the arrow visible even in high 
 * contrast too.
 */
public class AccessibleArrowImage extends CompositeImageDescriptor {
	
	
	public final static int DEFAULT_SIZE= 5;
	
	
	private final int fDirection;
	
	private final RGB fForegroundColor;
	private final RGB fBackgroundColor;
	
	private final int fSize;
	
	
	public AccessibleArrowImage(final int direction, final int size,
			final RGB foregroundColor, final RGB backgroundColor) {
		switch (direction) {
		case SWT.UP:
		case SWT.DOWN:
		case SWT.LEFT:
		case SWT.RIGHT:
			break;
		default:
			throw new IllegalArgumentException("direction: " + direction); //$NON-NLS-1$
		}
		if (foregroundColor == null) {
			throw new NullPointerException("foregroundColor");
		}
		if (backgroundColor == null) {
			throw new NullPointerException("backgroundColor");
		}
		fDirection = direction;
		fForegroundColor = foregroundColor;
		fBackgroundColor = backgroundColor;
		fSize = (size == -1) ? DEFAULT_SIZE : size;
	}
	
	
	@Override
	protected void drawCompositeImage(final int width, final int height) {
		final Display display= Display.getCurrent();
		
		final Image image= (fDirection == SWT.LEFT || fDirection == SWT.RIGHT) ?
				new Image(display, fSize, fSize * 2 - 1) :
				new Image(display, fSize * 2 - 1, fSize);
		
		final ImageData imageData= image.getImageData();
		final int foreground = imageData.palette.getPixel(
				ColorUtil.blend(fForegroundColor, fBackgroundColor, 80) );
		final int aliasing = imageData.palette.getPixel(
				ColorUtil.blend(fForegroundColor, fBackgroundColor, 60) );
		
		final int size1 = fSize - 1;
		int xOffset = 0;
		int yOffset = 0;
		switch (fDirection) {
		case SWT.UP:
			for (int i = 0; i <= size1; i++) {
				final int last = size1 * 2 - i;
				imageData.setPixel(i, size1 - i, aliasing);
				imageData.setAlpha(i, size1 - i, 255);
				imageData.setPixel(last, size1 - i, aliasing);
				imageData.setAlpha(last, size1 - i, 255);
				for (int j = i + 1; j < last; j++) {
					imageData.setPixel(j, size1 - i, foreground);
					imageData.setAlpha(j, size1 - i, 255);
				}
			}
			break;
		case SWT.DOWN:
			for (int i = 0; i <= size1; i++) {
				final int last = size1 * 2 - i;
				imageData.setPixel(i, i, aliasing);
				imageData.setAlpha(i, i, 255);
				imageData.setPixel(last, i, aliasing);
				imageData.setAlpha(last, i, 255);
				for (int j = i + 1; j < last; j++) {
					imageData.setPixel(j, i, foreground);
					imageData.setAlpha(j, i, 255);
				}
			}
			yOffset = 1;
			break;
		case SWT.LEFT:
			for (int i = 0; i <= size1; i++) {
				final int last = size1 * 2 - i;
				imageData.setPixel(size1 - i, i, aliasing);
				imageData.setAlpha(size1 - i, i, 255);
				imageData.setPixel(size1 - i, last, aliasing);
				imageData.setAlpha(size1 - i, last, 255);
				for (int j = i + 1; j < last; j++) {
					imageData.setPixel(size1 - i, j, foreground);
					imageData.setAlpha(size1 - i, j, 255);
				}
			}
			break;
		case SWT.RIGHT:
			for (int i = 0; i <= size1; i++) {
				final int last = size1 * 2 - i;
				imageData.setPixel(i, i, aliasing);
				imageData.setAlpha(i, i, 255);
				imageData.setPixel(i, last, aliasing);
				imageData.setAlpha(i, last, 255);
				for (int j = i + 1; j < last; j++) {
					imageData.setPixel(i, j, foreground);
					imageData.setAlpha(i, j, 255);
				}
			}
			xOffset = 1;
			break;
		}
		
		drawImage(imageData,
				(width - imageData.width) / 2 + xOffset,
				(height - imageData.height) / 2 + yOffset);
		
		image.dispose();
	}
	
	@Override
	protected Point getSize() {
		final int corr = ((fSize % 2) == 0) ? -1 : 0;
		switch (fDirection) {
		case SWT.UP:
		case SWT.DOWN:
			return new Point(fSize * 3 + corr, fSize * 2);
		case SWT.LEFT:
		case SWT.RIGHT:
			return new Point(fSize * 2, fSize * 3 + corr);
		default:
			throw new IllegalStateException();
		}
	}
	
	
	@Override
	public int hashCode() {
		return fDirection * 7 + fSize * 3460
				+ fForegroundColor.hashCode() * 343629
				+ fBackgroundColor.hashCode() * 987972;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AccessibleArrowImage other = (AccessibleArrowImage) obj;
		return (fDirection == other.fDirection
				&& fSize == other.fSize
				&& fForegroundColor.equals(other.fForegroundColor)
				&& fBackgroundColor.equals(other.fBackgroundColor) );
	}
	
}
