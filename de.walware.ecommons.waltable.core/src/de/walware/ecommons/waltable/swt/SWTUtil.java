/*******************************************************************************
 * Copyright (c) 2013-2016 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package de.walware.ecommons.waltable.swt;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;

import de.walware.ecommons.waltable.coordinate.LPoint;
import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.style.BorderStyle.LineStyle;
import de.walware.ecommons.waltable.style.HorizontalAlignment;


public class SWTUtil {
	
	
	public static final int getMouseWheelEventType(/*@NonNull*/ final Orientation orientation) {
		if (orientation == null) {
			throw new NullPointerException("orientation"); //$NON-NLS-1$
		}
		return (orientation == HORIZONTAL) ?
				SWT.MouseHorizontalWheel :
				SWT.MouseVerticalWheel;
	}
	
	public static final ScrollBar getScrollBar(/*@NonNull*/ final Scrollable control,
			/*@NonNull*/ final Orientation orientation) {
		if (control == null) {
			throw new NullPointerException("control"); //$NON-NLS-1$
		}
		if (orientation == null) {
			throw new NullPointerException("orientation"); //$NON-NLS-1$
		}
		return (orientation == HORIZONTAL) ?
				control.getHorizontalBar() :
				control.getVerticalBar();
	}
	
	public static final LRange getRange(/*@NonNull*/ final LRectangle lRectangle,
			/*@NonNull*/ final Orientation orientation) {
		if (lRectangle == null) {
			throw new NullPointerException("rectangle"); //$NON-NLS-1$
		}
		if (orientation == null) {
			throw new NullPointerException("orientation"); //$NON-NLS-1$
		}
		return (orientation == HORIZONTAL) ?
				new LRange(lRectangle.x, lRectangle.x + lRectangle.width) :
				new LRange(lRectangle.y, lRectangle.y + lRectangle.height);
	}
	
	
	public static final int get(final org.eclipse.swt.graphics.Point point, final Orientation orientation) {
		return (orientation == HORIZONTAL) ?
				point.x :
				point.y;
	}
	
	public static final int getStart(final org.eclipse.swt.graphics.Rectangle rect, final Orientation orientation) {
		return (orientation == HORIZONTAL) ?
				rect.x :
				rect.y;
	}
	
	public static final int getEnd(final org.eclipse.swt.graphics.Rectangle rect, final Orientation orientation) {
		return (orientation == HORIZONTAL) ?
				rect.x + rect.width :
				rect.y + rect.height;
	}
	
	public static final int getSize(final org.eclipse.swt.graphics.Rectangle rect, final Orientation orientation) {
		return (orientation == HORIZONTAL) ?
				rect.width :
				rect.height;
	}
	
	public static final int get(final org.eclipse.swt.events.MouseEvent point, final Orientation orientation) {
		return (orientation == HORIZONTAL) ?
				point.x :
				point.y;
	}
	
	
	public static final Rectangle toSWT(final LRectangle rect) {
		if (rect.x > Integer.MAX_VALUE || rect.y > Integer.MAX_VALUE
				|| rect.width > Integer.MAX_VALUE || rect.height > Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		return new Rectangle((int) rect.x, (int) rect.y, (int) rect.width, (int) rect.height);
	}
	
	public static final LRectangle toNatTable(final Rectangle rect) {
		return new LRectangle(rect.x, rect.y, rect.width, rect.height);
	}
	
	public static final Point toSWT(final LPoint lPoint) {
		if (lPoint.x > Integer.MAX_VALUE || lPoint.y > Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		return new org.eclipse.swt.graphics.Point((int) lPoint.x, (int) lPoint.y);
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
