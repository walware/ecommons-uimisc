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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;

import org.eclipse.nebula.widgets.nattable.style.BorderStyle.LineStyle;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignment;


public class SWTUtil {
	
	
	public static final int getStart(final Rectangle rect, final Orientation orientation) {
		return (orientation == HORIZONTAL) ?
				rect.x :
				rect.y;
	}
	
	public static final int getSize(final Rectangle rect, final Orientation orientation) {
		return (orientation == HORIZONTAL) ?
				rect.width :
				rect.height;
	}
	
	public static Rectangle switchOrientation(final Rectangle rect) {
		return new Rectangle(rect.y, rect.x, rect.height, rect.width);
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
