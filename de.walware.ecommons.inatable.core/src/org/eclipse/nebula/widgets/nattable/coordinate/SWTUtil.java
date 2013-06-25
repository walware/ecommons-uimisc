/*******************************************************************************
 * Copyright (c) 2013 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.coordinate;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;

import org.eclipse.nebula.widgets.nattable.style.BorderStyle.LineStyle;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignment;


public class SWTUtil {
	
	
	public static final int getStart(final org.eclipse.swt.graphics.Rectangle rect, final Orientation orientation) {
		return (orientation == HORIZONTAL) ?
				rect.x :
				rect.y;
	}
	
	public static final int getSize(final org.eclipse.swt.graphics.Rectangle rect, final Orientation orientation) {
		return (orientation == HORIZONTAL) ?
				rect.width :
				rect.height;
	}
	
	public static final org.eclipse.swt.graphics.Rectangle toSWT(final Rectangle rect) {
		if (rect.x > Integer.MAX_VALUE || rect.y > Integer.MAX_VALUE
				|| rect.width > Integer.MAX_VALUE || rect.height > Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		return new org.eclipse.swt.graphics.Rectangle((int) rect.x, (int) rect.y, (int) rect.width, (int) rect.height);
	}
	
	public static final Rectangle toNatTable(final org.eclipse.swt.graphics.Rectangle rect) {
		return new Rectangle(rect.x, rect.y, rect.width, rect.height);
	}
	
	public static final org.eclipse.swt.graphics.Point toSWT(final Point point) {
		if (point.x > Integer.MAX_VALUE || point.y > Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		return new org.eclipse.swt.graphics.Point((int) point.x, (int) point.y);
	}
	
	
	public static final ScrollBar getScrollBar(final Scrollable control, final Orientation orientation) {
		return (orientation == HORIZONTAL) ?
				control.getHorizontalBar() :
				control.getVerticalBar();
	}
	
	public static final int getMouseWheelEventType(final Orientation orientation) {
		return (orientation == HORIZONTAL) ?
				SWT.MouseHorizontalWheel :
				SWT.MouseVerticalWheel;
	}
	
	public static final int getPixel(final MouseEvent event, final Orientation orientation) {
		return (orientation == HORIZONTAL) ?
				event.x :
				event.y;
	}
	
	public static final int toSWT(final HorizontalAlignment alignment) {
		switch (alignment) {
		case LEFT:
			return SWT.LEFT;
		case CENTER:
			return SWT.CENTER;
		case RIGHT:
			return SWT.RIGHT;
		default:
			throw new IllegalStateException();
		}
	}
	
	public static final int toSWT(final LineStyle lineStyle) {
		switch (lineStyle) {
		case SOLID:
			return SWT.LINE_SOLID;
		case DASHED:
			return SWT.LINE_DASH;
		case DOTTED:
			return SWT.LINE_DOT;
		case DASHDOT:
			return SWT.LINE_DASHDOT;
		case DASHDOTDOT:
			return SWT.LINE_DASHDOTDOT;
		default:
			throw new IllegalStateException();
		}
	}
	
}
